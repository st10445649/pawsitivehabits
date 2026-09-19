package com.zahraag.pawsitivehabits.viewmodel

import android.app.Application
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.zahraag.pawsitivehabits.data.models.AppDatabase
import com.zahraag.pawsitivehabits.data.models.Pet
import com.zahraag.pawsitivehabits.data.models.UserSettings
import com.zahraag.pawsitivehabits.data.remote.RetrofitClient
import com.zahraag.pawsitivehabits.data.remote.TokenManager
import com.zahraag.pawsitivehabits.data.repository.UserRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class UserUiState(
    val userName: String = "",
    val userEmail: String = "",
    val settings: UserSettings? = null,
    val pets: List<Pet> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class UserViewModel(application: Application) : AndroidViewModel(application) {

    private val context = application.applicationContext
    private val tokenManager = TokenManager(context)
    private val database = AppDatabase.getDatabase(context)

    private val userRepository = UserRepository(
        userDao = database.userDao(),
        apiService = RetrofitClient.getApiService(context),
        context = context
    )

    private val _userName = MutableStateFlow("Pet Parent")
    private val _userEmail = MutableStateFlow("")
    private val _isLoading = MutableStateFlow(false)
    private val _errorMessage = MutableStateFlow<String?>(null)

    private val userId: String
        get() = tokenManager.getUserId() ?: ""

    val uiState: StateFlow<UserUiState> = combine(
        userRepository.getUserSettings(userId),
        database.petDao().getPetsByUserId(userId),
        _userName,
        _userEmail,
        _isLoading,
        _errorMessage
    ) { args ->
        @Suppress("UNCHECKED_CAST")
        UserUiState(
            settings = (args[0] as? UserSettings) ?: UserSettings(userId = userId),
            pets = args[1] as List<Pet>,
            userName = args[2] as String,
            userEmail = args[3] as String,
            isLoading = args[4] as Boolean,
            errorMessage = args[5] as? String
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = UserUiState(isLoading = true)
    )

    init {
        loadUserData()
    }

    fun loadUserData() {
        if (userId.isEmpty()) return

        viewModelScope.launch {
            _isLoading.value = true

            // Fetch non-persistent user details from the backend API
            val profileResult = userRepository.fetchUserProfile()
            profileResult.onSuccess { profile ->
                _userName.value = profile.name ?: "Pet Parent"
                _userEmail.value = profile.email ?: ""
            }.onFailure { error ->
                _errorMessage.value = error.message
            }

            // Sync Room-stored UserSettings from the backend API
            userRepository.syncUserSettings()

            _isLoading.value = false
        }
    }

    fun saveSettings(updatedSettings: UserSettings) {
        viewModelScope.launch {
            userRepository.updateSettings(updatedSettings)
        }
    }

    fun syncAllData() {
        viewModelScope.launch {
            // Trigger offline worker sync and reload API user profile + settings
            userRepository.triggerImmediateDataSync()
            loadUserData()
        }
    }

    fun exportPetData(petId: String, uri: Uri) {
        val currentUserId = userId
        if (currentUserId.isEmpty()) return

        viewModelScope.launch {
            try {
                val pet = database.petDao().getPetsByUserId(currentUserId).firstOrNull()
                    ?.firstOrNull { it.id == petId }
                    ?: run {
                        _errorMessage.value = "Pet not found"
                        return@launch
                    }

                val weights = database.weightDao().getWeightsForUser(currentUserId).firstOrNull()
                    ?.filter { it.petId == petId }
                    ?.sortedBy { it.date } ?: emptyList()

                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

                val pdfDocument = PdfDocument()
                val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 @ 72dpi
                var page = pdfDocument.startPage(pageInfo)
                var canvas = page.canvas

                val titlePaint = Paint().apply { textSize = 20f; isFakeBoldText = true }
                val headingPaint = Paint().apply { textSize = 14f; isFakeBoldText = true }
                val bodyPaint = Paint().apply { textSize = 12f }

                val leftMargin = 40f
                val lineHeight = 20f
                val pageHeight = 842f
                val bottomMargin = 40f
                var y = 50f

                fun newPageIfNeeded() {
                    if (y > pageHeight - bottomMargin) {
                        pdfDocument.finishPage(page)
                        page = pdfDocument.startPage(pageInfo)
                        canvas = page.canvas
                        y = 50f
                    }
                }

                canvas.drawText("Pet Care Report - ${pet.name}", leftMargin, y, titlePaint)
                y += lineHeight * 2

                canvas.drawText("Pet Details", leftMargin, y, headingPaint)
                y += lineHeight
                canvas.drawText("Name: ${pet.name}", leftMargin, y, bodyPaint); y += lineHeight
                canvas.drawText("Species: ${pet.petType}", leftMargin, y, bodyPaint); y += lineHeight
                canvas.drawText("Breed: ${pet.breed ?: "N/A"}", leftMargin, y, bodyPaint); y += lineHeight
                canvas.drawText(
                    "Date of Birth: ${dateFormat.format(Date(pet.dateOfBirth))}",
                    leftMargin, y, bodyPaint
                ); y += lineHeight
                canvas.drawText("Microchip ID: ${pet.microchipId ?: "N/A"}", leftMargin, y, bodyPaint); y += lineHeight
                canvas.drawText(
                    "Neutered/Spayed: ${if (pet.isNeutered) "Yes" else "No"}",
                    leftMargin, y, bodyPaint
                )
                y += lineHeight * 2

                newPageIfNeeded()
                canvas.drawText("Weight History", leftMargin, y, headingPaint)
                y += lineHeight

                if (weights.isEmpty()) {
                    canvas.drawText("No weight records logged yet.", leftMargin, y, bodyPaint)
                    y += lineHeight
                } else {
                    weights.forEach { weight ->
                        newPageIfNeeded()
                        canvas.drawText(
                            "${dateFormat.format(Date(weight.date))}   -   ${weight.weightValue} ${weight.unit}",
                            leftMargin, y, bodyPaint
                        )
                        y += lineHeight
                    }
                }

                pdfDocument.finishPage(page)

                context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                    pdfDocument.writeTo(outputStream)
                }
                pdfDocument.close()

                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = "Export failed: ${e.message}"
            }
        }
    }

    fun updateWeightUnit(newUnit: String) {
        val currentSettings = uiState.value.settings ?: return
        if (currentSettings.weightUnit == newUnit) return

        viewModelScope.launch {
            userRepository.updateWeightUnitAndSettings(currentSettings, newUnit)
        }
    }

    fun toggleNotifications(enabled: Boolean) {
        val currentSettings = uiState.value.settings ?: return
        val updated = currentSettings.copy(notificationsEnabled = enabled)

        viewModelScope.launch {
            userRepository.updateSettings(updated)
        }
    }
}
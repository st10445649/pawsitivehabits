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

    private val _uiState = MutableStateFlow(UserUiState())
    val uiState: StateFlow<UserUiState> = _uiState.asStateFlow()

    init {
        loadUserData()
    }

    fun loadUserData() {
        val userId = tokenManager.getUserId() ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            launch {
                userRepository.getUserSettings(userId).collect { settings ->
                    _uiState.update { current ->
                        current.copy(
                            settings = settings ?: UserSettings(userId = userId),
                            isLoading = false
                        )
                    }
                }
            }

            launch {
                database.petDao().getPetsByUserId(userId).collect { pets ->
                    _uiState.update { current -> current.copy(pets = pets) }
                }
            }

            val profileResult = userRepository.fetchUserProfile()
            profileResult.onSuccess { profile ->
                _uiState.update { current ->
                    current.copy(
                        userName = profile.name ?: "Pet Parent",
                        userEmail = profile.email ?: ""
                    )
                }
            }.onFailure { error ->
                _uiState.update { it.copy(errorMessage = error.message) }
            }

            userRepository.syncUserSettings()
        }
    }

    fun saveSettings(updatedSettings: UserSettings) {
        viewModelScope.launch {
            userRepository.updateSettings(updatedSettings)
        }
    }

    /**
     * Exports a single pet's details + weight history to a PDF at the given Uri
     * (obtained from an ActivityResultContracts.CreateDocument("application/pdf") launcher).
     */
    fun exportPetData(petId: String, uri: Uri) {
        val userId = tokenManager.getUserId() ?: return

        viewModelScope.launch {
            try {
                val pet = database.petDao().getPetsByUserId(userId).first()
                    .firstOrNull { it.id == petId }
                    ?: run {
                        _uiState.update { it.copy(errorMessage = "Pet not found") }
                        return@launch
                    }

                val weights = database.weightDao().getWeightsForUser(userId).first()
                    .filter { it.petId == petId }
                    .sortedBy { it.date }

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

                _uiState.update { it.copy(errorMessage = null) }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = "Export failed: ${e.message}") }
            }
        }
    }

    fun updateWeightUnit(newUnit: String) {
        val currentSettings = _uiState.value.settings ?: return
        if (currentSettings.weightUnit == newUnit) return

        viewModelScope.launch {
            userRepository.updateWeightUnitAndSettings(currentSettings, newUnit)
        }
    }

    fun toggleNotifications(enabled: Boolean) {
        val currentSettings = _uiState.value.settings ?: return
        val updated = currentSettings.copy(notificationsEnabled = enabled)

        viewModelScope.launch {
            userRepository.updateSettings(updated)
        }
    }

    fun syncAllData() {
        userRepository.triggerImmediateDataSync()
    }
}
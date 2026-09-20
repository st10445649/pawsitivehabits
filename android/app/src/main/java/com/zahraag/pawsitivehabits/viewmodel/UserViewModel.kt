package com.zahraag.pawsitivehabits.viewmodel

import android.app.Application
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.net.Uri
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.zahraag.pawsitivehabits.data.models.AppDatabase
import com.zahraag.pawsitivehabits.data.models.Pet
import com.zahraag.pawsitivehabits.data.models.UserSettings
import com.zahraag.pawsitivehabits.data.remote.RetrofitClient
import com.zahraag.pawsitivehabits.data.remote.TokenManager
import com.zahraag.pawsitivehabits.data.repository.UserRepository
import com.zahraag.pawsitivehabits.ui.theme.MintBackground
import com.zahraag.pawsitivehabits.ui.theme.MintCardSurface
import com.zahraag.pawsitivehabits.ui.theme.MintDarkGreen
import com.zahraag.pawsitivehabits.ui.theme.MintPrimary
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
                // Fetch Pet Details
                val pet = database.petDao().getPetsByUserId(currentUserId).firstOrNull()
                    ?.firstOrNull { it.id == petId }
                    ?: run {
                        _errorMessage.value = "Pet not found"
                        return@launch
                    }

                //Fetch Weight Records
                val weights = database.weightDao().getWeightsForUser(currentUserId).firstOrNull()
                    ?.filter { it.petId == petId }
                    ?.sortedBy { it.date } ?: emptyList()

                // Fetch Routines for this Pet
                val routines = database.routineDao().getRoutinesForPet(petId)
                    .firstOrNull() ?: emptyList()

                val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

                // Dimensions: A4
                val pageWidth = 595
                val pageHeight = 842
                val leftMargin = 40f
                val rightMargin = 555f
                val topMargin = 50f
                val bottomMargin = 50f
                val printableWidth = rightMargin - leftMargin

                val pdfDocument = PdfDocument()
                var pageNumber = 1
                var pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
                var page = pdfDocument.startPage(pageInfo)
                var canvas = page.canvas

                // Paints
                val headerPaint = Paint().apply {
                    color = MintPrimary.toArgb()
                    style = Paint.Style.FILL
                }
                val headerTitlePaint = Paint().apply {
                    color = android.graphics.Color.WHITE
                    textSize = 22f
                    isFakeBoldText = true
                }
                val headerSubPaint = Paint().apply {
                    color = android.graphics.Color.WHITE
                    textSize = 11f
                }

                val sectionHeaderPaint = Paint().apply {
                    color = MintDarkGreen.toArgb()
                    textSize = 14f
                    isFakeBoldText = true
                }
                val labelPaint = Paint().apply {
                    color = MintDarkGreen.toArgb()
                    textSize = 10f
                    isFakeBoldText = true
                }
                val valuePaint = Paint().apply {
                    color = MintPrimary.toArgb()
                    textSize = 11f
                }
                val tableTextPaint = Paint().apply {
                    color = MintDarkGreen.toArgb()
                    textSize = 10f
                }

                val linePaint = Paint().apply {
                    color = MintCardSurface.copy(alpha = 1f).toArgb()
                    strokeWidth = 1f
                    style = Paint.Style.STROKE
                }
                val zebraBgPaint = Paint().apply {
                    color = MintBackground.copy(alpha = 1f).toArgb()
                    style = Paint.Style.FILL
                }
                var y = topMargin

                fun drawPageHeader() {
                    // Header Banner
                    canvas.drawRect(0f, 0f, pageWidth.toFloat(), 70f, headerPaint)
                    canvas.drawText("Pawsitive Habits", leftMargin, 35f, headerTitlePaint)
                    canvas.drawText("Pet Care & Health Report • Generated ${dateFormat.format(Date())}", leftMargin, 54f, headerSubPaint)
                    y = 90f
                }

                fun drawPageFooter() {
                    canvas.drawText("Page $pageNumber", rightMargin - 40f, pageHeight - 20f, labelPaint)
                    canvas.drawLine(leftMargin, pageHeight - 35f, rightMargin, pageHeight - 35f, linePaint)
                }

                fun checkNewPage(heightNeeded: Float) {
                    if (y + heightNeeded > pageHeight - bottomMargin) {
                        drawPageFooter()
                        pdfDocument.finishPage(page)

                        pageNumber++
                        pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
                        page = pdfDocument.startPage(pageInfo)
                        canvas = page.canvas

                        drawPageHeader()
                    }
                }

                // Initial Header Setup
                drawPageHeader()

                // --- 1. PET DETAILS CARD ---
                canvas.drawText("PET DETAILS", leftMargin, y, sectionHeaderPaint)
                y += 8f
                canvas.drawLine(leftMargin, y, rightMargin, y, linePaint)
                y += 18f

                val detailsGrid = listOf(
                    "Name" to pet.name,
                    "Species / Type" to pet.petType,
                    "Breed" to (pet.breed?.ifBlank { "N/A" } ?: "N/A"),
                    "Microchip ID" to (pet.microchipId?.ifBlank { "Not Microchipped" } ?: "Not Microchipped"),
                    "Date of Birth" to (pet.dateOfBirth?.let { dateFormat.format(Date(it)) } ?: "Unknown"),
                    "Spayed / Neutered" to if (pet.isNeutered) "Yes" else "No"
                )

                // Draw grid in 2 columns
                val col1X = leftMargin
                val col2X = leftMargin + (printableWidth / 2f)

                detailsGrid.chunked(2).forEach { row ->
                    checkNewPage(35f)

                    // Column 1
                    canvas.drawText(row[0].first.uppercase(), col1X, y, labelPaint)
                    canvas.drawText(row[0].second, col1X, y + 14f, valuePaint)

                    // Column 2
                    if (row.size > 1) {
                        canvas.drawText(row[1].first.uppercase(), col2X, y, labelPaint)
                        canvas.drawText(row[1].second, col2X, y + 14f, valuePaint)
                    }

                    y += 32f
                }

                y += 15f

                // Routines
                checkNewPage(40f)
                canvas.drawText("ACTIVE ROUTINES & CARE SCHEDULE", leftMargin, y, sectionHeaderPaint)
                y += 8f
                canvas.drawLine(leftMargin, y, rightMargin, y, linePaint)
                y += 18f

                if (routines.isEmpty()) {
                    canvas.drawText("No active routines set up for ${pet.name}.", leftMargin, y, tableTextPaint)
                    y += 25f
                } else {
                    // Table Header
                    canvas.drawRect(leftMargin, y, rightMargin, y + 20f, zebraBgPaint)
                    canvas.drawText("ROUTINE TITLE", leftMargin + 8f, y + 14f, labelPaint)
                    canvas.drawText("FREQUENCY", leftMargin + 200f, y + 14f, labelPaint)
                    canvas.drawText("NOTES / REPEAT", leftMargin + 350f, y + 14f, labelPaint)
                    y += 24f

                    routines.forEachIndexed { index, routine ->
                        checkNewPage(22f)
                        if (index % 2 == 1) {
                            canvas.drawRect(leftMargin, y - 12f, rightMargin, y + 8f, zebraBgPaint)
                        }

                        canvas.drawText(routine.title, leftMargin + 8f, y, valuePaint)
                        canvas.drawText(routine.frequency, leftMargin + 200f, y, tableTextPaint)
                        canvas.drawText(routine.repeatDays ?: "Everyday", leftMargin + 350f, y, tableTextPaint)
                        y += 20f
                    }
                    y += 15f
                }

                // weight history
                checkNewPage(40f)
                canvas.drawText("WEIGHT HISTORY LOGS", leftMargin, y, sectionHeaderPaint)
                y += 8f
                canvas.drawLine(leftMargin, y, rightMargin, y, linePaint)
                y += 18f

                if (weights.isEmpty()) {
                    canvas.drawText("No weight entries recorded yet.", leftMargin, y, tableTextPaint)
                    y += 25f
                } else {
                    // Table Header
                    canvas.drawRect(leftMargin, y, rightMargin, y + 20f, zebraBgPaint)
                    canvas.drawText("LOG DATE", leftMargin + 8f, y + 14f, labelPaint)
                    canvas.drawText("RECORDED WEIGHT", leftMargin + 200f, y + 14f, labelPaint)
                    y += 24f

                    weights.forEachIndexed { index, weight ->
                        checkNewPage(22f)
                        if (index % 2 == 1) {
                            canvas.drawRect(leftMargin, y - 12f, rightMargin, y + 8f, zebraBgPaint)
                        }

                        val dateStr = try { dateFormat.format(Date(weight.date)) } catch (_: Exception) { "N/A" }
                        val weightStr = "${weight.weightValue} ${weight.unit}"

                        canvas.drawText(dateStr, leftMargin + 8f, y, tableTextPaint)
                        canvas.drawText(weightStr, leftMargin + 200f, y, valuePaint)
                        y += 20f
                    }
                }

                drawPageFooter()
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
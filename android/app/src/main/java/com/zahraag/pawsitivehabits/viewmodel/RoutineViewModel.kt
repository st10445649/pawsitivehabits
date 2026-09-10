package com.zahraag.pawsitivehabits.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.zahraag.pawsitivehabits.data.models.AppDatabase
import com.zahraag.pawsitivehabits.data.models.Routine
import com.zahraag.pawsitivehabits.data.remote.RetrofitClient
import com.zahraag.pawsitivehabits.data.remote.TokenManager
import com.zahraag.pawsitivehabits.data.repository.CalendarRepositoryImpl
import com.zahraag.pawsitivehabits.data.repository.PetRepository
import com.zahraag.pawsitivehabits.toEpochMilli
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId

data class AddRoutineUiState(
    val petsMap: Map<String, String> = emptyMap(),
    val isLoading: Boolean = true
)

class RoutineViewModel(application: Application) : AndroidViewModel(application) {

    private val context = application.applicationContext
    private val database = AppDatabase.getDatabase(application)
    private val apiService = RetrofitClient.getApiService(context)
    private val calendarRepository =
        CalendarRepositoryImpl(database.calendarDao(), database.routineDao(), database.routineLogsDao(), apiService, context)
    private val petRepository = PetRepository(database.petDao(), apiService, context)

    private val tokenManager = TokenManager(context)
    val userId: String = tokenManager.getUserId() ?: ""

    val uiState: StateFlow<AddRoutineUiState> = petRepository.getPetsForUser(userId)
        .map { pets ->
            AddRoutineUiState(
                petsMap = pets.associate { it.id to it.name },
                isLoading = false
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AddRoutineUiState(isLoading = true)
        )
    fun createOrUpdateRoutine(
        routineToEdit: Routine? = null,
        petId: String,
        routineType: String,
        customText: String,
        frequency: String,
        days: Set<String>,
        startDate: LocalDate,
        endDate: LocalDate,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            val currentUserId = tokenManager.getUserId()
            if (currentUserId.isNullOrEmpty()) {
                Log.e("CAL_VM", "Cannot create/update routine: User ID is null or empty.")
                return@launch
            }

            val title = if (routineType == "Custom") customText else routineType
            val startDateMillis = startDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
            val endDateMillis = endDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
            val repeatDaysString = days.joinToString(",")

            val routineToSave = routineToEdit?.copy(
                petId = petId,
                title = title,
                frequency = frequency,
                startDate = startDateMillis,
                endDate = endDateMillis,
                repeatDays = repeatDaysString
            ) ?: Routine(
                userId = currentUserId,
                petId = petId,
                title = title,
                frequency = frequency,
                startDate = startDateMillis,
                endDate = endDateMillis,
                repeatDays = repeatDaysString
            )

            if (routineToEdit != null) {
                calendarRepository.updateRoutine(routineToSave)
            } else {
                calendarRepository.insertRoutine(routineToSave)
            }

            onSuccess()
        }
    }


}
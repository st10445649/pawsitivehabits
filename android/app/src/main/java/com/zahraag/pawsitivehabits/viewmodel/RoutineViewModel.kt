package com.zahraag.pawsitivehabits.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zahraag.pawsitivehabits.data.models.Routine
import com.zahraag.pawsitivehabits.data.repository.CalendarRepository
import com.zahraag.pawsitivehabits.data.repository.PetRepository
import com.zahraag.pawsitivehabits.toEpochMilli
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

data class AddRoutineUiState(
    val petsMap: Map<String, String> = emptyMap(),
    val isLoading: Boolean = true
)

class RoutineViewModel(
    private val calendarRepository: CalendarRepository,
    private val petRepository: PetRepository,
    private val userId: String
) : ViewModel() {

    val uiState: StateFlow<AddRoutineUiState> = petRepository.getPetsByUserId(userId)
        .combine(MutableStateFlow(false)) { pets, _ ->
            AddRoutineUiState(
                petsMap = pets.associate { it.id to it.name },
                isLoading = false
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AddRoutineUiState(isLoading = true)
        )

    fun createRoutine(
        petId: String,
        routineType: String,
        customText: String,
        frequency: String,
        days: Set<String>,
        startDate: LocalDate,
        showInCalendar: Boolean,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            val title = if (routineType == "Custom") customText else routineType
            val newRoutine = Routine(
                userId = userId,
                petId = petId,
                title = title,
                frequency = frequency,
                startDate = startDate.toEpochMilli(),
                repeatDays = days.joinToString(",")
            )
            calendarRepository.insertRoutine(newRoutine)
            onSuccess()
        }
    }
}
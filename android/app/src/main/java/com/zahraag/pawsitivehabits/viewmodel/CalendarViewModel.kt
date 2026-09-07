package com.zahraag.pawsitivehabits.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zahraag.pawsitivehabits.data.models.CalendarEvents
import com.zahraag.pawsitivehabits.data.models.Routine
import com.zahraag.pawsitivehabits.data.repository.CalendarRepository
import com.zahraag.pawsitivehabits.data.repository.PetRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

//used for UI items displayed on the calendar agenda

data class AgendaUiState(
    val selectedDate: LocalDate = LocalDate.now(),
    val routines: List<Routine> = emptyList(),
    val calendarEvents: List<CalendarEvents> = emptyList(),
    val petNamesMap: Map<String, String> = emptyMap(),
    val isLoading: Boolean = false
)

class CalendarViewModel(
    private val repository: CalendarRepository,
    private val petRepository: PetRepository,
    private val userId: String
) : ViewModel() {

    private val _selectedDate = MutableStateFlow(LocalDate.now())

    val uiState: StateFlow<AgendaUiState> = combine(
        repository.getRoutinesForUser(userId),
        repository.getAllEventsForUser(userId),
        petRepository.getPetsByUserId(userId),
        _selectedDate
    ) { routines, events, pets, selectedDate ->

        val petMap = pets.associate { it.id to it.name }

        AgendaUiState(
            routines = routines,
            calendarEvents = events,
            petNamesMap = petMap,
            selectedDate = selectedDate,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AgendaUiState(isLoading = true)
    )

    fun onDateSelected(date: LocalDate) {
        _selectedDate.value = date
    }

    fun deleteCalendarEvent(event: CalendarEvents) {
        viewModelScope.launch {
            repository.deleteEvent(event.id)
        }
    }
    fun onRoutineToggled(routineId: String, petId: String) {
        viewModelScope.launch {
            repository.toggleRoutineCompletion(routineId, petId, _selectedDate.value)
        }
    }

}
package com.zahraag.pawsitivehabits.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zahraag.pawsitivehabits.data.models.CalendarEvents
import com.zahraag.pawsitivehabits.data.models.Routine
import com.zahraag.pawsitivehabits.data.repository.CalendarRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

//used for UI items displayed on the calendar agenda
sealed class CalendarAgendaItem {
    abstract val id: String
    abstract val title: String
    abstract val petId: String

    data class EventItem(val event: CalendarEvents) : CalendarAgendaItem() {
        override val id: String get() = event.id
        override val title: String get() = event.title
        override val petId: String get() = event.petId
    }

    data class RoutineItem(
        val routine: Routine,
        val isCompleted: Boolean
    ) : CalendarAgendaItem() {
        override val id: String get() = routine.id
        override val title: String get() = routine.title
        override val petId: String get() = routine.petId
    }
}

data class CalendarUiState(
    val selectedDate: LocalDate = LocalDate.now(),
    val items: List<CalendarAgendaItem> = emptyList(),
    val isLoading: Boolean = false
)

class CalendarViewModel(
    private val repository: CalendarRepository,
    private val userId: String
) : ViewModel() {

    private val _selectedDate = MutableStateFlow(LocalDate.now())

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<CalendarUiState> = _selectedDate
        .flatMapLatest { date ->
            combine(
                repository.getEventsForDate(userId, date),
                repository.getRoutinesForUser(userId),
                repository.getLogsForDate(date)
            ) { events, routines, logs ->

                val agendaList = mutableListOf<CalendarAgendaItem>()

                // Add one-time events (calendar)
                events.forEach { agendaList.add(CalendarAgendaItem.EventItem(it)) }

                // Add routines that match the day of week
                val currentDayName = date.dayOfWeek.name
                routines.filter { routine ->
                    routine.repeatDays?.contains(currentDayName, ignoreCase = true) == true
                }.forEach { routine ->
                    val isCompleted = logs.any { it.routineId == routine.id }
                    agendaList.add(CalendarAgendaItem.RoutineItem(routine, isCompleted))
                }

                CalendarUiState(
                    selectedDate = date,
                    items = agendaList,
                    isLoading = false
                )
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = CalendarUiState(isLoading = true)
        )

    fun onDateSelected(date: LocalDate) {
        _selectedDate.value = date
    }

    fun onRoutineToggled(routineId: String, petId: String) {
        viewModelScope.launch {
            repository.toggleRoutineCompletion(routineId, petId, _selectedDate.value)
        }
    }
}
package com.zahraag.pawsitivehabits.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.zahraag.pawsitivehabits.data.models.AppDatabase
import com.zahraag.pawsitivehabits.data.models.CalendarEvents
import com.zahraag.pawsitivehabits.data.models.Routine
import com.zahraag.pawsitivehabits.data.remote.RetrofitClient
import com.zahraag.pawsitivehabits.data.remote.TokenManager
import com.zahraag.pawsitivehabits.data.repository.CalendarRepositoryImpl
import com.zahraag.pawsitivehabits.data.repository.PetRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
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
class CalendarViewModel(application: Application) : AndroidViewModel(application) {

    private val context = application.applicationContext
    private val database = AppDatabase.getDatabase(application)
    private val apiService = RetrofitClient.getApiService(context)

    private val calendarRepository =
        CalendarRepositoryImpl(
            database.calendarDao(), database.routineDao(), database.routineLogsDao(),
            apiService,
            context,
        )
    private val petRepository = PetRepository(database.petDao(), apiService, context)

    private val tokenManager = TokenManager(context)
    val userId: String = tokenManager.getUserId() ?: ""

    private val _selectedDate = MutableStateFlow(LocalDate.now())

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<AgendaUiState> = _selectedDate.flatMapLatest { selectedDate ->
        val currentUserId = tokenManager.getUserId() ?: ""
        if (currentUserId.isEmpty()) {
            flowOf(AgendaUiState(selectedDate = selectedDate, isLoading = false))
        } else {
            combine(
                calendarRepository.getRoutinesForUser(currentUserId),
                calendarRepository.getAllEventsForUser(currentUserId),
                petRepository.getPetsForUser(currentUserId)
            ) { routines, events, pets ->
                AgendaUiState(
                    routines = routines,
                    calendarEvents = events,
                    petNamesMap = pets.associate { it.id to it.name },
                    selectedDate = selectedDate,
                    isLoading = false
                )
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AgendaUiState(isLoading = true)
    )

    init {
        refreshRemoteCalendarData()
    }


    fun refreshRemoteCalendarData() {
        viewModelScope.launch {
            val currentUserId = tokenManager.getUserId()
            if (currentUserId.isNullOrEmpty()) {
                Log.e("CALENDAR_VM", "Cannot sync calendar: User ID is null or empty.")
                return@launch
            }
            calendarRepository.syncCalendarData(currentUserId)
        }
    }

    fun onDateSelected(date: LocalDate) {
        _selectedDate.value = date
    }
    fun saveCalendarEvent(event: CalendarEvents, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            val currentUserId = tokenManager.getUserId()
            if (currentUserId.isNullOrEmpty()) {
                Log.e("CALENDAR_VM", "Cannot save event: User ID is null or empty.")
                return@launch
            }

            val eventWithUser = event.copy(userId = currentUserId)
            Log.d("CALENDAR_VM", "Saving calendar event for userId: $currentUserId")

            calendarRepository.insertEvent(eventWithUser)
            onSuccess()
        }
    }

    fun deleteCalendarEvent(event: CalendarEvents) {
        viewModelScope.launch {
            Log.d("CALENDAR_VM", "Deleting event ID: ${event.id}")
            calendarRepository.deleteEvent(event.id)
        }
    }

    fun onRoutineToggled(routineId: String, petId: String) {
        viewModelScope.launch {
            calendarRepository.toggleRoutineCompletion(routineId, petId, _selectedDate.value)
        }
    }

    fun deleteRoutine(routine: Routine) {
        viewModelScope.launch {
            Log.d("Routine_VM", "Deleting routine ID: ${routine.id}")
            calendarRepository.deleteRoutine(
                routine.id
            )
        }
    }
}
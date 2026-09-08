package com.zahraag.pawsitivehabits.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zahraag.pawsitivehabits.data.models.AppDatabase
import com.zahraag.pawsitivehabits.data.models.CalendarEvents
import com.zahraag.pawsitivehabits.data.remote.RetrofitClient
import com.zahraag.pawsitivehabits.data.remote.TokenManager
import com.zahraag.pawsitivehabits.data.repository.CalendarRepository
import com.zahraag.pawsitivehabits.data.repository.CalendarRepositoryImpl
import com.zahraag.pawsitivehabits.data.repository.PetRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class AddEventUiState(
    val petsMap: Map<String, String> = emptyMap(),
    val isLoading: Boolean = true
)

class EventViewModel(application: Application) : AndroidViewModel(application) {

    private val context = application.applicationContext
    private val database = AppDatabase.getDatabase(application)
    private val apiService = RetrofitClient.getApiService(context)

    private val calendarRepository =
        CalendarRepositoryImpl(database.calendarDao(), database.routineDao(), database.routineLogsDao(), apiService, context)
    private val petRepository = PetRepository(database.petDao(), apiService, context)

    val tokenManager = TokenManager(context)
    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<AddEventUiState> = flowOf(tokenManager.getUserId() ?: "")
        .flatMapLatest { currentUserId ->
            if (currentUserId.isEmpty()) {
                flowOf(emptyList())
            } else {
                petRepository.getPetsForUser(currentUserId)
            }
        }
        .map { pets ->
            AddEventUiState(
                petsMap = pets.associate { it.id to it.name },
                isLoading = false
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AddEventUiState(isLoading = true)
        )

    fun saveCalendarEvent(event: CalendarEvents, onSuccess: () -> Unit) {
        viewModelScope.launch {
            val currentUserId = tokenManager.getUserId()
            if (currentUserId.isNullOrEmpty()) {
                Log.e("EVENT_VM", "Cannot save event: User ID is null or empty.")
                return@launch
            }

            val eventWithUser = event.copy(userId = currentUserId)
            Log.d("EVENT_VM", "Saving event for userId: $currentUserId")

            calendarRepository.insertEvent(eventWithUser)
            onSuccess()
        }
    }
}
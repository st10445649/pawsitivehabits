package com.zahraag.pawsitivehabits.viewmodel

import android.app.Application
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
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
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
        CalendarRepositoryImpl(database.calendarDao(), database.routineDao(), database.routineLogsDao())
    private val petRepository = PetRepository(database.petDao(), apiService, context)

    private val tokenManager = TokenManager(context)
    val userId: String = tokenManager.getUserId() ?: ""

    val uiState: StateFlow<AddEventUiState> = petRepository.getPetsForUser(userId)
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
            calendarRepository.insertEvent(event)
            onSuccess()
        }
    }
}
package com.zahraag.pawsitivehabits.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.zahraag.pawsitivehabits.data.models.AppDatabase
import com.zahraag.pawsitivehabits.data.models.CalendarEvents
import com.zahraag.pawsitivehabits.data.models.Pet
import com.zahraag.pawsitivehabits.data.remote.RetrofitClient
import com.zahraag.pawsitivehabits.data.remote.TokenManager
import com.zahraag.pawsitivehabits.data.repository.CalendarRepositoryImpl
import com.zahraag.pawsitivehabits.data.repository.PetRepository
import com.zahraag.pawsitivehabits.data.repository.UserRepository
import com.zahraag.pawsitivehabits.screens.RoutineItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.ZoneId
import kotlin.collections.emptyList
import kotlin.collections.first
import kotlin.collections.isNotEmpty

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val context = application.applicationContext
    private val database = AppDatabase.getDatabase(context)
    private val apiService = RetrofitClient.getApiService(context)
    private val tokenManager = TokenManager(context)

    private val userRepository = UserRepository(database.userDao(), apiService, context)
    private val petRepository = PetRepository(database.petDao(), apiService, context)

    private val calendarRepository = CalendarRepositoryImpl(
        database.calendarDao(),database.routineDao(),database.routineLogsDao(),
        apiService,context
    )
    private val _userId = MutableStateFlow(tokenManager.getUserId() ?: "")
    val userId: StateFlow<String> = _userId.asStateFlow()

    private val _userName = MutableStateFlow("")
    val userName: StateFlow<String> = _userName.asStateFlow()

    init {
        fetchProfile()
    }

    fun fetchProfile() {
        viewModelScope.launch {
            userRepository.fetchUserProfile()
                .onSuccess { profile ->
                    _userName.value = profile?.name ?: "Pet Parent"
                }
                .onFailure {
                    _userName.value = "Pet Parent"
                }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val pets: StateFlow<List<Pet>> = _userId
        .flatMapLatest { id ->
            if (id.isEmpty()) flowOf(emptyList())
            else petRepository.getPetsForUser(id)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _selectedPetId = MutableStateFlow<String?>(null)
    val selectedPetId: StateFlow<String?> = _selectedPetId.asStateFlow()

    init {
        viewModelScope.launch {
            pets.collect { petList ->
                if (_selectedPetId.value == null && petList.isNotEmpty()) {
                    _selectedPetId.value = petList.first().id
                } else if (petList.isEmpty()) {
                    _selectedPetId.value = null
                }
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val routines: StateFlow<List<RoutineItem>> = _selectedPetId
        .flatMapLatest { petId ->
            if (petId.isNullOrEmpty()) {
                flowOf(emptyList())
            } else {
                calendarRepository.getRoutinesForPetAndDate(
                    petId = petId,
                    dateEpochMillis = getStartOfDayEpochMillis()
                )
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    val upcomingEvent: StateFlow<CalendarEvents?> = _selectedPetId
        .flatMapLatest { petId ->
            if (petId.isNullOrEmpty()) {
                flowOf(null)
            } else {
                calendarRepository.getNextUpcomingEventForPet(
                    petId = petId,
                    currentTimeMillis = getStartOfDayEpochMillis()
                )
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    fun selectPet(petId: String) {
        _selectedPetId.value = petId
    }

    fun toggleRoutine(routineId: String) {
        val currentPetId = _selectedPetId.value ?: return

        viewModelScope.launch {
            calendarRepository.toggleRoutineCompletion(
                routineId = routineId,
                petId = currentPetId,
                date = LocalDate.now()
            )
        }
    }

    fun getStartOfDayEpochMillis(): Long {
        return LocalDate.now()
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
    }

    fun logoutUser(onComplete: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            database.clearAllTables()

            tokenManager.clear()

            FirebaseAuth.getInstance().signOut()

            withContext(Dispatchers.Main) {
                _userId.value = ""
                _selectedPetId.value = null
                _userName.value = "Pet Parent"
                onComplete()
            }
        }
    }
}
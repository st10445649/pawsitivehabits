package com.zahraag.pawsitivehabits.viewmodel

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.zahraag.pawsitivehabits.data.models.AppDatabase
import com.zahraag.pawsitivehabits.data.models.CalendarEvents
import com.zahraag.pawsitivehabits.data.models.Pet
import com.zahraag.pawsitivehabits.data.models.Weight
import com.zahraag.pawsitivehabits.data.remote.RetrofitClient
import com.zahraag.pawsitivehabits.data.remote.TokenManager
import com.zahraag.pawsitivehabits.data.repository.CalendarRepository
import com.zahraag.pawsitivehabits.data.repository.CalendarRepositoryImpl
import com.zahraag.pawsitivehabits.data.repository.PetRepository
import com.zahraag.pawsitivehabits.data.repository.WeightRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PetViewModel(application: Application, private val calendarRepository: CalendarRepository,
                   private val weightRepository: WeightRepository
) : AndroidViewModel(application) {

    constructor(application: Application) : this(
        application = application,
        calendarRepository = CalendarRepositoryImpl(
            eventsDao = AppDatabase.getDatabase(application).calendarDao(),
            routineDao = AppDatabase.getDatabase(application).routineDao(),
            logsDao = AppDatabase.getDatabase(application).routineLogsDao(),
            apiService = RetrofitClient.getApiService(application.applicationContext),
            context = application.applicationContext
        ),
        weightRepository = WeightRepository(
            weightDao = AppDatabase.getDatabase(application).weightDao(),
            apiService = RetrofitClient.getApiService(application.applicationContext)
        )
    )

    private val context = application.applicationContext
    private val database = AppDatabase.getDatabase(application)
    private val apiService = RetrofitClient.getApiService(context)
    private val repository = PetRepository(database.petDao(), apiService, context)
    private val tokenManager = TokenManager(context)

    private val _selectedPetId = MutableStateFlow<String?>(null)
    val selectedPetId: StateFlow<String?> = _selectedPetId.asStateFlow()

    private val _userId = MutableStateFlow(tokenManager.getUserId() ?: "")

    @OptIn(ExperimentalCoroutinesApi::class)
    val localUserPets: StateFlow<List<Pet>> = _userId
        .flatMapLatest { id ->
            if (id.isEmpty()) flowOf(emptyList())
            else repository.getLocalPetsForUser(id)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    //Get the currently selected Pet details directly from local pets
    @OptIn(ExperimentalCoroutinesApi::class)
    val selectedPet: StateFlow<Pet?> = combine(localUserPets, _selectedPetId) { pets, id ->
        pets.find { it.id == id }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    //Stream Real Weight Logs from WeightRepository
    @OptIn(ExperimentalCoroutinesApi::class)
    val selectedPetWeightLogs: StateFlow<List<Weight>> = _selectedPetId
        .flatMapLatest { petId ->
            if (petId.isNullOrEmpty()) flowOf(emptyList())
            else weightRepository.getWeightsForPet(petId)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Stream Real Upcoming Events from CalendarRepository
    @OptIn(ExperimentalCoroutinesApi::class)
    val selectedPetUpcomingEvent: StateFlow<CalendarEvents?> = _selectedPetId
        .flatMapLatest { petId ->
            if (petId.isNullOrEmpty()) flowOf(null)
            else calendarRepository.getNextUpcomingEventForPet(petId, System.currentTimeMillis())
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )
    fun refreshUserId() {
        _userId.value = tokenManager.getUserId() ?: ""
    }

    fun getPets(userId: String): StateFlow<List<Pet>> {
        viewModelScope.launch {
            repository.fetchRemotePets(userId)
        }

        return repository.getPetsForUser(userId).stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    }

    fun selectPet(petId: String?) {
        _selectedPetId.update { petId }
    }

    fun addPet(pet: Pet, imageUri: Uri? = null) {
        viewModelScope.launch {
            val currentUserId = tokenManager.getUserId()
            if (currentUserId.isNullOrEmpty()) {
                Log.e("PET_VM", "Cannot create pet: User ID is null or empty. Ensure user is logged in.")
                return@launch
            }

            // Explicitly attach the authenticated user's ID
            val petWithUser = pet.copy(userId = currentUserId)

            Log.d("PET_VM", "Creating pet for userId: $currentUserId")
            repository.createPet(petWithUser, imageUri)
        }
    }

    fun updatePet(pet: Pet, newImageUri: Uri? = null) {
        viewModelScope.launch {
            val currentUserId = tokenManager.getUserId()
            if (currentUserId.isNullOrEmpty()) {
                Log.e("PET_VM", "Cannot update pet: User ID is null or empty.")
                return@launch
            }
            val petWithUser = pet.copy(userId = currentUserId)
            repository.updatePet(petWithUser, newImageUri)
        }
    }

    fun deletePet(pet: Pet) {
        viewModelScope.launch {
            if (_selectedPetId.value == pet.id) {
                _selectedPetId.update { null }
            }
            repository.deletePet(pet)
        }
    }
}
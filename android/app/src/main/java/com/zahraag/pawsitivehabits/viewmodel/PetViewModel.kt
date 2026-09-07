package com.zahraag.pawsitivehabits.viewmodel

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.zahraag.pawsitivehabits.data.models.AppDatabase
import com.zahraag.pawsitivehabits.data.models.Pet
import com.zahraag.pawsitivehabits.data.remote.RetrofitClient
import com.zahraag.pawsitivehabits.data.remote.TokenManager
import com.zahraag.pawsitivehabits.data.repository.PetRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PetViewModel(application: Application) : AndroidViewModel(application) {

    private val context = application.applicationContext
    private val database = AppDatabase.getDatabase(application)
    private val apiService = RetrofitClient.getApiService(
        context
    )

    //Instantiate repository
    private val repository = PetRepository(database.petDao(), apiService, context)

    private val _selectedPetId = MutableStateFlow<String?>(null)
    val selectedPetId: StateFlow<String?> = _selectedPetId.asStateFlow()

    val tokenManager = TokenManager(context)
    val userId = tokenManager.getUserId() ?: ""

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

    fun selectPet(petId: String) {
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
            Log.d("PET_VM", "Updating pet ID: ${petWithUser.id}")
            repository.updatePet(petWithUser, newImageUri)
        }
    }

    fun deletePet(pet: Pet) {
        viewModelScope.launch {
            Log.d("PET_VM", "Deleting pet ID: ${pet.id}")

            if (_selectedPetId.value == pet.id) {
                _selectedPetId.update { null }
            }

            repository.deletePet(pet)
        }
    }

    val localUserPets: StateFlow<List<Pet>> = repository.getLocalPetsForUser(userId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
}
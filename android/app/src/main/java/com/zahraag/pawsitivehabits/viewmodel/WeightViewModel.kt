package com.zahraag.pawsitivehabits.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.zahraag.pawsitivehabits.data.models.AppDatabase
import com.zahraag.pawsitivehabits.data.models.Weight
import com.zahraag.pawsitivehabits.data.remote.RetrofitClient
import com.zahraag.pawsitivehabits.data.remote.TokenManager
import com.zahraag.pawsitivehabits.data.repository.WeightRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class WeightViewModel(application: Application) : AndroidViewModel(application) {

    private val context = application.applicationContext
    private val database = AppDatabase.getDatabase(application)
    private val apiService = RetrofitClient.getApiService(context)
    private val tokenManager = TokenManager(context)

    private val repository = WeightRepository(database.weightDao(), apiService)

    val userId: String = tokenManager.getUserId() ?: ""

    val weightList: StateFlow<List<Weight>> = repository.getWeightsForUser(userId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        if (userId.isNotEmpty()) {
            syncData()
        } else {
            Log.e("WEIGHT_VM", "User ID is null or empty. Skipping remote sync")
        }
    }

    fun syncData() {
        viewModelScope.launch {
            if (userId.isNotEmpty()) {
                repository.syncWeightData(userId)
            }
        }
    }

    fun saveWeight(weight: Weight) {
        viewModelScope.launch {
            val currentUserId = tokenManager.getUserId()
            if (currentUserId.isNullOrEmpty()) {
                Log.e("WEIGHT_VM", "Cannot save weight: User ID is null or empty")
                return@launch
            }

            val weightWithUser = weight.copy(userId = currentUserId)
            repository.insertWeight(weightWithUser)
        }
    }

    fun deleteWeight(weightId: String) {
        viewModelScope.launch {
            repository.deleteWeight(weightId)
        }
    }
}
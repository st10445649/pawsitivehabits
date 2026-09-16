package com.zahraag.pawsitivehabits.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.zahraag.pawsitivehabits.data.models.AppDatabase
import com.zahraag.pawsitivehabits.data.models.UserSettings
import com.zahraag.pawsitivehabits.data.remote.RetrofitClient
import com.zahraag.pawsitivehabits.data.remote.TokenManager
import com.zahraag.pawsitivehabits.data.repository.UserRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class UserUiState(
    val userName: String = "",
    val userEmail: String = "",
    val settings: UserSettings? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class UserViewModel(application: Application) : AndroidViewModel(application) {

    private val context = application.applicationContext
    private val tokenManager = TokenManager(context)

    private val userRepository = UserRepository(
        userDao = AppDatabase.getDatabase(context).userDao(),
        apiService = RetrofitClient.getApiService(context),
        context = context
    )

    private val _uiState = MutableStateFlow(UserUiState())
    val uiState: StateFlow<UserUiState> = _uiState.asStateFlow()

    init {
        loadUserData()
    }

    fun loadUserData() {
        val userId = tokenManager.getUserId() ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            launch {
                userRepository.getUserSettings(userId).collect { settings ->
                    _uiState.update { current ->
                        current.copy(
                            settings = settings ?: UserSettings(userId = userId),
                            isLoading = false
                        )
                    }
                }
            }

            val profileResult = userRepository.fetchUserProfile()
            profileResult.onSuccess { profile ->
                _uiState.update { current ->
                    current.copy(
                        userName = profile.name ?: "Pet Parent",
                        userEmail = profile.email ?: ""
                    )
                }
            }.onFailure { error ->
                _uiState.update { it.copy(errorMessage = error.message) }
            }

            userRepository.syncUserSettings()
        }
    }

    fun updateWeightUnit(newUnit: String) {
        val currentSettings = _uiState.value.settings ?: return
        if (currentSettings.weightUnit == newUnit) return

        viewModelScope.launch {
            userRepository.updateWeightUnitAndSettings(currentSettings, newUnit)
        }
    }

    fun toggleNotifications(enabled: Boolean) {
        val currentSettings = _uiState.value.settings ?: return
        val updated = currentSettings.copy(notificationsEnabled = enabled)

        viewModelScope.launch {
            userRepository.updateSettings(updated)
        }
    }

    fun syncAllData() {
        userRepository.triggerImmediateDataSync()
    }
}
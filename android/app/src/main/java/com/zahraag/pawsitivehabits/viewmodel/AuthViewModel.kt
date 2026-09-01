package com.zahraag.pawsitivehabits.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zahraag.pawsitivehabits.data.models.User
import com.zahraag.pawsitivehabits.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class AuthUiState {
    object Idle : AuthUiState()
    object Loading : AuthUiState()
    data class Success(val user: User) : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = AuthRepository
    private val context = application.applicationContext
    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun register(email: String, pass: String, firstName: String, lastName: String) {
        _uiState.value = AuthUiState.Loading
        viewModelScope.launch {
            val result = repository.registerWithCustomEmail(context,email, pass, firstName, lastName)
            result.onSuccess { user ->
                _uiState.value = AuthUiState.Success(user)
            }.onFailure { error ->
                _uiState.value = AuthUiState.Error(error.localizedMessage ?: "Registration failed")
            }
        }
    }

    fun login(email: String, pass: String) {
        _uiState.value = AuthUiState.Loading
        viewModelScope.launch {
            val result = repository.loginWithCustomEmail(context,email, pass)
            result.onSuccess { user ->
                _uiState.value = AuthUiState.Success(user)
            }.onFailure { error ->
                _uiState.value = AuthUiState.Error(error.localizedMessage ?: "Login failed")
            }
        }
    }

    fun handleGoogleIdToken(idToken: String) {
        _uiState.value = AuthUiState.Loading
        viewModelScope.launch {
            val result = repository.authenticateAndSyncGoogleUser(context,idToken)
            result.onSuccess { user ->
                _uiState.value = AuthUiState.Success(user)
            }.onFailure { error ->
                _uiState.value = AuthUiState.Error(error.localizedMessage ?: "Google Auth failed")
            }
        }
    }
}
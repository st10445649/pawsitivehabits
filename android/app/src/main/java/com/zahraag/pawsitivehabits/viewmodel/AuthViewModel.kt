package com.zahraag.pawsitivehabits.viewmodel

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.zahraag.pawsitivehabits.data.models.AppDatabase
import com.zahraag.pawsitivehabits.data.models.User
import com.zahraag.pawsitivehabits.data.remote.TokenManager
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
    private val TAG = "AuthViewModel"
    private val repository = AuthRepository
    private val context = application.applicationContext

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun resetAuthState() {
        Log.d(TAG, "Resetting AuthUiState to Idle")
        _uiState.value = AuthUiState.Idle
    }

    fun register(email: String, pass: String, firstName: String, lastName: String) {
        Log.d(TAG, "register() triggered for $email")
        _uiState.value = AuthUiState.Loading
        viewModelScope.launch {
            val result = repository.registerWithCustomEmail(context, email, pass, firstName, lastName)
            result.onSuccess { user ->
                Log.d(TAG, "register() succeeded for user: ${user.email}")
                _uiState.value = AuthUiState.Success(user)
            }.onFailure { error ->
                Log.e(TAG, "register() failed: ${error.localizedMessage}")
                _uiState.value = AuthUiState.Error(error.message ?: "Registration failed")
            }
        }
    }

    fun login(email: String, pass: String) {
        Log.d(TAG, "login() triggered for $email")
        _uiState.value = AuthUiState.Loading
        viewModelScope.launch {
            val result = repository.loginWithCustomEmail(context, email, pass)
            result.onSuccess { user ->
                Log.d(TAG, "login() succeeded for user: ${user.email}")
                _uiState.value = AuthUiState.Success(user)
            }.onFailure { error ->
                Log.e(TAG, "login() failed: ${error.localizedMessage}")
                _uiState.value = AuthUiState.Error(error.localizedMessage ?: "Login failed")
            }
        }
    }

    fun handleGoogleIdToken(idToken: String) {
        Log.d(TAG, "handleGoogleIdToken() triggered")
        _uiState.value = AuthUiState.Loading
        viewModelScope.launch {
            val result = repository.authenticateAndSyncGoogleUser(context, idToken)
            result.onSuccess { user ->
                Log.d(TAG, "handleGoogleIdToken() succeeded for user: ${user.email}")
                _uiState.value = AuthUiState.Success(user)
            }.onFailure { error ->
                Log.e(TAG, "handleGoogleIdToken() failed: ${error.localizedMessage}")
                _uiState.value = AuthUiState.Error(error.localizedMessage ?: "Google Auth failed")
            }
        }
    }
}
package com.zahraag.pawsitivehabits.data.repository

import android.content.Context
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.google.firebase.auth.FirebaseAuth
import com.zahraag.pawsitivehabits.data.models.User
import com.zahraag.pawsitivehabits.data.remote.GoogleAuthRequest
import com.zahraag.pawsitivehabits.data.remote.LoginRequest
import com.zahraag.pawsitivehabits.data.remote.RegisterRequest
import com.zahraag.pawsitivehabits.data.remote.RetrofitClient
import com.zahraag.pawsitivehabits.data.remote.TokenManager
import com.zahraag.pawsitivehabits.helpers.OfflineSyncWorker
import kotlinx.coroutines.tasks.await

object AuthRepository {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    //register with custom email
    suspend fun registerWithCustomEmail(
        context: Context,
        email: String,
        pass: String,
        firstName: String,
        lastName: String
    ): Result<User> {
        val apiService = RetrofitClient.getApiService(context)
        return try {
            val response = apiService.register(
                RegisterRequest(
                    email = email,
                    password = pass,
                    firstName = firstName,
                    lastName = lastName
                )
            )

            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                val userDto = body.data?.user ?: throw Exception("User payload missing")

                body.token?.let { token ->
                    TokenManager(context).saveCustomJwtToken(token, userId = userDto.id)
                }
                val user = User(
                    firebaseUid = userDto.firebaseUid ?: "",
                    email = userDto.email,
                    firstName = userDto.firstName ?: firstName,
                    lastName = userDto.lastName ?: lastName,
                    displayName = userDto.displayName,
                    authProvider = "password",
                    password = pass
                )
                Result.success(user)
            } else {
                Result.failure(Exception(response.body()?.message ?: "Registration failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun loginWithCustomEmail(context: Context,email: String, pass: String): Result<User> {
        return try {
            val apiService = RetrofitClient.getApiService(context)
            val response = apiService.login(LoginRequest(email, pass))
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                val userDto = body.data?.user ?: throw Exception("User payload missing")

                body.token?.let { token ->
                    TokenManager(context).saveCustomJwtToken(token, userId = userDto.id)
                }
                val user = User(
                    firebaseUid = userDto.firebaseUid ?: "",
                    email = userDto.email,
                    firstName = userDto.firstName ?: "",
                    lastName = userDto.lastName ?: "",
                    displayName = userDto.displayName,
                    authProvider = "password",
                    password = pass
                )
                Result.success(user)
            } else {
                Result.failure(Exception(response.body()?.message ?: "Invalid email or password"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun authenticateAndSyncGoogleUser(context: Context, idToken: String): Result<User> {
        return try {
            // Authenticate with Firebase Auth via Google ID Token
            val credential = com.google.firebase.auth.GoogleAuthProvider.getCredential(idToken, null)
            val authResult = auth.signInWithCredential(credential).await()
            val firebaseUser = authResult.user ?: throw Exception("Firebase Auth failed")

            val firebaseIdToken = firebaseUser.getIdToken(true).await()?.token
                ?: throw Exception("Failed to retrieve Firebase ID Token")

            // Sync with Node.js backend
            val apiService = RetrofitClient.getApiService(context)
            val response = apiService.syncGoogleUser(GoogleAuthRequest(idToken = firebaseIdToken))

            if (response.isSuccessful && response.body() != null) {
                val authResponse = response.body()!!
                val userDto = authResponse.data?.user ?: throw Exception("User payload missing")

                authResponse.token?.let { token ->
                    TokenManager(context).saveCustomJwtToken(token, userId = userDto.id)
                }
                val user = User(
                    firebaseUid = firebaseUser.uid,
                    email = firebaseUser.email ?: "",
                    firstName = userDto.firstName ?: "",
                    lastName = userDto.lastName ?: "",
                    displayName = firebaseUser.displayName ?: "",
                    authProvider = "google.com",
                    password = "pass"
                )
                Result.success(user)
            } else {
                Result.failure(Exception("Backend sync failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun triggerFullSync(context: Context, userId: String) {
        val inputData = Data.Builder()
            .putString("USER_ID", userId)
            .build()

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val syncWorkRequest = OneTimeWorkRequestBuilder<OfflineSyncWorker>()
            .setConstraints(constraints)
            .setInputData(inputData)
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            "FullDeviceSyncWorker",
            ExistingWorkPolicy.REPLACE,
            syncWorkRequest
        )
    }
}


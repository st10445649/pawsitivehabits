package com.zahraag.pawsitivehabits.data.repository

import android.content.Context
import android.util.Log
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.google.firebase.auth.FirebaseAuth
import com.zahraag.pawsitivehabits.data.models.AppDatabase
import com.zahraag.pawsitivehabits.data.models.User
import com.zahraag.pawsitivehabits.data.remote.ErrorUtils
import com.zahraag.pawsitivehabits.data.remote.GoogleAuthRequest
import com.zahraag.pawsitivehabits.data.remote.LoginRequest
import com.zahraag.pawsitivehabits.data.remote.RegisterRequest
import com.zahraag.pawsitivehabits.data.remote.RetrofitClient
import com.zahraag.pawsitivehabits.data.remote.TokenManager
import com.zahraag.pawsitivehabits.helpers.OfflineSyncWorker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

object AuthRepository {
    private const val TAG = "AuthRepository"
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    suspend fun registerWithCustomEmail(
        context: Context,
        email: String,
        pass: String,
        firstName: String,
        lastName: String
    ): Result<User> {
        return withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Clearing previous Firebase session, TokenManager, and Room DB tables...")
                auth.signOut()
                TokenManager(context).clear()
                AppDatabase.getDatabase(context).clearAllTables()
                Log.d(TAG, "Previous session and DB tables cleared successfully.")

                val apiService = RetrofitClient.getApiService(context)
                Log.d(TAG, "Sending registration request to backend API...")
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
                    Log.d(TAG, "Backend registration successful. Response message: ${body.message}")

                    val userDto = body.data?.user ?: run {
                        Log.e(TAG, "Registration failed: User payload is null in response.")
                        throw Exception("User payload missing")
                    }

                    body.token?.let { token ->
                        Log.d(TAG, "Saving JWT token and User ID: ${userDto.id} to TokenManager...")
                        TokenManager(context).saveCustomJwtToken(token, userId = userDto.id)
                    } ?: Log.w(TAG, "JWT Token was null in registration response.")

                    val user = User(
                        firebaseUid = userDto.firebaseUid ?: "",
                        email = userDto.email,
                        firstName = userDto.firstName ?: firstName,
                        lastName = userDto.lastName ?: lastName,
                        displayName = userDto.displayName,
                        authProvider = "password",
                        password = pass
                    )
                    Log.d(TAG, "User object created successfully for ID: ${userDto.id}")
                    Result.success(user)
                } else {
                    val errorMessage = ErrorUtils.parseError(response)
                    Result.failure(Exception(errorMessage))
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception during registration", e)
                Result.failure(e)
            }
        }
    }

    suspend fun loginWithCustomEmail(context: Context, email: String, pass: String): Result<User> {
        return withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Starting login process for email: $email")
                WorkManager.getInstance(context).cancelAllWork()
                auth.signOut()
                TokenManager(context).clear()
                AppDatabase.getDatabase(context).clearAllTables()
                Log.d(TAG, "Previous session cleared.")

                val apiService = RetrofitClient.getApiService(context)
                Log.d(TAG, "Sending login request to backend API...")
                val response = apiService.login(LoginRequest(email, pass))

                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!
                    Log.d(TAG, "Backend login successful.")

                    val userDto = body.data?.user ?: run {
                        Log.e(TAG, "Login failed: User payload missing in response.")
                        throw Exception("User payload missing")
                    }

                    body.token?.let { token ->
                        Log.d(TAG, "Saving JWT token and User ID: ${userDto.id} to TokenManager...")
                        TokenManager(context).saveCustomJwtToken(token, userId = userDto.id)
                    } ?: Log.w(TAG, "JWT Token was null in login response.")

                    val user = User(
                        firebaseUid = userDto.firebaseUid ?: "",
                        email = userDto.email,
                        firstName = userDto.firstName ?: "",
                        lastName = userDto.lastName ?: "",
                        displayName = userDto.displayName,
                        authProvider = "password",
                        password = pass
                    )
                    Log.d(TAG, "User login successful for ID: ${userDto.id}")
                    Result.success(user)
                } else {
                    val errorMsg = response.body()?.message ?: "Invalid email or password"
                    Log.e(TAG, "Login API error: $errorMsg")
                    Result.failure(Exception(errorMsg))
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception during login", e)
                Result.failure(e)
            }
        }
    }

    suspend fun authenticateAndSyncGoogleUser(context: Context, idToken: String): Result<User> {
        return withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Starting Google Sign-In backend sync process...")
                val credential = com.google.firebase.auth.GoogleAuthProvider.getCredential(idToken, null)

                Log.d(TAG, "Signing in with Firebase credential...")
                val authResult = auth.signInWithCredential(credential).await()
                val firebaseUser = authResult.user ?: run {
                    Log.e(TAG, "Firebase Auth failed: Null user returned.")
                    throw Exception("Firebase Auth failed")
                }

                Log.d(TAG, "Retrieving Firebase ID Token...")
                val firebaseIdToken = firebaseUser.getIdToken(true).await()?.token ?: run {
                    Log.e(TAG, "Failed to retrieve Firebase ID Token.")
                    throw Exception("Failed to retrieve Firebase ID Token")
                }

                val apiService = RetrofitClient.getApiService(context)
                Log.d(TAG, "Syncing Google user with backend API...")
                val response = apiService.syncGoogleUser(GoogleAuthRequest(idToken = firebaseIdToken))

                if (response.isSuccessful && response.body() != null) {
                    val authResponse = response.body()!!
                    Log.d(TAG, "Google backend sync successful.")

                    val userDto = authResponse.data?.user ?: run {
                        Log.e(TAG, "Google Auth failed: User payload missing.")
                        throw Exception("User payload missing")
                    }

                    authResponse.token?.let { token ->
                        Log.d(TAG, "Saving Google JWT token and User ID: ${userDto.id} to TokenManager...")
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
                    Log.e(TAG, "Google Backend sync failed with response: ${response.errorBody()?.string()}")
                    Result.failure(Exception("Backend sync failed"))
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception during Google Auth sync", e)
                Result.failure(e)
            }
        }
    }

    fun triggerFullSync(context: Context, userId: String) {
        Log.d(TAG, "Enqueueing OfflineSyncWorker for User ID: $userId")
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
        Log.d(TAG, "OfflineSyncWorker enqueued successfully.")
    }

    suspend fun logoutUser(context: Context) {
        withContext(Dispatchers.IO) {
            Log.d(TAG, "Logging out user: Cancelling WorkManager jobs, clearing tokens and database...")
            WorkManager.getInstance(context).cancelAllWork()
            TokenManager(context).clear()
            FirebaseAuth.getInstance().signOut()
            AppDatabase.getDatabase(context).clearAllTables()
            Log.d(TAG, "Logout completed successfully.")
        }
    }
}


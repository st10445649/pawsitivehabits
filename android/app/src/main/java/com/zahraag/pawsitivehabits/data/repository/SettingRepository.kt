package com.zahraag.pawsitivehabits.data.repository

import android.content.Context
import android.util.Log
import androidx.work.*
import com.zahraag.pawsitivehabits.data.dao.UserDao
import com.zahraag.pawsitivehabits.data.models.UserSettings
import com.zahraag.pawsitivehabits.data.remote.ApiService
import com.zahraag.pawsitivehabits.data.remote.UserProfileDto
import com.zahraag.pawsitivehabits.helpers.OfflineSyncWorker
import kotlinx.coroutines.flow.Flow

class UserRepository(
    private val userDao: UserDao,
    private val apiService: ApiService,
    private val context: Context
) {

    fun getUserSettings(userId: String): Flow<UserSettings?> =
        userDao.getSettingsByUserId(userId)

    suspend fun fetchUserProfile(): Result<UserProfileDto> {
        return try {
            val response = apiService.getUserProfile()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to load profile (${response.code()})"))
            }
        } catch (e: Exception) {
            Log.e("USER_REPO", "Error fetching user profile", e)
            Result.failure(e)
        }
    }

    suspend fun syncUserSettings(): Result<Unit> {
        return try {
            val response = apiService.getUserSettings()
            if (response.isSuccessful) {
                val remoteSettings = response.body()?.data
                if (remoteSettings != null) {
                    userDao.saveSettings(remoteSettings.copy(isSynced = true))
                    Result.success(Unit)
                } else {
                    Result.failure(Exception("Settings payload empty"))
                }
            } else {
                Result.failure(Exception("Failed to sync settings: ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.w("USER_REPO", "Network fetch failed: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun updateWeightUnitAndSettings(settings: UserSettings, newUnit: String): Result<Unit> {
        return updateSettings(settings.copy(weightUnit = newUnit))
    }

    suspend fun updateSettings(settings: UserSettings): Result<Unit> {
        val localSettings = settings.copy(isSynced = false)
        userDao.saveSettings(localSettings)

        return try {
            val response = apiService.updateUserSettings(localSettings)
            if (response.isSuccessful && response.body() != null) {
                userDao.saveSettings(localSettings.copy(isSynced = true))
            } else {
                scheduleSyncWorker()
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Log.w("USER_REPO", "Offline mode active. Scheduled sync worker.", e)
            scheduleSyncWorker()
            Result.success(Unit)
        }
    }

    fun triggerImmediateDataSync() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val syncWorkRequest = OneTimeWorkRequestBuilder<OfflineSyncWorker>()
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            "PawsitiveHabitsSyncWorker",
            ExistingWorkPolicy.REPLACE,
            syncWorkRequest
        )
    }

    private fun scheduleSyncWorker() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val syncWorkRequest = OneTimeWorkRequestBuilder<OfflineSyncWorker>()
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            "UserSettingsSyncWorker",
            ExistingWorkPolicy.KEEP,
            syncWorkRequest
        )
    }
}
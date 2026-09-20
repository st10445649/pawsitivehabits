package com.zahraag.pawsitivehabits.data.repository

import android.util.Log
import com.zahraag.pawsitivehabits.data.dao.WeightDao
import com.zahraag.pawsitivehabits.data.models.Weight
import com.zahraag.pawsitivehabits.data.remote.ApiService
import kotlinx.coroutines.flow.Flow

class WeightRepository(
    private val weightDao: WeightDao,
    private val apiService: ApiService
) {
    fun getWeightsForUser(userId: String): Flow<List<Weight>> {
        return weightDao.getWeightsForUser(userId)
    }

    suspend fun syncWeightData(userId: String) {
        try {
            val response = apiService.getWeights()
            if (response.isSuccessful && response.body() != null) {
                val syncedWeights = response.body()!!.map { it.copy(isSynced = true) }
                weightDao.syncRemoteWeights(userId, syncedWeights)
            }
        } catch (e: Exception) {
            Log.e("SYNC_ERR", "Weight offline mode active: ${e.message}")
        }
    }

    fun getWeightsForPet(petId: String): Flow<List<Weight>> {
        return weightDao.getWeightsForPet(petId)
    }

    suspend fun insertWeight(weight: Weight) {
        weight.isSynced = false
        weightDao.insertWeight(weight)

        try {
            val response = apiService.createWeight(weight)
            if (response.isSuccessful) {
                weight.isSynced = true
                weightDao.insertWeight(weight)
            }
        } catch (e: Exception) {
            Log.e("WEIGHT_API", "Failed to sync created weight: ${e.message}")
        }
    }

    suspend fun deleteWeight(weightId: String) {
        weightDao.deleteWeightById(weightId)

        try {
            apiService.deleteWeight(weightId)
        } catch (e: Exception) {
            Log.e("WEIGHT_API", "Failed to delete remote weight: ${e.message}")
        }
    }
}
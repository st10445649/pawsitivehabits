package com.zahraag.pawsitivehabits.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.zahraag.pawsitivehabits.data.models.Pet
import com.zahraag.pawsitivehabits.data.models.Weight
import kotlinx.coroutines.flow.Flow

@Dao
interface WeightDao {
    @Query("SELECT * FROM weight_table WHERE userId = :userId ORDER BY date DESC")
    fun getWeightsForUser(userId: String): Flow<List<Weight>>

    @Query("SELECT * FROM weight_table WHERE petId = :petId ORDER BY date DESC")
    fun getWeightsForPet(petId: String): Flow<List<Weight>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeight(weight: Weight):Long
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeights(weights: List<Weight>):List<Long>
    @Query("DELETE FROM weight_table WHERE userId = :userId AND isSynced = 1")
    suspend fun deleteSyncedWeightsForUser(userId: String)

    @Query("DELETE FROM weight_table WHERE id = :weightId")
    suspend fun deleteWeightById(weightId: String)

    @Transaction
    suspend fun syncRemoteWeights(userId: String, remoteWeights: List<Weight>) {
        deleteSyncedWeightsForUser(userId)
        insertWeights(remoteWeights)
    }
}
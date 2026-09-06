package com.zahraag.pawsitivehabits.data.models

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PetDao {
    @Query("SELECT * FROM pet_table WHERE userId = :userId")
    fun getPetsByUserId(userId: String): Flow<List<Pet>>
    @Query("SELECT * FROM pet_table WHERE isSynced = 0")
    suspend fun getUnsyncedPets(): List<Pet>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPet(pet: Pet): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPets(pets: List<Pet>): List<Long>
    @Update
    suspend fun updatePet(pet: Pet)
    @Delete
    suspend fun deletePet(pet: Pet)
    @Query("DELETE FROM pet_table WHERE id = :petId")
    suspend fun deletePetById(petId: String): Int

    @Query("DELETE FROM pet_table WHERE userId = :userId")
    suspend fun clearUserPets(userId: String): Int
}
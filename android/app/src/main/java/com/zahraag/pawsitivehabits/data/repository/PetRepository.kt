package com.zahraag.pawsitivehabits.data.repository

import com.zahraag.pawsitivehabits.data.models.Pet
import com.zahraag.pawsitivehabits.data.models.PetDao
import com.zahraag.pawsitivehabits.data.remote.ApiService
import kotlinx.coroutines.flow.Flow

class PetRepository(
    private val petDao: PetDao,
    private val petApiService: ApiService
) {
    fun getPetsForUser(userId: String): Flow<List<Pet>> = petDao.getPetsByUserId(userId)

    suspend fun createPet(pet: Pet): Result<Unit> {
        return try {
            petDao.insertPet(pet.copy(isSynced = false))

            val response = petApiService.createPet(pet)
            if (response.isSuccessful) {
                petDao.insertPet(pet.copy(isSynced = true))
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to sync pet with server"))
            }
        } catch (e: Exception) {
            Result.success(Unit)
        }
    }

    suspend fun fetchRemotePets(userId: String) {
        try {
            val response = petApiService.getPets()
            if (response.isSuccessful) {
                response.body()?.data?.pets?.let { remotePets ->
                    val syncedPets = remotePets.map { it.copy(isSynced = true) }
                    petDao.insertPets(syncedPets)
                }
            }
        } catch (_: Exception) { }
    }
}
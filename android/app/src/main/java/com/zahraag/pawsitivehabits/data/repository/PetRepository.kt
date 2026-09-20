package com.zahraag.pawsitivehabits.data.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.google.android.gms.common.internal.service.Common.API
import com.zahraag.pawsitivehabits.data.models.Pet
import com.zahraag.pawsitivehabits.data.dao.PetDao
import com.zahraag.pawsitivehabits.data.remote.ApiService
import com.zahraag.pawsitivehabits.helpers.OfflineSyncWorker
import com.zahraag.pawsitivehabits.helpers.SupabaseClientProvider
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.flow.Flow
import java.io.File

class PetRepository(
    private val petDao: PetDao,
    private val petApiService: ApiService,
    private val context: Context
) {
    private val supabase = SupabaseClientProvider.client
    private val bucketName = "pawsitivehabits"

    fun getPetsForUser(userId: String): Flow<List<Pet>> = petDao.getPetsByUserId(userId)
    fun getPetsByUserId(userId: String): Flow<List<Pet>> = petDao.getPetsByUserId(userId)
    suspend fun createPet(pet: Pet, imageUri: Uri?): Result<Unit> {
        // Save local image file to internal app storage
        val localPath = imageUri?.let { saveImageToInternalStorage(context, it) }

        // 2. Create unsynced local pet entity
        var petToSave = pet.copy(
            localImagePath = localPath,
            remoteImageUrl = pet.remoteImageUrl,
            isSynced = false
        )

        // Save immediately to Room DB
        petDao.insertPet(petToSave)

        return try {
            // 3. Attempt direct Supabase upload if online
            var cloudUrl = petToSave.remoteImageUrl
            if (localPath != null && cloudUrl.isNullOrEmpty()) {
                val localFile = File(localPath)
                if (localFile.exists()) {
                    val fileName = "pet_${pet.id}_${System.currentTimeMillis()}.jpg"
                    val bytes = localFile.readBytes()

                    val bucket = supabase.storage.from(bucketName)
                    bucket.upload(path = fileName, data = bytes) {
                        upsert = true
                    }
                    cloudUrl = bucket.publicUrl(path = fileName)
                }
            }

            //Update pet with public cloud URL
            petToSave = petToSave.copy(remoteImageUrl = cloudUrl)

            //Try sending to Express API
            val response = petApiService.createPet(petToSave)

            if (response.isSuccessful) {
                // Success: Update Room DB status to synced = true
                petDao.insertPet(petToSave.copy(isSynced = true))
                Result.success(Unit)
            } else {
                Log.w(
                    "PET_REPO",
                    "Online sync failed (${response.code()}). Enqueuing background worker."
                )
                scheduleSyncWorker()
                Result.success(Unit)
            }

        } catch (e: Exception) {
            Log.w(
                "PET_REPO",
                "Device offline or network exception. Enqueuing background worker.",
                e
            )
            scheduleSyncWorker()
            Result.success(Unit)
        }
    }

    private fun scheduleSyncWorker() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val syncWorkRequest = OneTimeWorkRequestBuilder<OfflineSyncWorker>()
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            "PetSyncWorker",
            ExistingWorkPolicy.KEEP,
            syncWorkRequest
        )
    }

    private fun saveImageToInternalStorage(context: Context, uri: Uri): String? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val fileName = "pet_${System.currentTimeMillis()}.jpg"
            val file = File(context.filesDir, fileName)

            inputStream?.use { input ->
                file.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun updatePet(pet: Pet, newImageUri: Uri?): Result<Unit> {
        // Preserve existing path if no new URI provided
        val localPath = if (newImageUri != null) {
            saveImageToInternalStorage(context, newImageUri)
        } else {
            pet.localImagePath
        }

        var petToUpdate = pet.copy(
            localImagePath = localPath,
            isSynced = false
        )

        // Save immediately to Room
        petDao.insertPet(petToUpdate)

        return try {
            var cloudUrl = petToUpdate.remoteImageUrl

            //Only upload to Supabase if a new image was picked
            if (newImageUri != null && localPath != null) {
                val localFile = File(localPath)
                if (localFile.exists()) {
                    //Fixed name per pet ensures clean overwrite, no duplicates
                    val fileName = "pet_${pet.id}.jpg"
                    val bytes = localFile.readBytes()

                    val bucket = supabase.storage.from(bucketName)
                    bucket.upload(path = fileName, data = bytes) {
                        upsert = true
                    }
                    cloudUrl = bucket.publicUrl(path = fileName)
                }
            }

            petToUpdate = petToUpdate.copy(remoteImageUrl = cloudUrl)
            val response = petApiService.updatePet(petToUpdate.id, petToUpdate)

            if (response.isSuccessful) {
                petDao.insertPet(petToUpdate.copy(isSynced = true))
            } else {
                scheduleSyncWorker()
            }
            Result.success(Unit)
        } catch (e: Exception) {
            scheduleSyncWorker()
            Result.success(Unit)
        }
    }

    suspend fun deletePet(pet: Pet): Result<Unit> {
        // Delete immediately from Room DB
        petDao.deletePetById(pet.id)

        // Clean up local photo file if present
        pet.localImagePath?.let { path ->
            val file = File(path)
            if (file.exists()) file.delete()
        }

        try {
            val fileName = "pet_${pet.id}.jpg"
            supabase.storage.from(bucketName).delete(fileName)
        } catch (e: Exception) {
            Log.w("PET_REPO", "Failed to delete image from Supabase Storage: ${e.message}")
        }

        return try {
            val response = petApiService.deletePet(pet.id)

            if (!response.isSuccessful) {
                Log.w(
                    "PET_REPO",
                    "Online delete failed (${response.code()}). Enqueuing sync worker."
                )
                scheduleSyncWorker()
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Log.w(
                "PET_REPO",
                "Device offline or network exception on delete. Enqueuing sync worker.",
                e
            )
            scheduleSyncWorker()
            Result.success(Unit)
        }
    }

    suspend fun fetchRemotePets(userId: String) {
        try {
            val response = petApiService.getPets()
            if (response.isSuccessful) {
                response.body()?.data?.pets?.let { remotePets ->
                    val syncedPets = remotePets.map { pet ->
                        pet.copy(
                            userId = if (pet.userId.isBlank()) userId else pet.userId,
                            isSynced = true
                        )
                    }
                    petDao.insertPets(syncedPets)
                }
            }
        } catch (e: Exception) {
            Log.w("PET_REPO", "Failed to fetch remote pets: ${e.message}")
        }
    }

    fun getLocalPetsForUser(userId: String): Flow<List<Pet>> {
        return petDao.getPetsByUserId(userId)
    }

    fun getAllLocalPets(): Flow<List<Pet>> {
        return petDao.getAllPets()
    }
}
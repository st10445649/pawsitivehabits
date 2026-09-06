package com.zahraag.pawsitivehabits.helpers

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.zahraag.pawsitivehabits.data.models.AppDatabase
import com.zahraag.pawsitivehabits.data.remote.RetrofitClient
import io.github.jan.supabase.exceptions.NotFoundRestException
import io.github.jan.supabase.storage.storage
import io.ktor.http.ContentType
import kotlinx.io.IOException
import java.io.File


class OfflineSyncWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val database = AppDatabase.getDatabase(applicationContext)
        val petDao = database.petDao()
        val apiService = RetrofitClient.getApiService(applicationContext)
        val supabase = SupabaseClientProvider.client
        val bucketName = "pawsitivehabits"

        return try {
            val unsyncedPets = petDao.getUnsyncedPets()

            for (pet in unsyncedPets) {
                var cloudUrl = pet.remoteImageUrl

                // Upload local photo to Supabase Storage if present
                if (!pet.localImagePath.isNullOrEmpty() && cloudUrl.isNullOrEmpty()) {
                    val localFile = File(pet.localImagePath)

                    if (localFile.exists()) {
                        val fileName = "pet_${pet.id}_${System.currentTimeMillis()}.jpg"
                        val bytes = localFile.readBytes()

                        val bucket = supabase.storage.from(bucketName)
                        bucket.upload(path = fileName, data = bytes) {
                            upsert = true
                            contentType = ContentType.Image.JPEG
                        }

                        cloudUrl = bucket.publicUrl(path = fileName)
                    }
                }

                // Prepare pet payload with updated remoteImageUrl
                val syncedPet = pet.copy(
                    remoteImageUrl = cloudUrl,
                    isSynced = true
                )

                // Sync to API
                val response = apiService.createPet(syncedPet)

                if (response.isSuccessful) {
                    // Update Room DB to reflect synced state
                    petDao.insertPet(syncedPet)
                } else {
                    Log.e("SYNC_WORKER", "API sync failed: ${response.code()} ${response.errorBody()?.string()}")
                    return Result.retry()
                }
            }
            Result.success()
        } catch (e: NotFoundRestException) {
            Log.e("SYNC_WORKER", "Supabase Storage bucket missing: ${e.message}")
            Result.failure()
        } catch (e: IOException) {
            Log.e("SYNC_WORKER", "Network error during sync: ${e.message}")
            Result.retry()
        } catch (e: Exception) {
            Log.e("SYNC_WORKER", "Fatal sync exception: ${e.message}", e)
            Result.failure()
        }
    }
}
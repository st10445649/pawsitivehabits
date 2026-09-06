package com.zahraag.pawsitivehabits.helpers

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.zahraag.pawsitivehabits.data.models.AppDatabase
import io.github.jan.supabase.auth.auth


class OfflineSyncWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val supabase = SupabaseClientProvider.client
        if (supabase.auth.currentSessionOrNull() == null) {
            return Result.failure()
        }
        try {

            val database = AppDatabase.getDatabase(applicationContext)


        } catch (e: Exception) {
            Log.e("SyncWorker", "Exception in worker: ${e.message}")
            Result.retry()
        }
        return TODO("Provide the return value")
    }
}
package com.zahraag.pawsitivehabits.data.repository
import android.content.Context
import android.util.Log
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.zahraag.pawsitivehabits.data.dao.CalendarEventsDao
import com.zahraag.pawsitivehabits.data.dao.RoutineDao
import com.zahraag.pawsitivehabits.data.dao.RoutineLogsDao
import com.zahraag.pawsitivehabits.data.models.CalendarEvents
import com.zahraag.pawsitivehabits.data.models.Routine
import com.zahraag.pawsitivehabits.data.models.RoutineLogs
import com.zahraag.pawsitivehabits.data.remote.ApiService
import com.zahraag.pawsitivehabits.helpers.OfflineSyncWorker
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId

interface CalendarRepository {
    fun getEventsForDate(userId: String, date: LocalDate): Flow<List<CalendarEvents>>
    fun getAllEventsForUser(userId: String): Flow<List<CalendarEvents>>
    fun getRoutinesForUser(userId: String): Flow<List<Routine>>
    fun getLogsForDate(date: LocalDate): Flow<List<RoutineLogs>>
    suspend fun insertEvent(event: CalendarEvents)
    suspend fun deleteEvent(eventId: String)
    suspend fun insertRoutine(routine: Routine)
    suspend fun toggleRoutineCompletion(routineId: String, petId: String, date: LocalDate)
}

class CalendarRepositoryImpl(
    private val eventsDao: CalendarEventsDao,
    private val routineDao: RoutineDao,
    private val logsDao: RoutineLogsDao,
    private val apiService: ApiService,
    private val context: Context
) : CalendarRepository {

    override fun getEventsForDate(userId: String, date: LocalDate): Flow<List<CalendarEvents>> {
        val startOfDay = date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val endOfDay = date.atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        return eventsDao.getEventsForDateRange(userId, startOfDay, endOfDay)
    }

    override fun getAllEventsForUser(userId: String): Flow<List<CalendarEvents>> {
        return eventsDao.getAllEventsForUser(userId)
    }

    override fun getRoutinesForUser(userId: String): Flow<List<Routine>> {
        return routineDao.getRoutinesForUser(userId)
    }

    override fun getLogsForDate(date: LocalDate): Flow<List<RoutineLogs>> {
        val startOfDay = date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val endOfDay = date.atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        return logsDao.getLogsForDateRange(startOfDay, endOfDay)
    }

     suspend fun syncCalendarData(userId: String) {
        try {
            val eventsResp = apiService.getCalendarEvents(userId)
            if (eventsResp.isSuccessful && eventsResp.body() != null) {
                eventsDao.syncRemoteEvents(userId, eventsResp.body()!!)
            }

            val routinesResp = apiService.getRoutines(userId)
            if (routinesResp.isSuccessful && routinesResp.body() != null) {
                routineDao.syncRemoteRoutines(userId, routinesResp.body()!!)
            }

            val logsResp = apiService.getRoutineLogs(userId)
            if (logsResp.isSuccessful && logsResp.body() != null) {
                logsDao.syncRemoteLogs(userId,logsResp.body()!!)
            }
        } catch (e: Exception) {
            Log.e("SYNC_ERR", "Offline mode active: ${e.message}")
        }
    }

    override suspend fun insertEvent(event: CalendarEvents) {
        eventsDao.insertEvent(event)
        try {
            val response = apiService.createCalendarEvent(event)
            if (!response.isSuccessful) scheduleSyncWorker(event.userId)

        } catch (e: Exception) {
            Log.e("API_ERR", "Failed to sync insertEvent, enqueueing worker: ${e.message}")
            scheduleSyncWorker(event.userId)
        }
    }

    override suspend fun deleteEvent(eventId: String) {
        eventsDao.deleteEventById(eventId)
        try {
            val response = apiService.deleteCalendarEvent(eventId)
            if (!response.isSuccessful) scheduleSyncWorker()
        } catch (e: Exception) {
            Log.e("API_ERR", "Failed to sync deleteEvent, enqueueing worker: \${e.message}")
            scheduleSyncWorker()
        }
    }

    override suspend fun insertRoutine(routine: Routine) {
        routineDao.insertRoutine(routine)
        try {
            val response = apiService.createRoutine(routine)
            if (!response.isSuccessful) scheduleSyncWorker(routine.userId)
        } catch (e: Exception) {
            Log.e("API_ERR", "Failed to sync insertRoutine, enqueueing worker: ${e.message}")
            scheduleSyncWorker(routine.userId)
        }
    }

    override suspend fun toggleRoutineCompletion(routineId: String, petId: String, date: LocalDate) {
        val log = RoutineLogs(
            routineId = routineId,
            petId = petId,
            completedAt = date.atTime(LocalTime.now()).atZone(ZoneId.systemDefault()).toInstant()
                .toEpochMilli()
        )
        logsDao.insertLog(log)
        try {
            val response = apiService.logRoutineCompletion(log)
            if (!response.isSuccessful) scheduleSyncWorker()
        } catch (e: Exception) {
            Log.e("API_ERR", "Failed to sync routine completion, enqueueing worker: ${e.message}")
            scheduleSyncWorker()
        }
    }

    private fun scheduleSyncWorker(userId: String = "") {
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
            "CalendarSyncWorker",
            ExistingWorkPolicy.KEEP,
            syncWorkRequest
        )
    }
}
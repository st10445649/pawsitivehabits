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
    suspend fun updateRoutine(routine: Routine)
    suspend fun deleteRoutine(routineId: String)
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
            val eventsResp = apiService.getCalendarEvents()
            if (eventsResp.isSuccessful && eventsResp.body() != null) {
                val syncedEvents = eventsResp.body()!!.map { it.copy(isSynced = true) }
                eventsDao.syncRemoteEvents(userId, syncedEvents)
            }

            val routinesResp = apiService.getRoutines()
            if (routinesResp.isSuccessful && routinesResp.body() != null) {
                val syncedRoutines = routinesResp.body()!!.map { it.copy(isSynced = true) }
                routineDao.syncRemoteRoutines(userId, syncedRoutines)
            }

            val logsResp = apiService.getRoutineLogs()
            if (logsResp.isSuccessful && logsResp.body() != null) {
                logsDao.syncRemoteLogs(userId,logsResp.body()!!)
            }
        } catch (e: Exception) {
            Log.e("SYNC_ERR", "Offline mode active: ${e.message}")
        }
    }

    override suspend fun insertEvent(event: CalendarEvents) {
        val localEvent = event.copy(isSynced = false)
        eventsDao.insertEvent(localEvent)
        try {
            val response = apiService.createCalendarEvent(localEvent)
            if (!response.isSuccessful&& response.body() != null) {
                val remoteEvent = response.body()!!
                eventsDao.insertEvent(remoteEvent.copy(isSynced = true))
            } else {
                scheduleSyncWorker(event.userId)
            }

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
        val localRoutine = routine.copy(isSynced = false)
        routineDao.insertRoutine(localRoutine)
        try {
            val response = apiService.createRoutine(localRoutine)
            if (response.isSuccessful && response.body() != null) {
                val remoteRoutine = response.body()!!
                routineDao.insertRoutine(remoteRoutine.copy(isSynced = true))
            } else {
                scheduleSyncWorker(routine.userId)
            }
        } catch (e: Exception) {
            Log.e("API_ERR", "Failed to sync insertRoutine, enqueueing worker: ${e.message}")
            scheduleSyncWorker(routine.userId)
        }
    }

    override suspend fun updateRoutine(routine: Routine) {
        val localRoutine = routine.copy(isSynced = false)
        routineDao.insertRoutine(localRoutine)
        try {
            val response = apiService.createRoutine(localRoutine)
            if (response.isSuccessful && response.body() != null) {
                val remoteRoutine = response.body()!!
                routineDao.insertRoutine(remoteRoutine.copy(isSynced = true))
            } else {
                scheduleSyncWorker(routine.userId)
            }
        } catch (e: Exception) {
            Log.e("API_ERR", "Failed to sync updateRoutine, enqueueing worker: ${e.message}")
            scheduleSyncWorker(routine.userId)
        }
    }

    override suspend fun deleteRoutine(routineId: String) {
        routineDao.deleteRoutineById(routineId)
        try {
            val response = apiService.deleteRoutine(routineId)
            if (!response.isSuccessful) scheduleSyncWorker()
        } catch (e: Exception) {
            Log.e("API_ERR", "Failed to sync deleteRoutine, enqueueing worker: ${e.message}")
            scheduleSyncWorker()
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

    suspend fun fetchRemoteRoutines(userId: String) {
        try {
            val response = apiService.getRoutines()
            if (response.isSuccessful) {
                response.body()?.let { remoteRoutines ->
                    val syncedRoutines = remoteRoutines.map { routine ->
                        routine.copy(
                            userId = if (routine.userId.isBlank()) userId else routine.userId,
                            isSynced = true
                        )
                    }
                    routineDao.insertRoutines(syncedRoutines)
                }
            }
        } catch (e: Exception) {
            Log.w("ROUTINE_REPO", "Failed to fetch remote routines: ${e.message}")
        }
    }

    suspend fun fetchRemoteCalendarEvents(userId: String) {
        try {
            val response = apiService.getCalendarEvents()
            if (response.isSuccessful) {
                response.body()?.let { remoteEvents ->
                    val syncedEvents = remoteEvents.map { event ->
                        event.copy(
                            userId = if (event.userId.isBlank()) userId else event.userId,
                            isSynced = true
                        )
                    }
                    eventsDao.insertEvents(syncedEvents)
                }
            }
        } catch (e: Exception) {
            Log.w("CALENDAR_REPO", "Failed to fetch remote calendar events: ${e.message}")
        }
    }

    suspend fun fetchRemoteRoutineLogs(userId: String) {
        try {
            val userRoutineIds = routineDao.getRoutineIdsForUser(userId).toSet()

            val response = apiService.getRoutineLogs()
            if (response.isSuccessful) {
                response.body()?.let { remoteLogs ->
                    val syncedLogs = remoteLogs

                        .filter { log -> userRoutineIds.contains(log.routineId) }
                        .map { log -> log.copy(isSynced = true) }

                    logsDao.insertLogs(syncedLogs)
                }
            }
        } catch (e: Exception) {
            Log.w("LOGS_REPO", "Failed to fetch remote routine logs: ${e.message}")
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
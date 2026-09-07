package com.zahraag.pawsitivehabits.data.repository
import com.zahraag.pawsitivehabits.data.dao.CalendarEventsDao
import com.zahraag.pawsitivehabits.data.dao.RoutineDao
import com.zahraag.pawsitivehabits.data.dao.RoutineLogsDao
import com.zahraag.pawsitivehabits.data.models.CalendarEvents
import com.zahraag.pawsitivehabits.data.models.Routine
import com.zahraag.pawsitivehabits.data.models.RoutineLogs
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
    private val logsDao: RoutineLogsDao
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

    override suspend fun insertEvent(event: CalendarEvents) {
        eventsDao.insertEvent(event)
    }

    override suspend fun deleteEvent(eventId: String) {
        eventsDao.deleteEventById(eventId)
    }

    override suspend fun insertRoutine(routine: Routine) {
        routineDao.insertRoutine(routine)
    }

    override suspend fun toggleRoutineCompletion(routineId: String, petId: String, date: LocalDate) {
        val log = RoutineLogs(
            routineId = routineId,
            petId = petId,
            completedAt = date.atTime(LocalTime.now()).atZone(ZoneId.systemDefault()).toInstant()
                .toEpochMilli()
        )
        logsDao.insertLog(log)
    }
}
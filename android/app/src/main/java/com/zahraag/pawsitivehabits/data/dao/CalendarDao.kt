package com.zahraag.pawsitivehabits.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.patrykandpatrick.vico.compose.common.insets
import com.zahraag.pawsitivehabits.data.models.CalendarEvents
import com.zahraag.pawsitivehabits.data.models.Routine
import com.zahraag.pawsitivehabits.data.models.RoutineLogs
import kotlinx.coroutines.flow.Flow

@Dao
interface CalendarEventsDao {
    @Query("SELECT * FROM calendarEvents_table WHERE userId = :userId ORDER BY time ASC")
    fun getAllEventsForUser(userId: String): Flow<List<CalendarEvents>>
    @Query("SELECT * FROM calendarEvents_table WHERE userId = :userId AND time BETWEEN :startTimestamp AND :endTimestamp")
    fun getEventsForDateRange(userId: String, startTimestamp: Long, endTimestamp: Long): Flow<List<CalendarEvents>>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: CalendarEvents): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvents(events: List<CalendarEvents>): List<Long>


    @Query("DELETE FROM calendarEvents_table WHERE id = :eventId")
    suspend fun deleteEventById(eventId: String): Int

    @Query("DELETE FROM calendarEvents_table WHERE userId = :userId")
    suspend fun deleteAllForUser(userId: String): Int
    @Transaction
    suspend fun syncRemoteEvents(userId: String, events: List<CalendarEvents>) {
        deleteAllForUser(userId)
        insertEvents(events)
    }
}

@Dao
interface RoutineDao {
    @Query("SELECT * FROM routine_table WHERE userId = :userId")
    fun getRoutinesForUser(userId: String): Flow<List<Routine>>

    @Query("SELECT id FROM routine_table WHERE userId = :userId")
    suspend fun getRoutineIdsForUser(userId: String): List<String>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoutine(routine: Routine): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoutines(events: List<Routine>): List<Long>


    @Query("DELETE FROM routine_table WHERE id = :routineId")
    suspend fun deleteRoutineById(routineId: String): Int

    @Query("DELETE FROM routine_table WHERE userId = :userId")
    suspend fun deleteAllForUser(userId: String): Int

    @Transaction
    suspend fun syncRemoteRoutines(userId: String, routines: List<Routine>) {
        deleteAllForUser(userId)
        insertRoutines(routines)
    }
}

@Dao
interface RoutineLogsDao {
    @Query("SELECT * FROM routineLogs_table WHERE completedAt BETWEEN :startTimestamp AND :endTimestamp")
    fun getLogsForDateRange(startTimestamp: Long, endTimestamp: Long): Flow<List<RoutineLogs>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: RoutineLogs): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLogs(routineLogs: List<RoutineLogs>): List<Long>

    @Query("DELETE FROM routineLogs_table WHERE routineId = :routineId")
    suspend fun deleteLogsForRoutine(routineId: String): Int

    @Query("DELETE FROM routineLogs_table")
    suspend fun deleteAllLogs(): Int

    @Transaction
    suspend fun syncRemoteLogs(userId: String, routineLogs: List<RoutineLogs>) {
        deleteAllLogs()
        insertLogs(routineLogs)
    }
}
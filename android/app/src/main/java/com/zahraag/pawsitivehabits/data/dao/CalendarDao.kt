package com.zahraag.pawsitivehabits.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.zahraag.pawsitivehabits.data.models.CalendarEvents
import com.zahraag.pawsitivehabits.data.models.Routine
import com.zahraag.pawsitivehabits.data.models.RoutineLogs
import kotlinx.coroutines.flow.Flow

@Dao
interface CalendarEventsDao {
    @Query("SELECT * FROM calendarEvents_table WHERE userId = :userId AND date BETWEEN :startTimestamp AND :endTimestamp")
    fun getEventsForDateRange(userId: String, startTimestamp: Long, endTimestamp: Long): Flow<List<CalendarEvents>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: CalendarEvents): Long

    @Query("DELETE FROM calendarEvents_table WHERE id = :eventId")
    suspend fun deleteEventById(eventId: String): Int
}

@Dao
interface RoutineDao {
    @Query("SELECT * FROM routine_table WHERE userId = :userId")
    fun getRoutinesForUser(userId: String): Flow<List<Routine>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoutine(routine: Routine): Long

    @Query("DELETE FROM routine_table WHERE id = :routineId")
    suspend fun deleteRoutineById(routineId: String): Int
}

@Dao
interface RoutineLogsDao {
    @Query("SELECT * FROM routineLogs_table WHERE completedAt BETWEEN :startTimestamp AND :endTimestamp")
    fun getLogsForDateRange(startTimestamp: Long, endTimestamp: Long): Flow<List<RoutineLogs>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: RoutineLogs): Long
}
package com.zahraag.pawsitivehabits.data.models

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.zahraag.pawsitivehabits.data.dao.CalendarEventsDao
import com.zahraag.pawsitivehabits.data.dao.PetDao
import com.zahraag.pawsitivehabits.data.dao.RoutineDao
import com.zahraag.pawsitivehabits.data.dao.RoutineLogsDao
import com.zahraag.pawsitivehabits.data.dao.WeightDao

@Database(
    entities = [Pet::class, CalendarEvents::class, Routine::class, RoutineLogs::class, Weight::class],
    version = 9,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun petDao(): PetDao
    abstract fun calendarDao(): CalendarEventsDao
    abstract fun routineDao(): RoutineDao
    abstract fun routineLogsDao(): RoutineLogsDao
    abstract fun weightDao(): WeightDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "pawsitive_habits_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}
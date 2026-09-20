package com.zahraag.pawsitivehabits.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.zahraag.pawsitivehabits.data.models.UserSettings
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM userSettings_table WHERE userId = :userId")
    fun getSettingsByUserId(userId: String): Flow<UserSettings?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSettings(userSettings: UserSettings)
}



package com.zahraag.pawsitivehabits.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "routine_table")
data class Routine(
    @PrimaryKey var id: String = java.util.UUID.randomUUID().toString(),
    var userId: String,
    var petId: String,
    var title: String,
    var time: Long?= null,
    var frequency: String,
    var startDate: Long,
    var endDate: Long? =null,
    var repeatDays: String? = null,
    var reminderMinutes: Int = 30,


    @Transient
    var isSynced: Boolean = false
)

package com.zahraag.pawsitivehabits.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
@Entity(tableName = "calendarEvents_table")
data class CalendarEvents(
    @PrimaryKey var id: String = UUID.randomUUID().toString(),
    var userId: String,
    var petId: String,
    var title: String,
    var category: String,
    var time: Long?= null,
    var date: Long?=null,
    var notes: String,
    var reminderMinutes: Int = 30,
    @Transient
    var isSynced: Boolean = false
)


package com.zahraag.pawsitivehabits.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
@Entity(tableName = "routine_table")
data class Routine(
    @PrimaryKey @SerializedName("_id")
    val id: String = org.bson.types.ObjectId().toHexString(),
    var userId: String,
    var petId: String,
    var title: String,
    var time: Long?= null,
    var frequency: String,
    var startDate: Long,
    var endDate: Long? =null,
    var repeatDays: String? = null,
    var reminderMinutes: Int = 30,


    @kotlinx.serialization.Transient
    var isSynced: Boolean = false
)

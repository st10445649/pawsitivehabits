package com.zahraag.pawsitivehabits.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
@Entity(tableName = "routineLogs_table")
data class RoutineLogs(
    @PrimaryKey @SerializedName("_id")
    val id: String = org.bson.types.ObjectId().toHexString(),
    var routineId: String,
    var petId: String,
    var completedAt: Long,
    @Transient
    var isSynced: Boolean = false
)
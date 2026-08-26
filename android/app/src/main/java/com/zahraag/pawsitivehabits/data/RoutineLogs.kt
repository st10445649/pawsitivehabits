package com.zahraag.pawsitivehabits.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "routineLogs_table")
data class RoutineLogs(
    @PrimaryKey var id: String = java.util.UUID.randomUUID().toString(),
    var routineId: String,
    var petId: String,
    var completedAt: Long,
    @Transient
    var isSynced: Boolean = false
)
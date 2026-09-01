package com.zahraag.pawsitivehabits.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
@Entity(tableName = "routineLogs_table")
data class RoutineLogs(
    @PrimaryKey var id: String = UUID.randomUUID().toString(),
    var routineId: String,
    var petId: String,
    var completedAt: Long,
    @Transient
    var isSynced: Boolean = false
)
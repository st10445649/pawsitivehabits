package com.zahraag.pawsitivehabits.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
@Entity(tableName = "weight_table")
data class Weight(
    @PrimaryKey var id: String = UUID.randomUUID().toString(),
    var userId: String,
    var petId: String,
    var weightValue: Double,
    var unit: String,
    var date: Long,

    @Transient
    var isSynced: Boolean = false
)
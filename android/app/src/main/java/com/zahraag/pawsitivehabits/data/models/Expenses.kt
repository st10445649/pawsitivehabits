package com.zahraag.pawsitivehabits.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
@Entity(tableName = "expenses_table")
data class Expenses (
    @PrimaryKey var id: String = UUID.randomUUID().toString(),
    var userId: String,
    var petId: String,
    var title: String,
    var amount: Double,
    var category: String,
    var date: Long,
    var notes: String? = "",

    @Transient
    var isSynced: Boolean = false
)

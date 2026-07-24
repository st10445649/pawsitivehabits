package com.zahraag.pawsitivehabits.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "expenses_table")
data class Expenses (
    @PrimaryKey var id: String = java.util.UUID.randomUUID().toString(),
    var userId: String,
    var petId: String,
    var title: String,
    var amount: Double,
    var category: String,
    var date: Long,

    @Transient
    var isSynced: Boolean = false
)

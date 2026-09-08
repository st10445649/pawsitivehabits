package com.zahraag.pawsitivehabits.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
@Entity(tableName = "medicalRecords_table")
data class MedicalRecords(
    @PrimaryKey var id: String = UUID.randomUUID().toString(),
    var userId: String,
    var petId: String,
    var category: String,
    var title: String,
    var date: Long,
    var vet: String,
    val reason: String = "",
    val nextDueDate: Long? = null,
    val clinic: String = "",
    val notes: String = "",

    @Transient
    var isSynced: Boolean = false
)

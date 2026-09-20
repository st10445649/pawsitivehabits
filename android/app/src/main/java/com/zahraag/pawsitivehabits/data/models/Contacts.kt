package com.zahraag.pawsitivehabits.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
@Entity(tableName = "contacts_table")
data class Contacts (
    @PrimaryKey var id: String = UUID.randomUUID().toString(),
    var userId:String,
    var petId: String,
    var clinicName: String,
    var doctorName: String,
    var phoneNumber: String,
    var address: String,
    var is24HourEmergency: Boolean = false,

    @Transient
    var isSynced: Boolean = false
)
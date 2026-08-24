package com.zahraag.pawsitivehabits.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

data class EmergencyContact(
    @PrimaryKey
    val id: String = java.util.UUID.randomUUID().toString(),
    val name: String,
    val role: String, // e.g., "24/7 Vet Clinic", "Poison Control", "Pet Sitter"
    val phoneNumber: String,
    val address: String = "",

    @Transient
var isSynced: Boolean = false
)



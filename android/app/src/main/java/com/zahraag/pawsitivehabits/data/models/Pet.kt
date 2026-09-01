package com.zahraag.pawsitivehabits.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
@Entity(tableName ="pet_table")
data class Pet(
    @PrimaryKey var id: String = UUID.randomUUID().toString(),
    var userId: String,
    var name: String,
    var petType: String,
    var breed: String? = null,
    var dateOfBirth: Long,
    var adoptionDate: Long,
    var microchipId: String? = null,
    var isNeutered: Boolean = false,
    var imageUrl: String? = null,
    @Transient
    var isSynced: Boolean = false
)


package com.zahraag.pawsitivehabits.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
@Entity(tableName ="pet_table")
data class Pet(
    @PrimaryKey @SerializedName("_id")
    val id: String = org.bson.types.ObjectId().toHexString(),
    val serverId: String? = null,
    var userId: String,
    var name: String,
    var gender: String,
    var petType: String,
    var breed: String? = null,
    var dateOfBirth: Long,
    var adoptionDate: Long,
    var microchipId: String? = null,
    var isNeutered: Boolean = false,
    var imageUrl: String? = null,
    var colour: String?=null,
    var notes: String? = null,

    @kotlinx.serialization.Transient
    var isSynced: Boolean = false
)


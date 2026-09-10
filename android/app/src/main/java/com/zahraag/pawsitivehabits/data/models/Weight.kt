package com.zahraag.pawsitivehabits.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
@Entity(tableName = "weight_table")
data class Weight(
    @PrimaryKey @SerializedName("_id")
    val id: String = org.bson.types.ObjectId().toHexString(),
    var userId: String,
    var petId: String,
    var weightValue: Double,
    var unit: String,
    var date: Long,

    @Transient
    var isSynced: Boolean = false
)
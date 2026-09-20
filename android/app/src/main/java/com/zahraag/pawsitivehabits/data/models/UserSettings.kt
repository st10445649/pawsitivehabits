package com.zahraag.pawsitivehabits.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.bson.types.ObjectId

@Serializable
@Entity("userSettings_table")
data class UserSettings(
    @PrimaryKey @SerializedName("_id")
    val id: String = org.bson.types.ObjectId().toHexString(),
    var userId: String,
    var language: String = "en",
    var weightUnit: String = "kg",
    var notificationsEnabled: Boolean = true,
    var biometricLockEnabled: Boolean = false,

    @kotlinx.serialization.Transient
    var isSynced: Boolean = false
)
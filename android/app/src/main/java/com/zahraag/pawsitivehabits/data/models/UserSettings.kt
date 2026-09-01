package com.zahraag.pawsitivehabits.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity("userSettings_table")
data class UserSettings(
    @PrimaryKey var id: String,
    var language: String = "en",
    var weightUnit: String = "kg",
    //var tempUnit: String = "C",
    var notificationsEnabled: Boolean = true,
    var biometricLockEnabled: Boolean = false,

    @Transient
    var isSynced: Boolean = false
)
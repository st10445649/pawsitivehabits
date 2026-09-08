package com.zahraag.pawsitivehabits.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable
import java.util.UUID

/*
Author: Android Developers
Date Accessed: 27 April 2026
Link: https://developer.android.com/training/data-storage/room/defining-data
Reason: Guidelines for creating entities to save to RoomDB
*/
@Serializable
@Entity(tableName = "user_table")
data class User(
    @PrimaryKey var id: String = UUID.randomUUID().toString(),
    var firebaseUid: String,
    var firstName: String,
    var lastName: String,
    var email: String,
    var password: String,
    var displayName: String = "",
    var photoURL: String = "",
    var authProvider: String = "email"
)
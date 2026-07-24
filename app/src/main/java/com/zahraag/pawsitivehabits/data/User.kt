package com.zahraag.pawsitivehabits.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

/*
Author: Android Developers
Date Accessed: 27 April 2026
Link: https://developer.android.com/training/data-storage/room/defining-data
Reason: Guidelines for creating entities to save to RoomDB
*/
@Serializable
@Entity(tableName = "user_table")
data class User(
    @PrimaryKey var id: String = java.util.UUID.randomUUID().toString(),
    var firstName: String,
    var lastName: String,
    var email: String,
    var password: String,
    var authUid : String? = null
)
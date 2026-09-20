package com.zahraag.pawsitivehabits.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
@Entity("memories_table")
data class Memories(
    @PrimaryKey var id: String = UUID.randomUUID().toString(),
    var userId: String,
    var petId: String,
    var title: String,
    var imageUri: String,
    var date: Long,

    @Transient
    var isSynced: Boolean = false
)

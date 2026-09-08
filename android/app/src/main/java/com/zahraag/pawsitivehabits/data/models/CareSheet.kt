package com.zahraag.pawsitivehabits.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
@Entity("careSheet_table")
data class CareSheet(
    @PrimaryKey var id: String = UUID.randomUUID().toString(),
    var userId: String,
    var petId: String,
    var generatedAt: Long,
    var fileUri:String,

    @Transient
    var isSynced: Boolean = false
)

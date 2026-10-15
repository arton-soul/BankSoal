package com.banksoalpro.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "passages")
data class PassageEntity(
    @PrimaryKey
    val id: String,
    val title: String? = null,
    val content: String
)

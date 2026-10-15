package com.banksoalpro.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bookmarks")
data class BookmarkEntity(
    @PrimaryKey val questionId: String,
    val packageId: String,
    val bookmarkedAt: Long = System.currentTimeMillis(),
    val notes: String? = null
)

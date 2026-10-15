package com.banksoalpro.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "exam_packages")
data class ExamPackageEntity(
    @PrimaryKey
    val id: String,
    val categoryId: String,
    val subCategory: String,
    val title: String,
    val description: String,
    val totalQuestions: Int,
    val durationMinutes: Int,
    val passingScore: Int = 0,
    val isFree: Boolean = false,
    val priceIdr: Long = 0,
    val isDownloaded: Boolean = false,
    val version: Int = 1
)

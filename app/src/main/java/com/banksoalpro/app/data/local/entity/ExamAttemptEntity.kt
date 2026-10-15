package com.banksoalpro.app.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "exam_attempts",
    indices = [
        Index(value = ["packageId"])
    ]
)
data class ExamAttemptEntity(
    @PrimaryKey
    val attemptId: String,
    val packageId: String,
    val startedAt: Long,
    val finishedAt: Long? = null,
    val timeSpentSeconds: Long = 0,
    val totalScore: Double = 0.0,
    val isPassed: Boolean = false,
    val userAnswersJson: String = "{}", // Map<QuestionId, String> serialized
    val isSubmitted: Boolean = false
)

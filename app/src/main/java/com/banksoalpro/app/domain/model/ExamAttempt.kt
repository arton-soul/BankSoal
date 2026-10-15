package com.banksoalpro.app.domain.model

data class ExamAttempt(
    val attemptId: String,
    val packageId: String,
    val startedAt: Long,
    val finishedAt: Long? = null,
    val timeSpentSeconds: Long = 0,
    val totalScore: Double = 0.0,
    val isPassed: Boolean = false,
    val userAnswers: Map<String, String> = emptyMap(),
    val isSubmitted: Boolean = false
)

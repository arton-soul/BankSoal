package com.banksoalpro.app.domain.model

enum class ScoringType {
    STANDARD,
    TKP_WEIGHTED,
    PENALTY
}

data class Question(
    val id: String,
    val packageId: String,
    val passageId: String? = null,
    val sectionName: String,
    val questionNumber: Int,
    val questionText: String,
    val imageUrl: String? = null,
    val optionA: String,
    val optionB: String,
    val optionC: String,
    val optionD: String,
    val optionE: String? = null,
    val scoringType: ScoringType = ScoringType.STANDARD,
    val correctAnswer: String,
    val scoreA: Int = 0,
    val scoreB: Int = 0,
    val scoreC: Int = 0,
    val scoreD: Int = 0,
    val scoreE: Int = 0,
    val explanation: String,
    val explanationImageUrl: String? = null
)

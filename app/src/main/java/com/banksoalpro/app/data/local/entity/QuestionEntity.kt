package com.banksoalpro.app.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "questions",
    indices = [
        Index(value = ["packageId"]),
        Index(value = ["passageId"])
    ]
)
data class QuestionEntity(
    @PrimaryKey
    val id: String,
    val packageId: String,
    val passageId: String? = null,
    val sectionName: String,
    val questionNumber: Int,
    val questionText: String,
    val imageUrl: String? = null,

    // Opsi Jawaban (A - E)
    val optionA: String,
    val optionB: String,
    val optionC: String,
    val optionD: String,
    val optionE: String? = null,

    // Aturan Penilaian: "STANDARD", "TKP_WEIGHTED", "PENALTY"
    val scoringType: String = "STANDARD",
    val correctAnswer: String, // "A", "B", "C", "D", "E"
    val scoreA: Int = 0,
    val scoreB: Int = 0,
    val scoreC: Int = 0,
    val scoreD: Int = 0,
    val scoreE: Int = 0,

    val explanation: String,
    val explanationImageUrl: String? = null
)

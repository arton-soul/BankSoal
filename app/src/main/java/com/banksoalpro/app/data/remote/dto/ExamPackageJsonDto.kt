package com.banksoalpro.app.data.remote.dto

data class CatalogJsonDto(
    val version: Int,
    val updatedAt: String,
    val packages: List<PackageMetaDto>
)

data class PackageMetaDto(
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
    val version: Int = 1,
    val downloadUrl: String
)

data class FullPackageJsonDto(
    val packageMeta: PackageMetaDto,
    val passages: List<PassageJsonDto> = emptyList(),
    val questions: List<QuestionJsonDto>
)

data class PassageJsonDto(
    val id: String,
    val title: String? = null,
    val content: String
)

data class QuestionJsonDto(
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
    val scoringType: String = "STANDARD", // "STANDARD", "TKP_WEIGHTED", "PENALTY"
    val correctAnswer: String,
    val scoreA: Int = 0,
    val scoreB: Int = 0,
    val scoreC: Int = 0,
    val scoreD: Int = 0,
    val scoreE: Int = 0,
    val explanation: String,
    val explanationImageUrl: String? = null
)

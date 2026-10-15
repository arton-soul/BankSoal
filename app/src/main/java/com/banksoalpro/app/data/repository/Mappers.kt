package com.banksoalpro.app.data.repository

import com.banksoalpro.app.data.local.entity.ExamAttemptEntity
import com.banksoalpro.app.data.local.entity.ExamPackageEntity
import com.banksoalpro.app.data.local.entity.PassageEntity
import com.banksoalpro.app.data.local.entity.QuestionEntity
import com.banksoalpro.app.domain.model.ExamAttempt
import com.banksoalpro.app.domain.model.ExamPackage
import com.banksoalpro.app.domain.model.Passage
import com.banksoalpro.app.domain.model.Question
import com.banksoalpro.app.domain.model.ScoringType

fun ExamPackageEntity.toDomain(): ExamPackage = ExamPackage(
    id = id,
    categoryId = categoryId,
    subCategory = subCategory,
    title = title,
    description = description,
    totalQuestions = totalQuestions,
    durationMinutes = durationMinutes,
    passingScore = passingScore,
    isFree = isFree,
    priceIdr = priceIdr,
    isDownloaded = isDownloaded,
    version = version
)

fun ExamPackage.toEntity(): ExamPackageEntity = ExamPackageEntity(
    id = id,
    categoryId = categoryId,
    subCategory = subCategory,
    title = title,
    description = description,
    totalQuestions = totalQuestions,
    durationMinutes = durationMinutes,
    passingScore = passingScore,
    isFree = isFree,
    priceIdr = priceIdr,
    isDownloaded = isDownloaded,
    version = version
)

fun QuestionEntity.toDomain(): Question = Question(
    id = id,
    packageId = packageId,
    passageId = passageId,
    sectionName = sectionName,
    questionNumber = questionNumber,
    questionText = questionText,
    imageUrl = imageUrl,
    optionA = optionA,
    optionB = optionB,
    optionC = optionC,
    optionD = optionD,
    optionE = optionE,
    scoringType = try {
        ScoringType.valueOf(scoringType)
    } catch (_: Exception) {
        ScoringType.STANDARD
    },
    correctAnswer = correctAnswer,
    scoreA = scoreA,
    scoreB = scoreB,
    scoreC = scoreC,
    scoreD = scoreD,
    scoreE = scoreE,
    explanation = explanation,
    explanationImageUrl = explanationImageUrl
)

fun Question.toEntity(): QuestionEntity = QuestionEntity(
    id = id,
    packageId = packageId,
    passageId = passageId,
    sectionName = sectionName,
    questionNumber = questionNumber,
    questionText = questionText,
    imageUrl = imageUrl,
    optionA = optionA,
    optionB = optionB,
    optionC = optionC,
    optionD = optionD,
    optionE = optionE,
    scoringType = scoringType.name,
    correctAnswer = correctAnswer,
    scoreA = scoreA,
    scoreB = scoreB,
    scoreC = scoreC,
    scoreD = scoreD,
    scoreE = scoreE,
    explanation = explanation,
    explanationImageUrl = explanationImageUrl
)

fun PassageEntity.toDomain(): Passage = Passage(
    id = id,
    title = title,
    content = content
)

fun Passage.toEntity(): PassageEntity = PassageEntity(
    id = id,
    title = title,
    content = content
)

fun ExamAttemptEntity.toDomain(): ExamAttempt = ExamAttempt(
    attemptId = attemptId,
    packageId = packageId,
    startedAt = startedAt,
    finishedAt = finishedAt,
    timeSpentSeconds = timeSpentSeconds,
    totalScore = totalScore,
    isPassed = isPassed,
    userAnswers = parseAnswersJson(userAnswersJson),
    isSubmitted = isSubmitted
)

fun ExamAttempt.toEntity(): ExamAttemptEntity = ExamAttemptEntity(
    attemptId = attemptId,
    packageId = packageId,
    startedAt = startedAt,
    finishedAt = finishedAt,
    timeSpentSeconds = timeSpentSeconds,
    totalScore = totalScore,
    isPassed = isPassed,
    userAnswersJson = serializeAnswersMap(userAnswers),
    isSubmitted = isSubmitted
)

private fun parseAnswersJson(raw: String): Map<String, String> {
    if (raw.isBlank() || raw == "{}") return emptyMap()
    // Simple key-value serializer without heavy runtime dependencies
    return raw.removeSurrounding("{", "}").split(";;;")
        .mapNotNull { pair ->
            val parts = pair.split(":::")
            if (parts.size == 2) parts[0] to parts[1] else null
        }.toMap()
}

private fun serializeAnswersMap(map: Map<String, String>): String {
    if (map.isEmpty()) return "{}"
    return "{" + map.entries.joinToString(";;;") { "${it.key}:::${it.value}" } + "}"
}

fun com.banksoalpro.app.data.local.entity.UserAccountEntity.toProfile(): com.banksoalpro.app.domain.model.UserProfile =
    com.banksoalpro.app.domain.model.UserProfile(
        userId = userId,
        displayName = displayName,
        email = email,
        photoUrl = photoUrl,
        isGuest = isGuest,
        targetExamCategory = targetExamCategory
    )

fun com.banksoalpro.app.data.local.entity.UserAccountEntity.toEntitlement(): com.banksoalpro.app.domain.model.UserEntitlement =
    com.banksoalpro.app.domain.model.UserEntitlement(
        userId = userId,
        isPremiumUser = isPremiumUser,
        unlockedPackageIds = parseJsonStringList(unlockedPackageIdsJson),
        premiumExpiryDate = premiumExpiryDate,
        lastSyncedAt = lastSyncedAt
    )

fun parseJsonStringList(json: String): List<String> {
    if (json.isBlank() || json == "[]") return emptyList()
    return try {
        val array = org.json.JSONArray(json)
        val list = mutableListOf<String>()
        for (i in 0 until array.length()) {
            list.add(array.getString(i))
        }
        list
    } catch (_: Exception) {
        emptyList()
    }
}

fun List<String>.toJsonString(): String {
    val array = org.json.JSONArray()
    forEach { array.put(it) }
    return array.toString()
}


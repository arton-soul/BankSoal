package com.banksoalpro.app.domain.usecase

import com.banksoalpro.app.domain.model.Question
import com.banksoalpro.app.domain.model.ScoringType
import com.banksoalpro.app.domain.model.ShuffledQuestion

data class SectionScoreResult(
    val sectionName: String,
    val score: Double,
    val totalQuestions: Int,
    val correctCount: Int,
    val wrongCount: Int,
    val unansweredCount: Int,
    val passingScore: Int? = null,
    val isPassed: Boolean = true
)

data class ExamCalculationResult(
    val totalScore: Double,
    val maxPossibleScore: Double,
    val isPassed: Boolean,
    val totalCorrect: Int,
    val totalWrong: Int,
    val totalUnanswered: Int,
    val accuracyPercent: Double,
    val sectionScores: Map<String, SectionScoreResult>
)

object ExamScoreCalculator {

    // Passing Grade Standar Resmi BKN SKD CPNS
    const val CPNS_TWK_PASSING_GRADE = 65
    const val CPNS_TIU_PASSING_GRADE = 80
    const val CPNS_TKP_PASSING_GRADE = 166

    fun calculate(
        questions: List<ShuffledQuestion>,
        userAnswers: Map<String, String>, // Map<QuestionId, chosenOriginalKey>
        packagePassingScore: Int = 0,
        isCpnsSkd: Boolean = false
    ): ExamCalculationResult {
        if (questions.isEmpty()) {
            return ExamCalculationResult(
                totalScore = 0.0,
                maxPossibleScore = 0.0,
                isPassed = false,
                totalCorrect = 0,
                totalWrong = 0,
                totalUnanswered = 0,
                accuracyPercent = 0.0,
                sectionScores = emptyMap()
            )
        }

        var totalScore = 0.0
        var maxPossibleScore = 0.0
        var totalCorrect = 0
        var totalWrong = 0
        var totalUnanswered = 0

        // Kelompokkan per seksi (misal: TWK, TIU, TKP atau TPS, Literasi)
        val sectionQuestions = questions.groupBy { it.question.sectionName.ifBlank { "Umum" } }
        val sectionResults = mutableMapOf<String, SectionScoreResult>()

        for ((section, qList) in sectionQuestions) {
            var sectionScore = 0.0
            var sCorrect = 0
            var sWrong = 0
            var sUnanswered = 0

            for (shuffled in qList) {
                val q = shuffled.question
                val chosenKey = userAnswers[q.id]

                when (q.scoringType) {
                    ScoringType.TKP_WEIGHTED -> {
                        maxPossibleScore += 5.0
                        if (chosenKey == null) {
                            sUnanswered++
                        } else {
                            val matchedOption = shuffled.options.find { it.originalKey == chosenKey }
                            val points = (matchedOption?.score ?: 0).toDouble()
                            sectionScore += points
                            if (points == 5.0) {
                                sCorrect++
                            } else {
                                sCorrect++ // Pada TKP, semua jawaban memiliki nilai, namun opsi bernilai 5 adalah bobot terbaik
                            }
                        }
                    }

                    ScoringType.PENALTY -> {
                        maxPossibleScore += 4.0
                        if (chosenKey == null) {
                            sUnanswered++
                        } else if (chosenKey.equals(q.correctAnswer, ignoreCase = true)) {
                            sectionScore += 4.0
                            sCorrect++
                        } else {
                            sectionScore -= 1.0
                            sWrong++
                        }
                    }

                    ScoringType.STANDARD -> {
                        val maxPoint = if (q.scoreA > 0 || q.scoreB > 0 || q.scoreC > 0) {
                            when (q.correctAnswer.uppercase()) {
                                "A" -> q.scoreA
                                "B" -> q.scoreB
                                "C" -> q.scoreC
                                "D" -> q.scoreD
                                "E" -> q.scoreE
                                else -> 5
                            }
                        } else 5
                        maxPossibleScore += maxPoint.toDouble()

                        if (chosenKey == null) {
                            sUnanswered++
                        } else if (chosenKey.equals(q.correctAnswer, ignoreCase = true)) {
                            sectionScore += maxPoint.toDouble()
                            sCorrect++
                        } else {
                            sWrong++
                        }
                    }
                }
            }

            totalScore += sectionScore
            totalCorrect += sCorrect
            totalWrong += sWrong
            totalUnanswered += sUnanswered

            val passingForSection = when {
                isCpnsSkd && section.contains("TWK", ignoreCase = true) -> CPNS_TWK_PASSING_GRADE
                isCpnsSkd && section.contains("TIU", ignoreCase = true) -> CPNS_TIU_PASSING_GRADE
                isCpnsSkd && section.contains("TKP", ignoreCase = true) -> CPNS_TKP_PASSING_GRADE
                else -> null
            }

            val sectionPassed = if (passingForSection != null) {
                sectionScore >= passingForSection
            } else {
                true
            }

            sectionResults[section] = SectionScoreResult(
                sectionName = section,
                score = sectionScore,
                totalQuestions = qList.size,
                correctCount = sCorrect,
                wrongCount = sWrong,
                unansweredCount = sUnanswered,
                passingScore = passingForSection,
                isPassed = sectionPassed
            )
        }

        val totalQuestionsCount = questions.size
        val accuracy = if (totalQuestionsCount > 0) {
            ((totalCorrect.toDouble() / totalQuestionsCount.toDouble()) * 100.0).coerceIn(0.0, 100.0)
        } else 0.0

        val isPassed = if (isCpnsSkd) {
            // Pada CPNS SKD resmi, harus lolos ambang batas ketiga pilar sekaligus (TWK >= 65, TIU >= 80, TKP >= 166)
            sectionResults.values.all { it.isPassed }
        } else {
            totalScore >= packagePassingScore
        }

        return ExamCalculationResult(
            totalScore = totalScore,
            maxPossibleScore = maxPossibleScore,
            isPassed = isPassed,
            totalCorrect = totalCorrect,
            totalWrong = totalWrong,
            totalUnanswered = totalUnanswered,
            accuracyPercent = accuracy,
            sectionScores = sectionResults
        )
    }
}

package com.banksoalpro.app

import com.banksoalpro.app.domain.model.Question
import com.banksoalpro.app.domain.model.ScoringType
import com.banksoalpro.app.domain.model.toShuffled
import com.banksoalpro.app.domain.usecase.ExamScoreCalculator
import org.junit.Assert.*
import org.junit.Test

class ScoringCalculatorTest {

    private fun createQuestion(
        id: String,
        sectionName: String,
        scoringType: ScoringType,
        correctAnswer: String,
        scoreA: Int = 0,
        scoreB: Int = 0,
        scoreC: Int = 0,
        scoreD: Int = 0,
        scoreE: Int = 0
    ): Question {
        return Question(
            id = id,
            packageId = "pkg_test",
            passageId = null,
            sectionName = sectionName,
            questionNumber = 1,
            questionText = "Sample question text for $id",
            imageUrl = null,
            optionA = "Opsi A",
            optionB = "Opsi B",
            optionC = "Opsi C",
            optionD = "Opsi D",
            optionE = "Opsi E",
            scoringType = scoringType,
            correctAnswer = correctAnswer,
            scoreA = scoreA,
            scoreB = scoreB,
            scoreC = scoreC,
            scoreD = scoreD,
            scoreE = scoreE,
            explanation = "Pembahasan soal"
        )
    }

    @Test
    fun testStandardScoringCalculation() {
        val q1 = createQuestion("q1", "Matematika", ScoringType.STANDARD, "A").toShuffled()
        val q2 = createQuestion("q2", "Matematika", ScoringType.STANDARD, "B").toShuffled()
        val q3 = createQuestion("q3", "Matematika", ScoringType.STANDARD, "C").toShuffled()

        val userAnswers = mapOf(
            "q1" to "A", // Benar -> 5
            "q2" to "C"  // Salah -> 0
            // q3 tidak dijawab -> 0
        )

        val result = ExamScoreCalculator.calculate(
            questions = listOf(q1, q2, q3),
            userAnswers = userAnswers,
            packagePassingScore = 10,
            isCpnsSkd = false
        )

        assertEquals(5.0, result.totalScore, 0.001)
        assertEquals(1, result.totalCorrect)
        assertEquals(1, result.totalWrong)
        assertEquals(1, result.totalUnanswered)
        assertFalse("Total score 5 is below passing score 10", result.isPassed)
    }

    @Test
    fun testUtbkPenaltyScoring() {
        val q1 = createQuestion("q1", "Penalaran Kognitif", ScoringType.PENALTY, "A").toShuffled()
        val q2 = createQuestion("q2", "Penalaran Kognitif", ScoringType.PENALTY, "B").toShuffled()
        val q3 = createQuestion("q3", "Penalaran Kognitif", ScoringType.PENALTY, "C").toShuffled()
        val q4 = createQuestion("q4", "Penalaran Kognitif", ScoringType.PENALTY, "D").toShuffled()

        val userAnswers = mapOf(
            "q1" to "A", // Benar: +4
            "q2" to "B", // Benar: +4
            "q3" to "A"  // Salah (kunci C): -1
            // q4 tidak dijawab: 0
        )

        val result = ExamScoreCalculator.calculate(
            questions = listOf(q1, q2, q3, q4),
            userAnswers = userAnswers,
            packagePassingScore = 5,
            isCpnsSkd = false
        )

        // Total = 4 + 4 - 1 + 0 = 7.0
        assertEquals(7.0, result.totalScore, 0.001)
        assertEquals(2, result.totalCorrect)
        assertEquals(1, result.totalWrong)
        assertEquals(1, result.totalUnanswered)
        assertTrue("Total score 7 is above passing score 5", result.isPassed)
    }

    @Test
    fun testCpnsTkpWeightedScoring() {
        val tkpQ1 = createQuestion(
            id = "tkp1",
            sectionName = "TKP",
            scoringType = ScoringType.TKP_WEIGHTED,
            correctAnswer = "E",
            scoreA = 1,
            scoreB = 2,
            scoreC = 3,
            scoreD = 4,
            scoreE = 5
        ).toShuffled()

        val tkpQ2 = createQuestion(
            id = "tkp2",
            sectionName = "TKP",
            scoringType = ScoringType.TKP_WEIGHTED,
            correctAnswer = "C",
            scoreA = 2,
            scoreB = 3,
            scoreC = 5,
            scoreD = 4,
            scoreE = 1
        ).toShuffled()

        val userAnswers = mapOf(
            "tkp1" to "D", // skor 4
            "tkp2" to "C"  // skor 5
        )

        val result = ExamScoreCalculator.calculate(
            questions = listOf(tkpQ1, tkpQ2),
            userAnswers = userAnswers,
            packagePassingScore = 8,
            isCpnsSkd = false
        )

        // Total = 4 + 5 = 9.0
        assertEquals(9.0, result.totalScore, 0.001)
        assertEquals(2, result.totalCorrect)
        assertEquals(0, result.totalWrong)
        assertEquals(0, result.totalUnanswered)
        assertTrue(result.isPassed)
    }

    @Test
    fun testCpnsSkdPassingGradeRules() {
        // Simulasi soal TWK (Ambang batas = 65)
        // 13 soal benar * 5 = 65
        val twkQuestions = (1..14).map { i ->
            createQuestion("twk_$i", "TWK", ScoringType.STANDARD, "A").toShuffled()
        }

        // Simulasi soal TIU (Ambang batas = 80)
        // 16 soal benar * 5 = 80
        val tiuQuestions = (1..17).map { i ->
            createQuestion("tiu_$i", "TIU", ScoringType.STANDARD, "B").toShuffled()
        }

        // Simulasi soal TKP (Ambang batas = 166)
        // 34 soal masing-masing bernilai 5 = 170
        val tkpQuestions = (1..35).map { i ->
            createQuestion(
                id = "tkp_$i",
                sectionName = "TKP",
                scoringType = ScoringType.TKP_WEIGHTED,
                correctAnswer = "E",
                scoreA = 1, scoreB = 2, scoreC = 3, scoreD = 4, scoreE = 5
            ).toShuffled()
        }

        val allQuestions = twkQuestions + tiuQuestions + tkpQuestions

        // Skenario 1: Lolos semua pilar (TWK=70, TIU=85, TKP=175)
        val passedAnswers = mutableMapOf<String, String>()
        // 14 TWK benar = 70
        twkQuestions.forEach { passedAnswers[it.question.id] = "A" }
        // 17 TIU benar = 85
        tiuQuestions.forEach { passedAnswers[it.question.id] = "B" }
        // 35 TKP bernilai 5 = 175
        tkpQuestions.forEach { passedAnswers[it.question.id] = "E" }

        val passedResult = ExamScoreCalculator.calculate(
            questions = allQuestions,
            userAnswers = passedAnswers,
            isCpnsSkd = true
        )
        assertTrue("User harus lolos karena TWK, TIU, dan TKP memenuhi ambang batas", passedResult.isPassed)
        assertEquals(330.0, passedResult.totalScore, 0.001)

        // Skenario 2: Gagal karena TWK di bawah 65 meskipun total skor tinggi
        val failedAnswers = mutableMapOf<String, String>()
        // Hanya 10 TWK benar = 50 (Gagal TWK karena < 65)
        twkQuestions.take(10).forEach { failedAnswers[it.question.id] = "A" }
        // Semua TIU benar = 85
        tiuQuestions.forEach { failedAnswers[it.question.id] = "B" }
        // Semua TKP max = 175
        tkpQuestions.forEach { failedAnswers[it.question.id] = "E" }

        val failedResult = ExamScoreCalculator.calculate(
            questions = allQuestions,
            userAnswers = failedAnswers,
            isCpnsSkd = true
        )
        assertFalse("User harus gagal karena nilai TWK (50) di bawah ambang batas 65", failedResult.isPassed)
    }
}

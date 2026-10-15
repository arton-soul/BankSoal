package com.banksoalpro.app

import com.banksoalpro.app.domain.model.Question
import com.banksoalpro.app.domain.model.ScoringType
import com.banksoalpro.app.domain.model.toShuffled
import org.junit.Assert.*
import org.junit.Test

class ShufflingTest {

    private val sampleQuestion = Question(
        id = "test_q1",
        packageId = "pkg_01",
        sectionName = "TWK",
        questionNumber = 1,
        questionText = "Ibu kota Indonesia masa depan adalah?",
        optionA = "Jakarta",
        optionB = "Bandung",
        optionC = "Surabaya",
        optionD = "Nusantara (IKN)",
        optionE = "Medan",
        scoringType = ScoringType.STANDARD,
        correctAnswer = "D", // Nusantara (IKN)
        scoreA = 0, scoreB = 0, scoreC = 0, scoreD = 5, scoreE = 0,
        explanation = "IKN Nusantara"
    )

    @Test
    fun testShuffledOptions_preservesAllOptionsAndAssignsProperDisplayLabels() {
        val shuffled = sampleQuestion.toShuffled()

        assertEquals(5, shuffled.options.size)
        // Pastikan displayKey selalu berurutan A, B, C, D, E untuk kenyamanan tampilan
        assertEquals(listOf("A", "B", "C", "D", "E"), shuffled.options.map { it.displayKey })

        // Pastikan semua 5 teks opsi asli tetap ada
        val originalTexts = setOf("Jakarta", "Bandung", "Surabaya", "Nusantara (IKN)", "Medan")
        val shuffledTexts = shuffled.options.map { it.text }.toSet()
        assertEquals(originalTexts, shuffledTexts)

        // Pastikan hanya tepat satu opsi yang ditandai benar, dan opsi tersebut teksnya adalah Nusantara (IKN)
        val correctOptions = shuffled.options.filter { it.isCorrect }
        assertEquals(1, correctOptions.size)
        assertEquals("Nusantara (IKN)", correctOptions.first().text)
        assertEquals("D", correctOptions.first().originalKey)
    }

    @Test
    fun testTkpWeightedScores_preservedAcrossShuffling() {
        val tkpQuestion = Question(
            id = "tkp_01",
            packageId = "pkg_cpns",
            sectionName = "TKP",
            questionNumber = 2,
            questionText = "Respon saat terjadi komplain masyarakat?",
            optionA = "Diam saja",
            optionB = "Mendengarkan aktif dan mencari solusi segera",
            optionC = "Menyalahkan pihak lain",
            optionD = "Meminta atasan mengatasi",
            optionE = "Menunda pekerjaan",
            scoringType = ScoringType.TKP_WEIGHTED,
            correctAnswer = "B",
            scoreA = 1, scoreB = 5, scoreC = 1, scoreD = 3, scoreE = 2,
            explanation = "Pelayanan publik"
        )

        val shuffled = tkpQuestion.toShuffled()
        assertEquals(5, shuffled.options.size)

        // Opsi dengan nilai tertinggi (5) harus selalu sesuai dengan optionB ("Mendengarkan aktif...")
        val bestOption = shuffled.options.first { it.score == 5 }
        assertEquals("B", bestOption.originalKey)
        assertEquals("Mendengarkan aktif dan mencari solusi segera", bestOption.text)
    }

    @Test
    fun testRandomizationAcrossMultipleSessions_ensuresDynamicPositions() {
        // Panggil toShuffled berulang kali untuk memastikan posisinya tidak statis di satu tempat
        val observedPositions = mutableSetOf<String>()

        for (i in 1..50) {
            val shuffled = sampleQuestion.toShuffled()
            val correctDisplayKey = shuffled.options.first { it.isCorrect }.displayKey
            observedPositions.add(correctDisplayKey)
        }

        // Dari 50 iterasi, jawaban benar (IKN) harus muncul di lebih dari 1 posisi acak yang berbeda
        assertTrue(
            "Jawaban benar harus teracak dan muncul di berbagai posisi berbeda",
            observedPositions.size > 1
        )
    }
}

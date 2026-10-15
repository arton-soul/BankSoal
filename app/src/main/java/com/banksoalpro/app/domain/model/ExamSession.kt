package com.banksoalpro.app.domain.model

data class DisplayOption(
    val displayKey: String,   // Label yang tampil di layar ("A", "B", "C", "D", "E")
    val originalKey: String,  // Kunci asli ("A", "B", "C", "D", "E")
    val text: String,         // Teks pilihan jawaban
    val score: Int,           // Poin (1-5 untuk TKP, 0/1 untuk standar)
    val isCorrect: Boolean    // Status apakah opsi ini kunci benar
)

data class ShuffledQuestion(
    val question: Question,
    val options: List<DisplayOption>,
    val passage: Passage? = null
)

fun Question.toShuffled(passage: Passage? = null): ShuffledQuestion {
    // Kumpulkan opsi yang tersedia
    val rawOptions = mutableListOf<DisplayOption>()
    rawOptions.add(DisplayOption(displayKey = "", originalKey = "A", text = optionA, score = scoreA, isCorrect = correctAnswer.equals("A", ignoreCase = true)))
    rawOptions.add(DisplayOption(displayKey = "", originalKey = "B", text = optionB, score = scoreB, isCorrect = correctAnswer.equals("B", ignoreCase = true)))
    rawOptions.add(DisplayOption(displayKey = "", originalKey = "C", text = optionC, score = scoreC, isCorrect = correctAnswer.equals("C", ignoreCase = true)))
    rawOptions.add(DisplayOption(displayKey = "", originalKey = "D", text = optionD, score = scoreD, isCorrect = correctAnswer.equals("D", ignoreCase = true)))
    if (!optionE.isNullOrBlank()) {
        rawOptions.add(DisplayOption(displayKey = "", originalKey = "E", text = optionE, score = scoreE, isCorrect = correctAnswer.equals("E", ignoreCase = true)))
    }

    // Acak urutan opsi jawaban secara dinamis
    val shuffledList = rawOptions.shuffled()

    // Pasangkan label tampilan (A, B, C, D, E) sesuai posisi acak baru
    val labelList = listOf("A", "B", "C", "D", "E")
    val displayOptions = shuffledList.mapIndexed { index, option ->
        val label = labelList.getOrElse(index) { ('A' + index).toString() }
        option.copy(displayKey = label)
    }

    return ShuffledQuestion(
        question = this,
        options = displayOptions,
        passage = passage
    )
}

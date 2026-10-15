package com.banksoalpro.app.feature.exam

import com.banksoalpro.app.domain.model.ShuffledQuestion

data class ExamUiState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val packageId: String = "",
    val packageTitle: String = "",
    val subCategory: String = "",
    val questions: List<ShuffledQuestion> = emptyList(),
    val currentIndex: Int = 0,
    val selectedAnswers: Map<String, String> = emptyMap(), // questionId -> originalKey ("A".."E")
    val selectedDisplayLabels: Map<String, String> = emptyMap(), // questionId -> displayKey ("A".."E")
    val flaggedQuestionIds: Set<String> = emptySet(), // Set of questionId marked "Ragu-ragu"
    val remainingSeconds: Long = 0,
    val totalDurationMinutes: Int = 0,
    val isTimeUp: Boolean = false,
    val isSubmitDialogOpen: Boolean = false,
    val isGridSheetOpen: Boolean = false,
    val isPassageExpanded: Boolean = false,
    val completedAttemptId: String? = null
) {
    val currentQuestion: ShuffledQuestion?
        get() = questions.getOrNull(currentIndex)

    val totalQuestions: Int
        get() = questions.size

    val answeredCount: Int
        get() = selectedAnswers.size

    val flaggedCount: Int
        get() = flaggedQuestionIds.size

    val unansweredCount: Int
        get() = (totalQuestions - answeredCount).coerceAtLeast(0)

    val formattedRemainingTime: String
        get() {
            val hours = remainingSeconds / 3600
            val minutes = (remainingSeconds % 3600) / 60
            val seconds = remainingSeconds % 60
            return if (hours > 0) {
                String.format("%02d:%02d:%02d", hours, minutes, seconds)
            } else {
                String.format("%02d:%02d", minutes, seconds)
            }
        }

    fun isAnswered(questionId: String): Boolean = selectedAnswers.containsKey(questionId)

    fun isFlagged(questionId: String): Boolean = flaggedQuestionIds.contains(questionId)
}

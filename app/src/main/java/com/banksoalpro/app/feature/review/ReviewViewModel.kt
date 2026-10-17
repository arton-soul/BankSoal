package com.banksoalpro.app.feature.review

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.banksoalpro.app.domain.model.ExamAttempt
import com.banksoalpro.app.domain.model.Passage
import com.banksoalpro.app.domain.model.Question
import com.banksoalpro.app.domain.model.ScoringType
import com.banksoalpro.app.domain.repository.ExamRepository
import com.banksoalpro.app.domain.repository.PackageRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

enum class ReviewFilter {
    ALL, CORRECT, WRONG, UNANSWERED
}

data class ReviewQuestionItem(
    val question: Question,
    val passage: Passage?,
    val userAnswerKey: String?,
    val isCorrect: Boolean,
    val isUnanswered: Boolean
)

data class ReviewUiState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val attempt: ExamAttempt? = null,
    val packageTitle: String = "",
    val passingScore: Int = 0,
    val allQuestions: List<ReviewQuestionItem> = emptyList(),
    val activeFilter: ReviewFilter = ReviewFilter.ALL
) {
    val filteredQuestions: List<ReviewQuestionItem>
        get() = when (activeFilter) {
            ReviewFilter.ALL -> allQuestions
            ReviewFilter.CORRECT -> allQuestions.filter { it.isCorrect }
            ReviewFilter.WRONG -> allQuestions.filter { !it.isCorrect && !it.isUnanswered }
            ReviewFilter.UNANSWERED -> allQuestions.filter { it.isUnanswered }
        }

    val correctCount: Int
        get() = allQuestions.count { it.isCorrect }

    val wrongCount: Int
        get() = allQuestions.count { !it.isCorrect && !it.isUnanswered }

    val unansweredCount: Int
        get() = allQuestions.count { it.isUnanswered }

    val totalCount: Int
        get() = allQuestions.size
}

class ReviewViewModel(
    private val examRepository: ExamRepository,
    private val packageRepository: PackageRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReviewUiState())
    val uiState: StateFlow<ReviewUiState> = _uiState.asStateFlow()

    fun loadReview(attemptId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                withContext(Dispatchers.IO) {
                    val attempt = examRepository.getAttemptById(attemptId)
                    if (attempt == null) {
                        withContext(Dispatchers.Main) {
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    errorMessage = "Riwayat ujian tidak ditemukan."
                                )
                            }
                        }
                        return@withContext
                    }

                    val pkg = packageRepository.getPackageById(attempt.packageId)
                    val rawQuestions = examRepository.getQuestionsListByPackageId(attempt.packageId)

                    val passageIds = rawQuestions.mapNotNull { it.passageId }.distinct()
                    val passageMap = if (passageIds.isNotEmpty()) {
                        examRepository.getPassagesByIds(passageIds)
                    } else emptyMap()

                    val reviewItems = rawQuestions.map { q ->
                        val userAns = attempt.userAnswers[q.id]
                        val isUnanswered = userAns.isNullOrEmpty()
                        val isCorrect = if (q.scoringType == ScoringType.TKP_WEIGHTED) {
                            val maxPoints = maxOf(q.scoreA, q.scoreB, q.scoreC, q.scoreD, q.scoreE).coerceAtLeast(1)
                            val userPoints = when (userAns?.uppercase()) {
                                "A" -> q.scoreA
                                "B" -> q.scoreB
                                "C" -> q.scoreC
                                "D" -> q.scoreD
                                "E" -> q.scoreE
                                else -> 0
                            }
                            userPoints == maxPoints
                        } else {
                            userAns?.equals(q.correctAnswer, ignoreCase = true) == true
                        }

                        ReviewQuestionItem(
                            question = q,
                            passage = q.passageId?.let { passageMap[it] },
                            userAnswerKey = userAns,
                            isCorrect = isCorrect,
                            isUnanswered = isUnanswered
                        )
                    }

                    withContext(Dispatchers.Main) {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                attempt = attempt,
                                packageTitle = pkg?.title ?: "Simulasi Ujian",
                                passingScore = pkg?.passingScore ?: 0,
                                allQuestions = reviewItems
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Gagal memuat pembahasan: ${e.localizedMessage ?: "Terjadi kesalahan"}"
                    )
                }
            }
        }
    }

    fun setFilter(filter: ReviewFilter) {
        _uiState.update { it.copy(activeFilter = filter) }
    }
}

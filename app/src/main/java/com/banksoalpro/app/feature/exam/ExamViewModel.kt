package com.banksoalpro.app.feature.exam

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.banksoalpro.app.domain.model.*
import com.banksoalpro.app.domain.repository.ExamRepository
import com.banksoalpro.app.domain.repository.PackageRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

class ExamViewModel(
    private val examRepository: ExamRepository,
    private val packageRepository: PackageRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExamUiState())
    val uiState: StateFlow<ExamUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null
    private var examStartTimeMillis: Long = 0L

    fun initExam(packageId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val examPackage = packageRepository.getPackageById(packageId)
                val questionsList = examRepository.getQuestionsListByPackageId(packageId)

                if (examPackage == null || questionsList.isEmpty()) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "Paket soal tidak ditemukan atau belum memiliki butir soal."
                        )
                    }
                    return@launch
                }

                // Acak opsi jawaban per soal untuk sesi ujian baru ini
                val shuffledQuestions = questionsList.map { question ->
                    val passage = question.passageId?.let { examRepository.getPassageById(it) }
                    question.toShuffled(passage)
                }

                val totalSeconds = (examPackage.durationMinutes * 60).toLong()
                examStartTimeMillis = System.currentTimeMillis()

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        packageId = examPackage.id,
                        packageTitle = examPackage.title,
                        subCategory = examPackage.subCategory,
                        questions = shuffledQuestions,
                        currentIndex = 0,
                        totalDurationMinutes = examPackage.durationMinutes,
                        remainingSeconds = totalSeconds,
                        selectedAnswers = emptyMap(),
                        selectedDisplayLabels = emptyMap(),
                        flaggedQuestionIds = emptySet(),
                        isTimeUp = false,
                        isSubmitDialogOpen = false,
                        isGridSheetOpen = false,
                        isPassageExpanded = false,
                        completedAttemptId = null
                    )
                }

                startTimer(totalSeconds)
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Gagal memuat ujian: ${e.localizedMessage ?: "Terjadi kesalahan"}"
                    )
                }
            }
        }
    }

    private fun startTimer(initialSeconds: Long) {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            var seconds = initialSeconds
            while (seconds > 0) {
                delay(1000L)
                seconds--
                _uiState.update { it.copy(remainingSeconds = seconds) }
            }
            // Waktu habis -> Auto submit
            _uiState.update { it.copy(isTimeUp = true, remainingSeconds = 0) }
            submitExam(isAutoSubmit = true)
        }
    }

    fun selectOption(questionId: String, displayOption: DisplayOption) {
        _uiState.update { state ->
            val updatedAnswers = state.selectedAnswers.toMutableMap()
            val updatedLabels = state.selectedDisplayLabels.toMutableMap()

            updatedAnswers[questionId] = displayOption.originalKey
            updatedLabels[questionId] = displayOption.displayKey

            state.copy(
                selectedAnswers = updatedAnswers,
                selectedDisplayLabels = updatedLabels
            )
        }
    }

    fun toggleFlagCurrentQuestion() {
        val current = _uiState.value.currentQuestion ?: return
        val qId = current.question.id
        _uiState.update { state ->
            val updatedFlags = state.flaggedQuestionIds.toMutableSet()
            if (updatedFlags.contains(qId)) {
                updatedFlags.remove(qId)
            } else {
                updatedFlags.add(qId)
            }
            state.copy(flaggedQuestionIds = updatedFlags)
        }
    }

    fun jumpToQuestion(index: Int) {
        if (index in 0 until _uiState.value.totalQuestions) {
            _uiState.update { it.copy(currentIndex = index, isGridSheetOpen = false) }
        }
    }

    fun nextQuestion() {
        val next = _uiState.value.currentIndex + 1
        if (next < _uiState.value.totalQuestions) {
            _uiState.update { it.copy(currentIndex = next) }
        }
    }

    fun previousQuestion() {
        val prev = _uiState.value.currentIndex - 1
        if (prev >= 0) {
            _uiState.update { it.copy(currentIndex = prev) }
        }
    }

    fun setSubmitDialogVisible(visible: Boolean) {
        _uiState.update { it.copy(isSubmitDialogOpen = visible) }
    }

    fun setGridSheetVisible(visible: Boolean) {
        _uiState.update { it.copy(isGridSheetOpen = visible) }
    }

    fun togglePassageExpanded() {
        _uiState.update { it.copy(isPassageExpanded = !it.isPassageExpanded) }
    }

    fun submitExam(isAutoSubmit: Boolean = false) {
        timerJob?.cancel()
        val state = _uiState.value
        if (state.questions.isEmpty()) return

        viewModelScope.launch {
            val examPackage = packageRepository.getPackageById(state.packageId)
            var totalScore = 0.0

            // Hitung skor berdasarkan aturan penilaian tiap soal
            state.questions.forEach { shuffled ->
                val q = shuffled.question
                val chosenOriginalKey = state.selectedAnswers[q.id]

                if (chosenOriginalKey != null) {
                    when (q.scoringType) {
                        ScoringType.TKP_WEIGHTED -> {
                            val matchedOption = shuffled.options.find { it.originalKey == chosenOriginalKey }
                            totalScore += (matchedOption?.score ?: 0)
                        }
                        ScoringType.PENALTY -> {
                            if (chosenOriginalKey.equals(q.correctAnswer, ignoreCase = true)) {
                                totalScore += 4.0
                            } else {
                                totalScore -= 1.0
                            }
                        }
                        ScoringType.STANDARD -> {
                            if (chosenOriginalKey.equals(q.correctAnswer, ignoreCase = true)) {
                                val point = if (q.scoreA > 0 || q.scoreB > 0 || q.scoreC > 0) {
                                    when (chosenOriginalKey.uppercase()) {
                                        "A" -> q.scoreA
                                        "B" -> q.scoreB
                                        "C" -> q.scoreC
                                        "D" -> q.scoreD
                                        "E" -> q.scoreE
                                        else -> 5
                                    }
                                } else 5
                                totalScore += point
                            }
                        }
                    }
                }
            }

            val passingScore = examPackage?.passingScore ?: 0
            val isPassed = totalScore >= passingScore
            val now = System.currentTimeMillis()
            val totalSecondsSpent = ((now - examStartTimeMillis) / 1000).coerceAtLeast(1)
            val attemptId = UUID.randomUUID().toString()

            val attempt = ExamAttempt(
                attemptId = attemptId,
                packageId = state.packageId,
                startedAt = examStartTimeMillis,
                finishedAt = now,
                timeSpentSeconds = totalSecondsSpent,
                totalScore = totalScore,
                isPassed = isPassed,
                userAnswers = state.selectedAnswers,
                isSubmitted = true
            )

            examRepository.saveAttempt(attempt)

            _uiState.update {
                it.copy(
                    isSubmitDialogOpen = false,
                    isGridSheetOpen = false,
                    completedAttemptId = attemptId
                )
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}

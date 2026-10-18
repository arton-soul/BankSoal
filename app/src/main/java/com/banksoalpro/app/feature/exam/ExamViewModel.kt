package com.banksoalpro.app.feature.exam

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.banksoalpro.app.domain.model.*
import com.banksoalpro.app.domain.repository.ExamRepository
import com.banksoalpro.app.domain.repository.PackageRepository
import com.banksoalpro.app.data.remote.sync.ContentSyncManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

class ExamViewModel(
    private val examRepository: ExamRepository,
    private val packageRepository: PackageRepository,
    private val syncManager: ContentSyncManager? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExamUiState())
    val uiState: StateFlow<ExamUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null
    private var examStartTimeMillis: Long = 0L

    fun initExam(packageId: String) {
        // Jangan reload jika paket ujian yang sama sudah berhasil dimuat
        if (_uiState.value.packageId == packageId && _uiState.value.questions.isNotEmpty() && !_uiState.value.isLoading) {
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                withContext(Dispatchers.IO) {
                    var examPackage = packageRepository.getPackageById(packageId)
                    var questionsList = examRepository.getQuestionsListByPackageId(packageId)

                    // Jika belum ada di database atau jumlah butir soal belum lengkap:
                    val isMissingOrIncomplete = examPackage == null ||
                            questionsList.isEmpty() ||
                            (examPackage.totalQuestions > 0 && questionsList.size < examPackage.totalQuestions)

                    if (isMissingOrIncomplete) {
                        // 1. Coba pulihkan dari assets lokal
                        syncManager?.seedPackageFromAssetIfMissing(packageId)
                        examPackage = packageRepository.getPackageById(packageId)
                        questionsList = examRepository.getQuestionsListByPackageId(packageId)

                        // 2. Jika masih belum lengkap (misal aset di APK lama), unduh langsung dari GitHub
                        if (examPackage != null && questionsList.size < examPackage.totalQuestions) {
                            try {
                                syncManager?.downloadPackageFromGitHub("packages/$packageId.json")
                                examPackage = packageRepository.getPackageById(packageId)
                                questionsList = examRepository.getQuestionsListByPackageId(packageId)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }

                    if (examPackage == null || questionsList.isEmpty()) {
                        withContext(Dispatchers.Main) {
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    errorMessage = "Paket soal tidak ditemukan atau belum memiliki butir soal."
                                )
                            }
                        }
                        return@withContext
                    }

                    // Ambil seluruh passage unik dalam 1 query batch (hindari N+1 query database)
                    val uniquePassageIds = questionsList.mapNotNull { it.passageId }.distinct()
                    val passageMap = if (uniquePassageIds.isNotEmpty()) {
                        examRepository.getPassagesByIds(uniquePassageIds)
                    } else {
                        emptyMap()
                    }

                    // Acak opsi jawaban per soal di background thread (Dispatchers.Default)
                    val shuffledQuestions = withContext(Dispatchers.Default) {
                        questionsList.map { question ->
                            val passage = question.passageId?.let { passageMap[it] }
                            question.toShuffled(passage)
                        }
                    }

                    val totalSeconds = (examPackage.durationMinutes * 60).toLong()
                    examStartTimeMillis = System.currentTimeMillis()

                    withContext(Dispatchers.Main) {
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
                    }
                }
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
            val (attemptId, _) = withContext(Dispatchers.Default) {
                val examPackage = withContext(Dispatchers.IO) {
                    packageRepository.getPackageById(state.packageId)
                }
                val isCpns = examPackage?.categoryId?.contains("CPNS", ignoreCase = true) == true
                val calculationResult = com.banksoalpro.app.domain.usecase.ExamScoreCalculator.calculate(
                    questions = state.questions,
                    userAnswers = state.selectedAnswers,
                    packagePassingScore = examPackage?.passingScore ?: 0,
                    isCpnsSkd = isCpns
                )
                val totalScore = calculationResult.totalScore
                val isPassed = calculationResult.isPassed
                val now = System.currentTimeMillis()
                val totalSecondsSpent = ((now - examStartTimeMillis) / 1000).coerceAtLeast(1)
                val newAttemptId = UUID.randomUUID().toString()

                val attempt = ExamAttempt(
                    attemptId = newAttemptId,
                    packageId = state.packageId,
                    startedAt = examStartTimeMillis,
                    finishedAt = now,
                    timeSpentSeconds = totalSecondsSpent,
                    totalScore = totalScore,
                    isPassed = isPassed,
                    userAnswers = state.selectedAnswers,
                    isSubmitted = true
                )

                withContext(Dispatchers.IO) {
                    examRepository.saveAttempt(attempt)
                }

                Pair(newAttemptId, isPassed)
            }

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

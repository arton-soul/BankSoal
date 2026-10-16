package com.banksoalpro.app.feature.exam

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.banksoalpro.app.core.ui.theme.*
import com.banksoalpro.app.feature.exam.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExamScreen(
    packageId: String,
    viewModel: ExamViewModel,
    onFinishExam: (String) -> Unit,
    onBackClick: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    // Load soal saat pertama kali dibuka
    LaunchedEffect(viewModel, packageId) {
        viewModel.initExam(packageId)
    }

    // Navigasi ke ResultScreen jika ujian selesai dikumpulkan
    LaunchedEffect(state.completedAttemptId) {
        val attemptId = state.completedAttemptId
        if (attemptId != null) {
            onFinishExam(attemptId)
        }
    }

    Scaffold(
        topBar = {
            if (!state.isLoading && state.errorMessage == null) {
                ExamTimerHeader(
                    remainingTimeText = state.formattedRemainingTime,
                    remainingSeconds = state.remainingSeconds,
                    currentIndex = state.currentIndex,
                    totalQuestions = state.totalQuestions,
                    onGridClick = { viewModel.setGridSheetVisible(true) },
                    onSubmitClick = { viewModel.setSubmitDialogVisible(true) }
                )
            }
        },
        bottomBar = {
            if (!state.isLoading && state.errorMessage == null && state.currentQuestion != null) {
                ExamBottomActionBar(
                    currentIndex = state.currentIndex,
                    totalQuestions = state.totalQuestions,
                    onPrevClick = { viewModel.previousQuestion() },
                    onNextClick = { viewModel.nextQuestion() },
                    onGridClick = { viewModel.setGridSheetVisible(true) },
                    onSubmitClick = { viewModel.setSubmitDialogVisible(true) }
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                state.isLoading -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Memuat simulasi ujian CAT...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                state.errorMessage != null -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Gagal Membuka Ujian",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Crimson600
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = state.errorMessage ?: "",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = onBackClick) {
                            Text("Kembali ke Katalog")
                        }
                    }
                }
                state.currentQuestion != null -> {
                    val currentShuffled = state.currentQuestion!!
                    val qId = currentShuffled.question.id
                    val selectedOriginalKey = state.selectedAnswers[qId]
                    val isFlagged = state.isFlagged(qId)

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // Teks Wacana Panjang jika soal memiliki wacana
                        if (currentShuffled.passage != null) {
                            PassageViewer(
                                passage = currentShuffled.passage,
                                isExpanded = state.isPassageExpanded,
                                onToggleExpand = { viewModel.togglePassageExpanded() }
                            )
                        }

                        // Kartu Soal & Pilihan Jawaban Acak
                        QuestionCard(
                            shuffledQuestion = currentShuffled,
                            questionNumber = state.currentIndex + 1,
                            selectedOriginalKey = selectedOriginalKey,
                            isFlagged = isFlagged,
                            onOptionSelected = { option ->
                                viewModel.selectOption(qId, option)
                            },
                            onToggleFlag = {
                                viewModel.toggleFlagCurrentQuestion()
                            }
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }

            // Sheet Lembar Grid Nomor Soal
            if (state.isGridSheetOpen) {
                ExamGridModalSheet(
                    state = state,
                    onQuestionClick = { index ->
                        viewModel.jumpToQuestion(index)
                    },
                    onDismiss = {
                        viewModel.setGridSheetVisible(false)
                    }
                )
            }

            // Dialog Konfirmasi Selesai Ujian
            if (state.isSubmitDialogOpen || state.isTimeUp) {
                ExamSubmitConfirmationDialog(
                    state = state,
                    onConfirm = {
                        viewModel.submitExam()
                    },
                    onDismiss = {
                        viewModel.setSubmitDialogVisible(false)
                    }
                )
            }
        }
    }
}

@Composable
private fun ExamBottomActionBar(
    currentIndex: Int,
    totalQuestions: Int,
    onPrevClick: () -> Unit,
    onNextClick: () -> Unit,
    onGridClick: () -> Unit,
    onSubmitClick: () -> Unit
) {
    val isFirst = currentIndex == 0
    val isLast = currentIndex == totalQuestions - 1

    Surface(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 6.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedButton(
                onClick = onPrevClick,
                enabled = !isFirst,
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Sebelumnya",
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Sebelumnya")
            }

            IconButton(onClick = onGridClick) {
                Icon(
                    imageVector = Icons.Default.GridOn,
                    contentDescription = "Kisi Nomor",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            if (isLast) {
                Button(
                    onClick = onSubmitClick,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Emerald600),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text("Kumpulkan", color = PureWhite)
                }
            } else {
                Button(
                    onClick = onNextClick,
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Text("Selanjutnya")
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Selanjutnya",
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

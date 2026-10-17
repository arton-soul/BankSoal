package com.banksoalpro.app.feature.review

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.banksoalpro.app.core.ui.theme.*
import com.banksoalpro.app.domain.model.ScoringType
import com.banksoalpro.app.feature.exam.components.PassageViewer
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewScreen(
    attemptId: String,
    viewModel: ReviewViewModel,
    onBackClick: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(attemptId) {
        viewModel.loadReview(attemptId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Kunci & Pembahasan Ujian",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        if (state.packageTitle.isNotEmpty()) {
                            Text(
                                text = state.packageTitle,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
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
                            text = "Memuat pembahasan dan kunci jawaban...",
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
                            text = "Gagal Membuka Pembahasan",
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
                            Text("Kembali")
                        }
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Header Ringkasan Skor
                        item {
                            state.attempt?.let { attempt ->
                                ReviewScoreSummaryHeader(
                                    score = attempt.totalScore,
                                    passingScore = state.passingScore,
                                    isPassed = attempt.isPassed,
                                    correctCount = state.correctCount,
                                    wrongCount = state.wrongCount,
                                    unansweredCount = state.unansweredCount,
                                    totalCount = state.totalCount
                                )
                            }
                        }

                        // Filter Chips
                        item {
                            ReviewFilterBar(
                                activeFilter = state.activeFilter,
                                totalCount = state.totalCount,
                                correctCount = state.correctCount,
                                wrongCount = state.wrongCount,
                                unansweredCount = state.unansweredCount,
                                onFilterSelected = { viewModel.setFilter(it) }
                            )
                        }

                        // Daftar Butir Soal & Pembahasan
                        if (state.filteredQuestions.isEmpty()) {
                            item {
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 32.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                    )
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(24.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "Tidak ada soal dalam kategori filter ini.",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        } else {
                            itemsIndexed(
                                items = state.filteredQuestions,
                                key = { _, item -> item.question.id }
                            ) { index, item ->
                                ReviewQuestionCard(
                                    questionNumber = item.question.questionNumber,
                                    item = item
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ReviewScoreSummaryHeader(
    score: Double,
    passingScore: Int,
    isPassed: Boolean,
    correctCount: Int,
    wrongCount: Int,
    unansweredCount: Int,
    totalCount: Int
) {
    val formattedScore = if (score % 1.0 == 0.0) "${score.toInt()}" else String.format(Locale.US, "%.1f", score)

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isPassed) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
        ),
        border = BorderStroke(1.dp, if (isPassed) Emerald500.copy(alpha = 0.5f) else Crimson500.copy(alpha = 0.5f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Total Nilai Simulasi",
                        style = MaterialTheme.typography.labelMedium,
                        color = Slate600
                    )
                    Text(
                        text = formattedScore,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (isPassed) Emerald600 else Crimson600
                    )
                }

                Surface(
                    color = if (isPassed) Emerald500 else Crimson500,
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(
                        text = if (isPassed) "LOLOS PG" else "TIDAK LOLOS PG",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = PureWhite,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }

            HorizontalDivider(color = Slate200, thickness = 0.5.dp)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                StatBadge(label = "Benar", value = "$correctCount", color = Emerald600)
                StatBadge(label = "Salah", value = "$wrongCount", color = Crimson600)
                StatBadge(label = "Kosong", value = "$unansweredCount", color = Slate600)
                StatBadge(label = "Target PG", value = "$passingScore", color = Navy700)
            }
        }
    }
}

@Composable
private fun StatBadge(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = color)
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = Slate600)
    }
}

@Composable
private fun ReviewFilterBar(
    activeFilter: ReviewFilter,
    totalCount: Int,
    correctCount: Int,
    wrongCount: Int,
    unansweredCount: Int,
    onFilterSelected: (ReviewFilter) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterChip(
            selected = activeFilter == ReviewFilter.ALL,
            onClick = { onFilterSelected(ReviewFilter.ALL) },
            label = { Text("Semua ($totalCount)", maxLines = 1) }
        )
        FilterChip(
            selected = activeFilter == ReviewFilter.CORRECT,
            onClick = { onFilterSelected(ReviewFilter.CORRECT) },
            label = { Text("Benar ($correctCount)", maxLines = 1) },
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = Emerald100,
                selectedLabelColor = Emerald600
            )
        )
        FilterChip(
            selected = activeFilter == ReviewFilter.WRONG,
            onClick = { onFilterSelected(ReviewFilter.WRONG) },
            label = { Text("Salah ($wrongCount)", maxLines = 1) },
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = Crimson100,
                selectedLabelColor = Crimson600
            )
        )
        FilterChip(
            selected = activeFilter == ReviewFilter.UNANSWERED,
            onClick = { onFilterSelected(ReviewFilter.UNANSWERED) },
            label = { Text("Kosong ($unansweredCount)", maxLines = 1) }
        )
    }
}

@Composable
private fun ReviewQuestionCard(
    questionNumber: Int,
    item: ReviewQuestionItem
) {
    val q = item.question
    var isPassageExpanded by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .padding(18.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header: Subtest tag & Result Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = Navy100,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = q.sectionName,
                        style = MaterialTheme.typography.labelSmall,
                        color = Navy700,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                val (badgeBg, badgeText, badgeColor) = when {
                    item.isCorrect -> Triple(Emerald100, "BENAR", Emerald600)
                    item.isUnanswered -> Triple(Slate100, "TIDAK DIJAWAB", Slate700)
                    else -> Triple(Crimson100, "SALAH", Crimson600)
                }

                Surface(
                    color = badgeBg,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = badgeText,
                        style = MaterialTheme.typography.labelSmall,
                        color = badgeColor,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            // Teks Wacana (jika ada)
            if (item.passage != null) {
                PassageViewer(
                    passage = item.passage,
                    isExpanded = isPassageExpanded,
                    onToggleExpand = { isPassageExpanded = !isPassageExpanded }
                )
            }

            // Soal
            Row(
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "$questionNumber.",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = q.questionText,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            HorizontalDivider(color = Slate200, thickness = 0.5.dp)

            val optionsList = buildList {
                add("A" to q.optionA)
                add("B" to q.optionB)
                add("C" to q.optionC)
                add("D" to q.optionD)
                q.optionE?.let { add("E" to it) }
            }
            val isTkp = q.scoringType == ScoringType.TKP_WEIGHTED

            // Pilihan Opsi Jawaban dengan Highlight Kunci & Pilihan User
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                optionsList.forEach { (key, text) ->
                    val isCorrectKey = key.equals(q.correctAnswer, ignoreCase = true)
                    val isUserAnswer = key.equals(item.userAnswerKey, ignoreCase = true)
                    val weight = when (key) {
                        "A" -> q.scoreA
                        "B" -> q.scoreB
                        "C" -> q.scoreC
                        "D" -> q.scoreD
                        "E" -> q.scoreE
                        else -> 0
                    }

                    ReviewOptionRow(
                        key = key,
                        text = text,
                        isCorrectKey = isCorrectKey,
                        isUserAnswer = isUserAnswer,
                        weight = weight,
                        isTkp = isTkp
                    )
                }
            }

            // Box Kunci Jawaban & Pembahasan Lengkap
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Navy50
                ),
                border = BorderStroke(1.dp, Navy100),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lightbulb,
                            contentDescription = null,
                            tint = Amber500,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Kunci Jawaban & Pembahasan",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = Navy900
                        )
                    }

                    if (q.correctAnswer.isNotBlank()) {
                        Text(
                            text = "Kunci Resmi: ${q.correctAnswer}",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = Emerald600
                        )
                    }

                    Text(
                        text = if (q.explanation.isNotBlank()) q.explanation else "Belum ada keterangan pembahasan lebih lanjut untuk butir soal ini.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Navy800
                    )
                }
            }
        }
    }
}

@Composable
private fun ReviewOptionRow(
    key: String,
    text: String,
    isCorrectKey: Boolean,
    isUserAnswer: Boolean,
    weight: Int,
    isTkp: Boolean
) {
    val containerColor = when {
        isCorrectKey -> Color(0xFFE8F5E9)
        isUserAnswer -> Color(0xFFFFEBEE)
        else -> MaterialTheme.colorScheme.surface
    }
    val borderColor = when {
        isCorrectKey -> Emerald500
        isUserAnswer -> Crimson500
        else -> Slate200
    }
    val textColor = when {
        isCorrectKey -> Color(0xFF1B5E20)
        isUserAnswer -> Color(0xFFB71C1C)
        else -> Slate700
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        color = containerColor,
        border = BorderStroke(if (isCorrectKey || isUserAnswer) 1.5.dp else 1.dp, borderColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Badge Key A..E
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(
                        when {
                            isCorrectKey -> Emerald500
                            isUserAnswer -> Crimson500
                            else -> Slate100
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = key,
                    fontWeight = FontWeight.Bold,
                    color = if (isCorrectKey || isUserAnswer) PureWhite else Slate700,
                    style = MaterialTheme.typography.labelMedium
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                color = textColor,
                modifier = Modifier.weight(1f)
            )

            // Status Icons & Weight
            if (isTkp && weight > 0) {
                Text(
                    text = "+$weight Poin",
                    style = MaterialTheme.typography.labelSmall,
                    color = Amber600,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }

            if (isCorrectKey) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Kunci Jawaban",
                    tint = Emerald600,
                    modifier = Modifier.size(20.dp)
                )
            } else if (isUserAnswer) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Pilihan Anda (Salah)",
                    tint = Crimson600,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

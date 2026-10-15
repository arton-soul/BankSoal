package com.banksoalpro.app.feature.exam.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.banksoalpro.app.core.ui.theme.*
import com.banksoalpro.app.feature.exam.ExamUiState

@Composable
fun ExamSubmitConfirmationDialog(
    state: ExamUiState,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val hasUnfinished = state.unansweredCount > 0 || state.flaggedCount > 0

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (hasUnfinished) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = Amber600
                    )
                }
                Text(
                    text = if (state.isTimeUp) "Waktu Ujian Habis!" else "Kumpulkan Jawaban?",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                if (state.isTimeUp) {
                    Text(
                        text = "Waktu pengerjaan telah berakhir. Sistem akan menghitung skor jawaban Anda sekarang.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                } else {
                    Text(
                        text = "Pastikan Anda telah memeriksa kembali seluruh lembar jawaban sebelum mengakhiri simulasi ini.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = Slate100)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        StatRow(label = "Total Soal:", value = "${state.totalQuestions}")
                        StatRow(label = "Terjawab:", value = "${state.answeredCount}", color = Emerald600)
                        if (state.flaggedCount > 0) {
                            StatRow(label = "Masih Ragu-ragu:", value = "${state.flaggedCount}", color = Amber600)
                        }
                        if (state.unansweredCount > 0) {
                            StatRow(label = "Belum Terjawab:", value = "${state.unansweredCount}", color = Crimson600)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Emerald600)
            ) {
                Text(text = if (state.isTimeUp) "Lihat Hasil" else "Ya, Kumpulkan", color = PureWhite)
            }
        },
        dismissButton = {
            if (!state.isTimeUp) {
                OutlinedButton(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Periksa Lagi")
                }
            }
        }
    )
}

@Composable
private fun StatRow(label: String, value: String, color: androidx.compose.ui.graphics.Color = Slate700) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium)
        Text(text = value, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = color)
    }
}

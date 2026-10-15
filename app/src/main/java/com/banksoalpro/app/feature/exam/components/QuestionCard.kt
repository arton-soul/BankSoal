package com.banksoalpro.app.feature.exam.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.banksoalpro.app.core.ui.theme.*
import com.banksoalpro.app.domain.model.DisplayOption
import com.banksoalpro.app.domain.model.ShuffledQuestion

@Composable
fun QuestionCard(
    shuffledQuestion: ShuffledQuestion,
    questionNumber: Int,
    selectedOriginalKey: String?,
    isFlagged: Boolean,
    onOptionSelected: (DisplayOption) -> Unit,
    onToggleFlag: () -> Unit,
    modifier: Modifier = Modifier
) {
    val question = shuffledQuestion.question

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(18.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Bar: Subtest tag & Flag Ragu-ragu
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
                        text = question.sectionName,
                        style = MaterialTheme.typography.labelSmall,
                        color = Navy700,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                // Tombol Ragu-ragu
                OutlinedButton(
                    onClick = onToggleFlag,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = if (isFlagged) Amber100 else Color.Transparent,
                        contentColor = if (isFlagged) Amber600 else Slate600
                    ),
                    border = BorderStroke(
                        1.dp,
                        if (isFlagged) Amber500 else Slate400
                    ),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = if (isFlagged) Icons.Filled.Flag else Icons.Outlined.Flag,
                        contentDescription = "Ragu-ragu",
                        tint = if (isFlagged) Amber600 else Slate600,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isFlagged) "Ragu-ragu" else "Yakin",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Nomor & Teks Soal
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
                    text = question.questionText,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            HorizontalDivider(color = Slate200, thickness = 0.5.dp)

            // Pilihan Opsi Jawaban (Acak Dinamis)
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                shuffledQuestion.options.forEach { option ->
                    val isSelected = option.originalKey == selectedOriginalKey
                    OptionItemView(
                        option = option,
                        isSelected = isSelected,
                        onClick = { onOptionSelected(option) }
                    )
                }
            }
        }
    }
}

@Composable
private fun OptionItemView(
    option: DisplayOption,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val borderColor = if (isSelected) MaterialTheme.colorScheme.primary else Slate200
    val containerColor = if (isSelected) Navy50 else MaterialTheme.colorScheme.surface
    val labelBgColor = if (isSelected) MaterialTheme.colorScheme.primary else Slate100
    val labelTextColor = if (isSelected) PureWhite else Slate700

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        color = containerColor,
        border = BorderStroke(if (isSelected) 1.5.dp else 1.dp, borderColor)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Lingkaran Label A, B, C, D, E
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(labelBgColor),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = option.displayKey,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = labelTextColor
                )
            }

            // Teks Pilihan Jawaban
            Text(
                text = option.text,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

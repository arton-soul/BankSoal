package com.banksoalpro.app.feature.catalog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.banksoalpro.app.core.ui.theme.*
import com.banksoalpro.app.data.local.entity.ExamPackageEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogScreen(
    categoryId: String,
    packages: List<ExamPackageEntity> = samplePackages(categoryId),
    onPackageClick: (String) -> Unit,
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Paket Soal $categoryId",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
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
        if (packages.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Belum ada paket soal untuk kategori ini.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(packages) { item ->
                    ExamPackageCard(
                        examPackage = item,
                        onClick = { onPackageClick(item.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun ExamPackageCard(
    examPackage: ExamPackageEntity,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = examPackage.subCategory,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )

                if (examPackage.isFree) {
                    Surface(
                        color = Emerald100,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "GRATIS",
                            color = Emerald600,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                } else {
                    Surface(
                        color = Amber100,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "PREMIUM",
                            color = Amber600,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Text(
                text = examPackage.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = examPackage.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            HorizontalDivider(color = Slate200, thickness = 0.5.dp)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Quiz,
                            contentDescription = null,
                            tint = Slate600,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "${examPackage.totalQuestions} Soal",
                            style = MaterialTheme.typography.labelSmall,
                            color = Slate600
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Timer,
                            contentDescription = null,
                            tint = Slate600,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "${examPackage.durationMinutes} Menit",
                            style = MaterialTheme.typography.labelSmall,
                            color = Slate600
                        )
                    }
                }

                Button(
                    onClick = onClick,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "Mulai Ujian",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    }
}

private fun samplePackages(categoryId: String): List<ExamPackageEntity> {
    return when (categoryId.uppercase()) {
        "CPNS" -> listOf(
            ExamPackageEntity(
                id = "cpns_skd_free_01",
                categoryId = "CPNS",
                subCategory = "SKD CPNS",
                title = "Tryout Akbar SKD CPNS 2026 (Starter)",
                description = "Simulasi lengkap 110 butir soal: 30 TWK, 35 TIU, 45 TKP sesuai kisi-kisi MenPAN-RB terbaru.",
                totalQuestions = 110,
                durationMinutes = 100,
                passingScore = 311,
                isFree = true,
                isDownloaded = true,
                version = 1
            ),
            ExamPackageEntity(
                id = "cpns_skd_master_02",
                categoryId = "CPNS",
                subCategory = "SKD CPNS",
                title = "Paket Tuntas SKD CPNS Master #2",
                description = "Soal tingkat penalaran tinggi (HOTS), lengkap dengan sistem passing grade BKN dan pembahasan video.",
                totalQuestions = 110,
                durationMinutes = 100,
                passingScore = 311,
                isFree = false,
                isDownloaded = false,
                version = 1
            )
        )
        "UTBK" -> listOf(
            ExamPackageEntity(
                id = "utbk_snbt_free_01",
                categoryId = "UTBK",
                subCategory = "TPS & Literasi",
                title = "Simulasi UTBK-SNBT 2026 Gratis",
                description = "Kemampuan Penalaran Umum, Pengetahuan Kuantitatif, Pemahaman Bacaan, dan Literasi.",
                totalQuestions = 85,
                durationMinutes = 95,
                isFree = true,
                isDownloaded = true,
                version = 1
            )
        )
        else -> listOf(
            ExamPackageEntity(
                id = "${categoryId.lowercase()}_sample_01",
                categoryId = categoryId,
                subCategory = "Latihan Standar",
                title = "Tryout Standar $categoryId 2026",
                description = "Latihan pemantapan materi dan evaluasi kompetensi kurikulum merdeka.",
                totalQuestions = 40,
                durationMinutes = 60,
                isFree = true,
                isDownloaded = true,
                version = 1
            )
        )
    }
}

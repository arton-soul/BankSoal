package com.banksoalpro.app.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.banksoalpro.app.core.ui.theme.*
import com.banksoalpro.app.domain.model.UserProfile

data class ExamCategory(
    val id: String,
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val containerColor: Color,
    val contentColor: Color,
    val tag: String
)

val CATEGORIES = listOf(
    ExamCategory(
        id = "CPNS",
        title = "CPNS & PPPK",
        subtitle = "SKD (TWK, TIU, TKP) & Soal Kompetensi Bidang",
        icon = Icons.Default.Work,
        containerColor = Navy100,
        contentColor = Navy700,
        tag = "Terpopuler"
    ),
    ExamCategory(
        id = "UTBK",
        title = "UTBK - SNBT",
        subtitle = "TPS, Literasi Bahasa Indonesia/Inggris, & Matematika",
        icon = Icons.Default.AccountBalance,
        containerColor = Emerald100,
        contentColor = Emerald600,
        tag = "Seleksi PTN"
    ),
    ExamCategory(
        id = "KEDINASAN",
        title = "Kedinasan & Ikatan Dinas",
        subtitle = "STAN, IPDN, STIS, Poltekip, TNI & POLRI",
        icon = Icons.Default.Shield,
        containerColor = Amber100,
        contentColor = Amber600,
        tag = "Akademik & Psikotes"
    ),
    ExamCategory(
        id = "SMA",
        title = "SMA / SMK / MA",
        subtitle = "Asesmen Sumatif Akhir, Ujian Sekolah & TKA",
        icon = Icons.Default.School,
        containerColor = Slate100,
        contentColor = Slate700,
        tag = "Kelas 10–12"
    ),
    ExamCategory(
        id = "SMP",
        title = "SMP / MTs",
        subtitle = "Latihan Soal Harian, Sumatif & Asesmen Nasional",
        icon = Icons.Default.AutoStories,
        containerColor = Slate100,
        contentColor = Slate700,
        tag = "Kelas 7–9"
    )
)

@Composable
fun HomeScreen(
    userProfile: UserProfile? = null,
    isPremiumUser: Boolean = false,
    onCategoryClick: (String) -> Unit,
    onNavigateToPremium: () -> Unit,
    onNavigateToProfile: () -> Unit = {}
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (userProfile != null) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        .clickable { onNavigateToProfile() }
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(18.dp))
                                .background(MaterialTheme.colorScheme.primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = userProfile.initial,
                                color = PureWhite,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Column {
                            Text(
                                text = "Halo, ${userProfile.displayName}!",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (userProfile.isGuest) "Mode Tamu • Ketuk untuk sync" else "Cloud Sync Aktif",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = userProfile.targetExamCategory.replace("_", " "),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
        }

        item {
            // Header Banner
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Badge(
                        containerColor = Amber500,
                        contentColor = PureWhite
                    ) {
                        Text(
                            text = "CAT SIMULATION 2026",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = "Raih Skor Tertinggi & Lolos Ujian Impian",
                        style = MaterialTheme.typography.titleLarge,
                        color = PureWhite,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Ribuan bank soal lengkap dengan sistem penilaian CAT resmi (BKN & SNPMB), pembahasan tuntas, dan perankingan.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Slate200
                    )
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Pilih Jenjang / Kategori Ujian",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        items(CATEGORIES) { category ->
            CategoryCard(
                category = category,
                onClick = { onCategoryClick(category.id) }
            )
        }

        item {
            com.banksoalpro.app.core.ads.AdsManager.BannerAdView(
                isPremiumUser = isPremiumUser,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Composable
fun CategoryCard(
    category: ExamCategory,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(category.containerColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = category.icon,
                    contentDescription = category.title,
                    tint = category.contentColor,
                    modifier = Modifier.size(28.dp)
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = category.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Text(
                    text = category.subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = category.tag,
                    style = MaterialTheme.typography.labelSmall,
                    color = category.contentColor,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Buka",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

package com.banksoalpro.app.feature.billing

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
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

@Composable
fun SubscriptionScreen(
    onBackClick: () -> Unit = {}
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Navy700)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Amber500),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.WorkspacePremium,
                            contentDescription = null,
                            tint = PureWhite,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Text(
                        text = "Upgrade ke Bank Soal PRO",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = PureWhite
                    )

                    Text(
                        text = "Akses tanpa batas ke seluruh materi tryout berkualitas tinggi, pembahasan tuntas, dan bebas gangguan iklan.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Slate200
                    )
                }
            }
        }

        item {
            Text(
                text = "Keuntungan Akun Pro:",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                BenefitRow(
                    icon = Icons.Default.Block,
                    title = "Bebas Iklan Sepenuhnya",
                    desc = "Fokus belajar dan simulasi ujian tanpa distraksi pop-up banner atau video."
                )
                BenefitRow(
                    icon = Icons.AutoMirrored.Filled.MenuBook,
                    title = "Buka Ribuan Soal & Pembahasan",
                    desc = "Akses kunci jawaban detail, trik cepat, dan rumus praktis per butir soal."
                )
                BenefitRow(
                    icon = Icons.Default.Speed,
                    title = "Simulasi CAT Berstandar Resmi",
                    desc = "Latihan dengan antarmuka BKN/UTBK, perankingan passing grade nasional."
                )
                BenefitRow(
                    icon = Icons.Default.CloudSync,
                    title = "Sinkronisasi Cloud",
                    desc = "Riwayat nilai dan paket yang dibeli tersimpan aman di akun Anda."
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Pilihan Paket Berlangganan",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    PricingPlanCard(
                        title = "Langganan 1 Bulan",
                        price = "Rp 19.000 / bln",
                        badge = null,
                        isSelected = false
                    )

                    PricingPlanCard(
                        title = "Langganan 1 Tahun",
                        price = "Rp 99.000 / thn",
                        badge = "HEMAT 56%",
                        isSelected = true
                    )
                }
            }
        }
    }
}

@Composable
private fun BenefitRow(icon: ImageVector, title: String, desc: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Emerald100),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Emerald600,
                modifier = Modifier.size(20.dp)
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = desc,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun PricingPlanCard(
    title: String,
    price: String,
    badge: String?,
    isSelected: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Navy50 else MaterialTheme.colorScheme.surface
        ),
        border = CardDefaults.outlinedCardBorder().takeIf { isSelected }
    ) {
        Row(
            modifier = Modifier
                .padding(14.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    if (badge != null) {
                        Surface(
                            color = Amber500,
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = badge,
                                color = PureWhite,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
                Text(
                    text = price,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Button(
                onClick = {},
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isSelected) Emerald600 else MaterialTheme.colorScheme.primary
                )
            ) {
                Text("Pilih")
            }
        }
    }
}

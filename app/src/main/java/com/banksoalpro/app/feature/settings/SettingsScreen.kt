package com.banksoalpro.app.feature.settings

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.banksoalpro.app.core.common.AppConstants
import com.banksoalpro.app.core.ui.theme.Slate200

@Composable
fun SettingsScreen() {
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Pusat Konten & Pembaruan",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    SettingItem(
                        icon = Icons.Default.CloudDownload,
                        title = "Sumber Bank Soal (GitHub)",
                        subtitle = AppConstants.GITHUB_REPO_URL,
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(AppConstants.GITHUB_REPO_URL))
                            context.startActivity(intent)
                        }
                    )
                    HorizontalDivider(color = Slate200, thickness = 0.5.dp)
                    SettingItem(
                        icon = Icons.Default.Email,
                        title = "Kontak Pengembang / Bantuan",
                        subtitle = AppConstants.CONTACT_EMAIL,
                        onClick = {
                            val intent = Intent(Intent.ACTION_SENDTO).apply {
                                data = Uri.parse("mailto:${AppConstants.CONTACT_EMAIL}")
                                putExtra(Intent.EXTRA_SUBJECT, "Pertanyaan seputar Bank Soal Pro")
                            }
                            context.startActivity(intent)
                        }
                    )
                }
            }
        }

        item {
            Text(
                text = "Pengaturan Aplikasi",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    SettingItem(
                        icon = Icons.Default.DeleteOutline,
                        title = "Bersihkan Cache Soal Offline",
                        subtitle = "Menghapus soal yang tersimpan di memori perangkat",
                        onClick = {}
                    )
                    HorizontalDivider(color = Slate200, thickness = 0.5.dp)
                    SettingItem(
                        icon = Icons.Default.Info,
                        title = "Versi Aplikasi",
                        subtitle = "v1.0.0 (Bank Soal CAT Edition)",
                        onClick = {}
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

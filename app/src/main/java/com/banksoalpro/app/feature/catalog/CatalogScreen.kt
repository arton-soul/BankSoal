package com.banksoalpro.app.feature.catalog

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.banksoalpro.app.core.ui.theme.*
import com.banksoalpro.app.domain.model.ExamPackage

import android.app.Activity
import androidx.compose.ui.platform.LocalContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogScreen(
    categoryId: String,
    viewModel: CatalogViewModel,
    onPackageClick: (String) -> Unit,
    onNavigateToSubscription: () -> Unit = {},
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val state by viewModel.uiState.collectAsState()
    var lockedPackageToUnlock by remember { mutableStateOf<ExamPackage?>(null) }

    LaunchedEffect(categoryId) {
        viewModel.loadCategory(categoryId)
    }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.syncStatus) {
        when (val status = state.syncStatus) {
            is SyncStatus.Success -> {
                snackbarHostState.showSnackbar(status.message)
                viewModel.clearSyncStatus()
            }
            is SyncStatus.Error -> {
                snackbarHostState.showSnackbar(status.error)
                viewModel.clearSyncStatus()
            }
            else -> {}
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            com.banksoalpro.app.core.ads.AdsManager.BannerAdView(
                isPremiumUser = state.entitlement.hasPurchased,
                modifier = Modifier.padding(bottom = 4.dp)
            )
        },
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
                actions = {
                    IconButton(
                        onClick = { viewModel.syncFromGitHub() },
                        enabled = state.syncStatus !is SyncStatus.Syncing
                    ) {
                        if (state.syncStatus is SyncStatus.Syncing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Sync,
                                contentDescription = "Perbarui Katalog Soal"
                            )
                        }
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
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                }
                state.packages.isEmpty() -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Belum Ada Paket Soal",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Tekan tombol perbarui di bawah untuk memperbarui katalog soal secara online.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.syncFromGitHub() }) {
                            Icon(imageVector = Icons.Default.Sync, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Perbarui Katalog Soal")
                        }
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        contentPadding = PaddingValues(vertical = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(state.packages) { item ->
                            val isDownloading = state.downloadingPackageId == item.id
                            val isAccessible = state.entitlement.isPackageAccessible(item.id, item.isFree)
                            ExamPackageDomainCard(
                                examPackage = item,
                                isDownloading = isDownloading,
                                isAccessible = isAccessible,
                                onStartClick = {
                                    if (isAccessible) {
                                        onPackageClick(item.id)
                                    } else {
                                        lockedPackageToUnlock = item
                                    }
                                },
                                onDownloadClick = {
                                    viewModel.downloadPackage(item.id, "packages/${item.id}.json")
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    lockedPackageToUnlock?.let { pkg ->
        AlertDialog(
            onDismissRequest = { lockedPackageToUnlock = null },
            icon = { Icon(Icons.Default.Lock, contentDescription = null, tint = Amber500) },
            title = { Text("Buka Paket Tryout", fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "Paket '${pkg.title}' merupakan paket tryout PRO. Anda bisa membukanya gratis dengan menonton 1 video singkat, atau berlangganan akun PRO untuk akses seluruh bank soal."
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        val toUnlock = pkg.id
                        lockedPackageToUnlock = null
                        if (activity != null) {
                            com.banksoalpro.app.core.ads.AdsManager.showRewarded(
                                activity = activity,
                                isPremiumUser = false,
                                onRewardEarned = {
                                    viewModel.unlockPackageWithReward(toUnlock)
                                    onPackageClick(toUnlock)
                                },
                                onAdDismissed = {}
                            )
                        }
                    }
                ) {
                    Icon(Icons.Default.PlayCircle, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Buka Gratis (Tonton Video)")
                }
            },
            dismissButton = {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextButton(onClick = { lockedPackageToUnlock = null }) {
                        Text("Batal")
                    }
                    TextButton(
                        onClick = {
                            lockedPackageToUnlock = null
                            onNavigateToSubscription()
                        }
                    ) {
                        Text("Langganan PRO", fontWeight = FontWeight.Bold)
                    }
                }
            }
        )
    }
}

@Composable
fun ExamPackageDomainCard(
    examPackage: ExamPackage,
    isDownloading: Boolean,
    isAccessible: Boolean = true,
    onStartClick: () -> Unit,
    onDownloadClick: () -> Unit
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

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    if (examPackage.isDownloaded) {
                        Surface(
                            color = Emerald100,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "SIAP OFFLINE",
                                color = Emerald600,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }

                    if (examPackage.isFree) {
                        Surface(
                            color = Navy100,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "GRATIS",
                                color = Navy700,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    } else {
                        Surface(
                            color = if (isAccessible) Emerald100 else Amber100,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = if (isAccessible) "TERBUKA" else "PREMIUM",
                                color = if (isAccessible) Emerald600 else Amber600,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
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

                if (examPackage.isDownloaded) {
                    Button(
                        onClick = onStartClick,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isAccessible) MaterialTheme.colorScheme.primary else Amber500
                        ),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        if (!isAccessible) {
                            Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Buka Paket",
                                style = MaterialTheme.typography.labelLarge
                            )
                        } else {
                            Text(
                                text = "Mulai Ujian",
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }
                } else {
                    OutlinedButton(
                        onClick = onDownloadClick,
                        enabled = !isDownloading,
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        if (isDownloading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Mengunduh...")
                        } else {
                            Icon(
                                imageVector = Icons.Default.CloudDownload,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Unduh Soal")
                        }
                    }
                }
            }
        }
    }
}

package com.banksoalpro.app.feature.billing

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.LibraryBooks
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.banksoalpro.app.core.ui.theme.*
import com.banksoalpro.app.data.billing.model.AppBillingProduct
import com.banksoalpro.app.domain.repository.BillingPurchaseState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SubscriptionScreen(
    viewModel: SubscriptionViewModel,
    onBackClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val entitlement by viewModel.userEntitlement.collectAsStateWithLifecycle()
    val purchaseState by viewModel.purchaseState.collectAsStateWithLifecycle()
    val restoreMessage by viewModel.restoreMessage.collectAsStateWithLifecycle()

    var selectedSubscriptionId by remember { mutableStateOf(AppBillingProduct.ProYearly.id) }

    LaunchedEffect(purchaseState) {
        if (purchaseState is BillingPurchaseState.Success) {
            Toast.makeText(context, (purchaseState as BillingPurchaseState.Success).message, Toast.LENGTH_LONG).show()
        } else if (purchaseState is BillingPurchaseState.Error) {
            Toast.makeText(context, (purchaseState as BillingPurchaseState.Error).errorMessage, Toast.LENGTH_LONG).show()
        }
    }

    LaunchedEffect(restoreMessage) {
        restoreMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.clearRestoreMessage()
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Active Pro Banner if user already subscribed
        if (entitlement.isSubscriptionActive) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF2E7D32))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Verified,
                            contentDescription = null,
                            tint = PureWhite,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                "Status: PRO MEMBER AKTIF",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = PureWhite
                            )
                            val expiryText = entitlement.premiumExpiryDate?.let {
                                "Berlaku hingga " + SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(it))
                            } ?: "Akses Selamanya"
                            Text(
                                expiryText,
                                style = MaterialTheme.typography.bodySmall,
                                color = PureWhite.copy(alpha = 0.9f)
                            )
                        }
                    }
                }
            }
        }

        // Hero Paywall Banner
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
                        text = "Akses tanpa batas ke seluruh materi tryout berkualitas tinggi, simulasi CAT resmi, kunci pembahasan tuntas, dan bebas iklan.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Slate200
                    )
                }
            }
        }

        // Benefits Checklist
        item {
            Text(
                text = "Keuntungan Akun PRO:",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                ProBenefitItem(
                    icon = Icons.Default.Block,
                    title = "Bebas Iklan 100%",
                    description = "Fokus belajar dan simulasi ujian tanpa distraksi banner, interstitial, ataupun rewarded ads."
                )
                ProBenefitItem(
                    icon = Icons.AutoMirrored.Filled.LibraryBooks,
                    title = "Ribuan Paket Soal Terlengkap",
                    description = "Akses seluruh paket CPNS, PPPK, UTBK-SNBT, Kedinasan, SMA, dan SMP terbaru."
                )
                ProBenefitItem(
                    icon = Icons.AutoMirrored.Filled.MenuBook,
                    title = "Kunci Pembahasan Mendalam",
                    description = "Penjelasan langkah demi langkah, rumus cepat matematika, dan tips trik menjawab."
                )
                ProBenefitItem(
                    icon = Icons.Default.Speed,
                    title = "Simulasi CAT Tanpa Batas",
                    description = "Ulangi tryout sesuka hati dengan acak opsi otomatis di setiap sesi pengerjaan."
                )
            }
        }

        // Section: Pilihan Langganan (Subscriptions)
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Pilih Paket Langganan PRO:",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                viewModel.subscriptions.forEach { sub ->
                    val isSelected = selectedSubscriptionId == sub.id
                    val isYearly = sub.id == AppBillingProduct.ProYearly.id

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedSubscriptionId = sub.id }
                            .then(
                                if (isSelected) Modifier.border(2.dp, Amber500, RoundedCornerShape(16.dp))
                                else Modifier.border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                            ),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) Amber500.copy(alpha = 0.08f)
                            else MaterialTheme.colorScheme.surface
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 3.dp else 1.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    modifier = Modifier.weight(1f, fill = false)
                                ) {
                                    RadioButton(
                                        selected = isSelected,
                                        onClick = { selectedSubscriptionId = sub.id },
                                        colors = RadioButtonDefaults.colors(selectedColor = Amber500)
                                    )
                                    Text(
                                        text = sub.title,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                if (isYearly) {
                                    Surface(
                                        color = Amber500,
                                        shape = RoundedCornerShape(20.dp)
                                    ) {
                                        Text(
                                            "HEMAT 56%",
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = PureWhite
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 38.dp),
                                verticalAlignment = Alignment.Bottom,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = sub.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.weight(1f)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = sub.formattedPrice,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }
        }

        // Subscribe Action Button
        item {
            val selectedProduct = viewModel.subscriptions.firstOrNull { it.id == selectedSubscriptionId }
                ?: AppBillingProduct.ProYearly

            Button(
                onClick = {
                    if (activity != null) {
                        viewModel.purchaseProduct(activity, selectedProduct)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Amber500),
                shape = RoundedCornerShape(14.dp),
                enabled = purchaseState !is BillingPurchaseState.Loading
            ) {
                if (purchaseState is BillingPurchaseState.Loading) {
                    CircularProgressIndicator(color = PureWhite, modifier = Modifier.size(24.dp))
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.WorkspacePremium,
                            contentDescription = null,
                            tint = PureWhite,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Langganan PRO Sekarang",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = PureWhite
                            )
                            Text(
                                text = selectedProduct.formattedPrice,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold,
                                color = PureWhite.copy(alpha = 0.95f)
                            )
                        }
                    }
                }
            }
        }

        // Section: Pembelian Paket Satuan (In-App Purchases)
        item {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Atau Beli Paket Satuan (Sekali Bayar):",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        items(viewModel.inAppProducts) { product ->
            val isUnlocked = entitlement.unlockedPackageIds.contains(product.id) || entitlement.isPremiumUser
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = product.title,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = product.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = product.formattedPrice,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    if (isUnlocked) {
                        Surface(
                            color = Color(0xFF2E7D32).copy(alpha = 0.15f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                "TERBUKA",
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2E7D32)
                            )
                        }
                    } else {
                        Button(
                            onClick = {
                                if (activity != null) {
                                    viewModel.purchaseProduct(activity, product)
                                }
                            },
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Beli")
                        }
                    }
                }
            }
        }

        // Info Box: Di mana paket yang dibeli dapat ditemukan
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Navy50),
                border = BorderStroke(1.dp, Navy100),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = Navy700,
                        modifier = Modifier.size(20.dp)
                    )
                    Column {
                        Text(
                            text = "Di Mana Paket yang Dibeli Dapat Ditemukan?",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = Navy900
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Semua paket yang Anda beli atau aktifkan via langganan PRO otomatis terbuka di menu Beranda (Katalog Soal) pada kategori masing-masing tanpa tanda gembok, dan juga dapat langsung dikerjakan melalui menu Profil di tab 'Paket Soal Saya'.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Navy800
                        )
                    }
                }
            }
        }

        // Restore Purchases Button
        item {
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedButton(
                onClick = { viewModel.restorePurchases() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Restore, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Pulihkan Pembelian (Restore)")
            }
        }

        // Footer disclaimer
        item {
            Text(
                text = "Langganan dapat dibatalkan kapan saja melalui Akun Google Play Store > Pembayaran & Langganan. Pembelian paket satuan memberikan akses permanen pada paket soal yang dipilih.",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }
    }
}

@Composable
private fun ProBenefitItem(
    icon: ImageVector,
    title: String,
    description: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(12.dp),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(22.dp)
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

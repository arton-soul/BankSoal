package com.banksoalpro.app.feature.billing

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.banksoalpro.app.data.billing.model.AppBillingProduct
import com.banksoalpro.app.domain.model.UserEntitlement
import com.banksoalpro.app.domain.repository.BillingPurchaseState
import com.banksoalpro.app.domain.repository.BillingRepository
import com.banksoalpro.app.domain.repository.EntitlementRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SubscriptionViewModel(
    private val billingRepository: BillingRepository,
    private val entitlementRepository: EntitlementRepository
) : ViewModel() {

    val userEntitlement: StateFlow<UserEntitlement> = entitlementRepository.currentEntitlement
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserEntitlement(userId = ""))

    val purchaseState: StateFlow<BillingPurchaseState> = billingRepository.purchaseState

    private val _restoreMessage = MutableStateFlow<String?>(null)
    val restoreMessage: StateFlow<String?> = _restoreMessage.asStateFlow()

    val inAppProducts = AppBillingProduct.IN_APP_PRODUCTS
    val subscriptions = AppBillingProduct.SUBSCRIPTIONS

    fun purchaseProduct(activity: Activity, product: AppBillingProduct) {
        billingRepository.launchPurchaseFlow(activity, product)
    }

    fun restorePurchases() {
        viewModelScope.launch {
            val result = billingRepository.restorePurchases()
            result.fold(
                onSuccess = { count ->
                    _restoreMessage.value = if (count > 0) {
                        "Berhasil memulihkan $count produk pembelian!"
                    } else {
                        "Tidak ada riwayat pembelian sebelumnya pada akun Google ini."
                    }
                },
                onFailure = { error ->
                    _restoreMessage.value = "Gagal memulihkan pembelian: ${error.message}"
                }
            )
        }
    }

    fun clearRestoreMessage() {
        _restoreMessage.value = null
    }

    override fun onCleared() {
        super.onCleared()
        billingRepository.destroy()
    }
}

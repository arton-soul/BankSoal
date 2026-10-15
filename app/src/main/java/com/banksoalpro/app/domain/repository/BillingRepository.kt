package com.banksoalpro.app.domain.repository

import android.app.Activity
import com.banksoalpro.app.data.billing.model.AppBillingProduct
import kotlinx.coroutines.flow.StateFlow

sealed interface BillingPurchaseState {
    object Idle : BillingPurchaseState
    object Loading : BillingPurchaseState
    data class Success(val message: String, val productId: String) : BillingPurchaseState
    data class Error(val errorMessage: String) : BillingPurchaseState
}

interface BillingRepository {
    val purchaseState: StateFlow<BillingPurchaseState>
    fun startConnection(onConnected: () -> Unit = {})
    fun launchPurchaseFlow(activity: Activity, product: AppBillingProduct)
    suspend fun restorePurchases(): Result<Int>
    fun destroy()
}

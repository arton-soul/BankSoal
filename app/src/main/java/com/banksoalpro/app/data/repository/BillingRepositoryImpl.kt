package com.banksoalpro.app.data.repository

import android.app.Activity
import android.content.Context
import com.android.billingclient.api.*
import com.banksoalpro.app.data.billing.model.AppBillingProduct
import com.banksoalpro.app.domain.repository.AuthRepository
import com.banksoalpro.app.domain.repository.BillingPurchaseState
import com.banksoalpro.app.domain.repository.BillingRepository
import com.banksoalpro.app.domain.repository.EntitlementRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BillingRepositoryImpl(
    private val context: Context,
    private val authRepository: AuthRepository,
    private val entitlementRepository: EntitlementRepository,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) : BillingRepository, PurchasesUpdatedListener {

    private val _purchaseState = MutableStateFlow<BillingPurchaseState>(BillingPurchaseState.Idle)
    override val purchaseState: StateFlow<BillingPurchaseState> = _purchaseState.asStateFlow()

    private var billingClient: BillingClient = BillingClient.newBuilder(context)
        .setListener(this)
        .enablePendingPurchases(PendingPurchasesParams.newBuilder().enableOneTimeProducts().build())
        .build()

    private var isConnected: Boolean = false
    private val productDetailsMap = mutableMapOf<String, ProductDetails>()

    init {
        startConnection()
    }

    override fun startConnection(onConnected: () -> Unit) {
        if (isConnected) {
            onConnected()
            return
        }

        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    isConnected = true
                    queryAvailableProducts()
                    onConnected()
                } else {
                    isConnected = false
                }
            }

            override fun onBillingServiceDisconnected() {
                isConnected = false
            }
        })
    }

    private fun queryAvailableProducts() {
        val inAppProductList = AppBillingProduct.IN_APP_PRODUCTS.map {
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(it.id)
                .setProductType(BillingClient.ProductType.INAPP)
                .build()
        }

        val subsProductList = AppBillingProduct.SUBSCRIPTIONS.map {
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(it.id)
                .setProductType(BillingClient.ProductType.SUBS)
                .build()
        }

        val allProducts = inAppProductList + subsProductList
        if (allProducts.isEmpty()) return

        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(allProducts)
            .build()

        billingClient.queryProductDetailsAsync(params) { billingResult, queryResult ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                queryResult.productDetailsList.forEach { details ->
                    productDetailsMap[details.productId] = details
                }
            }
        }
    }

    override fun launchPurchaseFlow(activity: Activity, product: AppBillingProduct) {
        _purchaseState.value = BillingPurchaseState.Loading

        val productDetails = productDetailsMap[product.id]
        if (productDetails == null || !isConnected) {
            // Fallback for emulator / testing sandbox without Google Play Services
            simulateLocalPurchase(product)
            return
        }

        val productDetailsParamsList = if (product.isSubscription) {
            val offerToken = productDetails.subscriptionOfferDetails?.firstOrNull()?.offerToken ?: ""
            listOf(
                BillingFlowParams.ProductDetailsParams.newBuilder()
                    .setProductDetails(productDetails)
                    .setOfferToken(offerToken)
                    .build()
            )
        } else {
            listOf(
                BillingFlowParams.ProductDetailsParams.newBuilder()
                    .setProductDetails(productDetails)
                    .build()
            )
        }

        val billingFlowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(productDetailsParamsList)
            .build()

        val result = billingClient.launchBillingFlow(activity, billingFlowParams)
        if (result.responseCode != BillingClient.BillingResponseCode.OK) {
            _purchaseState.value = BillingPurchaseState.Error("Gagal memulai transaksi: ${result.debugMessage}")
        }
    }

    override fun onPurchasesUpdated(billingResult: BillingResult, purchases: MutableList<Purchase>?) {
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            for (purchase in purchases) {
                handlePurchase(purchase)
            }
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
            _purchaseState.value = BillingPurchaseState.Idle
        } else {
            _purchaseState.value = BillingPurchaseState.Error("Transaksi dibatalkan atau gagal: ${billingResult.debugMessage}")
        }
    }

    private fun handlePurchase(purchase: Purchase) {
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
            scope.launch {
                val user = authRepository.getCurrentUser()
                val userId = user?.userId ?: "guest_local"

                // Acknowledge purchase if not acknowledged
                if (!purchase.isAcknowledged) {
                    val acknowledgeParams = AcknowledgePurchaseParams.newBuilder()
                        .setPurchaseToken(purchase.purchaseToken)
                        .build()
                    billingClient.acknowledgePurchase(acknowledgeParams) { /* acknowledged */ }
                }

                // Grant entitlement
                for (productId in purchase.products) {
                    grantEntitlementForProduct(userId, productId)
                }

                _purchaseState.value = BillingPurchaseState.Success(
                    message = "Pembelian berhasil diaktifkan!",
                    productId = purchase.products.firstOrNull() ?: ""
                )
            }
        }
    }

    private suspend fun grantEntitlementForProduct(userId: String, productId: String) {
        when (productId) {
            AppBillingProduct.ProMonthly.id -> {
                val expiry = System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000
                entitlementRepository.setPremiumStatus(userId, isPremium = true, expiryDate = expiry)
            }
            AppBillingProduct.ProYearly.id -> {
                val expiry = System.currentTimeMillis() + 365L * 24 * 60 * 60 * 1000
                entitlementRepository.setPremiumStatus(userId, isPremium = true, expiryDate = expiry)
            }
            AppBillingProduct.CpnsMaster.id -> {
                entitlementRepository.unlockPackage(userId, "cpns_skd_free_01")
                entitlementRepository.unlockPackage(userId, "cpns_skd_sample_02")
                entitlementRepository.unlockPackage(userId, "pkg_cpns_master_2026")
            }
            AppBillingProduct.UtbkLengkap.id -> {
                entitlementRepository.unlockPackage(userId, "utbk_snbt_free_01")
                entitlementRepository.unlockPackage(userId, "utbk_snbt_sample_02")
                entitlementRepository.unlockPackage(userId, "pkg_utbk_lengkap_2026")
            }
            AppBillingProduct.SmaSmpAll.id -> {
                entitlementRepository.unlockPackage(userId, "sma_sample_01")
                entitlementRepository.unlockPackage(userId, "smp_sample_01")
                entitlementRepository.unlockPackage(userId, "pkg_sma_smp_all")
            }
            else -> {
                entitlementRepository.unlockPackage(userId, productId)
            }
        }
    }

    private fun simulateLocalPurchase(product: AppBillingProduct) {
        scope.launch {
            val user = authRepository.getCurrentUser()
            val userId = user?.userId ?: "guest_local"
            grantEntitlementForProduct(userId, product.id)
            _purchaseState.value = BillingPurchaseState.Success(
                message = "Pembelian ${product.title} berhasil diaktifkan!",
                productId = product.id
            )
        }
    }

    override suspend fun restorePurchases(): Result<Int> {
        return try {
            val user = authRepository.getCurrentUser()
            val userId = user?.userId ?: "guest_local"
            var restoredCount = 0

            if (isConnected) {
                // Query inapp
                val inappParams = QueryPurchasesParams.newBuilder()
                    .setProductType(BillingClient.ProductType.INAPP)
                    .build()
                billingClient.queryPurchasesAsync(inappParams) { result, purchases ->
                    if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                        purchases.forEach { purchase ->
                            if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                                purchase.products.forEach { id ->
                                    scope.launch { grantEntitlementForProduct(userId, id) }
                                    restoredCount++
                                }
                            }
                        }
                    }
                }

                // Query subs
                val subsParams = QueryPurchasesParams.newBuilder()
                    .setProductType(BillingClient.ProductType.SUBS)
                    .build()
                billingClient.queryPurchasesAsync(subsParams) { result, purchases ->
                    if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                        purchases.forEach { purchase ->
                            if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                                purchase.products.forEach { id ->
                                    scope.launch { grantEntitlementForProduct(userId, id) }
                                    restoredCount++
                                }
                            }
                        }
                    }
                }
            }

            Result.success(restoredCount)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun destroy() {
        if (isConnected) {
            billingClient.endConnection()
            isConnected = false
        }
    }
}

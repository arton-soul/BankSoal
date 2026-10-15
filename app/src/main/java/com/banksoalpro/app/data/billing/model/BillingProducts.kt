package com.banksoalpro.app.data.billing.model

sealed class AppBillingProduct(
    val id: String,
    val title: String,
    val description: String,
    val formattedPrice: String,
    val isSubscription: Boolean
) {
    // In-App Purchases (Paket Tryout Satuan)
    object CpnsMaster : AppBillingProduct(
        id = "pkg_cpns_master_2026",
        title = "Paket Lengkap SKD CPNS 2026",
        description = "Akses permanen seluruh paket tryout SKD (TWK, TIU, TKP) dengan sistem CAT dan pembahasan resmi.",
        formattedPrice = "Rp 29.000",
        isSubscription = false
    )

    object UtbkLengkap : AppBillingProduct(
        id = "pkg_utbk_lengkap_2026",
        title = "Paket Super Intensif UTBK-SNBT",
        description = "Akses permanen seluruh soal TPS, Penalaran Matematika, dan Literasi Bahasa Indonesia & Inggris.",
        formattedPrice = "Rp 39.000",
        isSubscription = false
    )

    object SmaSmpAll : AppBillingProduct(
        id = "pkg_sma_smp_all",
        title = "Paket Latihan SMP & SMA Terpadu",
        description = "Akses permanen kumpulan bank soal dan pembahasan lengkap untuk jenjang SMP dan SMA.",
        formattedPrice = "Rp 25.000",
        isSubscription = false
    )

    // Subscriptions (Langganan Akses Penuh Semua Paket + Tanpa Iklan)
    object ProMonthly : AppBillingProduct(
        id = "sub_pro_monthly",
        title = "PRO Bulanan",
        description = "Akses semua paket ujian (CPNS, UTBK, Kedinasan, SMA, SMP) & Bebas Iklan selama 1 bulan.",
        formattedPrice = "Rp 19.000 / bln",
        isSubscription = true
    )

    object ProYearly : AppBillingProduct(
        id = "sub_pro_yearly",
        title = "PRO Tahunan (Paling Hemat)",
        description = "Akses penuh tanpa batas seluruh bank soal dan simulasi CAT selama 1 tahun penuh.",
        formattedPrice = "Rp 99.000 / thn",
        isSubscription = true
    )

    companion object {
        val IN_APP_PRODUCTS = listOf(CpnsMaster, UtbkLengkap, SmaSmpAll)
        val SUBSCRIPTIONS = listOf(ProMonthly, ProYearly)
        val ALL_PRODUCTS = IN_APP_PRODUCTS + SUBSCRIPTIONS

        fun findById(id: String): AppBillingProduct? {
            return ALL_PRODUCTS.firstOrNull { it.id == id }
        }
    }
}

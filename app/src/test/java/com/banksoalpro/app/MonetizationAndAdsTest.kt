package com.banksoalpro.app

import com.banksoalpro.app.core.ads.AdsConfig
import com.banksoalpro.app.data.billing.model.AppBillingProduct
import org.junit.Assert.*
import org.junit.Test

class MonetizationAndAdsTest {

    @Test
    fun testAdMobConfiguration_officialIdsIntegrity() {
        assertEquals("ca-app-pub-4098008677804088~2352896167", AdsConfig.APP_ID)
        assertEquals("ca-app-pub-4098008677804088/6068779892", AdsConfig.BANNER_ID)
        assertEquals("ca-app-pub-4098008677804088/4180983159", AdsConfig.INTERSTITIAL_ID)
        assertEquals("ca-app-pub-4098008677804088/1419088445", AdsConfig.NATIVE_ID)
        assertEquals("ca-app-pub-4098008677804088/8858594761", AdsConfig.REWARD_ID)
    }

    @Test
    fun testAdMob_productionToggle() {
        AdsConfig.forceUseProductionAds = true
        assertEquals(AdsConfig.BANNER_ID, AdsConfig.getBannerId())
        assertEquals(AdsConfig.INTERSTITIAL_ID, AdsConfig.getInterstitialId())
        assertEquals(AdsConfig.REWARD_ID, AdsConfig.getRewardId())
        assertEquals(AdsConfig.NATIVE_ID, AdsConfig.getNativeId())
        AdsConfig.forceUseProductionAds = false
    }

    @Test
    fun testBillingProducts_catalogIntegrity() {
        assertEquals(3, AppBillingProduct.IN_APP_PRODUCTS.size)
        assertEquals(2, AppBillingProduct.SUBSCRIPTIONS.size)
        assertEquals(5, AppBillingProduct.ALL_PRODUCTS.size)

        // Verify In-App product IDs
        val cpns = AppBillingProduct.findById("pkg_cpns_master_2026")
        assertNotNull(cpns)
        assertFalse(cpns!!.isSubscription)
        assertEquals("Rp 29.000", cpns.formattedPrice)

        val utbk = AppBillingProduct.findById("pkg_utbk_lengkap_2026")
        assertNotNull(utbk)
        assertFalse(utbk!!.isSubscription)
        assertEquals("Rp 39.000", utbk.formattedPrice)

        val smaSmp = AppBillingProduct.findById("pkg_sma_smp_all")
        assertNotNull(smaSmp)
        assertFalse(smaSmp!!.isSubscription)
        assertEquals("Rp 25.000", smaSmp.formattedPrice)

        // Verify Subscription product IDs
        val monthly = AppBillingProduct.findById("sub_pro_monthly")
        assertNotNull(monthly)
        assertTrue(monthly!!.isSubscription)
        assertEquals("Rp 19.000 / bln", monthly.formattedPrice)

        val yearly = AppBillingProduct.findById("sub_pro_yearly")
        assertNotNull(yearly)
        assertTrue(yearly!!.isSubscription)
        assertEquals("Rp 99.000 / thn", yearly.formattedPrice)
    }

    @Test
    fun testSubscriptionDurationCalculation() {
        val now = 1000000000L
        val monthDuration = 30L * 24 * 60 * 60 * 1000
        val yearDuration = 365L * 24 * 60 * 60 * 1000

        val monthlyExpiry = now + monthDuration
        val yearlyExpiry = now + yearDuration

        assertTrue(monthlyExpiry > now)
        assertTrue(yearlyExpiry > monthlyExpiry)
        assertEquals(2592000000L, monthDuration)
        assertEquals(31536000000L, yearDuration)
    }
}

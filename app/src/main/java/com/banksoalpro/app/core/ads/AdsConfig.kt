package com.banksoalpro.app.core.ads

import com.banksoalpro.app.BuildConfig

object AdsConfig {
    // Official AdMob IDs provided by maintainer (arton.79@gmail.com)
    const val APP_ID = "ca-app-pub-4098008677804088~2352896167"
    const val BANNER_ID = "ca-app-pub-4098008677804088/6068779892"
    const val INTERSTITIAL_ID = "ca-app-pub-4098008677804088/4180983159"
    const val NATIVE_ID = "ca-app-pub-4098008677804088/1419088445"
    const val REWARD_ID = "ca-app-pub-4098008677804088/8858594761"

    // Official Google AdMob Test IDs (used during development & test to prevent policy violation)
    const val TEST_BANNER_ID = "ca-app-pub-3940256099942544/6300978111"
    const val TEST_INTERSTITIAL_ID = "ca-app-pub-3940256099942544/1033173712"
    const val TEST_REWARD_ID = "ca-app-pub-3940256099942544/5224354917"
    const val TEST_NATIVE_ID = "ca-app-pub-3940256099942544/2247696110"

    var forceUseProductionAds: Boolean = false

    fun getBannerId(): String {
        return if (BuildConfig.DEBUG && !forceUseProductionAds) TEST_BANNER_ID else BANNER_ID
    }

    fun getInterstitialId(): String {
        return if (BuildConfig.DEBUG && !forceUseProductionAds) TEST_INTERSTITIAL_ID else INTERSTITIAL_ID
    }

    fun getRewardId(): String {
        return if (BuildConfig.DEBUG && !forceUseProductionAds) TEST_REWARD_ID else REWARD_ID
    }

    fun getNativeId(): String {
        return if (BuildConfig.DEBUG && !forceUseProductionAds) TEST_NATIVE_ID else NATIVE_ID
    }
}

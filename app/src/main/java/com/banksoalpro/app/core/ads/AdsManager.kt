package com.banksoalpro.app.core.ads

import android.app.Activity
import android.content.Context
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardItem
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback

object AdsManager {

    private var interstitialAd: InterstitialAd? = null
    private var isInterstitialLoading: Boolean = false

    private var rewardedAd: RewardedAd? = null
    private var isRewardedLoading: Boolean = false

    private var isInitialized: Boolean = false

    fun initialize(context: Context) {
        if (isInitialized) return
        MobileAds.initialize(context) {
            isInitialized = true
            loadInterstitial(context)
            loadRewarded(context)
        }
    }

    fun loadInterstitial(context: Context) {
        if (interstitialAd != null || isInterstitialLoading) return
        isInterstitialLoading = true

        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(
            context,
            AdsConfig.getInterstitialId(),
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                    isInterstitialLoading = false
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    interstitialAd = null
                    isInterstitialLoading = false
                }
            }
        )
    }

    fun showInterstitial(
        activity: Activity,
        isPremiumUser: Boolean,
        onAdDismissed: () -> Unit
    ) {
        // If user is premium, bypass all ads
        if (isPremiumUser) {
            onAdDismissed()
            return
        }

        val ad = interstitialAd
        if (ad != null) {
            ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    interstitialAd = null
                    loadInterstitial(activity)
                    onAdDismissed()
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    interstitialAd = null
                    loadInterstitial(activity)
                    onAdDismissed()
                }
            }
            ad.show(activity)
        } else {
            // If ad is not ready, don't block user
            loadInterstitial(activity)
            onAdDismissed()
        }
    }

    fun loadRewarded(context: Context) {
        if (rewardedAd != null || isRewardedLoading) return
        isRewardedLoading = true

        val adRequest = AdRequest.Builder().build()
        RewardedAd.load(
            context,
            AdsConfig.getRewardId(),
            adRequest,
            object : RewardedAdLoadCallback() {
                override fun onAdLoaded(ad: RewardedAd) {
                    rewardedAd = ad
                    isRewardedLoading = false
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    rewardedAd = null
                    isRewardedLoading = false
                }
            }
        )
    }

    fun showRewarded(
        activity: Activity,
        isPremiumUser: Boolean,
        onRewardEarned: (RewardItem?) -> Unit,
        onAdDismissed: () -> Unit
    ) {
        if (isPremiumUser) {
            onRewardEarned(null)
            onAdDismissed()
            return
        }

        val ad = rewardedAd
        if (ad != null) {
            var earned = false
            ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    rewardedAd = null
                    loadRewarded(activity)
                    onAdDismissed()
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    rewardedAd = null
                    loadRewarded(activity)
                    onAdDismissed()
                }
            }
            ad.show(activity) { rewardItem ->
                earned = true
                onRewardEarned(rewardItem)
            }
        } else {
            loadRewarded(activity)
            onAdDismissed()
        }
    }

    @Composable
    fun BannerAdView(
        isPremiumUser: Boolean,
        modifier: Modifier = Modifier
    ) {
        if (isPremiumUser) return

        AndroidView(
            modifier = modifier
                .fillMaxWidth()
                .height(50.dp),
            factory = { context ->
                AdView(context).apply {
                    setAdSize(AdSize.BANNER)
                    adUnitId = AdsConfig.getBannerId()
                    loadAd(AdRequest.Builder().build())
                }
            }
        )
    }
}

package com.royalcardclash.game.ads

import android.app.Activity
import android.content.Context
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback

class AdManager(private val context: Context) {

    companion object {
        private const val IS_TEST_MODE = true

        // Official Google Test Ad IDs
        private const val TEST_BANNER_ID = "ca-app-pub-3940256099942544/6300978111"
        private const val TEST_INTERSTITIAL_ID = "ca-app-pub-3940256099942544/1033173712"
        private const val TEST_REWARDED_ID = "ca-app-pub-3940256099942544/5224354917"

        // Replace with production unit IDs before store submission
        private const val PROD_BANNER_ID = "ca-app-pub-XXXXXXXXXXXXXXXX/XXXXXXXXXX"
        private const val PROD_INTERSTITIAL_ID = "ca-app-pub-XXXXXXXXXXXXXXXX/XXXXXXXXXX"
        private const val PROD_REWARDED_ID = "ca-app-pub-XXXXXXXXXXXXXXXX/XXXXXXXXXX"

        val bannerAdUnitId: String get() = if (IS_TEST_MODE) TEST_BANNER_ID else PROD_BANNER_ID
        val interstitialAdUnitId: String get() = if (IS_TEST_MODE) TEST_INTERSTITIAL_ID else PROD_INTERSTITIAL_ID
        val rewardedAdUnitId: String get() = if (IS_TEST_MODE) TEST_REWARDED_ID else PROD_REWARDED_ID
    }

    private var interstitialAd: InterstitialAd? = null
    private var rewardedAd: RewardedAd? = null

    init {
        try {
            MobileAds.initialize(context) {}
            preloadInterstitial()
            preloadRewarded()
        } catch (_: Exception) { }
    }

    fun createBannerAdView(): AdView {
        return AdView(context).apply {
            setAdSize(AdSize.BANNER)
            adUnitId = bannerAdUnitId
            loadAd(AdRequest.Builder().build())
        }
    }

    fun preloadInterstitial() {
        InterstitialAd.load(
            context,
            interstitialAdUnitId,
            AdRequest.Builder().build(),
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                }
                override fun onAdFailedToLoad(error: LoadAdError) {
                    interstitialAd = null
                }
            }
        )
    }

    fun showInterstitial(activity: Activity, onDismissed: () -> Unit) {
        if (interstitialAd != null) {
            interstitialAd?.show(activity)
            interstitialAd = null
            preloadInterstitial()
            onDismissed()
        } else {
            onDismissed()
            preloadInterstitial()
        }
    }

    fun preloadRewarded() {
        RewardedAd.load(
            context,
            rewardedAdUnitId,
            AdRequest.Builder().build(),
            object : RewardedAdLoadCallback() {
                override fun onAdLoaded(ad: RewardedAd) {
                    rewardedAd = ad
                }
                override fun onAdFailedToLoad(error: LoadAdError) {
                    rewardedAd = null
                }
            }
        )
    }

    fun showRewardedAd(activity: Activity, onRewardEarned: (Int) -> Unit, onFailure: () -> Unit) {
        if (rewardedAd != null) {
            rewardedAd?.show(activity) { rewardItem ->
                onRewardEarned(rewardItem.amount)
            }
            rewardedAd = null
            preloadRewarded()
        } else {
            onFailure()
            preloadRewarded()
        }
    }
}

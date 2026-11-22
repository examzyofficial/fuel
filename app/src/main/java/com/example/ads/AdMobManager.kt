package com.example.ads

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.R
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

class InterstitialAdController(private val context: Context) {
    private var interstitialAd: InterstitialAd? = null
    private var isLoading = false
    private var calculationTapCount = 0

    private val adUnitId: String = context.getString(R.string.admob_interstitial_unit_id)

    fun loadAd() {
        if (isLoading || interstitialAd != null) return
        isLoading = true
        runCatching {
            val adRequest = AdRequest.Builder().build()
            InterstitialAd.load(
                context,
                adUnitId,
                adRequest,
                object : InterstitialAdLoadCallback() {
                    override fun onAdLoaded(ad: InterstitialAd) {
                        isLoading = false
                        interstitialAd = ad
                    }

                    override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                        isLoading = false
                        interstitialAd = null
                    }
                }
            )
        }.onFailure { err ->
            isLoading = false
            interstitialAd = null
            Log.w("AdMobManager", "Interstitial load skipped", err)
        }
    }

    /**
     * Shows an interstitial ad every 3rd explicit calculation tap so UX stays smooth.
     */
    fun onCalculateTapped(activity: Activity?) {
        calculationTapCount++
        if (calculationTapCount % 3 != 0) {
            if (interstitialAd == null) loadAd()
            return
        }
        val ad = interstitialAd
        if (ad != null && activity != null) {
            runCatching {
                ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                    override fun onAdDismissedFullScreenContent() {
                        interstitialAd = null
                        loadAd()
                    }

                    override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                        interstitialAd = null
                        loadAd()
                    }
                }
                ad.show(activity)
            }.onFailure {
                interstitialAd = null
            }
        } else {
            loadAd()
        }
    }
}

@Composable
fun AdMobBanner(
    isAdsInitialized: Boolean,
    modifier: Modifier = Modifier
) {
    if (!isAdsInitialized) return
    val context = LocalContext.current
    val bannerUnitId = stringResource(R.string.admob_banner_unit_id)

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("admob_banner_container"),
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surface
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            val adWidthDp = maxWidth.value.toInt().coerceAtLeast(320)
            val adView = remember(bannerUnitId, adWidthDp) {
                runCatching {
                    val adSize = AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(context, adWidthDp)
                    AdView(context).apply {
                        adUnitId = bannerUnitId
                        setAdSize(adSize)
                        loadAd(AdRequest.Builder().build())
                    }
                }.getOrNull()
            }

            if (adView != null) {
                DisposableEffect(adView) {
                    onDispose {
                        runCatching { adView.destroy() }
                    }
                }

                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    AndroidView(
                        factory = { adView }
                    )
                }
            }
        }
    }
}

tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

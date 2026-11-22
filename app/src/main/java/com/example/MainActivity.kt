package com.example

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.ui.FuelDensityApp
import com.google.android.gms.ads.MobileAds

class MainActivity : ComponentActivity() {

    private var isMobileAdsReady by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // MobileAds.initialize must be called on the main UI thread
        runCatching {
            MobileAds.initialize(this) {
                isMobileAdsReady = true
            }
        }.onFailure { err ->
            Log.e("MainActivity", "MobileAds initialization failed", err)
        }

        setContent {
            FuelDensityApp(isAdsInitialized = isMobileAdsReady)
        }
    }
}

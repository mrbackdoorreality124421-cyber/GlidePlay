package com.smoothplay.app

import android.os.Bundle

import android.content.pm.ActivityInfo
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.smoothplay.app.ui.navigation.AppNavigation
import com.smoothplay.app.ui.theme.SmoothPlayTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            OrientationManager.isLandscape.collect { landscape ->
                requestedOrientation = if (landscape) {
                    ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
                } else {
                    ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                }
            }
        }

        setContent {
            SmoothPlayTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }
}

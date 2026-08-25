package com.smoothplay.app.ui.screens

import android.Manifest
import android.content.Intent
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.smoothplay.app.engine.RuntimeInstallerEngine
import com.smoothplay.app.engine.RuntimeInstallerState
import com.smoothplay.app.service.DownloadService

@Composable
fun SetupScreen(onSetupComplete: () -> Unit) {
    val context = LocalContext.current
    
    val statusText by RuntimeInstallerState.statusText.collectAsState()
    val progress by RuntimeInstallerState.progress.collectAsState()
    val isInstalling by RuntimeInstallerState.isInstalling.collectAsState()
    val hasError by RuntimeInstallerState.hasError.collectAsState()
    
    val installer = remember { RuntimeInstallerEngine(context) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) {
        // Ignored for now, we continue anyway
    }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
        
        if (installer.isRuntimeInstalled()) {
            RuntimeInstallerState.statusText.value = "Runtime installed!"
            onSetupComplete()
        } else { 
            RuntimeInstallerState.statusText.value = "PC Emulator Cores Missing" 
        }
    }

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                Text("GlidePlay Setup", style = MaterialTheme.typography.headlineLarge)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Let's get your PC emulator ready", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(32.dp))
                
                Text(statusText, color = if (hasError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.titleMedium)
                
                if (isInstalling) {
                    Spacer(modifier = Modifier.height(16.dp))
                    LinearProgressIndicator(progress = (progress / 100f).coerceIn(0f, 1f), modifier = Modifier.fillMaxWidth().height(8.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("$progress%", style = MaterialTheme.typography.bodySmall)
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                if (!isInstalling && statusText != "Installation Complete!") {
                    Button(onClick = {
                        RuntimeInstallerState.hasError.value = false
                        RuntimeInstallerState.isInstalling.value = true
                        
                        val intent = Intent(context, DownloadService::class.java).apply {
                            action = DownloadService.ACTION_START
                        }
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            context.startForegroundService(intent)
                        } else {
                            context.startService(intent)
                        }
                    }, modifier = Modifier.fillMaxWidth().height(56.dp)) {
                        Text(if (hasError) "Retry" else "Download & Install (~400MB)")
                    }
                }
                
                if (statusText == "Installation Complete!") {
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { onSetupComplete() }) {
                        Text("Continue")
                    }
                }
            }
        }
    }
}

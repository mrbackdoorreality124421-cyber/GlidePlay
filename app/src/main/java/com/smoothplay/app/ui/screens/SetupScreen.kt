package com.smoothplay.app.ui.screens

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.smoothplay.app.engine.RuntimeInstallerEngine
import com.smoothplay.app.service.DownloadService
import kotlinx.coroutines.launch

@Composable
fun SetupScreen(onSetupComplete: () -> Unit) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var statusText by remember { mutableStateOf("Initializing...") }
    var isInstalling by remember { mutableStateOf(false) }
    var hasError by remember { mutableStateOf(false) }
    var downloadedBytes by remember { mutableLongStateOf(0L) }
    var showResumeOption by remember { mutableStateOf(false) }
    
    val installer = remember { RuntimeInstallerEngine(context) }

    LaunchedEffect(Unit) {
        if (installer.isRuntimeInstalled()) {
            onSetupComplete()
        } else {
            statusText = "PC Emulator Cores Missing"
            downloadedBytes = installer.getDownloadProgress()
            showResumeOption = downloadedBytes > 0
        }
    }

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("GlidePlay Setup", style = MaterialTheme.typography.headlineLarge)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Let's get your PC emulator ready",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(32.dp))
                
                Text(
                    statusText,
                    color = if (hasError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.titleMedium
                )
                
                if (isInstalling) {
                    Spacer(modifier = Modifier.height(16.dp))
                    CircularProgressIndicator(modifier = Modifier.size(48.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Download will continue in background if you leave the app",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                if (!isInstalling) {
                    if (showResumeOption && !hasError) {
                        Button(
                            onClick = {
                                isInstalling = true
                                startDownloadService(context)
                                statusText = "Download resuming in background..."
                            },
                            modifier = Modifier.fillMaxWidth().height(56.dp)
                        ) {
                            Text("Resume Download (${formatBytes(downloadedBytes)} downloaded)")
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    
                    Button(
                        onClick = {
                            hasError = false
                            isInstalling = true
                            startDownloadService(context)
                            statusText = "Starting download..."
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp)
                    ) {
                        Text(if (hasError) "Retry" else "Download & Install (~400MB)")
                    }
                    
                    if (hasError) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "Tip: Download continues in background even if you close the app",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

private fun startDownloadService(context: android.content.Context) {
    val intent = Intent(context, DownloadService::class.java).apply {
        action = DownloadService.ACTION_START
    }
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
        context.startForegroundService(intent)
    } else {
        context.startService(intent)
    }
}

private fun formatBytes(bytes: Long): String {
    return when {
        bytes >= 1_000_000_000 -> String.format("%.1f GB", bytes / 1_000_000_000.0)
        bytes >= 1_000_000 -> String.format("%.1f MB", bytes / 1_000_000.0)
        bytes >= 1_000 -> String.format("%.1f KB", bytes / 1_000.0)
        else -> "$bytes B"
    }
}

package com.smoothplay.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.smoothplay.app.engine.RuntimeInstallerEngine
import kotlinx.coroutines.launch

@Composable
fun SetupScreen(onSetupComplete: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var statusText by remember { mutableStateOf("Checking runtime...") }
    var progress by remember { mutableIntStateOf(0) }
    var isInstalling by remember { mutableStateOf(false) }
    var hasError by remember { mutableStateOf(false) }
    val installer = remember { RuntimeInstallerEngine(context) }

    LaunchedEffect(Unit) {
        if (installer.isRuntimeInstalled()) {
            statusText = "Runtime installed!"
            onSetupComplete()
        } else { statusText = "PC Emulator Cores Missing" }
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
                    LinearProgressIndicator(progress = { progress / 100f }, modifier = Modifier.fillMaxWidth().height(8.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("$progress%", style = MaterialTheme.typography.bodySmall)
                }
                Spacer(modifier = Modifier.height(32.dp))
                if (!isInstalling) {
                    Button(onClick = {
                        hasError = false; isInstalling = true
                        scope.launch {
                            val s = installer.downloadAndInstallRuntime { m, p -> statusText = m; progress = p }
                            isInstalling = false
                            if (s) onSetupComplete() else { hasError = true; statusText = "Install failed. Check internet." }
                        }
                    }, modifier = Modifier.fillMaxWidth().height(56.dp)) {
                        Text(if (hasError) "Retry" else "Download & Install (~400MB)")
                    }
                }
            }
        }
    }
}

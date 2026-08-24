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
    val coroutineScope = rememberCoroutineScope()
    var statusText by remember { mutableStateOf("Initializing...") }
    var isInstalling by remember { mutableStateOf(false) }
    var hasError by remember { mutableStateOf(false) }

    val installer = remember { RuntimeInstallerEngine(context) }

    LaunchedEffect(Unit) {
        if (installer.isRuntimeInstalled()) {
            onSetupComplete()
        } else {
            statusText = "PC Emulator Cores Missing."
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("SmoothPlay First Setup", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        Text(statusText, color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(32.dp))

        if (isInstalling) {
            CircularProgressIndicator()
        } else if (hasError) {
            Button(onClick = {
                hasError = false
                isInstalling = true
                coroutineScope.launch {
                    val success = installer.downloadAndInstallRuntime { msg, _ -> statusText = msg }
                    isInstalling = false
                    if (success) {
                        onSetupComplete()
                    } else {
                        hasError = true
                        statusText = "Installation Failed. Check internet connection."
                    }
                }
            }) {
                Text("Retry Install")
            }
        } else {
            Button(
                onClick = {
                    isInstalling = true
                    coroutineScope.launch {
                        val success = installer.downloadAndInstallRuntime { msg, _ -> statusText = msg }
                        isInstalling = false
                        if (success) {
                            onSetupComplete()
                        } else {
                            hasError = true
                            statusText = "Installation Failed."
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                Text("Download & Install Core Files (~400MB)")
            }
        }
    }
}\n
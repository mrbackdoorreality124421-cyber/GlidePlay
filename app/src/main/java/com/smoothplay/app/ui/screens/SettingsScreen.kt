package com.smoothplay.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.smoothplay.app.engine.RuntimeInstallerEngine
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val installer = remember { RuntimeInstallerEngine(context) }
    var autoOpt by remember { mutableStateOf(true) }
    var haptic by remember { mutableStateOf(true) }
    var keepOn by remember { mutableStateOf(true) }
    var devOpt by remember { mutableStateOf(false) }
    var showUninstall by remember { mutableStateOf(false) }
    var status by remember { mutableStateOf("Checking...") }

    LaunchedEffect(Unit) { status = if (installer.isRuntimeInstalled()) "Installed" else "Not Installed" }

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)) {
        Text("Settings", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(16.dp))
        
        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp)) {
                Text("Runtime Engine", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(12.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column {
                        Text("PC Emulator Core", style = MaterialTheme.typography.bodyMedium)
                        Text(status, style = MaterialTheme.typography.bodySmall, 
                             color = if (status == "Installed") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error)
                    }
                    if (status == "Installed") OutlinedButton(onClick = { showUninstall = true }) { Text("Uninstall") }
                }
            }
        }
        
        Spacer(Modifier.height(16.dp))
        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp)) {
                Text("Gameplay", style = MaterialTheme.typography.titleMedium)
                SS("Auto-optimize profiles", "Match profile to device", autoOpt) { autoOpt = it }
                SS("Haptic feedback", "Vibrate on button press", haptic) { haptic = it }
                SS("Keep screen on", "Prevent sleep", keepOn) { keepOn = it }
            }
        }
        
        Spacer(Modifier.height(16.dp))
        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp)) {
                Text("About", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                Text("GlidePlay v2.0.0", style = MaterialTheme.typography.bodyMedium)
                Text("PC games on Android, optimized.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        
        Spacer(Modifier.height(16.dp))
        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp)) {
                SS("Developer Options", "Advanced settings", devOpt) { devOpt = it }
            }
        }
    }
    
    if (showUninstall) {
        AlertDialog(
            onDismissRequest = { showUninstall = false },
            title = { Text("Uninstall Runtime?") },
            text = { Text("Removes ~400MB of emulator files.") },
            confirmButton = { TextButton(onClick = { scope.launch { installer.uninstallRuntime(); status = "Not Installed" }; showUninstall = false }) { Text("Uninstall", color = MaterialTheme.colorScheme.error) } },
            dismissButton = { TextButton(onClick = { showUninstall = false }) { Text("Cancel") } }
        )
    }
}

@Composable private fun SS(t: String, s: String, c: Boolean, oc: (Boolean) -> Unit) {
    Row(Modifier.fillMaxWidth().padding(vertical = 8.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(1f)) { Text(t, style = MaterialTheme.typography.bodyMedium); Text(s, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant) }
        Switch(checked = c, onCheckedChange = oc)
    }
}

package com.smoothplay.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.smoothplay.app.engine.DeviceBenchmark

@Composable
fun OptimizerScreen() {
    val context = LocalContext.current
    val deviceScore = remember { DeviceBenchmark.getScore(context) }
    val deviceSummary = remember { DeviceBenchmark.getDeviceSummary(context) }
    val cores = remember { DeviceBenchmark.getCpuCores() }
    val ramGb = remember { DeviceBenchmark.getTotalRamGb(context) }
    
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)) {
        Text("Device Optimizer", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(16.dp))
        
        Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
            Column(Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("$deviceScore", style = MaterialTheme.typography.displayLarge, color = MaterialTheme.colorScheme.onPrimaryContainer)
                Text("/100 Score", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f))
                Spacer(Modifier.height(16.dp))
                LinearProgressIndicator(progress = { deviceScore / 100f }, modifier = Modifier.fillMaxWidth().height(8.dp))
                Spacer(Modifier.height(8.dp))
                Text(getTier(deviceScore), style = MaterialTheme.typography.titleMedium)
            }
        }
        
        Spacer(Modifier.height(16.dp))
        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp)) {
                Text("Hardware Specs", style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(12.dp))
                SpecRow("Device", deviceSummary)
                SpecRow("CPU Cores", "$cores cores")
                SpecRow("RAM", "${String.format("%.1f", ramGb)} GB")
                SpecRow("Android", android.os.Build.VERSION.RELEASE)
            }
        }
        
        Spacer(Modifier.height(16.dp))
        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp)) {
                Text("Recommended Profiles", style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(12.dp))
                ProfileRow("Super Extreme", "Small games", deviceScore >= 80)
                ProfileRow("Ultra", "Up to 500MB", deviceScore >= 60)
                ProfileRow("Balanced", "Up to 1GB", deviceScore >= 40)
                ProfileRow("Smooth", "Up to 2GB", deviceScore >= 20)
                ProfileRow("Super Smooth", "Heavy AAA", true)
            }
        }
    }
}

@Composable private fun SpecRow(l: String, v: String) {
    Row(Modifier.fillMaxWidth().padding(vertical = 6.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(l, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(v, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable private fun ProfileRow(n: String, d: String, r: Boolean) {
    Row(Modifier.fillMaxWidth().padding(vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(if (r) Icons.Default.CheckCircle else Icons.Default.Cancel, null, 
             tint = if (r) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
             modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(8.dp))
        Column { Text(n, style = MaterialTheme.typography.bodyMedium); Text(d, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant) }
    }
}

private fun getTier(s: Int) = when {
    s >= 80 -> "Flagship"; s >= 60 -> "High-End"
    s >= 40 -> "Mid-Range"; s >= 20 -> "Entry-Level"; else -> "Power Saver"
}

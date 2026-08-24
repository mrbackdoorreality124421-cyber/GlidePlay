package com.smoothplay.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.smoothplay.app.engine.ControlsOverlay

@Composable
fun ControlsScreen() {
    var activeProfile by remember { mutableStateOf("Balanced") }
    var sensitivity by remember { mutableFloatStateOf(1.2f) }
    var showPreview by remember { mutableStateOf(false) }
    var lastInput by remember { mutableStateOf("None") }

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)) {
        Text("Controls Editor", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(16.dp))
        
        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp)) {
                Text("Control Profile", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("FPS", "Racing", "RPG", "Custom").forEach { p ->
                        FilterChip(selected = activeProfile == p, onClick = { activeProfile = p }, label = { Text(p) })
                    }
                }
            }
        }
        
        Spacer(Modifier.height(16.dp))
        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Mouse Sensitivity", style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                    Text(String.format("%.1f", sensitivity), style = MaterialTheme.typography.bodyLarge)
                }
                Slider(value = sensitivity, onValueChange = { sensitivity = it }, valueRange = 0.5f..3.0f, steps = 9)
            }
        }
        
        Spacer(Modifier.height(16.dp))
        Button(onClick = { showPreview = true }, modifier = Modifier.fillMaxWidth().height(56.dp)) {
            Icon(Icons.Default.Visibility, null); Spacer(Modifier.width(8.dp)); Text("Preview Overlay")
        }
        
        Spacer(Modifier.height(16.dp))
        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp)) {
                Text("Active Mappings", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                MR("FIRE", "Left Mouse"); MR("AIM", "Right Mouse"); MR("JUMP", "Space")
                MR("RELOAD", "R"); MR("MOVE", "WASD Joystick"); MR("LOOK", "Swipe")
            }
        }
        
        Spacer(Modifier.height(16.dp))
        Text("Last input: $lastInput", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
    
    if (showPreview) {
        Box(Modifier.fillMaxSize().background(androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.7f))) {
            ControlsOverlay(activeProfile = activeProfile) { k, p -> lastInput = if (p) "$k pressed" else "$k released" }
            IconButton(onClick = { showPreview = false }, modifier = Modifier.align(Alignment.TopEnd).padding(16.dp)) {
                Icon(Icons.Default.Close, null, tint = androidx.compose.ui.graphics.Color.White)
            }
            Text("Tap buttons, drag joystick", color = androidx.compose.ui.graphics.Color.White, 
                 modifier = Modifier.align(Alignment.TopCenter).padding(top = 24.dp))
        }
    }
}

@Composable private fun MR(l: String, k: String) {
    Row(Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(l, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
        Text(k, style = MaterialTheme.typography.bodyMedium)
    }
}

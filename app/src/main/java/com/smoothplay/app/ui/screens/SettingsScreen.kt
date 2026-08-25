package com.smoothplay.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SettingsScreen() {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Settings", style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(16.dp))
        Text("System & Performance", style = MaterialTheme.typography.titleMedium)
        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Auto Optimize Games")
            Switch(checked = true, onCheckedChange = {})
        }
        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Thermal Protection")
            Switch(checked = true, onCheckedChange = {})
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { }) {
            Text("Clear Shader Cache")
        }
    }
}

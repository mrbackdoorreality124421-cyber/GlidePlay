package com.smoothplay.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ControlsScreen() {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Controls Editor", style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(16.dp))
        Text("Active Template: FPS")
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { }) {
            Text("Edit Layout")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = { }) {
            Text("Adjust Haptics & Gyro")
        }
    }
}

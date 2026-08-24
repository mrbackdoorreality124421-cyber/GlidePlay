package com.smoothplay.app.ui.screens
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun OptimizerScreen() {
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Optimizer", style = MaterialTheme.typography.headlineMedium)
        Card(Modifier.fillMaxWidth().padding(vertical = 16.dp)) {
            Column(Modifier.padding(16.dp)) {
                Text("Device Score: Calculating...", style = MaterialTheme.typography.titleLarge)
                Text("Hardware Specs: Checked")
            }
        }
    }
}\n
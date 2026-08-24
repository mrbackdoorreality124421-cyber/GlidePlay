package com.smoothplay.app.ui.screens
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ControlsScreen() {
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Controls Editor", style = MaterialTheme.typography.headlineMedium)
        Button(onClick = {}, modifier = Modifier.padding(top=16.dp)) { Text("Edit Virtual Layout") }
    }
}\n
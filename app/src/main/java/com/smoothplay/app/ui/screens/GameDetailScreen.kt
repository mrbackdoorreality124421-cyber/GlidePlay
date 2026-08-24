package com.smoothplay.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.smoothplay.app.ui.viewmodels.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameDetailScreen(gameId: String, onBack: () -> Unit, viewModel: HomeViewModel = hiltViewModel()) {
    val games by viewModel.games.collectAsState(initial = emptyList())
    val game = games.find { it.id == gameId }
    val isRunning by viewModel.isGameRunning.collectAsState()
    val logs by viewModel.launchLog.collectAsState()
    
    Scaffold(topBar = {
        TopAppBar(title = { Text(game?.name ?: "Game") },
            navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) } },
            actions = { if (isRunning) IconButton(onClick = { viewModel.stopGame() }) { Icon(Icons.Default.Stop, null, tint = MaterialTheme.colorScheme.error) } })
    }) { p ->
        if (game == null) { Box(Modifier.fillMaxSize().padding(p), contentAlignment = Alignment.Center) { Text("Game not found") }; return@Scaffold }
        LazyColumn(Modifier.fillMaxSize().padding(p).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            item {
                Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                    Column(Modifier.padding(20.dp)) {
                        Text(game.name, style = MaterialTheme.typography.headlineSmall)
                        Spacer(Modifier.height(8.dp))
                        Text("Profile: ${game.profile}", style = MaterialTheme.typography.titleMedium)
                        Text("Status: ${game.status}", style = MaterialTheme.typography.bodyMedium)
                        Spacer(Modifier.height(12.dp))
                        Button(onClick = { viewModel.launchGame(game) }, modifier = Modifier.fillMaxWidth().height(52.dp), enabled = !isRunning) {
                            Icon(if (isRunning) Icons.Default.HourglassTop else Icons.Default.PlayArrow, null)
                            Spacer(Modifier.width(8.dp)); Text(if (isRunning) "Running..." else "Launch")
                        }
                        if (isRunning) {
                            Spacer(Modifier.height(8.dp))
                            OutlinedButton(onClick = { viewModel.stopGame() }, modifier = Modifier.fillMaxWidth()) {
                                Icon(Icons.Default.Stop, null); Spacer(Modifier.width(8.dp)); Text("Stop")
                            }
                        }
                    }
                }
            }
            item {
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp)) {
                        Text("Game Info", style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.height(8.dp))
                        IR("Size", "${game.totalSizeMb} MB"); IR("Weight", "${game.weightScore}/100")
                        IR("Deps", if (game.dependencies.isEmpty()) "None" else game.dependencies)
                        IR("Executable", game.mainExecutable.substringAfterLast("/"))
                    }
                }
            }
            if (logs.isNotEmpty()) {
                item {
                    Card(Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(16.dp)) {
                            Text("Launch Log", style = MaterialTheme.typography.titleMedium)
                            Spacer(Modifier.height(8.dp))
                            Box(Modifier.fillMaxWidth().height(300.dp).background(Color(0xFF0D1117), shape = MaterialTheme.shapes.small).padding(8.dp)) {
                                LazyColumn { items(logs) { l -> Text(l, color = Color(0xFF7EE787), style = MaterialTheme.typography.bodySmall, fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace) } }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable private fun IR(l: String, v: String) {
    Row(Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(l, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(v, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.widthIn(max = 200.dp))
    }
}

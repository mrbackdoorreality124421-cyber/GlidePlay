package com.smoothplay.app.ui.screens

import android.content.pm.ActivityInfo
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.smoothplay.app.ui.viewmodels.HomeViewModel

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    activity: ComponentActivity? = null
) {
    val games by viewModel.games.collectAsState(initial = emptyList())
    val status by viewModel.statusMsg.collectAsState()
    
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let { 
            viewModel.importGame(it)
        }
    }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Button(
            onClick = { 
                launcher.launch(arrayOf(
                    "application/zip",
                    "application/x-zip-compressed",
                    "application/x-7z-compressed",
                    "application/x-rar-compressed",
                    "application/x-msdos-program",
                    "application/octet-stream",
                    "application/x-iso9660-image"
                ))
            },
            modifier = Modifier.fillMaxWidth().height(80.dp)
        ) { 
            Text("Add Game (ZIP/EXE/ISO)", style = MaterialTheme.typography.titleLarge) 
        }
        
        Spacer(Modifier.height(8.dp))
        Text(status, color = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.height(16.dp))
        
        if (games.isEmpty()) {
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text("No games imported yet", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Supported formats: ZIP, EXE, ISO, and extracted game folders",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(games) { game ->
                    Card(
                        Modifier.fillMaxWidth(),
                        onClick = {
                            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
                            viewModel.launchGame(game) { success ->
                                if (success) {
                                    activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                                }
                            }
                        }
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Text(game.name, style = MaterialTheme.typography.titleMedium)
                            Spacer(Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Profile: ${game.profile}", style = MaterialTheme.typography.bodySmall)
                                Text("Size: ${game.totalSizeMb}MB", style = MaterialTheme.typography.bodySmall)
                            }
                            if (game.dependencies.isNotBlank()) {
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    "Requires: ${game.dependencies}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Spacer(Modifier.height(8.dp))
                            Text("Tap to launch", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }
        }
    }
}

package com.smoothplay.app.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.smoothplay.app.data.Game
import com.smoothplay.app.ui.viewmodels.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: HomeViewModel = hiltViewModel(), onGameClick: (String) -> Unit = {}) {
    val games by viewModel.games.collectAsState(initial = emptyList())
    val status by viewModel.statusMsg.collectAsState()
    val isProcessing by viewModel.isProcessing.collectAsState()
    var showDeleteDialog by remember { mutableStateOf<Game?>(null) }
    
    val zipLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri -> 
        uri?.let { viewModel.importZip(it) } 
    }
    val folderLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocumentTree()) { uri -> 
        uri?.let { viewModel.importFolder(it) } 
    }

    Scaffold(topBar = { TopAppBar(title = { Text("Game Library") }) }) { p ->
        Column(Modifier.fillMaxSize().padding(p).padding(16.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { zipLauncher.launch("application/zip") }, modifier = Modifier.weight(1f).height(64.dp), enabled = !isProcessing) {
                    Icon(Icons.Default.FolderZip, null); Spacer(Modifier.width(8.dp)); Text("Add ZIP")
                }
                Button(onClick = { folderLauncher.launch(null) }, modifier = Modifier.weight(1f).height(64.dp), enabled = !isProcessing) {
                    Icon(Icons.Default.Folder, null); Spacer(Modifier.width(8.dp)); Text("Add Folder")
                }
            }
            if (status.isNotEmpty()) {
                Spacer(Modifier.height(8.dp))
                Text(status, color = if (status.contains("Error") || status.contains("failed")) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary)
            }
            if (isProcessing) { Spacer(Modifier.height(8.dp)); LinearProgressIndicator(modifier = Modifier.fillMaxWidth()) }
            Spacer(Modifier.height(16.dp))
            if (games.isEmpty() && !isProcessing) {
                Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.SportsEsports, null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(Modifier.height(8.dp))
                        Text("No games yet", style = MaterialTheme.typography.titleMedium)
                        Text("Tap a button above to import", style = MaterialTheme.typography.bodySmall)
                    }
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(games, key = { it.id }) { g ->
                        Card(Modifier.fillMaxWidth().clickable { onGameClick(g.id) }, colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                            Column(Modifier.padding(16.dp)) {
                                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                    Column(Modifier.weight(1f)) {
                                        Text(g.name, style = MaterialTheme.typography.titleMedium)
                                        Text("Profile: ${g.profile}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                                        Text("Size: ${g.totalSizeMb}MB | ${g.status}", style = MaterialTheme.typography.bodySmall)
                                    }
                                    IconButton(onClick = { showDeleteDialog = g }) { Icon(Icons.Default.Delete, null, tint = MaterialTheme.colorScheme.error) }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    showDeleteDialog?.let { g ->
        AlertDialog(onDismissRequest = { showDeleteDialog = null }, title = { Text("Delete ${g.name}?") },
            text = { Text("This will remove the game permanently.") },
            confirmButton = { TextButton(onClick = { viewModel.deleteGame(g); showDeleteDialog = null }) { Text("Delete", color = MaterialTheme.colorScheme.error) } },
            dismissButton = { TextButton(onClick = { showDeleteDialog = null }) { Text("Cancel") } })
    }
}

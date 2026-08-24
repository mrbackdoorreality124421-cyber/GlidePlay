package com.smoothplay.app.ui.screens
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
fun HomeScreen(viewModel: HomeViewModel = hiltViewModel()) {
    val games by viewModel.games.collectAsState(initial = emptyList())
    val status by viewModel.statusMsg.collectAsState()
    
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { viewModel.importZip(it) }
    }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Button(
            onClick = { launcher.launch("application/zip") },
            modifier = Modifier.fillMaxWidth().height(80.dp)
        ) { Text("Add Game ZIP", style = MaterialTheme.typography.titleLarge) }
        Spacer(Modifier.height(8.dp))
        Text(status, color = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.height(16.dp))
        
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(games) { game ->
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp)) {
                        Text(game.name, style = MaterialTheme.typography.titleMedium)
                        Text("Profile: ${game.profile} | Size: ${game.totalSizeMb}MB", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}\n
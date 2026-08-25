package com.smoothplay.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.smoothplay.app.models.Game
import com.smoothplay.app.ui.viewmodels.HomeViewModel

@Composable
fun HomeScreen(viewModel: HomeViewModel = hiltViewModel()) {
    val games by viewModel.games.collectAsState(initial = emptyList())

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Button(
            onClick = { viewModel.importMockGame() },
            modifier = Modifier.fillMaxWidth().height(100.dp),
            shape = MaterialTheme.shapes.large
        ) {
            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Text("Add Game ZIP", style = MaterialTheme.typography.headlineMedium)
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text("Your Library", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(games) { game ->
                GameCard(game = game, onPlay = { /* Launch Engine here */ })
            }
        }
    }
}

@Composable
fun GameCard(game: Game, onPlay: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(game.name, style = MaterialTheme.typography.titleMedium)
                Text(game.status + " • " + game.profile, style = MaterialTheme.typography.bodySmall)
            }
            IconButton(onClick = onPlay, modifier = Modifier.size(48.dp)) {
                Icon(Icons.Default.PlayArrow, contentDescription = "Play", modifier = Modifier.size(32.dp))
            }
        }
    }
}

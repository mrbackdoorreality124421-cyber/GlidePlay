package com.smoothplay.app.ui.navigation

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.smoothplay.app.ui.screens.*

@Composable
fun AppNavigation(activity: ComponentActivity? = null) {
    val navController = rememberNavController()
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    var isSetupMode by remember { mutableStateOf(true) }

    if (isSetupMode) {
        SetupScreen(onSetupComplete = {
            isSetupMode = false
        })
    } else {
        Scaffold(
            bottomBar = {
                NavigationBar {
                    listOf(
                        Triple("home", "Library", Icons.Default.Games),
                        Triple("optimizer", "Optimizer", Icons.Default.Speed),
                        Triple("controls", "Controls", Icons.Default.Build),
                        Triple("settings", "Settings", Icons.Default.Settings)
                    ).forEach { (route, label, icon) ->
                        NavigationBarItem(
                            icon = { Icon(icon, label) }, 
                            label = { Text(label) },
                            selected = currentRoute == route,
                            onClick = { navController.navigate(route) { popUpTo("home"); launchSingleTop = true } }
                        )
                    }
                }
            }
        ) { innerPadding ->
            NavHost(navController, startDestination = "home", modifier = Modifier.padding(innerPadding)) {
                composable("home") { HomeScreen(activity = activity) }
                composable("optimizer") { OptimizerScreen() }
                composable("controls") { ControlsScreen() }
                composable("settings") { SettingsScreen() }
            }
        }
    }
}

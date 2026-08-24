package com.smoothplay.app.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.smoothplay.app.engine.RuntimeInstallerEngine
import com.smoothplay.app.ui.screens.*

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    val context = androidx.compose.ui.platform.LocalContext.current
    val installer = remember { RuntimeInstallerEngine(context) }
    var isSetupDone by remember { mutableStateOf(installer.isRuntimeInstalled()) }

    if (!isSetupDone) { SetupScreen(onSetupComplete = { isSetupDone = true }) }
    else {
        Scaffold(bottomBar = {
            NavigationBar {
                listOf(Triple("home", "Library", Icons.Default.Games), Triple("optimizer", "Optimizer", Icons.Default.Speed),
                       Triple("controls", "Controls", Icons.Default.Gamepad), Triple("settings", "Settings", Icons.Default.Settings)
                ).forEach { (r, l, i) ->
                    NavigationBarItem(icon = { Icon(i, l) }, label = { Text(l) }, selected = currentRoute == r,
                        onClick = { navController.navigate(r) { popUpTo("home") { saveState = true }; launchSingleTop = true; restoreState = true } })
                }
            }
        }) { p ->
            NavHost(navController, startDestination = "home", modifier = Modifier.padding(p)) {
                composable("home") { HomeScreen(onGameClick = { id -> navController.navigate("game/$id") }) }
                composable("optimizer") { OptimizerScreen() }
                composable("controls") { ControlsScreen() }
                composable("settings") { SettingsScreen() }
                composable("game/{gameId}", arguments = listOf(navArgument("gameId") { type = NavType.StringType })) { b ->
                    val id = b.arguments?.getString("gameId") ?: return@composable
                    GameDetailScreen(gameId = id, onBack = { navController.popBackStack() })
                }
            }
        }
    }
}

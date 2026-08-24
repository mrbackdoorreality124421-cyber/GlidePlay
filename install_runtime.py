import os

files = {
    "app/src/main/java/com/smoothplay/app/engine/RuntimeInstallerEngine.kt": """package com.smoothplay.app.engine

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.zip.ZipInputStream

class RuntimeInstallerEngine(private val context: Context) {

    // In a real production app, this would point to your server hosting the compiled Box64/Wine rootfs
    private val RUNTIME_URL = "https://github.com/brunodev85/winlator/releases/download/v7.1.2/main.7.1.2.com.winlator.obb" // Example placeholder

    suspend fun isRuntimeInstalled(): Boolean {
        val rootfsDir = File(context.filesDir, "rootfs")
        val box64 = File(rootfsDir, "usr/local/bin/box64")
        return box64.exists()
    }

    suspend fun downloadAndInstallRuntime(onProgress: (String, Int) -> Unit): Boolean = withContext(Dispatchers.IO) {
        val rootfsDir = File(context.filesDir, "rootfs")
        val tempZip = File(context.cacheDir, "runtime.zip")
        
        try {
            // 1. Download the Runtime Core
            onProgress("Downloading PC Emulation Cores...", 0)
            val connection = URL(RUNTIME_URL).openConnection() as HttpURLConnection
            connection.connect()
            
            val fileLength = connection.contentLength
            val input = connection.inputStream
            val output = FileOutputStream(tempZip)
            
            val data = ByteArray(8192)
            var total: Long = 0
            var count: Int
            
            while (input.read(data).also { count = it } != -1) {
                total += count.toLong()
                if (fileLength > 0) {
                    val progress = (total * 100 / fileLength).toInt()
                    onProgress("Downloading... \${progress}%", progress)
                }
                output.write(data, 0, count)
            }
            output.flush()
            output.close()
            input.close()

            // 2. Extract the Core
            onProgress("Installing Emulator Cores...", 100)
            if (!rootfsDir.exists()) rootfsDir.mkdirs()
            
            tempZip.inputStream().use { fis ->
                ZipInputStream(fis).use { zis ->
                    var entry = zis.nextEntry
                    var extCount = 0
                    while (entry != null) {
                        val file = File(rootfsDir, entry.name)
                        if (entry.isDirectory) {
                            file.mkdirs()
                        } else {
                            file.parentFile?.mkdirs()
                            FileOutputStream(file).use { fos ->
                                zis.copyTo(fos)
                            }
                        }
                        extCount++
                        if (extCount % 100 == 0) {
                            onProgress("Installing components: $extCount", 100)
                        }
                        entry = zis.nextEntry
                    }
                }
            }

            // 3. Set Execute Permissions
            onProgress("Configuring permissions...", 100)
            val box64 = File(rootfsDir, "usr/local/bin/box64")
            val proot = File(rootfsDir, "usr/local/bin/proot")
            val wine = File(rootfsDir, "opt/wine/bin/wine")
            
            box64.setExecutable(true)
            proot.setExecutable(true)
            wine.setExecutable(true)

            tempZip.delete()
            return@withContext true
        } catch (e: Exception) {
            e.printStackTrace()
            tempZip.delete()
            return@withContext false
        }
    }
}
""",
    "app/src/main/java/com/smoothplay/app/ui/screens/SetupScreen.kt": """package com.smoothplay.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.smoothplay.app.engine.RuntimeInstallerEngine
import kotlinx.coroutines.launch

@Composable
fun SetupScreen(onSetupComplete: () -> Unit) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var statusText by remember { mutableStateOf("Initializing...") }
    var isInstalling by remember { mutableStateOf(false) }
    var hasError by remember { mutableStateOf(false) }

    val installer = remember { RuntimeInstallerEngine(context) }

    LaunchedEffect(Unit) {
        if (installer.isRuntimeInstalled()) {
            onSetupComplete()
        } else {
            statusText = "PC Emulator Cores Missing."
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("SmoothPlay First Setup", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        Text(statusText, color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(32.dp))

        if (isInstalling) {
            CircularProgressIndicator()
        } else if (hasError) {
            Button(onClick = {
                hasError = false
                isInstalling = true
                coroutineScope.launch {
                    val success = installer.downloadAndInstallRuntime { msg, _ -> statusText = msg }
                    isInstalling = false
                    if (success) {
                        onSetupComplete()
                    } else {
                        hasError = true
                        statusText = "Installation Failed. Check internet connection."
                    }
                }
            }) {
                Text("Retry Install")
            }
        } else {
            Button(
                onClick = {
                    isInstalling = true
                    coroutineScope.launch {
                        val success = installer.downloadAndInstallRuntime { msg, _ -> statusText = msg }
                        isInstalling = false
                        if (success) {
                            onSetupComplete()
                        } else {
                            hasError = true
                            statusText = "Installation Failed."
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                Text("Download & Install Core Files (~400MB)")
            }
        }
    }
}
""",
    "app/src/main/java/com/smoothplay/app/ui/navigation/AppNavigation.kt": """package com.smoothplay.app.ui.navigation

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
fun AppNavigation() {
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
                            icon = { Icon(icon, label) }, label = { Text(label) },
                            selected = currentRoute == route,
                            onClick = { navController.navigate(route) { popUpTo("home"); launchSingleTop = true } }
                        )
                    }
                }
            }
        ) { innerPadding ->
            NavHost(navController, startDestination = "home", modifier = Modifier.padding(innerPadding)) {
                composable("home") { HomeScreen() }
                composable("optimizer") { OptimizerScreen() }
                composable("controls") { ControlsScreen() }
                composable("settings") { SettingsScreen() }
            }
        }
    }
}
"""
}

for filepath, content in files.items():
    os.makedirs(os.path.dirname(filepath), exist_ok=True)
    with open(filepath, 'w', encoding='utf-8') as f:
        f.write(content.strip() + '\\n')

print("Runtime Downloader pipeline added successfully.")

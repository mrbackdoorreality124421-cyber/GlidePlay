import os

files = {
    "settings.gradle.kts": """pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}
rootProject.name = "SmoothPlay"
include(":app")
""",
    "build.gradle.kts": """plugins {
    id("com.android.application") version "8.2.2" apply false
    id("org.jetbrains.kotlin.android") version "1.9.22" apply false
    id("com.google.devtools.ksp") version "1.9.22-1.0.17" apply false
    id("com.google.dagger.hilt.android") version "2.50" apply false
}
""",
    "gradle.properties": """org.gradle.jvmargs=-Xmx2048m -Dfile.encoding=UTF-8
android.useAndroidX=true
android.nonTransitiveRClass=true
""",
    "app/build.gradle.kts": """plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
    id("dagger.hilt.android.plugin")
    id("kotlin-parcelize")
}

android {
    namespace = "com.smoothplay.app"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.smoothplay.app"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.8"
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.activity:activity-compose:1.8.2")
    
    implementation(platform("androidx.compose:compose-bom:2024.01.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.navigation:navigation-compose:2.7.7")
    implementation("androidx.compose.material:material-icons-extended")
    
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")
    
    implementation("com.google.dagger:hilt-android:2.50")
    ksp("com.google.dagger:hilt-android-compiler:2.50")
    implementation("androidx.hilt:hilt-navigation-compose:1.1.0")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
}
""",
    "app/src/main/AndroidManifest.xml": """<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools">

    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" android:maxSdkVersion="32"/>
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" android:maxSdkVersion="29"/>
    <uses-permission android:name="android.permission.MANAGE_EXTERNAL_STORAGE" tools:ignore="ScopedStorage" />
    <uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
    <uses-permission android:name="android.permission.WAKE_LOCK" />
    <uses-permission android:name="android.permission.VIBRATE" />

    <application
        android:name=".SmoothPlayApp"
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="SmoothPlay"
        android:requestLegacyExternalStorage="true"
        android:theme="@style/Theme.SmoothPlay">
        <activity
            android:name=".MainActivity"
            android:exported="true"
            android:configChanges="orientation|screenSize|keyboardHidden"
            android:screenOrientation="sensorLandscape">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
    </application>
</manifest>
""",
    "app/src/main/java/com/smoothplay/app/SmoothPlayApp.kt": """package com.smoothplay.app
import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class SmoothPlayApp : Application()
""",
    "app/src/main/java/com/smoothplay/app/MainActivity.kt": """package com.smoothplay.app
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.smoothplay.app.ui.navigation.AppNavigation
import com.smoothplay.app.ui.theme.SmoothPlayTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SmoothPlayTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }
}
""",
    "app/src/main/java/com/smoothplay/app/ui/theme/Theme.kt": """package com.smoothplay.app.ui.theme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF10B981),
    background = Color(0xFF09090B),
    surface = Color(0xFF18181B)
)

@Composable
fun SmoothPlayTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = DarkColorScheme, content = content)
}
""",
    "app/src/main/java/com/smoothplay/app/data/Game.kt": """package com.smoothplay.app.data
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "games")
data class Game(
    @PrimaryKey val id: String,
    val name: String,
    val installPath: String,
    val mainExecutable: String,
    val status: String,
    val profile: String,
    val weightScore: Int,
    val totalSizeMb: Long,
    val dependencies: String
)
""",
    "app/src/main/java/com/smoothplay/app/data/GameDao.kt": """package com.smoothplay.app.data
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface GameDao {
    @Query("SELECT * FROM games")
    fun getAllGames(): Flow<List<Game>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGame(game: Game)
}
""",
    "app/src/main/java/com/smoothplay/app/data/AppDatabase.kt": """package com.smoothplay.app.data
import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Game::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun gameDao(): GameDao
}
""",
    "app/src/main/java/com/smoothplay/app/di/DatabaseModule.kt": """package com.smoothplay.app.di
import android.content.Context
import androidx.room.Room
import com.smoothplay.app.data.AppDatabase
import com.smoothplay.app.data.GameDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, "smoothplay.db").build()
    }
    @Provides
    fun provideGameDao(database: AppDatabase): GameDao = database.gameDao()
}
""",
    "app/src/main/java/com/smoothplay/app/engine/OptimizationEngine.kt": """package com.smoothplay.app.engine
import android.content.Context
import android.os.Build
import android.os.PowerManager
import android.app.ActivityManager

object DeviceBenchmark {
    fun getScore(context: Context): Int {
        val cores = Runtime.getRuntime().availableProcessors()
        val actManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memInfo = ActivityManager.MemoryInfo().apply { actManager.getMemoryInfo(this) }
        val ramGb = memInfo.totalMem / (1024f * 1024f * 1024f)
        return ((cores * 5) + (ramGb * 3)).toInt().coerceIn(0, 100)
    }
}

class OptimizationEngine(context: Context) {
    private val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
    var currentThermalStatus = 0

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            powerManager.addThermalStatusListener { status -> currentThermalStatus = status }
        }
    }

    companion object {
        fun recommendProfile(deviceScore: Int, gameWeight: Int): String {
            val delta = deviceScore - gameWeight
            return when {
                delta >= 40 -> "Super Extreme"
                delta in 20..39 -> "Ultra"
                delta in 0..19 -> "Balance"
                delta in -20..-1 -> "Smooth"
                else -> "Super Smooth"
            }
        }
    }
}
""",
    "app/src/main/java/com/smoothplay/app/engine/GamePipelineEngine.kt": """package com.smoothplay.app.engine
import android.content.Context
import android.net.Uri
import com.smoothplay.app.data.Game
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.UUID
import java.util.zip.ZipInputStream

class GamePipelineEngine(private val context: Context) {
    suspend fun processZip(uri: Uri, destDir: File, onProgress: (String, Int) -> Unit): Game? = withContext(Dispatchers.IO) {
        val gameId = UUID.randomUUID().toString()
        val gameDir = File(destDir, gameId).apply { mkdirs() }
        try {
            onProgress("Extracting...", 0)
            context.contentResolver.openInputStream(uri)?.use { ips ->
                ZipInputStream(ips).use { zis ->
                    var entry = zis.nextEntry
                    var count = 0
                    val buffer = ByteArray(8192)
                    while (entry != null) {
                        val file = File(gameDir, entry.name)
                        if (entry.isDirectory) file.mkdirs() else {
                            file.parentFile?.mkdirs()
                            FileOutputStream(file).use { fos ->
                                var len: Int
                                while (zis.read(buffer).also { len = it } > 0) fos.write(buffer, 0, len)
                            }
                        }
                        count++
                        if (count % 10 == 0) onProgress("Extracted $count files", count)
                        entry = zis.nextEntry
                    }
                }
            }
            onProgress("Scanning...", 100)
            val allFiles = gameDir.walkTopDown().toList()
            val exes = allFiles.filter { it.extension.equals("exe", true) }
            val mainExe = exes.find { it.name.lowercase() in listOf("game.exe", "launcher.exe", "run.exe") } 
                ?: exes.maxByOrNull { it.length() } ?: throw Exception("No .exe found")

            val fileNames = allFiles.map { it.name.lowercase() }
            val deps = mutableListOf<String>()
            if (fileNames.any { "dxgi" in it || "d3dcompiler" in it }) deps.add("DirectX")
            
            val sizeMb = allFiles.sumOf { it.length() } / (1024 * 1024)
            val weight = (sizeMb / 500).toInt()
            val devScore = DeviceBenchmark.getScore(context)
            val profile = OptimizationEngine.recommendProfile(devScore, weight)

            return@withContext Game(
                id = gameId, name = uri.lastPathSegment ?: "Unknown", installPath = gameDir.absolutePath,
                mainExecutable = mainExe.absolutePath, status = "Ready", profile = profile,
                weightScore = weight, totalSizeMb = sizeMb, dependencies = deps.joinToString(",")
            )
        } catch (e: Exception) {
            gameDir.deleteRecursively()
            return@withContext null
        }
    }
}
""",
    "app/src/main/java/com/smoothplay/app/engine/RuntimeLauncher.kt": """package com.smoothplay.app.engine
import java.io.BufferedReader
import java.io.InputStreamReader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RuntimeLauncher {
    private var process: Process? = null

    suspend fun launchGame(gameDir: String, mainExe: String, envVars: Map<String, String>, onLog: (String) -> Unit) = withContext(Dispatchers.IO) {
        val command = mutableListOf("proot", "-b", "/dev", "-r", "/data/data/com.smoothplay.app/rootfs", "-w", gameDir, "/usr/local/bin/box64", "wine", mainExe)
        val pb = ProcessBuilder(command)
        pb.environment().putAll(envVars)
        pb.redirectErrorStream(true)
        try {
            process = pb.start()
            val reader = BufferedReader(InputStreamReader(process!!.inputStream))
            var line: String?
            while (reader.readLine().also { line = it } != null) { onLog(line ?: "") }
            process!!.waitFor()
        } catch (e: Exception) {
            onLog("CRASH: ${e.message}")
        }
    }
    fun stop() { process?.destroy() }
}
""",
    "app/src/main/java/com/smoothplay/app/engine/ControlsEngine.kt": """package com.smoothplay.app.engine
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput

data class VirtualButton(val id: String, val cx: Float, val cy: Float, val r: Float, val mappedKey: String)
data class VirtualJoystick(val id: String, val cx: Float, val cy: Float, val r: Float, val mappedKey: String)

@Composable
fun ControlsOverlay(activeProfile: String, onInput: (String, Boolean) -> Unit) {
    val buttons = listOf(VirtualButton("fire", 0.85f, 0.70f, 0.06f, "MOUSE_LEFT"), VirtualButton("jump", 0.90f, 0.50f, 0.05f, "SPACE"))
    val joystick = VirtualJoystick("move", 0.15f, 0.70f, 0.1f, "WASD")
    var joyThumbOffset by remember { mutableStateOf(Offset.Zero) }

    Canvas(modifier = Modifier.fillMaxSize().pointerInput(Unit) {
        detectTapGestures(onPress = { offset ->
            val w = size.width; val h = size.height
            buttons.forEach { b ->
                val bx = b.cx * w; val by = b.cy * h; val br = b.r * w
                if ((offset.x - bx)*(offset.x - bx) + (offset.y - by)*(offset.y - by) <= br*br) {
                    onInput(b.mappedKey, true)
                    tryAwaitRelease()
                    onInput(b.mappedKey, false)
                }
            }
        })
    }.pointerInput(Unit) {
        detectDragGestures(
            onDragStart = { },
            onDragEnd = { joyThumbOffset = Offset.Zero; onInput("WASD_RELEASE", false) },
            onDrag = { change, dragAmount -> 
                joyThumbOffset += dragAmount
                onInput("WASD_MOVE", true) 
            }
        )
    }) {
        val w = size.width; val h = size.height
        drawCircle(Color.White.copy(alpha = 0.2f), radius = joystick.r * w, center = Offset(joystick.cx * w, joystick.cy * h), style = Stroke(4f))
        drawCircle(Color.White.copy(alpha = 0.5f), radius = (joystick.r * w) * 0.4f, center = Offset(joystick.cx * w, joystick.cy * h) + joyThumbOffset)
        buttons.forEach { b -> drawCircle(Color.Green.copy(alpha = 0.3f), radius = b.r * w, center = Offset(b.cx * w, b.cy * h)) }
    }
}
""",
    "app/src/main/java/com/smoothplay/app/ui/viewmodels/HomeViewModel.kt": """package com.smoothplay.app.ui.viewmodels
import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smoothplay.app.data.GameDao
import com.smoothplay.app.engine.GamePipelineEngine
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val gameDao: GameDao,
    @ApplicationContext private val context: Context
) : ViewModel() {
    val games = gameDao.getAllGames()
    private val _statusMsg = MutableStateFlow("Library Ready")
    val statusMsg: StateFlow<String> = _statusMsg

    fun importZip(uri: Uri) {
        viewModelScope.launch {
            _statusMsg.value = "Processing ZIP..."
            val pipeline = GamePipelineEngine(context)
            val game = pipeline.processZip(uri, context.cacheDir) { msg, _ -> _statusMsg.value = msg }
            if (game != null) {
                gameDao.insertGame(game)
                _statusMsg.value = "Import Complete!"
            } else {
                _statusMsg.value = "Import Failed."
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
""",
    "app/src/main/java/com/smoothplay/app/ui/screens/HomeScreen.kt": """package com.smoothplay.app.ui.screens
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
}
""",
    "app/src/main/java/com/smoothplay/app/ui/screens/OptimizerScreen.kt": """package com.smoothplay.app.ui.screens
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
}
""",
    "app/src/main/java/com/smoothplay/app/ui/screens/ControlsScreen.kt": """package com.smoothplay.app.ui.screens
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
}
""",
    "app/src/main/java/com/smoothplay/app/ui/screens/SettingsScreen.kt": """package com.smoothplay.app.ui.screens
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SettingsScreen() {
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Settings", style = MaterialTheme.typography.headlineMedium)
        Row(Modifier.fillMaxWidth().padding(vertical = 16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Thermal Protection")
            Switch(checked = true, onCheckedChange = {})
        }
    }
}
"""
}

for filepath, content in files.items():
    dir = os.path.dirname(filepath)
    if dir:
        os.makedirs(dir, exist_ok=True)
    with open(filepath, 'w', encoding='utf-8') as f:
        f.write(content.strip() + '\\n')

print("All Android files generated.")

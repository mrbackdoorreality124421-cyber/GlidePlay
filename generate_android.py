import os

files = {
    ".github/workflows/android.yml": """name: Android CI
on:
  push:
    branches: [ "main" ]
  pull_request:
    branches: [ "main" ]
jobs:
  build:
    runs-on: ubuntu-latest
    steps:
    - uses: actions/checkout@v4
    - name: set up JDK 17
      uses: actions/setup-java@v4
      with:
        java-version: '17'
        distribution: 'temurin'
    - name: Setup Gradle
      uses: gradle/actions/setup-gradle@v3
      with:
        gradle-version: '8.7'
    - name: Build APK
      run: gradle build assembleDebug
    - name: Upload APK
      uses: actions/upload-artifact@v4
      with:
        name: app-debug
        path: app/build/outputs/apk/debug/app-debug.apk
""",
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
    "gradle.properties": """org.gradle.jvmargs=-Xmx2048m -Dfile.encoding=UTF-8
android.useAndroidX=true
android.nonTransitiveRClass=true
""",
    "build.gradle.kts": """plugins {
    id("com.android.application") version "8.2.2" apply false
    id("org.jetbrains.kotlin.android") version "1.9.22" apply false
    id("com.google.devtools.ksp") version "1.9.22-1.0.17" apply false
    id("com.google.dagger.hilt.android") version "2.50" apply false
}
""",
    "app/build.gradle.kts": """plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
    id("dagger.hilt.android.plugin")
}

android {
    namespace = "com.smoothplay.app"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.smoothplay.app"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
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

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
}
""",
    "app/src/main/AndroidManifest.xml": """<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools">

    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" android:maxSdkVersion="32"/>
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" android:maxSdkVersion="29"/>
    <uses-permission android:name="android.permission.MANAGE_EXTERNAL_STORAGE" tools:ignore="ScopedStorage" />
    <uses-permission android:name="android.permission.VIBRATE" />
    <uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
    <uses-permission android:name="android.permission.WAKE_LOCK" />
    <uses-permission android:name="android.permission.BLUETOOTH_CONNECT" />

    <application
        android:name=".SmoothPlayApp"
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:supportsRtl="true"
        android:requestLegacyExternalStorage="true"
        android:theme="@style/Theme.SmoothPlay">
        <activity
            android:name=".MainActivity"
            android:exported="true"
            android:screenOrientation="sensorLandscape"
            android:theme="@style/Theme.SmoothPlay">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
    </application>
</manifest>
""",
    "app/src/main/res/values/strings.xml": """<resources>
    <string name="app_name">SmoothPlay</string>
</resources>
""",
    "app/src/main/res/values/themes.xml": """<?xml version="1.0" encoding="utf-8"?>
<resources>
    <style name="Theme.SmoothPlay" parent="android:Theme.Material.Light.NoActionBar" />
</resources>
""",
    "app/src/main/java/com/smoothplay/app/SmoothPlayApp.kt": """package com.smoothplay.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class SmoothPlayApp : Application()
""",
    "app/src/main/java/com/smoothplay/app/ui/theme/Theme.kt": """package com.smoothplay.app.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF10B981),
    secondary = Color(0xFF047857),
    background = Color(0xFF09090B),
    surface = Color(0xFF18181B),
    onPrimary = Color(0xFF052E16),
    onBackground = Color(0xFFF4F4F5),
    onSurface = Color(0xFFF4F4F5)
)

@Composable
fun SmoothPlayTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        content = content
    )
}
""",
    "app/src/main/java/com/smoothplay/app/models/Game.kt": """package com.smoothplay.app.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "games")
data class Game(
    @PrimaryKey val id: String,
    val name: String,
    val status: String,
    val profile: String,
    val weight: String,
    val size: String,
    val installPath: String
)
""",
    "app/src/main/java/com/smoothplay/app/data/GameDao.kt": """package com.smoothplay.app.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.smoothplay.app.models.Game
import kotlinx.coroutines.flow.Flow

@Dao
interface GameDao {
    @Query("SELECT * FROM games")
    fun getAllGames(): Flow<List<Game>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGame(game: Game)

    @Query("DELETE FROM games WHERE id = :gameId")
    suspend fun deleteGame(gameId: String)
}
""",
    "app/src/main/java/com/smoothplay/app/data/AppDatabase.kt": """package com.smoothplay.app.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.smoothplay.app.models.Game

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
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "smoothplay.db"
        ).build()
    }

    @Provides
    fun provideGameDao(database: AppDatabase): GameDao {
        return database.gameDao()
    }
}
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
    "app/src/main/java/com/smoothplay/app/ui/navigation/AppNavigation.kt": """package com.smoothplay.app.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Games
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.smoothplay.app.ui.screens.ControlsScreen
import com.smoothplay.app.ui.screens.HomeScreen
import com.smoothplay.app.ui.screens.OptimizerScreen
import com.smoothplay.app.ui.screens.SettingsScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            NavigationBar {
                val items = listOf(
                    Triple("home", "Library", Icons.Default.Games),
                    Triple("optimizer", "Optimizer", Icons.Default.Speed),
                    Triple("controls", "Controls", Icons.Default.Build),
                    Triple("settings", "Settings", Icons.Default.Settings)
                )
                items.forEach { (route, label, icon) ->
                    NavigationBarItem(
                        icon = { Icon(icon, contentDescription = label) },
                        label = { Text(label) },
                        selected = currentRoute == route,
                        onClick = {
                            navController.navigate(route) {
                                popUpTo("home") { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("home") { HomeScreen() }
            composable("optimizer") { OptimizerScreen() }
            composable("controls") { ControlsScreen() }
            composable("settings") { SettingsScreen() }
        }
    }
}
""",
    "app/src/main/java/com/smoothplay/app/ui/screens/HomeScreen.kt": """package com.smoothplay.app.ui.screens

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
""",
    "app/src/main/java/com/smoothplay/app/ui/viewmodels/HomeViewModel.kt": """package com.smoothplay.app.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smoothplay.app.data.GameDao
import com.smoothplay.app.models.Game
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val gameDao: GameDao
) : ViewModel() {
    val games = gameDao.getAllGames()

    fun importMockGame() {
        viewModelScope.launch {
            val newGame = Game(
                id = UUID.randomUUID().toString(),
                name = "PC Game Imported",
                status = "Ready",
                profile = "Balance",
                weight = "Medium",
                size = "1.5 GB",
                installPath = "/data/user/0/com.smoothplay.app/games"
            )
            gameDao.insertGame(newGame)
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
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Optimizer", style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(16.dp))
        Card(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Device Score: 85/100", style = MaterialTheme.typography.titleLarge)
                Text("Flagship Tier - Recommended: Ultra Settings")
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text("Hardware Status", style = MaterialTheme.typography.titleMedium)
        Text("CPU: Snapdragon 8 Gen 2")
        Text("GPU: Adreno 740 (Vulkan 1.3)")
        Text("RAM: 12GB Total")
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
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Controls Editor", style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(16.dp))
        Text("Active Template: FPS")
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { }) {
            Text("Edit Layout")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = { }) {
            Text("Adjust Haptics & Gyro")
        }
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
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Settings", style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(16.dp))
        Text("System & Performance", style = MaterialTheme.typography.titleMedium)
        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Auto Optimize Games")
            Switch(checked = true, onCheckedChange = {})
        }
        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Thermal Protection")
            Switch(checked = true, onCheckedChange = {})
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { }) {
            Text("Clear Shader Cache")
        }
    }
}
""",
    "app/src/main/java/com/smoothplay/app/engine/RuntimeLauncher.kt": """package com.smoothplay.app.engine

// This is the abstraction layer that would interface with Box64, Wine, VirGL, Turnip.
interface RuntimeLauncher {
    fun prepareGameContainer(gameId: String, installPath: String)
    fun generateConfiguration(profile: String, dxvkEnabled: Boolean, turnipEnabled: Boolean)
    fun launch(executablePath: String)
    fun stop()
}

class SmoothPlayRuntimeLauncher : RuntimeLauncher {
    override fun prepareGameContainer(gameId: String, installPath: String) {
        // Implementation for setting up proot/chroot environment
    }

    override fun generateConfiguration(profile: String, dxvkEnabled: Boolean, turnipEnabled: Boolean) {
        // Generate box64rc and wine registry patches
    }

    override fun launch(executablePath: String) {
        // Execute box64 wine game.exe
    }

    override fun stop() {
        // Kill processes
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

print("Android project files generated successfully.")

export interface CodeBlock {
  language: string;
  code: string;
  title?: string;
}

export interface Section {
  id: number;
  title: string;
  description: string;
  content?: string;
  codeBlocks?: CodeBlock[];
}

export const architectureSections: Section[] = [
  {
    id: 1,
    title: "1. Project Overview",
    description: "Core concept, goals, and UX priorities for SmoothPlay.",
    content: `**Goal:** SmoothPlay is a next-generation Android application designed to bridge the gap between PC games and mobile hardware. It automates the extraction, configuration, and execution of Windows/PC games via compatibility layers, delivering a one-click "console-like" experience on Android devices.

**Absolute Priorities:**
1. **Performance First:** Aggressive background optimization, dynamic resolution scaling, and thermal management.
2. **Native Mobile Feel:** Advanced touch controls that mimic native Android games, complete with auto-mapping and responsive haptics.
3. **Frictionless UI:** A clean, zero-configuration interface designed for gamers, not engineers.

**Target Environment:**
- Minimum SDK: 26 (Android 8.0) / Target SDK: 34 (Android 14)
- Stack: Kotlin, Jetpack Compose, Material 3, Coroutines/Flow, Room Database.
- Runtime integrations: Wine, Box64, DXVK, Turnip, VirGL (via abstraction layer).
- Offline-first & Privacy-focused.`
  },
  {
    id: 2,
    title: "2. Technical Architecture",
    description: "High-level system design and integration layers.",
    content: `The architecture follows **Clean Architecture** principles combined with **MVVM** for the presentation layer.

**Core Layers:**
- **Presentation Layer (Compose):** Stateless UI components driven by ViewModels emitting UI States (StateFlow).
- **Domain Layer:** Use cases for complex logic (e.g., \`AnalyzeGameWeightUseCase\`, \`RecommendProfileUseCase\`).
- **Data Layer:** 
  - \`Room\` for local persistence (Games, Profiles, Layouts).
  - \`Storage Access Framework (SAF)\` for scoped storage ZIP extraction.
- **Engine Layer (The Core):**
  - **Import Engine:** Heuristic analysis of extracted files to identify the main executable and dependencies.
  - **Optimization Engine:** Interfaces with Android's \`PerformanceHintManager\` and \`HardwarePropertiesManager\`.
  - **Runtime Abstraction:** A wrapper interface (\`RuntimeLauncher\`) that translates SmoothPlay profiles into container-specific configurations (e.g., generating Wine registry keys or Box64 env vars).`
  },
  {
    id: 3,
    title: "3. Complete File Tree",
    description: "Proposed Android project structure.",
    codeBlocks: [
      {
        language: "text",
        title: "Android Project Structure",
        code: `com.smoothplay.app
├── application/
│   └── SmoothPlayApp.kt                 // App class, DI initialization
├── core/
│   ├── hardware/                        // Device profiling, thermals
│   ├── runtime/                         // Runtime abstraction (Wine/Box64 wrapper interfaces)
│   └── utils/                           // SAF, Zip extraction extensions
├── data/
│   ├── database/                        // Room DB, DAOs
│   ├── models/                          // Entities
│   └── repository/                      // Repository implementations
├── domain/
│   ├── models/                          // Business models (GameEntry, Profiles)
│   └── usecases/                        // ImportGame, OptimizeGame use cases
├── engines/
│   ├── controls/                        // Touch layout processing, Gamepad mapping
│   ├── import/                          // Zip extraction, executable detection
│   └── optimization/                    // Dynamic scaling, hint manager
└── presentation/
    ├── controls/                        // Controls Editor UI
    ├── detail/                          // Game Detail Screen
    ├── home/                            // Add Game, Library UI
    ├── optimization/                    // Benchmark, Profiles UI
    └── theme/                           // Material 3 Colors, Typography`
      }
    ]
  },
  {
    id: 4,
    title: "4. Kotlin Code Skeleton",
    description: "Core data models and domain entities.",
    codeBlocks: [
      {
        language: "kotlin",
        title: "DomainModels.kt",
        code: `package com.smoothplay.app.domain.models

import java.util.UUID

data class GameEntry(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val installPath: String,
    val mainExecutable: String,
    val status: GameStatus,
    val weightScore: Int,
    val recommendedProfile: PerformanceProfile,
    val controlsTemplateId: String
)

enum class GameStatus {
    READY, NEEDS_SETUP, HEAVY, EXPERIMENTAL, UNSUPPORTED
}

enum class PerformanceProfile(val displayName: String) {
    SUPER_SMOOTH("Super Smooth"),
    SMOOTH("Smooth"),
    BALANCE("Balance"),
    HIGH("High"),
    ULTRA("Ultra"),
    EXTREME("Extreme"),
    SUPER_EXTREME("Super Extreme")
}

data class DeviceProfile(
    val cpuScore: Int,
    val gpuScore: Int,
    val totalRamMb: Long,
    val renderer: String,
    val supportsVulkan: Boolean,
    val overallScore: Int // 0 - 100
)

data class ControlButton(
    val id: String,
    val actionId: String,
    val x: Float,
    val y: Float,
    val radius: Float,
    val opacity: Float,
    val mappedKey: Int?
)`
      }
    ]
  },
  {
    id: 5,
    title: "5. Jetpack Compose UI Code",
    description: "Frictionless, gamer-friendly interfaces.",
    codeBlocks: [
      {
        language: "kotlin",
        title: "HomeScreen.kt",
        code: `package com.smoothplay.app.presentation.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen(
    onAddZipClicked: () -> Unit,
    gameLibrary: List<GameEntry>,
    onGameClicked: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Massive Add Button
        Button(
            onClick = onAddZipClicked,
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            shape = MaterialTheme.shapes.extraLarge,
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text("Add Game ZIP", style = MaterialTheme.typography.headlineMedium)
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Game Library List
        Text(
            "Your Library", 
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.align(Alignment.Start)
        )
        
        // Render cards... (LazyColumn)
    }
}`
      }
    ]
  },
  {
    id: 6,
    title: "6. Import Engine Logic",
    description: "Automatic ZIP extraction and heuristic dependency detection.",
    codeBlocks: [
      {
        language: "kotlin",
        title: "ImportEngine.kt",
        code: `package com.smoothplay.app.engines.import

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class ImportEngine {
    
    suspend fun analyzeExtractedGame(gameDir: File): AnalysisResult = withContext(Dispatchers.IO) {
        val allFiles = gameDir.walkTopDown().toList()
        val executables = allFiles.filter { it.extension.equals("exe", ignoreCase = true) }
        
        val mainExe = detectMainExecutable(executables)
        val dependencies = detectDependencies(allFiles)
        val weight = estimateWeight(allFiles)
        val genre = inferGenre(allFiles, mainExe)
        
        AnalysisResult(mainExe, dependencies, weight, genre)
    }
    
    private fun detectMainExecutable(exes: List<File>): File? {
        // Priority: game.exe > launcher.exe > setup.exe > largest .exe
        val exactMatches = listOf("game.exe", "launcher.exe", "run.exe")
        exes.find { exactMatches.contains(it.name.lowercase()) }?.let { return it }
        return exes.maxByOrNull { it.length() }
    }
    
    private fun detectDependencies(files: List<File>): List<String> {
        val deps = mutableListOf<String>()
        val fileNames = files.map { it.name.lowercase() }
        
        if (fileNames.any { it.contains("d3dcompiler") || it.contains("dxgi") }) deps.add("DirectX")
        if (fileNames.any { it.contains("vulkan") }) deps.add("Vulkan")
        if (fileNames.any { it.contains("msvcp") }) deps.add("VC++ Redist")
        if (fileNames.any { it.contains("physx") }) deps.add("PhysX")
        
        return deps
    }
}`
      }
    ]
  },
  {
    id: 7,
    title: "7. Optimization Engine Logic",
    description: "Android system integration for peak game performance.",
    codeBlocks: [
      {
        language: "kotlin",
        title: "OptimizationEngine.kt",
        code: `package com.smoothplay.app.engines.optimization

import android.os.PerformanceHintManager
import android.os.Build

class OptimizationEngine(private val hintManager: PerformanceHintManager?) {

    private var gameSession: PerformanceHintManager.Session? = null

    fun beginGameSession(threadIds: IntArray) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // Target 60 FPS frame duration (16.6ms)
            val targetDurationNanos = 16_666_666L 
            gameSession = hintManager?.createHintSession(threadIds, targetDurationNanos)
        }
    }

    fun reportActualFrameTime(actualDurationNanos: Long) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            gameSession?.reportActualWorkDuration(actualDurationNanos)
        }
    }

    fun handleThermalThrottling(thermalStatus: Int) {
        // Values map to PowerManager.THERMAL_STATUS_*
        when (thermalStatus) {
            android.os.PowerManager.THERMAL_STATUS_SEVERE -> {
                // Trigger dynamic resolution drop via Runtime Abstraction
                reduceResolutionScale(0.75f)
                capFramerate(30)
            }
            android.os.PowerManager.THERMAL_STATUS_CRITICAL -> {
                // Extremely aggressive throttling protection
                reduceResolutionScale(0.5f)
                disablePostProcessing()
            }
        }
    }
    
    private fun reduceResolutionScale(scale: Float) { /* Call to Runtime Abstraction */ }
    private fun capFramerate(fps: Int) { /* Call to Runtime Abstraction */ }
    private fun disablePostProcessing() { /* Call to Runtime Abstraction */ }
}`
      }
    ]
  }
];

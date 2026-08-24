import os

files = {
    "app/src/main/java/com/smoothplay/app/engine/ZipImportManager.kt": """package com.smoothplay.app.engine

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class ZipImportManager(private val context: Context) {
    suspend fun importAndExtract(zipUri: Uri, destDir: File, onProgress: (Int) -> Unit): Boolean = withContext(Dispatchers.IO) {
        try {
            val contentResolver = context.contentResolver
            val inputStream = contentResolver.openInputStream(zipUri) ?: return@withContext false
            GameExtractor.extractZip(inputStream, destDir, onProgress)
            return@withContext true
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext false
        }
    }
}
""",
    "app/src/main/java/com/smoothplay/app/engine/GameExtractor.kt": """package com.smoothplay.app.engine

import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.zip.ZipInputStream

object GameExtractor {
    fun extractZip(inputStream: InputStream, destDir: File, onProgress: (Int) -> Unit) {
        if (!destDir.exists()) destDir.mkdirs()
        ZipInputStream(inputStream).use { zis ->
            var zipEntry = zis.nextEntry
            var extractedCount = 0
            while (zipEntry != null) {
                val newFile = File(destDir, zipEntry.name)
                if (zipEntry.isDirectory) {
                    newFile.mkdirs()
                } else {
                    newFile.parentFile?.mkdirs()
                    FileOutputStream(newFile).use { fos ->
                        zis.copyTo(fos)
                    }
                }
                extractedCount++
                if (extractedCount % 10 == 0) onProgress(extractedCount)
                zipEntry = zis.nextEntry
            }
            zis.closeEntry()
        }
    }
}
""",
    "app/src/main/java/com/smoothplay/app/engine/GameScanner.kt": """package com.smoothplay.app.engine

import java.io.File

data class ScanResult(
    val mainExecutable: String,
    val isHeavy: Boolean,
    val dependencies: List<String>,
    val weightScore: Int,
    val totalSizeMb: Long
)

class GameScanner {
    fun scanDirectory(gameDir: File): ScanResult {
        val allFiles = gameDir.walkTopDown().toList()
        val executables = allFiles.filter { it.extension.equals("exe", ignoreCase = true) }
        
        val mainExe = detectMainExecutable(executables)
        val deps = detectDependencies(allFiles)
        val weight = calculateWeight(allFiles)
        val sizeMb = allFiles.sumOf { it.length() } / (1024 * 1024)
        
        return ScanResult(
            mainExecutable = mainExe?.absolutePath ?: "",
            isHeavy = weight > 50,
            dependencies = deps,
            weightScore = weight,
            totalSizeMb = sizeMb
        )
    }

    private fun detectMainExecutable(exes: List<File>): File? {
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
        return deps
    }

    private fun calculateWeight(files: List<File>): Int {
        var score = 0
        val sizeMb = files.sumOf { it.length() } / (1024 * 1024)
        score += (sizeMb / 500).toInt()
        val dllCount = files.count { it.extension.lowercase() == "dll" }
        score += (dllCount / 10)
        return score.coerceIn(0, 100)
    }
}
""",
    "app/src/main/java/com/smoothplay/app/engine/DeviceBenchmark.kt": """package com.smoothplay.app.engine

import android.app.ActivityManager
import android.content.Context
import android.content.pm.PackageManager

class DeviceBenchmark(private val context: Context) {
    fun calculateDeviceScore(): Int {
        val cores = Runtime.getRuntime().availableProcessors()
        val ramGb = getTotalRamGb()
        val hasVulkan = context.packageManager.hasSystemFeature(PackageManager.FEATURE_VULKAN_HARDWARE_LEVEL)
        
        var score = 0
        score += cores * 5
        score += (ramGb * 2).toInt()
        if (hasVulkan) score += 20
        
        return score.coerceIn(0, 100)
    }

    private fun getTotalRamGb(): Float {
        val actManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memInfo = ActivityManager.MemoryInfo()
        actManager.getMemoryInfo(memInfo)
        return memInfo.totalMem / (1024f * 1024f * 1024f)
    }
}
""",
    "app/src/main/java/com/smoothplay/app/engine/ProfileRecommender.kt": """package com.smoothplay.app.engine

object ProfileRecommender {
    val PROFILES = listOf(
        "Super Smooth", "Smooth", "Balance", "High", "Ultra", "Extreme", "Super Extreme"
    )

    fun recommendProfile(deviceScore: Int, gameWeight: Int): String {
        val capabilityDelta = deviceScore - gameWeight
        return when {
            capabilityDelta >= 50 -> "Super Extreme"
            capabilityDelta in 30..49 -> "Extreme"
            capabilityDelta in 10..29 -> "Ultra"
            capabilityDelta in -10..9 -> "Balance"
            capabilityDelta in -30..-11 -> "Smooth"
            else -> "Super Smooth"
        }
    }
}
""",
    "app/src/main/assets/control_template_fps.json": """{
  "id": "fps_standard",
  "name": "Standard FPS",
  "joysticks": [
    { "id": "move", "cx": 0.15, "cy": 0.75, "radius": 0.1, "mappedKey": "WASD" }
  ],
  "swipeZones": [
    { "id": "look", "x": 0.5, "y": 0, "w": 0.5, "h": 1.0, "mappedAxis": "MOUSE_XY", "sensitivity": 1.2 }
  ],
  "buttons": [
    { "id": "fire", "x": 0.85, "y": 0.75, "r": 0.05, "mappedKey": "MOUSE_LEFT" },
    { "id": "aim", "x": 0.75, "y": 0.85, "r": 0.04, "mappedKey": "MOUSE_RIGHT" },
    { "id": "jump", "x": 0.9, "y": 0.6, "r": 0.04, "mappedKey": "SPACE" }
  ]
}
""",
    "app/src/main/assets/optimization_profile.json": """{
  "Super Smooth": { "resolutionScale": 0.5, "fpsCap": 30, "dxvkAsync": true, "turnip": false },
  "Balance": { "resolutionScale": 0.75, "fpsCap": 60, "dxvkAsync": true, "turnip": true },
  "Ultra": { "resolutionScale": 1.0, "fpsCap": 0, "dxvkAsync": true, "turnip": true }
}
"""
}

for filepath, content in files.items():
    dir = os.path.dirname(filepath)
    if dir:
        os.makedirs(dir, exist_ok=True)
    with open(filepath, 'w', encoding='utf-8') as f:
        f.write(content.strip() + '\\n')

print("Missing Kotlin engines generated.")

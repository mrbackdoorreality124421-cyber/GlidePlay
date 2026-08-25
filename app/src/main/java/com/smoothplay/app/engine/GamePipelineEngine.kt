package com.smoothplay.app.engine

import android.content.Context
import android.net.Uri
import android.util.Log
import com.smoothplay.app.data.Game
import com.smoothplay.app.engine.GameScanner.GameDetection
import com.smoothplay.app.engine.GameScanner.GameType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.UUID
import java.util.zip.ZipException
import java.util.zip.ZipInputStream

class GamePipelineEngine(private val context: Context) {
    
    companion object {
        private const val TAG = "GamePipeline"
    }
    
    suspend fun processGame(
        uri: Uri,
        detection: GameDetection,
        destDir: File,
        onProgress: (String, Int) -> Unit
    ): Game? = withContext(Dispatchers.IO) {
        val gameId = UUID.randomUUID().toString()
        val gameDir = File(destDir, gameId).apply { mkdirs() }
        
        try {
            onProgress("Preparing ${detection.type.displayName}...", 0)
            
            when (detection.type) {
                GameType.COMPRESSED -> {
                    onProgress("Extracting...", 0)
                    context.contentResolver.openInputStream(uri)?.use { inputStream ->
                        ZipInputStream(inputStream).use { zis ->
                            var entry = zis.nextEntry
                            var count = 0
                            var lastProgress = 0
                            
                            while (entry != null) {
                                val file = File(gameDir, entry.name)
                                val canonicalPath = file.canonicalPath
                                if (!canonicalPath.startsWith(gameDir.canonicalPath + File.separator)) {
                                    Log.w(TAG, "Skipping malicious entry: ${entry.name}")
                                    entry = zis.nextEntry
                                    continue
                                }
                                
                                if (entry.isDirectory) {
                                    file.mkdirs()
                                } else {
                                    file.parentFile?.mkdirs()
                                    FileOutputStream(file).use { fos ->
                                        zis.copyTo(fos)
                                    }
                                }
                                
                                count++
                                if (count % 10 == 0) {
                                    val progress = (count * 10) % 100
                                    if (progress != lastProgress) {
                                        onProgress("Extracted $count files", progress)
                                        lastProgress = progress
                                    }
                                }
                                
                                try {
                                    entry = zis.nextEntry
                                } catch (e: ZipException) {
                                    Log.w(TAG, "Corrupted entry, continuing: ${e.message}")
                                    break
                                }
                            }
                        }
                    }
                }
                
                GameType.WINDOWS_EXE, 
                GameType.DISC_IMAGE -> {
                    onProgress("Copying game file...", 0)
                    context.contentResolver.openInputStream(uri)?.use { input ->
                        val fileName = uri.lastPathSegment ?: "game.${detection.type.name.lowercase()}"
                        val destFile = File(gameDir, fileName)
                        FileOutputStream(destFile).use { output ->
                            input.copyTo(output)
                        }
                    }
                }
                
                GameType.EXTRACTED,
                GameType.UNKNOWN -> {
                    Log.w(TAG, "Unsupported or unknown format")
                    return@withContext null
                }
            }
            
            onProgress("Scanning for executable...", 90)
            val allFiles = gameDir.walkTopDown().toList()
            
            val mainExe = findMainExecutable(allFiles)
                ?: throw Exception("No executable found in game files")
            
            val fileNames = allFiles.map { it.name.lowercase() }
            val deps = mutableListOf<String>()
            if (fileNames.any { "dxgi" in it || "d3dcompiler" in it || "d3d11" in it || "d3d12" in it }) {
                deps.add("DirectX")
            }
            if (fileNames.any { "vcruntime" in it || "msvcp" in it }) {
                deps.add("Visual C++ Runtime")
            }
            if (fileNames.any { "steam" in it && "steam_api" in it }) {
                deps.add("Steam")
            }
            
            val sizeMb = allFiles.sumOf { it.length() } / (1024 * 1024)
            val weight = (sizeMb / 500).toInt()
            val devScore = DeviceBenchmark.getScore(context)
            val profile = OptimizationEngine.recommendProfile(devScore, weight)
            
            onProgress("Import complete!", 100)
            
            return@withContext Game(
                id = gameId,
                name = uri.lastPathSegment?.substringBeforeLast('.') ?: "Unknown Game",
                installPath = gameDir.absolutePath,
                mainExecutable = mainExe.absolutePath,
                status = "Ready",
                profile = profile,
                weightScore = weight,
                totalSizeMb = sizeMb,
                dependencies = deps.joinToString(",")
            )
            
        } catch (e: Exception) {
            Log.e(TAG, "Processing failed: ${e.message}", e)
            gameDir.deleteRecursively()
            return@withContext null
        }
    }
    
    private fun findMainExecutable(files: List<File>): File? {
        val exes = files.filter { it.extension.equals("exe", ignoreCase = true) }
        
        if (exes.isEmpty()) return null
        
        val launcherNames = listOf("game.exe", "launcher.exe", "run.exe", "start.exe", "play.exe")
        val launcher = exes.find { it.name.lowercase() in launcherNames }
        if (launcher != null) return launcher
        
        return exes.maxByOrNull { it.length() }
    }
    
    suspend fun launchGame(game: Game): Boolean = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Launching game: ${game.name}")
            Log.d(TAG, "Executable: ${game.mainExecutable}")
            
            val exeFile = File(game.mainExecutable)
            if (!exeFile.exists()) {
                Log.e(TAG, "Executable not found: ${game.mainExecutable}")
                return@withContext false
            }
            
            Log.d(TAG, "Game launched successfully")
            return@withContext true
            
        } catch (e: Exception) {
            Log.e(TAG, "Launch failed: ${e.message}", e)
            return@withContext false
        }
    }
}

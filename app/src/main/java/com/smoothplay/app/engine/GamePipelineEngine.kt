package com.smoothplay.app.engine

import android.content.Context
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import com.smoothplay.app.data.Game
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

class GamePipelineEngine(private val context: Context) {
    class PipelineException(message: String, cause: Throwable? = null) : Exception(message, cause)
    
    suspend fun processFolder(uri: Uri, destDir: File, onProgress: (String, Int) -> Unit): Game? = withContext(Dispatchers.IO) {
        val gameId = UUID.randomUUID().toString()
        val gameDir = File(destDir, gameId).apply { mkdirs() }
        
        try {
            onProgress("Scanning folder...", 5)
            val docFile = DocumentFile.fromTreeUri(context, uri) ?: throw PipelineException("Cannot access folder")
            
            var copiedCount = 0
            
            suspend fun copyFolder(source: DocumentFile, dest: File) {
                if (!dest.exists()) dest.mkdirs()
                source.listFiles().forEach { child ->
                    val newFile = File(dest, child.name ?: "unnamed")
                    if (child.isDirectory) {
                        copyFolder(child, newFile)
                    } else {
                        context.contentResolver.openInputStream(child.uri)?.use { ips ->
                            FileOutputStream(newFile).use { fos ->
                                ips.copyTo(fos)
                            }
                        }
                        copiedCount++
                        if (copiedCount % 10 == 0) onProgress("Copied $copiedCount files", 5 + copiedCount.coerceAtMost(50))
                    }
                }
            }
            
            copyFolder(docFile, gameDir)
            
            onProgress("Analyzing files...", 60)
            val scanner = GameScanner()
            val scanResult = scanner.scanDirectory(gameDir)
            
            if (scanResult.mainExecutable.isEmpty()) {
                throw PipelineException("No executable (.exe) found in folder.")
            }
            
            onProgress("Analyzing performance...", 85)
            val devScore = DeviceBenchmark.getScore(context)
            val profile = OptimizationEngine.recommendProfile(devScore, scanResult.weightScore)
            
            onProgress("Ready!", 100)
            return@withContext Game(
                id = gameId,
                name = docFile.name ?: "Unknown Game",
                installPath = gameDir.absolutePath,
                mainExecutable = scanResult.mainExecutable,
                status = "Ready",
                profile = profile,
                weightScore = scanResult.weightScore,
                totalSizeMb = scanResult.totalSizeMb,
                dependencies = scanResult.dependencies.joinToString(",")
            )
        } catch (e: Throwable) {
            gameDir.deleteRecursively()
            onProgress("Error: ${e.message}", 0)
            return@withContext null
        }
    }

    suspend fun processZip(uri: Uri, destDir: File, onProgress: (String, Int) -> Unit): Game? = withContext(Dispatchers.IO) {
        val gameId = UUID.randomUUID().toString()
        val gameDir = File(destDir, gameId).apply { mkdirs() }
        
        try {
            onProgress("Extracting...", 5)
            val inputStream = context.contentResolver.openInputStream(uri) 
                ?: throw PipelineException("Cannot open ZIP stream")
            
            val filesExtracted = inputStream.use { ips ->
                GameExtractor.extractZip(ips, gameDir) { count ->
                    onProgress("Extracted $count files", 5 + count.coerceAtMost(50))
                }
            }
            
            onProgress("Scanning $filesExtracted files...", 60)
            val scanner = GameScanner()
            val scanResult = scanner.scanDirectory(gameDir)
            
            if (scanResult.mainExecutable.isEmpty()) {
                throw PipelineException("No executable (.exe) found in ZIP.")
            }
            
            onProgress("Analyzing performance...", 85)
            val devScore = DeviceBenchmark.getScore(context)
            val profile = OptimizationEngine.recommendProfile(devScore, scanResult.weightScore)
            
            onProgress("Ready!", 100)
            return@withContext Game(
                id = gameId,
                name = uri.lastPathSegment?.removeSuffix(".zip")?.replace("_", " ") ?: "Unknown Game",
                installPath = gameDir.absolutePath,
                mainExecutable = scanResult.mainExecutable,
                status = "Ready",
                profile = profile,
                weightScore = scanResult.weightScore,
                totalSizeMb = scanResult.totalSizeMb,
                dependencies = scanResult.dependencies.joinToString(",")
            )
        } catch (e: Throwable) {
            gameDir.deleteRecursively()
            onProgress("Error: ${e.message}", 0)
            return@withContext null
        }
    }
}

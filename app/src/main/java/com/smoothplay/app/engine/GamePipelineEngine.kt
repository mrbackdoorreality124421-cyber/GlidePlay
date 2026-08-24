package com.smoothplay.app.engine
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
}\n
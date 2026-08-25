package com.smoothplay.app.engine

import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipException
import java.util.zip.ZipInputStream

object GameExtractor {
    private const val TAG = "GameExtractor"
    private const val MAX_FILE_SIZE = 10L * 1024 * 1024 * 1024
    private const val MAX_FILES = 100000

    @Throws(IOException::class)
    fun extractZip(
        inputStream: InputStream,
        destDir: File,
        onProgress: (Int) -> Unit,
        onComplete: () -> Unit = {},
        onError: (Exception) -> Unit = {}
    ) {
        try {
            if (!destDir.exists()) destDir.mkdirs()
            
            ZipInputStream(inputStream).use { zis ->
                var zipEntry: ZipEntry? = null
                var extractedCount = 0
                var totalBytes: Long = 0
                
                try {
                    zipEntry = zis.nextEntry
                } catch (e: ZipException) {
                    throw IOException("Invalid or corrupted ZIP file: ${e.message}")
                }
                
                while (zipEntry != null) {
                    val newFile = File(destDir, zipEntry.name)
                    val canonicalDest = newFile.canonicalPath
                    val canonicalDestDir = destDir.canonicalPath
                    
                    if (!canonicalDest.startsWith(canonicalDestDir + File.separator)) {
                        throw IOException("Invalid ZIP entry (path traversal attempt): ${zipEntry.name}")
                    }
                    
                    if (extractedCount >= MAX_FILES) {
                        throw IOException("ZIP contains too many files (max: $MAX_FILES)")
                    }
                    
                    if (zipEntry.isDirectory) {
                        newFile.mkdirs()
                    } else {
                        newFile.parentFile?.mkdirs()
                        
                        FileOutputStream(newFile).use { fos ->
                            val buffer = ByteArray(8192)
                            var count: Int
                            var fileSize: Long = 0
                            
                            try {
                                while (zis.read(buffer).also { count = it } != -1) {
                                    fileSize += count
                                    totalBytes += count
                                    
                                    if (fileSize > MAX_FILE_SIZE || totalBytes > MAX_FILE_SIZE) {
                                        throw IOException("File too large (max: ${MAX_FILE_SIZE / (1024*1024*1024)}GB)")
                                    }
                                    
                                    fos.write(buffer, 0, count)
                                }
                            } catch (e: IOException) {
                                Log.e(TAG, "Error extracting ${zipEntry.name}: ${e.message}")
                                throw e
                            }
                        }
                    }
                    
                    extractedCount++
                    if (extractedCount % 10 == 0) {
                        try {
                            onProgress(extractedCount)
                        } catch (e: Exception) {
                            Log.w(TAG, "Progress callback error: ${e.message}")
                        }
                    }
                    
                    try {
                        zipEntry = zis.nextEntry
                    } catch (e: ZipException) {
                        Log.w(TAG, "Corrupted entry, stopping extraction: ${e.message}")
                        break
                    }
                }
                
                try {
                    zis.closeEntry()
                } catch (e: Exception) {
                    Log.w(TAG, "Error closing entry: ${e.message}")
                }
            }
            
            onComplete()
        } catch (e: Exception) {
            Log.e(TAG, "Extraction failed: ${e.message}", e)
            onError(e)
            throw e
        }
    }
    
    fun detectGameExecutable(directory: File): GameFormat? {
        if (!directory.exists() || !directory.isDirectory) return null
        
        val files = directory.walkTopDown()
            .filter { it.isFile }
            .take(1000)
            .toList()
        
        files.find { it.extension.equals("exe", ignoreCase = true) }?.let {
            return GameFormat.EXE(it.absolutePath)
        }
        
        files.find { it.extension.equals("iso", ignoreCase = true) }?.let {
            return GameFormat.ISO(it.absolutePath)
        }
        
        files.find { it.extension.equals("bin", ignoreCase = true) }?.let {
            return GameFormat.BIN(it.absolutePath)
        }
        
        files.find { it.name.equals("start.sh", ignoreCase = true) || 
                     it.name.equals("run.sh", ignoreCase = true) ||
                     it.name.equals("launch.sh", ignoreCase = true) }?.let {
            return GameFormat.SCRIPT(it.absolutePath)
        }
        
        files.find { it.name.contains("setup", ignoreCase = true) && 
                     it.extension.equals("exe", ignoreCase = true) }?.let {
            return GameFormat.EXE(it.absolutePath)
        }
        
        return null
    }
}

sealed class GameFormat {
    data class EXE(val path: String) : GameFormat()
    data class ISO(val path: String) : GameFormat()
    data class BIN(val path: String) : GameFormat()
    data class SCRIPT(val path: String) : GameFormat()
    data class DIRECTORY(val path: String) : GameFormat()
    
    fun getExecutablePath(): String = when (this) {
        is EXE -> path
        is ISO -> path
        is BIN -> path
        is SCRIPT -> path
        is DIRECTORY -> path
    }
    
    fun getDisplayName(): String = when (this) {
        is EXE -> "Windows Executable"
        is ISO -> "ISO Disc Image"
        is BIN -> "CD/DVD Image"
        is SCRIPT -> "Shell Script"
        is DIRECTORY -> "Game Directory"
    }
}

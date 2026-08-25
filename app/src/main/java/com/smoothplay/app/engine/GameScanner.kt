package com.smoothplay.app.engine

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import java.io.File

object GameScanner {
    private const val TAG = "GameScanner"
    
    private val SUPPORTED_EXTENSIONS = setOf(
        "exe", "msi", "bat", "cmd",
        "iso", "img", "bin", "cue",
        "zip", "7z", "rar", "tar", "gz"
    )
    
    data class GameDetection(
        val name: String,
        val path: String,
        val type: GameType,
        val executable: String?,
        val size: Long
    )
    
    enum class GameType(val displayName: String) {
        WINDOWS_EXE("Windows Game"),
        DISC_IMAGE("Disc Image"),
        COMPRESSED("Archive"),
        EXTRACTED("Extracted Game"),
        UNKNOWN("Unknown Format")
    }
    
    fun detectFromUri(context: Context, uri: Uri): GameDetection? {
        try {
            val projection = arrayOf(
                OpenableColumns.DISPLAY_NAME,
                OpenableColumns.SIZE
            )
            
            context.contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
                    
                    val name = if (nameIndex >= 0) cursor.getString(nameIndex) else "Unknown"
                    val size = if (sizeIndex >= 0) cursor.getLong(sizeIndex) else 0L
                    
                    val extension = name.substringAfterLast('.', "").lowercase()
                    
                    return when {
                        extension == "exe" || extension == "msi" -> GameDetection(
                            name = name.substringBeforeLast('.'),
                            path = uri.toString(),
                            type = GameType.WINDOWS_EXE,
                            executable = uri.toString(),
                            size = size
                        )
                        extension == "iso" || extension == "img" || extension == "bin" -> GameDetection(
                            name = name.substringBeforeLast('.'),
                            path = uri.toString(),
                            type = GameType.DISC_IMAGE,
                            executable = null,
                            size = size
                        )
                        extension in SUPPORTED_EXTENSIONS -> GameDetection(
                            name = name.substringBeforeLast('.'),
                            path = uri.toString(),
                            type = GameType.COMPRESSED,
                            executable = null,
                            size = size
                        )
                        else -> null
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error detecting from URI: ${e.message}", e)
        }
        
        return null
    }
    
    fun formatSize(bytes: Long): String {
        return when {
            bytes >= 1_000_000_000 -> String.format("%.1f GB", bytes / 1_000_000_000.0)
            bytes >= 1_000_000 -> String.format("%.1f MB", bytes / 1_000_000.0)
            bytes >= 1_000 -> String.format("%.1f KB", bytes / 1_000.0)
            else -> "$bytes B"
        }
    }
}

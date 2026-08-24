package com.smoothplay.app.engine

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

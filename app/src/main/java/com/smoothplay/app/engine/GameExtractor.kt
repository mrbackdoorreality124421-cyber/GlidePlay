package com.smoothplay.app.engine

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
}\n
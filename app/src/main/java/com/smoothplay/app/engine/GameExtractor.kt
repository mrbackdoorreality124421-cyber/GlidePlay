package com.smoothplay.app.engine

import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.zip.ZipInputStream

object GameExtractor {
    @Throws(SecurityException::class)
    fun extractZip(inputStream: InputStream, destDir: File, onProgress: (Int) -> Unit): Int {
        if (!destDir.exists()) destDir.mkdirs()
        val canonicalDest = destDir.canonicalPath
        var extractedCount = 0
        
        ZipInputStream(inputStream).use { zis ->
            var zipEntry = zis.nextEntry
            while (zipEntry != null) {
                val newFile = File(destDir, zipEntry.name)
                val canonicalNewFile = newFile.canonicalPath
                if (!canonicalNewFile.startsWith(canonicalDest + File.separator) && 
                    canonicalNewFile != canonicalDest) {
                    throw SecurityException("Zip Slip detected: ${zipEntry.name}")
                }
                
                if (zipEntry.isDirectory) {
                    newFile.mkdirs()
                } else {
                    newFile.parentFile?.mkdirs()
                    FileOutputStream(newFile).use { fos -> zis.copyTo(fos) }
                }
                extractedCount++
                if (extractedCount % 50 == 0) onProgress(extractedCount)
                zipEntry = zis.nextEntry
            }
        }
        return extractedCount
    }
}

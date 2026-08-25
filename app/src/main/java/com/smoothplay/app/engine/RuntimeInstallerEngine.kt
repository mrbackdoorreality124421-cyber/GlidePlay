package com.smoothplay.app.engine

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.util.zip.ZipException
import java.util.zip.ZipInputStream

class RuntimeInstallerEngine(private val context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("installer_prefs", Context.MODE_PRIVATE)
    
    companion object {
        private const val TAG = "RuntimeInstaller"
        private const val RUNTIME_URL = "https://archive.org/download/winlator-2-0/main.2.com.winlator.obb"
        private const val PREF_INSTALLED = "runtime_installed"
        private const val PREF_INSTALL_VERSION = "install_version"
        private const val INSTALL_VERSION = 1
    }

    suspend fun isRuntimeInstalled(): Boolean = withContext(Dispatchers.IO) {
        val persistedState = prefs.getBoolean(PREF_INSTALLED, false)
        val installedVersion = prefs.getInt(PREF_INSTALL_VERSION, 0)
        
        if (!persistedState || installedVersion != INSTALL_VERSION) {
            return@withContext false
        }
        
        val rootfsDir = File(context.filesDir, "rootfs")
        val box64 = File(rootfsDir, "usr/local/bin/box64")
        val proot = File(rootfsDir, "usr/local/bin/proot")
        val wine = File(rootfsDir, "opt/wine/bin/wine")
        
        val verified = box64.exists() && box64.canExecute() && 
                       proot.exists() && proot.canExecute() &&
                       wine.exists() && wine.canExecute()
        
        if (!verified && persistedState) {
            prefs.edit().putBoolean(PREF_INSTALLED, false).apply()
        }
        
        return@withContext verified
    }
    
    suspend fun getDownloadProgress(): Long = withContext(Dispatchers.IO) {
        val tempFile = File(context.cacheDir, "runtime.zip")
        if (tempFile.exists()) tempFile.length() else 0L
    }

    suspend fun downloadAndInstallRuntime(
        onProgress: (String, Int) -> Unit,
        isBackground: Boolean = false
    ): Boolean = withContext(Dispatchers.IO) {
        val rootfsDir = File(context.filesDir, "rootfs")
        val tempZip = File(context.cacheDir, "runtime.zip")
        
        try {
            if (isRuntimeInstalled()) {
                onProgress("Runtime already installed", 100)
                return@withContext true
            }
            
            onProgress("Preparing download...", 0)
            val connection = URL(RUNTIME_URL).openConnection() as HttpURLConnection
            connection.connectTimeout = 30000
            connection.readTimeout = 60000
            connection.requestMethod = "GET"
            
            val existingBytes = if (tempZip.exists()) tempZip.length() else 0L
            if (existingBytes > 0) {
                connection.setRequestProperty("Range", "bytes=$existingBytes-")
            }
            
            connection.connect()
            
            val responseCode = connection.responseCode
            if (responseCode != HttpURLConnection.HTTP_OK && responseCode != HttpURLConnection.HTTP_PARTIAL) {
                throw IOException("Server returned $responseCode")
            }
            
            val fileLength = connection.contentLengthLong + existingBytes
            val append = responseCode == HttpURLConnection.HTTP_PARTIAL
            
            onProgress("Starting download...", 0)
            
            connection.inputStream.use { input ->
                FileOutputStream(tempZip, append).use { output ->
                    val buffer = ByteArray(65536)
                    var total = existingBytes
                    var count: Int
                    var lastProgressTime = System.currentTimeMillis()
                    
                    while (input.read(buffer).also { count = it } != -1) {
                        total += count
                        output.write(buffer, 0, count)
                        
                        val now = System.currentTimeMillis()
                        if (now - lastProgressTime > 500) {
                            if (fileLength > 0) {
                                val progress = ((total * 100) / fileLength).toInt().coerceIn(0, 100)
                                val mbDownloaded = total / (1024 * 1024)
                                val mbTotal = fileLength / (1024 * 1024)
                                onProgress("Downloading... ${mbDownloaded}MB / ${mbTotal}MB", progress)
                            }
                            lastProgressTime = now
                        }
                    }
                }
            }
            
            connection.disconnect()
            
            onProgress("Verifying download...", 100)
            if (!tempZip.exists() || tempZip.length() == 0L) {
                throw IOException("Download failed - no data received")
            }
            
            onProgress("Extracting runtime...", 0)
            if (!rootfsDir.exists()) rootfsDir.mkdirs()
            
            tempZip.inputStream().use { fis ->
                ZipInputStream(fis).use { zis ->
                    var entry = zis.nextEntry
                    var extCount = 0
                    var lastProgress = 0
                    
                    while (entry != null) {
                        val file = File(rootfsDir, entry.name)
                        val canonicalPath = file.canonicalPath
                        if (!canonicalPath.startsWith(rootfsDir.canonicalPath + File.separator)) {
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
                        extCount++
                        
                        if (extCount % 50 == 0) {
                            val progress = ((extCount * 100) / 5000).coerceIn(0, 100)
                            if (progress != lastProgress) {
                                onProgress("Extracting... $extCount files", progress)
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
            
            onProgress("Setting permissions...", 100)
            val box64 = File(rootfsDir, "usr/local/bin/box64")
            val proot = File(rootfsDir, "usr/local/bin/proot")
            val wine = File(rootfsDir, "opt/wine/bin/wine")
            
            box64.setExecutable(true)
            proot.setExecutable(true)
            wine.setExecutable(true)
            
            tempZip.delete()
            
            prefs.edit()
                .putBoolean(PREF_INSTALLED, true)
                .putInt(PREF_INSTALL_VERSION, INSTALL_VERSION)
                .apply()
            
            onProgress("Installation complete!", 100)
            return@withContext true
        } catch (e: Exception) {
            Log.e(TAG, "Installation failed: ${e.message}", e)
            onProgress("Error: ${e.message}", 0)
            return@withContext false
        }
    }
    
    fun resetInstallation() {
        prefs.edit()
            .putBoolean(PREF_INSTALLED, false)
            .apply()
        val rootfsDir = File(context.filesDir, "rootfs")
        rootfsDir.deleteRecursively()
        val tempZip = File(context.cacheDir, "runtime.zip")
        tempZip.delete()
    }
}

package com.smoothplay.app.engine

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

class RuntimeInstallerEngine(private val context: Context) {
    companion object {
        private const val RUNTIME_URL = "https://github.com/brunodev85/winlator/releases/download/v7.1.2/main.7.1.2.com.winlator.obb"
        private const val BOX64_PATH = "usr/local/bin/box64"
        private const val PROOT_PATH = "usr/local/bin/proot"
    }

    fun isRuntimeInstalled(): Boolean {
        val rootfsDir = File(context.filesDir, "rootfs")
        val box64 = File(rootfsDir, BOX64_PATH)
        val proot = File(rootfsDir, PROOT_PATH)
        return box64.exists() && proot.exists() && box64.canExecute()
    }

    suspend fun downloadAndInstallRuntime(onProgress: (String, Int) -> Unit): Boolean = withContext(Dispatchers.IO) {
        val rootfsDir = File(context.filesDir, "rootfs")
        val tempZip = File(context.cacheDir, "runtime.zip")
        try {
            onProgress("Downloading PC Emulation Cores...", 0)
            var connection: HttpURLConnection? = null
            try {
                connection = URL(RUNTIME_URL).openConnection() as HttpURLConnection
                connection.connectTimeout = 30000
                connection.readTimeout = 60000
                connection.connect()
                if (connection.responseCode != HttpURLConnection.HTTP_OK) {
                    throw Exception("Server returned ${connection.responseCode}")
                }
                val fileLength = connection.contentLengthLong
                connection.inputStream.use { input ->
                    FileOutputStream(tempZip).use { output ->
                        val data = ByteArray(65536)
                        var total: Long = 0
                        var count: Int
                        while (input.read(data).also { count = it } != -1) {
                            total += count
                            output.write(data, 0, count)
                            if (fileLength > 0) {
                                val progress = (total * 80 / fileLength).toInt()
                                onProgress("Downloading... ${total/(1024*1024)}MB / ${fileLength/(1024*1024)}MB", progress)
                            }
                        }
                    }
                }
            } finally { connection?.disconnect() }
            
            onProgress("Installing Emulator Cores...", 80)
            if (!rootfsDir.exists()) rootfsDir.mkdirs()
            tempZip.inputStream().use { fis ->
                GameExtractor.extractZip(fis, rootfsDir) { count ->
                    onProgress("Installing components: $count files", 85)
                }
            }
            
            onProgress("Configuring permissions...", 95)
            File(rootfsDir, BOX64_PATH).setExecutable(true, false)
            File(rootfsDir, PROOT_PATH).setExecutable(true, false)
            File(rootfsDir, "opt/wine/bin/wine").takeIf { it.exists() }?.setExecutable(true, false)
            File(rootfsDir, "usr/local/bin").takeIf { it.exists() }?.listFiles()?.forEach { it.setExecutable(true, false) }
            
            tempZip.delete()
            onProgress("Installation Complete!", 100)
            return@withContext true
        } catch (e: Exception) {
            tempZip.delete()
            onProgress("Error: ${e.message}", 0)
            return@withContext false
        }
    }
    
    fun uninstallRuntime() { File(context.filesDir, "rootfs").deleteRecursively() }
}

package com.smoothplay.app.engine

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.zip.ZipInputStream

class RuntimeInstallerEngine(private val context: Context) {

    // In a real production app, this would point to your server hosting the compiled Box64/Wine rootfs
    private val RUNTIME_URL = "https://github.com/brunodev85/winlator/releases/download/v7.1.2/main.7.1.2.com.winlator.obb" // Example placeholder

    suspend fun isRuntimeInstalled(): Boolean {
        val rootfsDir = File(context.filesDir, "rootfs")
        val box64 = File(rootfsDir, "usr/local/bin/box64")
        return box64.exists()
    }

    suspend fun downloadAndInstallRuntime(onProgress: (String, Int) -> Unit): Boolean = withContext(Dispatchers.IO) {
        val rootfsDir = File(context.filesDir, "rootfs")
        val tempZip = File(context.cacheDir, "runtime.zip")
        
        try {
            // 1. Download the Runtime Core
            onProgress("Downloading PC Emulation Cores...", 0)
            val connection = URL(RUNTIME_URL).openConnection() as HttpURLConnection
            connection.connect()
            
            val fileLength = connection.contentLength
            val input = connection.inputStream
            val output = FileOutputStream(tempZip)
            
            val data = ByteArray(8192)
            var total: Long = 0
            var count: Int
            
            while (input.read(data).also { count = it } != -1) {
                total += count.toLong()
                if (fileLength > 0) {
                    val progress = (total * 100 / fileLength).toInt()
                    onProgress("Downloading... \${progress}%", progress)
                }
                output.write(data, 0, count)
            }
            output.flush()
            output.close()
            input.close()

            // 2. Extract the Core
            onProgress("Installing Emulator Cores...", 100)
            if (!rootfsDir.exists()) rootfsDir.mkdirs()
            
            tempZip.inputStream().use { fis ->
                ZipInputStream(fis).use { zis ->
                    var entry = zis.nextEntry
                    var extCount = 0
                    while (entry != null) {
                        val file = File(rootfsDir, entry.name)
                        if (entry.isDirectory) {
                            file.mkdirs()
                        } else {
                            file.parentFile?.mkdirs()
                            FileOutputStream(file).use { fos ->
                                zis.copyTo(fos)
                            }
                        }
                        extCount++
                        if (extCount % 100 == 0) {
                            onProgress("Installing components: $extCount", 100)
                        }
                        entry = zis.nextEntry
                    }
                }
            }

            // 3. Set Execute Permissions
            onProgress("Configuring permissions...", 100)
            val box64 = File(rootfsDir, "usr/local/bin/box64")
            val proot = File(rootfsDir, "usr/local/bin/proot")
            val wine = File(rootfsDir, "opt/wine/bin/wine")
            
            box64.setExecutable(true)
            proot.setExecutable(true)
            wine.setExecutable(true)

            tempZip.delete()
            return@withContext true
        } catch (e: Exception) {
            e.printStackTrace()
            tempZip.delete()
            return@withContext false
        }
    }
}\n
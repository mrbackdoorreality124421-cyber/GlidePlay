package com.smoothplay.app.engine

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader

class RuntimeLauncher {
    @Volatile private var process: Process? = null
    @Volatile private var isRunning = false
    
    suspend fun launchGame(
        gameDir: String, mainExe: String, envVars: Map<String, String>, onLog: (String) -> Unit
    ) = withContext(Dispatchers.IO) {
        val rootfs = "/data/data/com.smoothplay.app/files/rootfs"
        val command = listOf(
            "proot", "-b", "/dev", "-b", "/proc", "-b", "/sys",
            "-r", rootfs, "-w", gameDir,
            "/usr/local/bin/box64", "wine", mainExe
        )
        val pb = ProcessBuilder(command)
        pb.environment().apply {
            putAll(envVars)
            put("DISPLAY", ":0")
        }
        pb.redirectErrorStream(true)
        
        try {
            isRunning = true
            process = pb.start()
            val proc = process ?: throw IllegalStateException("Process failed to start")
            BufferedReader(InputStreamReader(proc.inputStream)).use { reader ->
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    line?.let { onLog(it) }
                }
            }
            val exitCode = proc.waitFor()
            onLog("Process exited with code: $exitCode")
        } catch (e: Exception) {
            onLog("CRASH: ${e.javaClass.simpleName} - ${e.message}")
        } finally {
            isRunning = false
            cleanup()
        }
    }
    
    fun stop() {
        process?.let { proc ->
            try {
                if (proc.isAlive) {
                    proc.destroy()
                }
            } catch (_: Exception) {}
        }
    }
    
    fun isRunning(): Boolean = isRunning && (process?.isAlive == true)
    
    private fun cleanup() {
        try {
            process?.inputStream?.close()
            process?.outputStream?.close()
            process?.errorStream?.close()
        } catch (_: Exception) {}
        process = null
    }
}

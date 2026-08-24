package com.smoothplay.app.engine
import java.io.BufferedReader
import java.io.InputStreamReader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RuntimeLauncher {
    private var process: Process? = null

    suspend fun launchGame(gameDir: String, mainExe: String, envVars: Map<String, String>, onLog: (String) -> Unit) = withContext(Dispatchers.IO) {
        val command = mutableListOf("proot", "-b", "/dev", "-r", "/data/data/com.smoothplay.app/rootfs", "-w", gameDir, "/usr/local/bin/box64", "wine", mainExe)
        val pb = ProcessBuilder(command)
        pb.environment().putAll(envVars)
        pb.redirectErrorStream(true)
        try {
            process = pb.start()
            val reader = BufferedReader(InputStreamReader(process!!.inputStream))
            var line: String?
            while (reader.readLine().also { line = it } != null) { onLog(line ?: "") }
            process!!.waitFor()
        } catch (e: Exception) {
            onLog("CRASH: ${e.message}")
        }
    }
    fun stop() { process?.destroy() }
}\n
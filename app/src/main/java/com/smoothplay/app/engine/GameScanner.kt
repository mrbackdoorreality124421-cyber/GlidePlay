package com.smoothplay.app.engine

import java.io.File

data class ScanResult(
    val mainExecutable: String,
    val isHeavy: Boolean,
    val dependencies: List<String>,
    val weightScore: Int,
    val totalSizeMb: Long
)

class GameScanner {
    fun scanDirectory(gameDir: File): ScanResult {
        val allFiles = gameDir.walkTopDown().filter { it.isFile }.toList()
        val executables = allFiles.filter { it.extension.equals("exe", ignoreCase = true) }
        val mainExe = detectMainExecutable(executables)
        val deps = detectDependencies(allFiles)
        val weight = calculateWeight(allFiles)
        val sizeBytes = allFiles.sumOf { it.length() }
        val sizeMb = sizeBytes / (1024 * 1024)
        return ScanResult(
            mainExecutable = mainExe?.absolutePath ?: "",
            isHeavy = weight > 50 || sizeMb > 2000,
            dependencies = deps,
            weightScore = weight,
            totalSizeMb = sizeMb
        )
    }

    private fun detectMainExecutable(exes: List<File>): File? {
        if (exes.isEmpty()) return null
        val exactMatches = listOf("game.exe", "launcher.exe", "run.exe", "start.exe", "play.exe")
        exes.find { exactMatches.contains(it.name.lowercase()) }?.let { return it }
        return exes.maxByOrNull { it.length() }
    }

    private fun detectDependencies(files: List<File>): List<String> {
        val deps = mutableListOf<String>()
        val fileNames = files.map { it.name.lowercase() }
        if (fileNames.any { it.contains("d3dcompiler") || it.contains("dxgi") || it.contains("d3d") }) deps.add("DirectX")
        if (fileNames.any { it.contains("vulkan") }) deps.add("Vulkan")
        if (fileNames.any { it.contains("msvcp") || it.contains("msvcr") || it.contains("vcruntime") }) deps.add("VC++ Redist")
        if (fileNames.any { it.contains("openal") }) deps.add("OpenAL")
        if (fileNames.any { it.contains("physx") }) deps.add("PhysX")
        return deps
    }

    private fun calculateWeight(files: List<File>): Int {
        val sizeMb = files.sumOf { it.length() } / (1024 * 1024)
        var score = (sizeMb / 500).toInt()
        val dllCount = files.count { it.extension.lowercase() == "dll" }
        score += (dllCount / 10)
        return score.coerceIn(0, 100)
    }
}

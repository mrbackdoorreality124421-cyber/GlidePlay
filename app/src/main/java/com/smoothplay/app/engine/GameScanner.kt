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
        val allFiles = gameDir.walkTopDown().toList()
        val executables = allFiles.filter { it.extension.equals("exe", ignoreCase = true) }
        
        val mainExe = detectMainExecutable(executables)
        val deps = detectDependencies(allFiles)
        val weight = calculateWeight(allFiles)
        val sizeMb = allFiles.sumOf { it.length() } / (1024 * 1024)
        
        return ScanResult(
            mainExecutable = mainExe?.absolutePath ?: "",
            isHeavy = weight > 50,
            dependencies = deps,
            weightScore = weight,
            totalSizeMb = sizeMb
        )
    }

    private fun detectMainExecutable(exes: List<File>): File? {
        val exactMatches = listOf("game.exe", "launcher.exe", "run.exe")
        exes.find { exactMatches.contains(it.name.lowercase()) }?.let { return it }
        return exes.maxByOrNull { it.length() }
    }

    private fun detectDependencies(files: List<File>): List<String> {
        val deps = mutableListOf<String>()
        val fileNames = files.map { it.name.lowercase() }
        if (fileNames.any { it.contains("d3dcompiler") || it.contains("dxgi") }) deps.add("DirectX")
        if (fileNames.any { it.contains("vulkan") }) deps.add("Vulkan")
        if (fileNames.any { it.contains("msvcp") }) deps.add("VC++ Redist")
        return deps
    }

    private fun calculateWeight(files: List<File>): Int {
        var score = 0
        val sizeMb = files.sumOf { it.length() } / (1024 * 1024)
        score += (sizeMb / 500).toInt()
        val dllCount = files.count { it.extension.lowercase() == "dll" }
        score += (dllCount / 10)
        return score.coerceIn(0, 100)
    }
}\n
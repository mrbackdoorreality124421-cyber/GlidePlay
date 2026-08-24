package com.smoothplay.app.engine

import android.app.ActivityManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build

object DeviceBenchmark {
    fun getScore(context: Context): Int {
        val cores = Runtime.getRuntime().availableProcessors()
        val ramGb = getTotalRamGb(context)
        val hasVulkan = context.packageManager.hasSystemFeature(PackageManager.FEATURE_VULKAN_HARDWARE_LEVEL)
        val sdkLevel = Build.VERSION.SDK_INT
        
        var score = 0.0
        score += (cores * 4.5).coerceAtMost(30.0)
        score += when {
            ramGb >= 12 -> 35.0; ramGb >= 8 -> 28.0; ramGb >= 6 -> 22.0
            ramGb >= 4 -> 15.0; else -> 8.0
        }
        if (hasVulkan) score += 15.0
        score += when {
            sdkLevel >= 34 -> 10.0; sdkLevel >= 33 -> 8.0
            sdkLevel >= 31 -> 5.0; else -> 0.0
        }
        score += estimateGpuTier()
        return score.toInt().coerceIn(0, 100)
    }
    
    fun getTotalRamGb(context: Context): Float {
        val actManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memInfo = ActivityManager.MemoryInfo()
        actManager.getMemoryInfo(memInfo)
        return memInfo.totalMem / (1024f * 1024f * 1024f)
    }
    
    fun getCpuCores(): Int = Runtime.getRuntime().availableProcessors()
    
    private fun estimateGpuTier(): Double {
        val model = Build.MODEL.lowercase()
        val brand = Build.BRAND.lowercase()
        return when {
            "pixel 8" in model || "pixel 7" in model -> 9.0
            "samsung" in brand && ("s24" in model || "s23" in model) -> 9.0
            "samsung" in brand && ("s22" in model || "s21" in model) -> 7.0
            "xiaomi" in brand && ("14" in model || "13" in model) -> 8.0
            "oneplus" in brand -> 7.0
            else -> 4.0
        }
    }
    
    fun getDeviceSummary(context: Context): String {
        val cores = getCpuCores()
        val ramGb = getTotalRamGb(context)
        val hasVulkan = context.packageManager.hasSystemFeature(PackageManager.FEATURE_VULKAN_HARDWARE_LEVEL)
        return "${Build.MANUFACTURER} ${Build.MODEL} | ${cores}C | ${String.format("%.1f", ramGb)}GB | Vulkan: ${if (hasVulkan) "Yes" else "No"}"
    }
}

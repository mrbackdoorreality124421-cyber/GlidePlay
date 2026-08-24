package com.smoothplay.app.engine

import android.app.ActivityManager
import android.content.Context
import android.content.pm.PackageManager

class DeviceBenchmark(private val context: Context) {
    fun calculateDeviceScore(): Int {
        val cores = Runtime.getRuntime().availableProcessors()
        val ramGb = getTotalRamGb()
        val hasVulkan = context.packageManager.hasSystemFeature(PackageManager.FEATURE_VULKAN_HARDWARE_LEVEL)
        
        var score = 0
        score += cores * 5
        score += (ramGb * 2).toInt()
        if (hasVulkan) score += 20
        
        return score.coerceIn(0, 100)
    }

    private fun getTotalRamGb(): Float {
        val actManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memInfo = ActivityManager.MemoryInfo()
        actManager.getMemoryInfo(memInfo)
        return memInfo.totalMem / (1024f * 1024f * 1024f)
    }
}\n
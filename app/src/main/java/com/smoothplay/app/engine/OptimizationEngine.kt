package com.smoothplay.app.engine
import android.content.Context
import android.os.Build
import android.os.PowerManager
import android.app.ActivityManager

object DeviceBenchmark {
    fun getScore(context: Context): Int {
        val cores = Runtime.getRuntime().availableProcessors()
        val actManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memInfo = ActivityManager.MemoryInfo().apply { actManager.getMemoryInfo(this) }
        val ramGb = memInfo.totalMem / (1024f * 1024f * 1024f)
        return ((cores * 5) + (ramGb * 3)).toInt().coerceIn(0, 100)
    }
}

class OptimizationEngine(context: Context) {
    private val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
    var currentThermalStatus = 0

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            powerManager.addThermalStatusListener { status -> currentThermalStatus = status }
        }
    }

    companion object {
        fun recommendProfile(deviceScore: Int, gameWeight: Int): String {
            val delta = deviceScore - gameWeight
            return when {
                delta >= 40 -> "Super Extreme"
                delta in 20..39 -> "Ultra"
                delta in 0..19 -> "Balance"
                delta in -20..-1 -> "Smooth"
                else -> "Super Smooth"
            }
        }
    }
}\n
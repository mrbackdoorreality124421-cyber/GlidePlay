package com.smoothplay.app.engine

import android.content.Context
import android.os.Build
import android.os.PowerManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class OptimizationEngine(context: Context) {
    private val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
    private val _thermalStatus = MutableStateFlow(0)
    val thermalStatus: StateFlow<Int> = _thermalStatus
    private var thermalListener: PowerManager.OnThermalStatusChangedListener? = null
    
    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            thermalListener = PowerManager.OnThermalStatusChangedListener { status ->
                _thermalStatus.value = status
            }
            powerManager.addThermalStatusListener(thermalListener!!)
        }
    }
    
    fun cleanup() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            thermalListener?.let { powerManager.removeThermalStatusListener(it) }
        }
    }
    
    companion object {
        fun recommendProfile(deviceScore: Int, gameWeight: Int): String {
            val delta = deviceScore - gameWeight
            return when {
                delta >= 40 -> "Super Extreme"
                delta in 20..39 -> "Ultra"
                delta in 0..19 -> "Balanced"
                delta in -20..-1 -> "Smooth"
                else -> "Super Smooth"
            }
        }
        
        fun getEnvVarsForProfile(profile: String): Map<String, String> = when (profile) {
            "Super Extreme" -> mapOf("MESA_GL_VERSION_OVERRIDE" to "4.5", "DXVK_HUD" to "fps", "WINEDEBUG" to "-all")
            "Ultra" -> mapOf("MESA_GL_VERSION_OVERRIDE" to "4.3", "WINEDEBUG" to "-all")
            "Balanced" -> mapOf("MESA_GL_VERSION_OVERRIDE" to "3.3", "WINEDEBUG" to "-all")
            "Smooth" -> mapOf("MESA_GL_VERSION_OVERRIDE" to "3.0")
            else -> mapOf("MESA_GL_VERSION_OVERRIDE" to "2.1")
        }
    }
}

import { CodeBlock, Section } from './sectionsPart1';

export const architectureSectionsPart2: Section[] = [
  {
    id: 8,
    title: "8. Controls Engine Logic",
    description: "Smart touch layout generation and virtual gamepad mapping.",
    codeBlocks: [
      {
        language: "kotlin",
        title: "SmartTouchControls.kt",
        code: `package com.smoothplay.app.engines.controls

import android.view.MotionEvent

class SmartTouchControlsEngine {
    
    fun processTouch(event: MotionEvent, layout: ControlLayout): InputAction? {
        val x = event.x
        val y = event.y
        
        layout.buttons.forEach { button ->
            if (isInsideCircle(x, y, button.x, button.y, button.radius)) {
                return InputAction.ButtonPress(button.mappedKey)
            }
        }
        
        layout.joysticks.forEach { joy ->
            if (isInsideCircle(x, y, joy.centerX, joy.centerY, joy.zoneRadius)) {
                return calculateJoystickAxis(x, y, joy)
            }
        }
        
        return null
    }
    
    fun generateTemplateForGenre(genre: GameGenre): ControlLayout {
        return when(genre) {
            GameGenre.FPS -> createFpsTemplate()
            GameGenre.RACING -> createRacingTemplate()
            GameGenre.RPG -> createRpgTemplate()
            else -> createActionTemplate()
        }
    }
    
    private fun isInsideCircle(x: Float, y: Float, cx: Float, cy: Float, r: Float) = 
        Math.pow((x - cx).toDouble(), 2.0) + Math.pow((y - cy).toDouble(), 2.0) <= Math.pow(r.toDouble(), 2.0)
}`
      }
    ]
  },
  {
    id: 9,
    title: "9. Device Benchmark Logic",
    description: "Hardware capability scoring to determine optimization baselines.",
    codeBlocks: [
      {
        language: "kotlin",
        title: "DeviceCheckEngine.kt",
        code: `package com.smoothplay.app.core.hardware

import android.app.ActivityManager
import android.content.Context
import android.content.pm.PackageManager

class DeviceCheckEngine(private val context: Context) {
    
    fun calculateDeviceScore(): Int {
        val cores = Runtime.getRuntime().availableProcessors()
        val ram = getTotalRam(context)
        val hasVulkan = context.packageManager.hasSystemFeature(PackageManager.FEATURE_VULKAN_HARDWARE_LEVEL)
        
        var score = 0
        score += cores * 5
        score += (ram / (1024 * 1024 * 1024)).toInt() * 2 // 2 pts per GB
        if (hasVulkan) score += 20
        
        return score.coerceIn(0, 100)
    }
    
    private fun getTotalRam(context: Context): Long {
        val actManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memInfo = ActivityManager.MemoryInfo()
        actManager.getMemoryInfo(memInfo)
        return memInfo.totalMem
    }
}`
      }
    ]
  },
  {
    id: 10,
    title: "10. Game Weight Logic",
    description: "Heuristic evaluation of game complexity.",
    codeBlocks: [
      {
        language: "kotlin",
        title: "GameWeightEngine.kt",
        code: `package com.smoothplay.app.engines.import

import java.io.File

class GameWeightEngine {
    fun estimateWeight(files: List<File>): Int {
        var score = 0
        
        val totalSizeMb = files.sumOf { it.length() } / (1024 * 1024)
        score += (totalSizeMb / 500).toInt() // 1 pt per 500MB
        
        val dllCount = files.count { it.extension.lowercase() == "dll" }
        score += (dllCount / 10).toInt()
        
        if (files.any { it.name.lowercase().contains("d3d12") }) {
            score += 20 // DX12 usually heavy
        }
        
        return score.coerceIn(0, 100)
    }
    
    fun getCategory(score: Int): GameWeightCategory {
        return when (score) {
            in 0..15 -> GameWeightCategory.VERY_LIGHT
            in 16..30 -> GameWeightCategory.LIGHT
            in 31..50 -> GameWeightCategory.MEDIUM
            in 51..75 -> GameWeightCategory.HEAVY
            in 76..90 -> GameWeightCategory.VERY_HEAVY
            else -> GameWeightCategory.EXTREME_HEAVY
        }
    }
}`
      }
    ]
  },
  {
    id: 11,
    title: "11. Profile Recommendation Logic",
    description: "Matrix resolution for Device Score vs Game Weight.",
    codeBlocks: [
      {
        language: "kotlin",
        title: "ProfileLogic.kt",
        code: `package com.smoothplay.app.domain.usecases

class RecommendProfileUseCase {
    fun invoke(deviceScore: Int, gameWeightScore: Int): PerformanceProfile {
        // Higher means better capable of running heavier games
        val capabilityDelta = deviceScore - gameWeightScore
        
        return when {
            capabilityDelta >= 50 -> PerformanceProfile.SUPER_EXTREME
            capabilityDelta in 30..49 -> PerformanceProfile.EXTREME
            capabilityDelta in 10..29 -> PerformanceProfile.ULTRA
            capabilityDelta in -10..9 -> PerformanceProfile.BALANCE
            capabilityDelta in -30..-11 -> PerformanceProfile.SMOOTH
            else -> PerformanceProfile.SUPER_SMOOTH
        }
    }
}`
      }
    ]
  },
  {
    id: 12,
    title: "12. Runtime Launcher Abstraction",
    description: "Wrapper for Wine/Box64/Termux compatibility containers.",
    codeBlocks: [
      {
        language: "kotlin",
        title: "RuntimeLauncher.kt",
        code: `package com.smoothplay.app.core.runtime

import com.smoothplay.app.domain.models.PerformanceProfile

/**
 * Interface that compatibility layer plugins (e.g. Winlator wrapper, Box64 wrapper) implement.
 */
interface RuntimeLauncher {
    
    fun prepareGameContainer(gameId: String, installPath: String)
    
    fun generateConfiguration(profile: PerformanceProfile, dxvkEnabled: Boolean, turnipEnabled: Boolean)
    
    fun launch(executablePath: String)
    
    fun stop()
    
    fun injectInputEvent(event: InputEvent)
    
    fun getMetricsFlow(): kotlinx.coroutines.flow.Flow<RuntimeMetrics>
}`
      }
    ]
  }
];

package com.smoothplay.app.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.smoothplay.app.engine.RuntimeInstallerEngine
import com.smoothplay.app.engine.RuntimeInstallerState
import kotlinx.coroutines.*

class DownloadService : Service() {
    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private lateinit var installer: RuntimeInstallerEngine
    
    companion object {
        const val CHANNEL_ID = "download_channel"
        const val NOTIFICATION_ID = 1
        const val ACTION_START = "START_DOWNLOAD"
        const val ACTION_STOP = "STOP_DOWNLOAD"
    }
    
    override fun onCreate() {
        super.onCreate()
        installer = RuntimeInstallerEngine(this)
        createNotificationChannel()
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> startDownload()
            ACTION_STOP -> stopDownload()
        }
        return START_NOT_STICKY
    }
    
    override fun onBind(intent: Intent?): IBinder? = null
    
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Runtime Download",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Shows download progress for game runtime"
            }
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }
    
    private fun startDownload() {
        val notification = buildNotification("Preparing download...", 0)
        startForeground(NOTIFICATION_ID, notification)
        
        RuntimeInstallerState.isInstalling.value = true
        RuntimeInstallerState.hasError.value = false
        
        serviceScope.launch {
            try {
                val success = installer.downloadAndInstallRuntime(
                    onProgress = { message, progress ->
                        RuntimeInstallerState.statusText.value = message
                        RuntimeInstallerState.progress.value = progress
                        updateNotification(message, progress)
                    }
                )
                
                if (success) {
                    updateNotification("Installation complete!", 100)
                    Log.d("DownloadService", "Installation successful")
                } else {
                    RuntimeInstallerState.hasError.value = true
                    updateNotification("Installation failed", 0)
                    Log.e("DownloadService", "Installation failed")
                }
            } catch (e: Exception) {
                Log.e("DownloadService", "Service error: ${e.message}", e)
                RuntimeInstallerState.hasError.value = true
                updateNotification("Error: ${e.message}", 0)
            } finally {
                RuntimeInstallerState.isInstalling.value = false
                delay(3000)
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
            }
        }
    }
    
    private fun stopDownload() {
        serviceScope.cancel()
        RuntimeInstallerState.isInstalling.value = false
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }
    
    private fun buildNotification(text: String, progress: Int): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("SmoothPlay Setup")
            .setContentText(text)
            .setSmallIcon(android.R.drawable.stat_sys_download)
            .setProgress(100, progress, progress == 0)
            .setOngoing(true)
            .build()
    }
    
    private fun updateNotification(text: String, progress: Int) {
        val notification = buildNotification(text, progress)
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.notify(NOTIFICATION_ID, notification)
    }
    
    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }
}

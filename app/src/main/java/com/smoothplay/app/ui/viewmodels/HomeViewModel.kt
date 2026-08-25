package com.smoothplay.app.ui.viewmodels

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smoothplay.app.data.Game
import com.smoothplay.app.data.GameDao
import com.smoothplay.app.engine.GamePipelineEngine
import com.smoothplay.app.engine.OptimizationEngine
import com.smoothplay.app.engine.RuntimeLauncher
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val gameDao: GameDao,
    @ApplicationContext private val context: Context
) : ViewModel() {
    val games = gameDao.getAllGames()
    private val _statusMsg = MutableStateFlow("")
    val statusMsg: StateFlow<String> = _statusMsg
    private val _isProcessing = MutableStateFlow(false)
    val isProcessing: StateFlow<Boolean> = _isProcessing
    private val _launchLog = MutableStateFlow<List<String>>(emptyList())
    val launchLog: StateFlow<List<String>> = _launchLog
    private val _isGameRunning = MutableStateFlow(false)
    val isGameRunning: StateFlow<Boolean> = _isGameRunning
    private val launcher = RuntimeLauncher()
    
    fun importZip(uri: Uri) {
        viewModelScope.launch {
            _isProcessing.value = true; _statusMsg.value = "Processing ZIP..."
            val pipeline = GamePipelineEngine(context)
            val game = pipeline.processZip(uri, context.filesDir) { m, _ -> _statusMsg.value = m }
            if (game != null) { gameDao.insertGame(game); _statusMsg.value = "Imported: ${game.name}" }
            else { _statusMsg.value = "Import failed." }
            _isProcessing.value = false
        }
    }
    
    fun launchGame(game: Game) {
        viewModelScope.launch {
            _isGameRunning.value = true
            _launchLog.value = listOf("Starting ${game.name}...", "Profile: ${game.profile}")
            val env = OptimizationEngine.getEnvVarsForProfile(game.profile)
            launcher.launchGame(game.installPath, game.mainExecutable, env) { l ->
                _launchLog.value = _launchLog.value + l
                if (_launchLog.value.size > 200) _launchLog.value = _launchLog.value.takeLast(200)
            }
            _isGameRunning.value = false
        }
    }
    
    fun stopGame() { launcher.stop(); _isGameRunning.value = false }
    
    fun deleteGame(game: Game) {
        viewModelScope.launch {
            try { File(game.installPath).deleteRecursively() } catch (_: Exception) {}
            gameDao.deleteGame(game.id)
            _statusMsg.value = "Deleted ${game.name}"
        }
    }
    
    override fun onCleared() { super.onCleared(); launcher.stop() }
}

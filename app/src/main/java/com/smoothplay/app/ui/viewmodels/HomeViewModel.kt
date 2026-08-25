package com.smoothplay.app.ui.viewmodels

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smoothplay.app.data.Game
import com.smoothplay.app.data.GameDao
import com.smoothplay.app.engine.GamePipelineEngine
import com.smoothplay.app.engine.GameScanner
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val gameDao: GameDao,
    @ApplicationContext private val context: Context
) : ViewModel() {
    
    companion object {
        private const val TAG = "HomeViewModel"
    }
    
    val games = gameDao.getAllGames()
    private val _statusMsg = MutableStateFlow("Library Ready")
    val statusMsg: StateFlow<String> = _statusMsg

    fun importGame(uri: Uri) {
        viewModelScope.launch {
            try {
                _statusMsg.value = "Detecting game format..."
                
                val detection = GameScanner.detectFromUri(context, uri)
                if (detection == null) {
                    _statusMsg.value = "Unsupported file format"
                    Log.w(TAG, "Could not detect file format for: $uri")
                    return@launch
                }
                
                _statusMsg.value = "Processing ${detection.type.displayName}..."
                val pipeline = GamePipelineEngine(context)
                val game = pipeline.processGame(uri, detection, context.cacheDir) { msg, _ -> 
                    _statusMsg.value = msg 
                }
                
                if (game != null) {
                    gameDao.insertGame(game)
                    _statusMsg.value = "Import Complete: ${game.name}"
                } else {
                    _statusMsg.value = "Import Failed"
                }
            } catch (e: Exception) {
                Log.e(TAG, "Import error: ${e.message}", e)
                _statusMsg.value = "Error: ${e.message}"
            }
        }
    }
    
    fun launchGame(game: Game, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                _statusMsg.value = "Launching ${game.name}..."
                val pipeline = GamePipelineEngine(context)
                val success = pipeline.launchGame(game)
                
                if (success) {
                    _statusMsg.value = "Game session ended"
                    onResult(true)
                } else {
                    _statusMsg.value = "Failed to launch game"
                    onResult(false)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Launch error: ${e.message}", e)
                _statusMsg.value = "Launch error: ${e.message}"
                onResult(false)
            }
        }
    }
}

package com.smoothplay.app.ui.viewmodels
import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smoothplay.app.data.GameDao
import com.smoothplay.app.engine.GamePipelineEngine
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
    val games = gameDao.getAllGames()
    private val _statusMsg = MutableStateFlow("Library Ready")
    val statusMsg: StateFlow<String> = _statusMsg

    fun importZip(uri: Uri) {
        viewModelScope.launch {
            _statusMsg.value = "Processing ZIP..."
            val pipeline = GamePipelineEngine(context)
            val game = pipeline.processZip(uri, context.cacheDir) { msg, _ -> _statusMsg.value = msg }
            if (game != null) {
                gameDao.insertGame(game)
                _statusMsg.value = "Import Complete!"
            } else {
                _statusMsg.value = "Import Failed."
            }
        }
    }
}\n
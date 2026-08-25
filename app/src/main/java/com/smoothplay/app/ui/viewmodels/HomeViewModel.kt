package com.smoothplay.app.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smoothplay.app.data.GameDao
import com.smoothplay.app.models.Game
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val gameDao: GameDao
) : ViewModel() {
    val games = gameDao.getAllGames()

    fun importMockGame() {
        viewModelScope.launch {
            val newGame = Game(
                id = UUID.randomUUID().toString(),
                name = "PC Game Imported",
                status = "Ready",
                profile = "Balance",
                weight = "Medium",
                size = "1.5 GB",
                installPath = "/data/user/0/com.smoothplay.app/games"
            )
            gameDao.insertGame(newGame)
        }
    }
}

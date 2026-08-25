package com.smoothplay.app.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.smoothplay.app.models.Game
import kotlinx.coroutines.flow.Flow

@Dao
interface GameDao {
    @Query("SELECT * FROM games")
    fun getAllGames(): Flow<List<Game>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGame(game: Game)

    @Query("DELETE FROM games WHERE id = :gameId")
    suspend fun deleteGame(gameId: String)
}

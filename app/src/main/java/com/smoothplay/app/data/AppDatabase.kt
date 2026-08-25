package com.smoothplay.app.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.smoothplay.app.models.Game

@Database(entities = [Game::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun gameDao(): GameDao
}

package com.smoothplay.app.data
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "games")
data class Game(
    @PrimaryKey val id: String,
    val name: String,
    val installPath: String,
    val mainExecutable: String,
    val status: String,
    val profile: String,
    val weightScore: Int,
    val totalSizeMb: Long,
    val dependencies: String
)\n
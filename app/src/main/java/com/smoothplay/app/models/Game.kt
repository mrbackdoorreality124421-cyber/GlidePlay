package com.smoothplay.app.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "games")
data class Game(
    @PrimaryKey val id: String,
    val name: String,
    val status: String,
    val profile: String,
    val weight: String,
    val size: String,
    val installPath: String
)

package com.example.music.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "songs")
data class SongEntity(
    @PrimaryKey val id: String,
    val title: String,
    val artist: String,
    val thumbnailUrl: String,
    val audioUrl: String?,
    val durationMs: Long = 0,
    val cachedAt: Long = System.currentTimeMillis()
)
package com.example.music.data.repository

import com.example.music.data.local.SongEntity
import com.example.music.data.remote.SongResult
import com.example.music.ui.features.lyrics.LyricLine
import kotlinx.coroutines.flow.Flow

interface MusicRepository {
    // YouTube provider methods
    suspend fun searchSongs(query: String): Result<List<SongResult>>
    suspend fun getAudioUrl(songId: String): Result<String>
    suspend fun getLyrics(title: String, artist: String): Result<List<LyricLine>>
    suspend fun getCachedSongs(query: String): List<SongEntity>
    suspend fun getRecentSongs(limit: Int): List<SongEntity>
    suspend fun clearOldCache()
    suspend fun updateLastAccessed(songId: String)

    // Flow versions for UI observation
    fun getCachedSongsFlow(query: String): Flow<List<SongEntity>>
    fun getRecentSongsFlow(limit: Int): Flow<List<SongEntity>>

    // Apple Music provider methods (MusicKit public API)
    suspend fun searchAppleMusic(query: String): Result<List<SongResult>>
}
package com.example.music.data.local

import com.example.music.data.remote.SongResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SongCache(private val database: SongDatabase) {

    suspend fun cacheSearchResults(query: String, results: List<SongResult>) = withContext(Dispatchers.IO) {
        val entities = results.map { result ->
            SongEntity(
                id = result.id,
                title = result.title,
                artist = result.artist,
                thumbnailUrl = result.thumbnailUrl,
                audioUrl = result.audioUrl
            )
        }
        database.songDao().insertSongs(entities)
    }

    suspend fun getCachedSongs(query: String): List<SongEntity> = withContext(Dispatchers.IO) {
        database.songDao().searchSongs(query)
    }

    suspend fun getRecentSongs(limit: Int = 50): List<SongEntity> = withContext(Dispatchers.IO) {
        database.songDao().getRecentSongs(limit)
    }

    suspend fun updateLastAccessed(songId: String) = withContext(Dispatchers.IO) {
        database.songDao().updateLastAccessed(songId, System.currentTimeMillis())
    }

    suspend fun clearOldCache(maxAgeMs: Long = 7 * 24 * 60 * 60 * 1000) = withContext(Dispatchers.IO) {
        val cutoff = System.currentTimeMillis() - maxAgeMs
        database.songDao().clearOldCache(cutoff)
    }
}
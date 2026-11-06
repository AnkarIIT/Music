package com.example.music.data.repository

import com.example.music.data.local.SongCache
import com.example.music.data.local.SongEntity
import com.example.music.data.remote.AppleMusicCatalogProvider
import com.example.music.data.remote.LyricsDataSource
import com.example.music.data.remote.SongResult
import com.example.music.data.remote.YoutubeDataSource
import com.example.music.ui.features.lyrics.LyricLine
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class MusicRepositoryImpl @javax.inject.Inject constructor(
    private val youtubeDataSource: YoutubeDataSource,
    private val appleMusicProvider: AppleMusicCatalogProvider,
    private val lyricsDataSource: LyricsDataSource,
    private val cache: SongCache
) : MusicRepository {

    override suspend fun searchSongs(query: String): Result<List<SongResult>> {
        return try {
            val results = youtubeDataSource.searchSongs(query)
            cache.cacheSearchResults(query, results)
            Result.success(results)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getAudioUrl(songId: String): Result<String> {
        return try {
            val url = youtubeDataSource.getAudioStreamUrl(songId)
            if (url != null) Result.success(url)
            else Result.failure(Exception("No audio stream found for $songId"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getLyrics(title: String, artist: String): Result<List<LyricLine>> {
        return lyricsDataSource.fetchLyrics(title, artist)
    }

    override suspend fun getCachedSongs(query: String): List<SongEntity> {
        return cache.getCachedSongs(query)
    }

    override suspend fun getRecentSongs(limit: Int): List<SongEntity> {
        return cache.getRecentSongs(limit)
    }

    override suspend fun clearOldCache() {
        cache.clearOldCache()
    }

    override suspend fun updateLastAccessed(songId: String) {
        cache.updateLastAccessed(songId)
    }

    override fun getCachedSongsFlow(query: String): Flow<List<SongEntity>> = flow {
        emit(getCachedSongs(query))
    }

    override fun getRecentSongsFlow(limit: Int): Flow<List<SongEntity>> = flow {
        emit(getRecentSongs(limit))
    }

    // Apple Music Catalog Provider (MusicKit public API)
    override suspend fun searchAppleMusic(query: String): Result<List<SongResult>> {
        return try {
            val results = appleMusicProvider.searchCatalog(query)
            Result.success(results)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
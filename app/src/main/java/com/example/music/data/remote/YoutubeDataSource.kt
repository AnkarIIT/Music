package com.example.music.data.remote

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

data class SongResult(
    val id: String,
    val title: String,
    val artist: String,
    val thumbnailUrl: String,
    val audioUrl: String? = null
)

/**
 * Mock YoutubeDataSource for development - returns dummy data.
 * Replace with real NewPipeExtractor implementation when dependency is available.
 */
class YoutubeDataSource {

    suspend fun searchSongs(query: String): List<SongResult> = withContext(Dispatchers.IO) {
        // Simulate network delay
        delay(500)
        
        // Return mock data based on query
        if (query.isBlank()) return@withContext emptyList()
        
        listOf(
            SongResult(
                id = "mock_1",
                title = "$query - Mock Song 1",
                artist = "Mock Artist",
                thumbnailUrl = "https://picsum.photos/seed/mock1/400/400"
            ),
            SongResult(
                id = "mock_2", 
                title = "$query - Mock Song 2",
                artist = "Another Artist",
                thumbnailUrl = "https://picsum.photos/seed/mock2/400/400"
            ),
            SongResult(
                id = "mock_3",
                title = "$query - Mock Song 3",
                artist = "Third Artist",
                thumbnailUrl = "https://picsum.photos/seed/mock3/400/400"
            )
        )
    }

    suspend fun getAudioStreamUrl(videoId: String): String? = withContext(Dispatchers.IO) {
        // Return a mock audio URL (silence.mp3 for testing)
        if (videoId.startsWith("mock_")) {
            "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3"
        } else {
            null
        }
    }
}
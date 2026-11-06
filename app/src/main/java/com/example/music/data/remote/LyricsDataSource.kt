package com.example.music.data.remote

import android.util.Log
import com.example.music.ui.features.lyrics.LyricLine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request

private const val TAG = "LyricsDataSource"

/**
 * Fetches time-synced lyrics from LRCLIB (https://lrclib.net) — free, no API key.
 */
class LyricsDataSource(private val client: OkHttpClient) {

    private val json = Json { ignoreUnknownKeys = true }

    suspend fun fetchLyrics(title: String, artist: String): Result<List<LyricLine>> =
        withContext(Dispatchers.IO) {
            try {
                val url = "https://lrclib.net/api/search".toHttpUrl().newBuilder()
                    .addQueryParameter("track_name", title)
                    .addQueryParameter("artist_name", artist)
                    .build()

                val request = Request.Builder()
                    .url(url)
                    .header("User-Agent", "MusicApp/1.0 (https://github.com/example)")
                    .build()

                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) {
                        return@withContext Result.failure(Exception("HTTP ${response.code}"))
                    }
                    val body = response.body?.string()
                        ?: return@withContext Result.failure(Exception("Empty response"))

                    val results = json.decodeFromString<List<LrcLibResponse>>(body)

                    // Prefer the first result that has synced lyrics; fall back to any result
                    val best = results.firstOrNull { !it.syncedLyrics.isNullOrBlank() }
                        ?: results.firstOrNull()

                    val lines = best?.syncedLyrics?.let { parseLrc(it) }
                        ?: best?.plainLyrics?.let { parsePlainLyrics(it) }
                        ?: emptyList()

                    Result.success(lines)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to fetch lyrics for '$title' by '$artist'", e)
                Result.failure(e)
            }
        }

    private fun parseLrc(lrcContent: String): List<LyricLine> {
        return lrcContent.lines().mapNotNull { line ->
            val match = Regex("\\[(\\d{2}):(\\d{2})[.:](\\d{2,3})\\](.*)").find(line)
            if (match != null) {
                val (min, sec, ms, text) = match.destructured
                val timeMs = min.toLong() * 60000 + sec.toLong() * 1000 + ms.toLong()
                LyricLine(timeMs, text.trim())
            } else {
                null
            }
        }.filter { it.text.isNotBlank() }
    }

    private fun parsePlainLyrics(plainLyrics: String): List<LyricLine> {
        // Plain lyrics have no timestamps; distribute evenly as an approximation
        return plainLyrics.lines().filter { it.isNotBlank() }.mapIndexed { index, text ->
            LyricLine(index.toLong() * 5000, text.trim())
        }
    }

    @kotlinx.serialization.Serializable
    data class LrcLibResponse(
        val id: Long,
        val trackName: String,
        val artistName: String,
        val albumName: String?,
        val duration: Double?,
        val instrumental: Boolean,
        val plainLyrics: String?,
        val syncedLyrics: String?
    )
}
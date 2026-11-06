package com.example.music.data.remote

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppleMusicCatalogProvider @Inject constructor(
    private val okHttpClient: OkHttpClient
) {
    // Optional Developer JWT Token (ES256 signed)
    var developerJwtToken: String = ""

    suspend fun searchCatalog(query: String): List<SongResult> = withContext(Dispatchers.IO) {
        if (developerJwtToken.isBlank()) {
            // Return mock/curated Apple Music sample catalog items if no JWT token is configured yet
            return@withContext listOf(
                SongResult(
                    id = "am_1",
                    title = "As It Was (Apple Music Spatial Edition)",
                    artist = "Harry Styles",
                    thumbnailUrl = "https://images.unsplash.com/photo-1514525253161-7a46d19cd819?w=400",
                    audioUrl = null
                ),
                SongResult(
                    id = "am_2",
                    title = "Blinding Lights (Lossless)",
                    artist = "The Weeknd",
                    thumbnailUrl = "https://images.unsplash.com/photo-1470225620780-dba8ba36b745?w=400",
                    audioUrl = null
                ),
                SongResult(
                    id = "am_3",
                    title = "Levitating (Dolby Atmos)",
                    artist = "Dua Lipa",
                    thumbnailUrl = "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=400",
                    audioUrl = null
                )
            ).filter { it.title.contains(query, ignoreCase = true) || it.artist.contains(query, ignoreCase = true) || query.isBlank() }
        }

        try {
            val encodedQuery = java.net.URLEncoder.encode(query, "UTF-8")
            val request = Request.Builder()
                .url("https://api.music.apple.com/v1/catalog/us/search?term=$encodedQuery&types=songs&limit=25")
                .addHeader("Authorization", "Bearer $developerJwtToken")
                .addHeader("Origin", "https://music.apple.com")
                .build()

            val response = okHttpClient.newCall(request).execute()
            val body = response.body?.string() ?: return@withContext emptyList()
            val json = JSONObject(body)

            val results = mutableListOf<SongResult>()
            val songsObj = json.optJSONObject("results")?.optJSONObject("songs")
            val dataArray = songsObj?.optJSONArray("data") ?: return@withContext emptyList()

            for (i in 0 until dataArray.length()) {
                val item = dataArray.getJSONObject(i)
                val id = item.optString("id")
                val attributes = item.optJSONObject("attributes") ?: continue
                val name = attributes.optString("name")
                val artistName = attributes.optString("artistName")
                val artwork = attributes.optJSONObject("artwork")
                val urlTemplate = artwork?.optString("url") ?: ""
                val artworkUrl = urlTemplate.replace("{w}", "400").replace("{h}", "400")

                results.add(
                    SongResult(
                        id = id,
                        title = name,
                        artist = artistName,
                        thumbnailUrl = artworkUrl,
                        audioUrl = null
                    )
                )
            }
            results
        } catch (e: Exception) {
            emptyList()
        }
    }
}

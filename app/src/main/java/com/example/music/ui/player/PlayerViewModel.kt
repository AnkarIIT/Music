package com.example.music.ui.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.music.data.local.PreferencesManager
import com.example.music.data.remote.SongResult
import com.example.music.data.repository.MusicRepository
import com.example.music.playback.PlayableSong
import com.example.music.playback.PlaybackManager
import com.example.music.ui.features.lyrics.LyricLine
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val playbackManager: PlaybackManager,
    private val repository: MusicRepository,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    // Shared, process-wide playback state (PlaybackManager is a @Singleton)
    val playbackState = playbackManager.playbackState
    val currentPosition = playbackManager.currentPosition
    val duration = playbackManager.duration
    val isPlaying = playbackManager.isPlaying
    val error = playbackManager.error
    val queue = playbackManager.queue
    val queueIndex = playbackManager.queueIndex
    val nowPlaying = playbackManager.nowPlaying

    private val _lyrics = MutableStateFlow<List<LyricLine>>(emptyList())
    val lyrics = _lyrics.asStateFlow()

    private val _isPreparing = MutableStateFlow(false)
    val isPreparing = _isPreparing.asStateFlow()

    // Track failed song resolutions for user feedback
    private val _failedSongs = MutableStateFlow<Map<String, String>>(emptyMap())
    val failedSongs = _failedSongs.asStateFlow()

    private var lastLyricsSongId: String? = null
    private var lyricsJob: Job? = null
    private var lastPlaySongsJob: Job? = null

    init {
        // Auto-load lyrics whenever the song changes
        viewModelScope.launch {
            playbackManager.nowPlaying.collect { song ->
                if (song == null) {
                    _lyrics.value = emptyList()
                    lastLyricsSongId = null
                } else if (song.id != lastLyricsSongId) {
                    lastLyricsSongId = song.id
                    loadLyrics(song)
                    // Update last accessed time for LRU cache eviction
                    repository.updateLastAccessed(song.id)
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        playbackManager.release()
        lyricsJob?.cancel()
        lastPlaySongsJob?.cancel()
    }

    /** Replaces the queue with [songs], starting playback at [startIndex]. */
    fun playSongs(songs: List<SongResult>, startIndex: Int = 0) {
        lastPlaySongsJob?.cancel()
        lastPlaySongsJob = viewModelScope.launch {
            if (songs.isEmpty()) return@launch
            _isPreparing.value = true
            _failedSongs.value = emptyMap()
            try {
                // Resolve stream URLs in parallel, tracking failures per song
                val results = coroutineScope {
                    songs.map { song ->
                        async { song to repository.getAudioUrl(song.id) }
                    }.awaitAll()
                }

                val entries = mutableListOf<PlayableSong>()
                val failed = mutableMapOf<String, String>()

                results.forEach { (song, result) ->
                    result.fold(
                        onSuccess = { url ->
                            entries.add(PlayableSong(song, url))
                        },
                        onFailure = { e ->
                            failed[song.id] = e.message ?: "Unknown error"
                        }
                    )
                }

                _failedSongs.value = failed

                if (entries.isNotEmpty()) {
                    playbackManager.playQueue(entries, startIndex.coerceAtMost(entries.lastIndex))
                } else if (failed.isNotEmpty()) {
                    // All songs failed - surface error
                    playbackManager.clearError()
                    // Error will be shown via failedSongs flow in UI
                }
            } finally {
                _isPreparing.value = false
            }
        }
    }

    /** Play a single song: replaces an empty queue, otherwise inserts after the current one. */
    fun playSong(song: SongResult) {
        if (playbackManager.hasQueue()) {
            playNext(song)
        } else {
            playSongs(listOf(song), 0)
        }
    }

    /** "Play next" — inserts [song] right after the currently playing one. */
    fun playNext(song: SongResult) {
        viewModelScope.launch {
            // Check if song is already in queue with resolved URL
            val currentQueue = playbackManager.queue.value
            val existing = currentQueue.find { it.id == song.id }
            if (existing != null) {
                // Song already in queue - just skip to it
                val index = currentQueue.indexOf(existing)
                if (index >= 0) {
                    playbackManager.skipTo(index)
                    return@launch
                }
            }
            // Resolve URL and play next
            val result = repository.getAudioUrl(song.id)
            result.onSuccess { url ->
                playbackManager.playNext(PlayableSong(song, url))
            }.onFailure { e ->
                _failedSongs.value = _failedSongs.value + (song.id to (e.message ?: "Failed to resolve stream"))
            }
        }
    }

    private fun loadLyrics(song: SongResult) {
        // Check user consent for lyrics search (privacy/GDPR)
        if (!preferencesManager.lyricsConsent.value) {
            _lyrics.value = emptyList()
            return
        }

        // Cancel previous lyrics request to avoid race conditions
        lyricsJob?.cancel()
        lyricsJob = viewModelScope.launch {
            repository.getLyrics(song.title, song.artist)
                .onSuccess { _lyrics.value = it }
                .onFailure { _lyrics.value = emptyList() }
        }
    }

    // ---------- Transport ----------

    fun playPause() = playbackManager.playPause()
    fun seekTo(positionMs: Long) = playbackManager.seekTo(positionMs)
    fun skipNext() = playbackManager.skipNext()
    fun skipPrevious() = playbackManager.skipPrevious()
    fun setRepeatMode(mode: Int) = playbackManager.setRepeatMode(mode)
    fun setShuffle(enabled: Boolean) = playbackManager.setShuffle(enabled)

    // ---------- Queue ----------

    fun playAt(index: Int) = playbackManager.skipTo(index)
    fun moveQueueItem(from: Int, to: Int) = playbackManager.moveQueueItem(from, to)
    fun removeFromQueue(index: Int) = playbackManager.removeFromQueue(index)

    fun clearError() {
        playbackManager.clearError()
        _failedSongs.value = emptyMap()
    }
}
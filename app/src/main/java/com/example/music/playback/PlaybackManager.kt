package com.example.music.playback

import android.content.ComponentName
import android.content.Context
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.Timeline
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.example.music.data.remote.SongResult
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.guava.await
import javax.inject.Inject
import javax.inject.Singleton
import dagger.hilt.android.qualifiers.ApplicationContext

/** A song together with its resolved (playable) stream URL. */
data class PlayableSong(val song: SongResult, val streamUrl: String)

@Singleton
class PlaybackManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private var controller: MediaController? = null
    private var listener: Player.Listener? = null

    private val _playbackState = MutableStateFlow(Player.STATE_IDLE)
    val playbackState = _playbackState.asStateFlow()

    private val _currentPosition = MutableStateFlow(0L)
    val currentPosition = _currentPosition.asStateFlow()

    private val _duration = MutableStateFlow(0L)
    val duration = _duration.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying = _isPlaying.asStateFlow()

    /** Human-readable error message, or null. */
    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private val _queue = MutableStateFlow<List<SongResult>>(emptyList())
    val queue = _queue.asStateFlow()

    private val _queueIndex = MutableStateFlow(-1)
    val queueIndex = _queueIndex.asStateFlow()

    private val _nowPlaying = MutableStateFlow<SongResult?>(null)
    val nowPlaying = _nowPlaying.asStateFlow()

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private var progressJob: Job? = null

    init {
        val appContext = context.applicationContext
        val sessionToken = SessionToken(appContext, ComponentName(appContext, MusicService::class.java))
        val controllerFuture = MediaController.Builder(appContext, sessionToken).buildAsync()
        scope.launch {
            try {
                val ctrl = controllerFuture.await()
                setupController(ctrl)
            } catch (e: Exception) {
                _error.value = "Failed to connect to playback service: ${e.message}"
            }
        }
    }

    private fun setupController(ctrl: MediaController) {
        controller = ctrl

        listener?.let { ctrl.removeListener(it) }

        listener = object : Player.Listener {
            override fun onPlaybackStateChanged(state: Int) {
                _playbackState.value = state
                if (state == Player.STATE_READY) {
                    _duration.value = ctrl.duration
                    startProgressUpdate()
                } else {
                    stopProgressUpdate()
                }
            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                _isPlaying.value = isPlaying
                if (!isPlaying) _currentPosition.value = ctrl.currentPosition
            }

            override fun onPlayerError(error: PlaybackException) {
                _error.value = error.errorCodeName
            }

            override fun onMediaItemTransition(mediaItem: androidx.media3.common.MediaItem?, reason: Int) {
                _currentPosition.value = 0L
                refreshQueue()
            }

            override fun onTimelineChanged(timeline: Timeline, reason: Int) {
                refreshQueue()
            }
        }
        ctrl.addListener(listener!!)

        _playbackState.value = ctrl.playbackState
        _isPlaying.value = ctrl.isPlaying
        _currentPosition.value = ctrl.currentPosition
        _duration.value = ctrl.duration
        refreshQueue()
    }

    private fun refreshQueue() {
        val c = controller ?: return
        try {
            val songs = (0 until c.mediaItemCount).map { c.getMediaItemAt(it).asSongResult() }
            _queue.value = songs
            val idx = c.currentMediaItemIndex
            _queueIndex.value = idx
            _nowPlaying.value = songs.getOrNull(idx)
        } catch (e: Exception) {
            android.util.Log.e("PlaybackManager", "Failed to refresh queue", e)
        }
    }

    private fun androidx.media3.common.MediaItem.asSongResult(): SongResult = SongResult(
        id = mediaId,
        title = mediaMetadata.title?.toString().orEmpty(),
        artist = mediaMetadata.artist?.toString().orEmpty(),
        thumbnailUrl = mediaMetadata.artworkUri?.toString().orEmpty()
    )

    private fun PlayableSong.toMediaItem(): androidx.media3.common.MediaItem = androidx.media3.common.MediaItem.Builder()
        .setMediaId(song.id)
        .setUri(streamUrl)
        .setMediaMetadata(
            androidx.media3.common.MediaMetadata.Builder()
                .setTitle(song.title)
                .setArtist(song.artist)
                .setArtworkUri(song.thumbnailUrl.takeIf { it.isNotBlank() }?.toUri())
                .build()
        )
        .build()

    private fun startProgressUpdate() {
        stopProgressUpdate()
        progressJob = scope.launch {
            while (isActive) {
                controller?.let { _currentPosition.value = it.currentPosition }
                delay(500)
            }
        }
    }

    private fun stopProgressUpdate() {
        progressJob?.cancel()
        progressJob = null
    }

    fun hasQueue(): Boolean = _queue.value.isNotEmpty()

    // ---------- Queue building ----------

    /** Replaces the queue with the given songs and starts playback at [startIndex]. */
    fun playQueue(entries: List<PlayableSong>, startIndex: Int = 0) {
        val c = controller ?: return
        val items = entries.map { it.toMediaItem() }
        c.setMediaItems(items, startIndex.coerceIn(0, entries.lastIndex), 0L)
        
        scope.launch {
            try {
                (c.prepare() as ListenableFuture<Void>).await()
                c.play()
                refreshQueue()
            } catch (e: Exception) {
                _error.value = "Prepare failed: ${e.message}"
            }
        }
    }

    /** Inserts a song right after the currently playing one ("Play next"). */
    fun playNext(entry: PlayableSong) {
        val c = controller ?: return
        val index = if (c.mediaItemCount == 0) 0 else c.currentMediaItemIndex + 1
        c.addMediaItem(index, entry.toMediaItem())
        if (c.mediaItemCount == 1) {
            scope.launch {
                try {
                    (c.prepare() as ListenableFuture<Void>).await()
                    c.play()
                } catch (e: Exception) {
                    _error.value = "Prepare failed: ${e.message}"
                }
            }
        }
        refreshQueue()
    }

    /** Appends a song to the end of the queue. */
    fun addToQueue(entry: PlayableSong) {
        val c = controller ?: return
        c.addMediaItem(entry.toMediaItem())
        if (c.mediaItemCount == 1) {
            scope.launch {
                try {
                    (c.prepare() as ListenableFuture<Void>).await()
                    c.play()
                } catch (e: Exception) {
                    _error.value = "Prepare failed: ${e.message}"
                }
            }
        }
        refreshQueue()
    }

    // ---------- Queue editing ----------

    fun removeFromQueue(index: Int) {
        val c = controller ?: return
        if (index in 0 until c.mediaItemCount) {
            c.removeMediaItem(index)
            refreshQueue()
        }
    }

    fun moveQueueItem(from: Int, to: Int) {
        val c = controller ?: return
        if (from in 0 until c.mediaItemCount && to in 0 until c.mediaItemCount) {
            c.moveMediaItem(from, to)
            refreshQueue()
        }
    }

    /** Jumps to the song at [index] in the queue and starts playing it. */
    fun skipTo(index: Int) {
        val c = controller ?: return
        if (index in 0 until c.mediaItemCount) {
            c.seekTo(index, 0L)
            c.play()
        }
    }

    fun clearQueue() {
        controller?.clearMediaItems()
        refreshQueue()
    }

    // ---------- Transport controls ----------

    fun playPause() {
        controller?.let {
            if (it.isPlaying) it.pause() else it.play()
        }
    }

    fun play() {
        controller?.play()
    }

    fun pause() {
        controller?.pause()
    }

    fun seekTo(positionMs: Long) {
        controller?.seekTo(positionMs)
    }

    fun skipNext() {
        controller?.seekToNext()
    }

    fun skipPrevious() {
        controller?.seekToPrevious()
    }

    fun setRepeatMode(repeatMode: Int) {
        controller?.repeatMode = repeatMode
    }

    fun setShuffle(enabled: Boolean) {
        controller?.shuffleModeEnabled = enabled
    }

    fun clearError() {
        _error.value = null
    }

    fun release() {
        stopProgressUpdate()
        listener?.let { controller?.removeListener(it) }
        controller?.release()
        controller = null
        listener = null
    }
}
package com.example.music.ui.player
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.drag
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.music.ui.features.lyrics.LyricsView
import com.example.music.ui.theme.DarkBackground
import com.example.music.ui.theme.GlassWhite
import com.example.music.ui.theme.GlassWhiteStrong
import com.example.music.ui.theme.PrimaryRed
import com.example.music.ui.theme.TextPrimary
import com.example.music.ui.theme.TextSecondary
import kotlinx.coroutines.launch

@Composable
fun PlayerScreen(
    vm: PlayerViewModel,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val song by vm.nowPlaying.collectAsStateWithLifecycle()
    val isPlaying by vm.isPlaying.collectAsStateWithLifecycle()
    val currentPosition by vm.currentPosition.collectAsStateWithLifecycle()
    val duration by vm.duration.collectAsStateWithLifecycle()
    val lyrics by vm.lyrics.collectAsStateWithLifecycle()
    val isPreparing by vm.isPreparing.collectAsStateWithLifecycle()
    val queueSongs by vm.queue.collectAsStateWithLifecycle()
    val queueIndex by vm.queueIndex.collectAsStateWithLifecycle()
    val error by vm.error.collectAsStateWithLifecycle()

    var showLyrics by remember { mutableStateOf(true) }
    var dragPosition by remember { mutableFloatStateOf(0f) }
    var isDragging by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .verticalScroll(rememberScrollState())
    ) {
        if (song == null) {
            // Empty state
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.MusicNote,
                        contentDescription = null,
                        tint = TextSecondary.copy(alpha = 0.4f),
                        modifier = Modifier.size(80.dp)
                    )
                    Spacer(Modifier.height(16.dp))
                    Text(text = "Nothing Playing", color = TextSecondary, fontSize = 18.sp)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Select a song from Browse, Radio, Library, or Search",
                        color = TextSecondary.copy(alpha = 0.6f),
                        fontSize = 14.sp
                    )
                }
            }
            return
        }

        val current = song!!

        val progress = if (duration > 0) {
            (if (isDragging) dragPosition else currentPosition).toFloat() / duration
        } else 0f

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Header with drag handle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onClose) {
                    Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Close", tint = TextPrimary)
                }
                Text(
                    text = "Now Playing",
                    color = TextSecondary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
                IconButton(onClick = { /* Options menu */ }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "Options", tint = TextPrimary)
                }
            }

            // Error banner
            if (error != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(PrimaryRed.copy(alpha = 0.15f))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Playback error: $error",
                        color = PrimaryRed,
                        fontSize = 13.sp,
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    IconButton(onClick = vm::clearError) {
                        Icon(Icons.Default.Close, contentDescription = "Dismiss error", tint = PrimaryRed)
                    }
                }
            }

            // Album Art - Large, rounded, with shadow
            Box(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 40.dp, vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                if (isPreparing) {
                    CircularProgressIndicator(modifier = Modifier.size(64.dp), color = PrimaryRed)
                }
                AsyncImage(
                    model = current.thumbnailUrl,
                    contentDescription = current.title,
                    modifier = Modifier
                        .size(280.dp)
                        .shadow(24.dp, RoundedCornerShape(24.dp))
                        .clip(RoundedCornerShape(24.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            // Song Info
            Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = current.title,
                    color = TextPrimary,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = current.artist,
                    color = TextSecondary,
                    fontSize = 16.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center
                )
            }

            // Progress Bar
            Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = formatTime(if (isDragging) dragPosition.toLong() else currentPosition),
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                    Text(text = formatTime(duration), color = TextSecondary, fontSize = 12.sp)
                }
                androidx.compose.material3.Slider(
                    value = progress.coerceIn(0f, 1f),
                    onValueChange = {
                        dragPosition = it * duration
                        isDragging = true
                    },
                    onValueChangeFinished = {
                        vm.seekTo(dragPosition.toLong())
                        isDragging = false
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = androidx.compose.material3.SliderDefaults.colors(
                        thumbColor = PrimaryRed,
                        activeTrackColor = PrimaryRed,
                        inactiveTrackColor = GlassWhiteStrong
                    )
                )
            }

            // Playback Controls
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 48.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { vm.setShuffle(true) }) {
                    Icon(Icons.Default.Shuffle, contentDescription = "Shuffle", tint = TextSecondary)
                }

                IconButton(onClick = vm::skipPrevious) {
                    Icon(
                        Icons.Default.SkipPrevious,
                        contentDescription = "Previous",
                        tint = TextPrimary,
                        modifier = Modifier.size(32.dp)
                    )
                }

                // Main Play/Pause Button
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .shadow(16.dp, CircleShape)
                        .clip(CircleShape)
                        .background(PrimaryRed)
                ) {
                    IconButton(onClick = vm::playPause, modifier = Modifier.fillMaxSize()) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isPlaying) "Pause" else "Play",
                            tint = Color.White,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }

                IconButton(onClick = vm::skipNext) {
                    Icon(
                        Icons.Default.SkipNext,
                        contentDescription = "Next",
                        tint = TextPrimary,
                        modifier = Modifier.size(32.dp)
                    )
                }

                IconButton(onClick = { vm.setRepeatMode(1) }) {
                    Icon(Icons.Default.Repeat, contentDescription = "Repeat", tint = TextSecondary)
                }
            }

            // Lyrics / Queue Toggle
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                TextButton(onClick = { showLyrics = true }) {
                    Text(
                        "Lyrics",
                        color = if (showLyrics) PrimaryRed else TextSecondary,
                        fontWeight = if (showLyrics) FontWeight.Bold else FontWeight.Normal
                    )
                }
                TextButton(onClick = { showLyrics = false }) {
                    Text(
                        "Queue (${queueSongs.size})",
                        color = if (!showLyrics) PrimaryRed else TextSecondary,
                        fontWeight = if (!showLyrics) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }

            // Lyrics or Queue Content
            if (showLyrics) {
                LyricsView(
                    lyrics = lyrics,
                    currentPositionMs = if (isDragging) dragPosition.toLong() else currentPosition,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(320.dp)
                )
            } else {
                QueueList(
                    songs = queueSongs,
                    currentIndex = queueIndex,
                    onMove = vm::moveQueueItem,
                    onRemove = vm::removeFromQueue,
                    onSelect = vm::playAt,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(320.dp)
                )
            }
        }
    }
}

private fun formatTime(ms: Long): String {
    val totalSeconds = (ms.coerceAtLeast(0)) / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%02d:%02d".format(minutes, seconds)
}
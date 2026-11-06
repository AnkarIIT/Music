package com.example.music.ui.player
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage
import com.example.music.data.remote.SongResult
import com.example.music.ui.theme.AppleRed
import com.example.music.ui.theme.AppleShapes
import com.example.music.ui.theme.GlassWhite
import com.example.music.ui.theme.TextPrimary
import com.example.music.ui.theme.TextSecondary

@Composable
fun QueueList(
    songs: List<SongResult>,
    currentIndex: Int,
    onMove: (from: Int, to: Int) -> Unit,
    onRemove: (index: Int) -> Unit,
    onSelect: (index: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()

    // Keep the currently playing item visible
    LaunchedEffect(currentIndex, songs.size) {
        if (currentIndex in songs.indices) {
            listState.animateScrollToItem(currentIndex)
        }
    }

    var draggingIndex by remember { mutableStateOf<Int?>(null) }
    var dragOffset by remember { mutableFloatStateOf(0f) }

    LazyColumn(
        state = listState,
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        itemsIndexed(songs) { index, song ->
            val isDragging = draggingIndex == index
            val isCurrent = index == currentIndex

            val itemModifier = Modifier
                .fillMaxWidth()
                .zIndex(if (isDragging) 1f else 0f)
                .graphicsLayer {
                    translationY = if (isDragging) dragOffset else 0f
                }
                .then(
                    if (isDragging) {
                        Modifier
                            .shadow(8.dp, RoundedCornerShape(12.dp))
                            .alpha(0.95f)
                    } else Modifier
                )
                .clip(RoundedCornerShape(12.dp))
                .background(GlassWhite)
                .clickable { onSelect(index) }
                .pointerInput(index, songs.size) {
                    detectDragGesturesAfterLongPress(
                        onDragStart = {
                            draggingIndex = index
                            dragOffset = 0f
                        },
                        onDrag = { change, dragAmount ->
                            change.consume()
                            dragOffset += dragAmount.y
                            val current = draggingIndex
                            if (current != null) {
                                val myHeight = size.height.toFloat()
                                if (myHeight > 0f) {
                                    when {
                                        dragOffset > myHeight && current < songs.lastIndex -> {
                                            onMove(current, current + 1)
                                            draggingIndex = current + 1
                                            dragOffset -= myHeight
                                        }
                                        dragOffset < -myHeight && current > 0 -> {
                                            onMove(current, current - 1)
                                            draggingIndex = current - 1
                                            dragOffset += myHeight
                                        }
                                    }
                                }
                            }
                        },
                        onDragEnd = {
                            draggingIndex = null
                            dragOffset = 0f
                        },
                        onDragCancel = {
                            draggingIndex = null
                            dragOffset = 0f
                        }
                    )
                }

            Box(modifier = itemModifier) {
                QueueRow(
                    song = song,
                    isCurrent = isCurrent,
                    onRemove = { onRemove(index) }
                )
            }
        }
    }
}

@Composable
private fun QueueRow(
    song: SongResult,
    isCurrent: Boolean,
    onRemove: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        AsyncImage(
            model = song.thumbnailUrl,
            contentDescription = song.title,
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(12.dp)),
            contentScale = ContentScale.Crop
        )

        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = song.title,
                color = if (isCurrent) AppleRed else TextPrimary,
                fontSize = 14.sp,
                fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = song.artist,
                color = TextSecondary,
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        if (isCurrent) {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = "Playing",
                tint = AppleRed,
                modifier = Modifier.size(20.dp)
            )
        }

        IconButton(onClick = onRemove) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Remove from queue",
                tint = TextSecondary,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
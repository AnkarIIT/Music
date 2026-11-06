package com.example.music.ui.features.lyrics

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.music.ui.theme.AppleRed
import com.example.music.ui.theme.TextPrimary
import com.example.music.ui.theme.TextSecondary
import kotlinx.coroutines.launch

data class LyricLine(
    val startTimeMs: Long,
    val text: String
)

@Composable
fun LyricsView(
    lyrics: List<LyricLine>,
    currentPositionMs: Long,
    modifier: Modifier = Modifier
) {
    if (lyrics.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.MusicNote,
                    contentDescription = "Lyrics",
                    tint = TextSecondary.copy(alpha = 0.5f),
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "No lyrics available",
                    color = TextSecondary,
                    fontSize = 16.sp
                )
            }
        }
        return
    }

    val listState = rememberLazyListState()
    
    // Memoize active index to avoid O(n) search on every recomposition
    val activeIndex by remember(currentPositionMs) {
        derivedStateOf {
            lyrics.indexOfLast { it.startTimeMs <= currentPositionMs }
                .coerceAtLeast(0)
                .coerceAtMost(lyrics.size - 1)
        }
    }

    // Auto-scroll to the active line
    LaunchedEffect(activeIndex) {
        if (activeIndex >= 0 && activeIndex < lyrics.size) {
            // Calculate offset to center the active line in viewport
            val viewportCenter = listState.layoutInfo.viewportSize.height / 2
            listState.animateScrollToItem(
                index = activeIndex,
                scrollOffset = -viewportCenter
            )
        }
    }

    LazyColumn(
        state = listState,
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 400.dp, horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        itemsIndexed(lyrics) { index, line ->
            val isActive = index == activeIndex
            val alpha by animateFloatAsState(if (isActive) 1f else 0.5f)
            val scale by animateFloatAsState(if (isActive) 1.2f else 1f)
            val color = if (isActive) TextPrimary else TextSecondary

            Text(
                text = line.text,
                color = color,
                fontSize = (24 * scale).sp,
                fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .alpha(alpha)
            )
        }
    }
}
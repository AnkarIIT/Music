package com.example.music.ui.screens.home
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.annotation.OptIn
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.music.data.local.SongEntity
import com.example.music.data.remote.SongResult
import com.example.music.ui.components.CircularAlbumArt
import com.example.music.ui.theme.AppleRed
import com.example.music.ui.theme.AppleShapes
import com.example.music.ui.theme.DarkBackground
import com.example.music.ui.theme.GlassWhite
import com.example.music.ui.theme.TextPrimary
import com.example.music.ui.theme.TextSecondary
import androidx.navigation.NavController
import androidx.compose.ui.draw.clip

@Composable
fun HomeScreen(
    onPlaySong: (SongResult) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val recentSongs by viewModel.recentSongs.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize()) {
        // Hero section with gradient background
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFF2C1010), Color.Transparent)
                    )
                )
        ) {
            Column(
                modifier = Modifier.fillMaxSize().padding(24.dp),
                verticalArrangement = Arrangement.Bottom
            ) {
                Text(
                    text = "Listen Now",
                    color = TextPrimary,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Your personalized music feed",
                    color = TextSecondary,
                    fontSize = 14.sp
                )
            }
            // Refresh button
            IconButton(
                onClick = viewModel::refresh,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 16.dp, end = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Refresh",
                    tint = TextSecondary
                )
            }
        }

        // Content
        when {
            isLoading && recentSongs.isEmpty() -> Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = AppleRed)
            }
            else -> Column(modifier = Modifier.fillMaxSize()) {
                if (recentSongs.isNotEmpty()) {
                    // Made For You section (first 4 items)
                    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
                        SectionHeader(title = "Made For You", onSeeAll = { /* Navigate */ })
                        Spacer(Modifier.height(8.dp))
                        LazyColumn(
                            contentPadding = PaddingValues(vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(recentSongs.take(4), key = { it.id }) { song ->
                                RecentPlayCard(
                                    song = song.toSongResult(),
                                    onClick = { onPlaySong(song.toSongResult()) }
                                )
                            }
                        }
                    }

                    // Recently Played section (remaining items)
                    if (recentSongs.size > 4) {
                        Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
                            SectionHeader(title = "Recently Played", onSeeAll = { /* Navigate */ })
                            Spacer(Modifier.height(8.dp))
                            LazyColumn(
                                contentPadding = PaddingValues(vertical = 8.dp, horizontal = 16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(
                                    items = recentSongs.drop(4),
                                    key = { it.id }
                                ) { song ->
                                    RecentSongItem(
                                        song = song.toSongResult(),
                                        onClick = { onPlaySong(song.toSongResult()) }
                                    )
                                }
                            }
                        }
                    }
                } else {
                    // Empty state
                    Box(
                        modifier = Modifier.fillMaxSize().padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.MusicNote,
                                contentDescription = "Music",
                                tint = TextSecondary.copy(alpha = 0.5f),
                                modifier = Modifier.size(64.dp)
                            )
                            Text(
                                text = "No recent plays yet",
                                color = TextSecondary,
                                fontSize = 16.sp
                            )
                            Text(
                                text = "Search for a song to get started",
                                color = TextSecondary.copy(alpha = 0.6f),
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    onSeeAll: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            color = TextPrimary,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        androidx.compose.material3.TextButton(onClick = onSeeAll) {
            Text(
                text = "See All",
                color = TextSecondary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun RecentPlayCard(
    song: SongResult,
    onClick: () -> Unit
) {
    val cardWidth = 180.dp
    val cardHeight = 240.dp

    Card(
        modifier = Modifier
            .width(cardWidth)
            .height(cardHeight),
        colors = CardDefaults.cardColors(containerColor = GlassWhite),
        shape = RoundedCornerShape(24.dp),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Artwork with play overlay
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(24.dp))
            ) {
                CircularAlbumArt(
                    imageUrl = song.thumbnailUrl,
                    modifier = Modifier.fillMaxSize()
                )
            }
            Column(modifier = Modifier.padding(horizontal = 12.dp)) {
                Text(
                    text = song.title,
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = song.artist,
                    color = TextSecondary,
                    fontSize = 13.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun RecentSongItem(
    song: SongResult,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = GlassWhite),
        shape = RoundedCornerShape(16.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            CircularAlbumArt(
                imageUrl = song.thumbnailUrl,
                modifier = Modifier.size(56.dp)
            )
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = song.title,
                    color = TextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = song.artist,
                    color = TextSecondary,
                    fontSize = 13.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = "Play",
                tint = TextPrimary
            )
        }
    }
}

fun SongEntity.toSongResult(): SongResult {
    return SongResult(
        id = this.id,
        title = this.title,
        artist = this.artist,
        thumbnailUrl = this.thumbnailUrl,
        audioUrl = this.audioUrl
    )
}
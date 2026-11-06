package com.example.music.ui.screens.browse

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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.music.data.remote.SongResult
import com.example.music.ui.components.CircularAlbumArt
import com.example.music.ui.theme.AppleRed
import com.example.music.ui.theme.AppleShapes
import com.example.music.ui.theme.GlassWhite
import com.example.music.ui.theme.TextPrimary
import com.example.music.ui.theme.TextSecondary
import kotlinx.coroutines.delay
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.music.R
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.draw.clip

@Composable
fun BrowseScreen(
    onPlaySong: (SongResult) -> Unit,
    viewModel: BrowseViewModel = hiltViewModel()
) {
    val newReleases by viewModel.newReleases.collectAsStateWithLifecycle()
    val charts by viewModel.charts.collectAsStateWithLifecycle()
    val genres by viewModel.genres.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Header
        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
            Text(
                text = "Browse",
                color = TextPrimary,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
        }

        if (isLoading && newReleases.isEmpty() && charts.isEmpty() && genres.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 96.dp),
                contentAlignment = Alignment.Center
            ) {
                androidx.compose.material3.CircularProgressIndicator(color = AppleRed)
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // New Releases Section
                if (newReleases.isNotEmpty()) {
                    item {
                        SectionHeader(
                            title = "New Releases",
                            onSeeAll = { /* Navigate to all new releases */ }
                        )
                    }
                    item {
                        HorizontalSongList(
                            songs = newReleases,
                            onPlaySong = onPlaySong
                        )
                    }
                }

                // Charts Section
                if (charts.isNotEmpty()) {
                    item {
                        SectionHeader(
                            title = "Top Charts",
                            onSeeAll = { /* Navigate to charts */ }
                        )
                    }
                    item {
                        HorizontalSongList(
                            songs = charts,
                            onPlaySong = onPlaySong
                        )
                    }
                }

                // Genres Section
                if (genres.isNotEmpty()) {
                    item {
                        SectionHeader(
                            title = "Genres",
                            onSeeAll = { /* Navigate to genres */ }
                        )
                    }
                    item {
                        HorizontalSongList(
                            songs = genres,
                            onPlaySong = onPlaySong
                        )
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
private fun HorizontalSongList(
    songs: List<SongResult>,
    onPlaySong: (SongResult) -> Unit
) {
    androidx.compose.foundation.lazy.LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(start = 16.dp, end = 16.dp)
    ) {
        items(songs, key = { it.id }) { song ->
            BrowseSongCard(
                song = song,
                onClick = { onPlaySong(song) }
            )
        }
    }
}

@Suppress("INTERNAL_API_USAGE")
@Composable
private fun BrowseSongCard(
    song: SongResult,
    onClick: () -> Unit
) {
    val width = 160.dp
    val height = 220.dp

    Card(
        modifier = Modifier
            .width(width)
            .height(height),
        colors = CardDefaults.cardColors(containerColor = GlassWhite),
        shape = RoundedCornerShape(24.dp),
        onClick = onClick
    ) {
        Column {
            // Artwork
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(song.thumbnailUrl)
                    .crossfade(true)
                    .placeholder(R.drawable.placeholder_album_art)
                    .error(R.drawable.placeholder_album_art)
                    .build(),
                contentDescription = song.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(Modifier.height(12.dp))
            // Title
            androidx.compose.material3.Text(
                text = song.title,
                color = com.example.music.ui.theme.TextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                modifier = Modifier.padding(horizontal = 12.dp)
            )
            // Artist
            androidx.compose.material3.Text(
                text = song.artist,
                color = com.example.music.ui.theme.TextSecondary,
                fontSize = 12.sp,
                maxLines = 1,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
            )
        }
    }
}
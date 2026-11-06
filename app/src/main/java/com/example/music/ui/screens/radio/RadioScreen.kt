package com.example.music.ui.screens.radio
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
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Radio
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
import androidx.compose.ui.graphics.Color
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
import com.example.music.ui.theme.DarkBackground
import com.example.music.ui.theme.GlassWhite
import com.example.music.ui.theme.PrimaryRed
import com.example.music.ui.theme.TextPrimary
import com.example.music.ui.theme.TextSecondary
import androidx.navigation.NavController
import androidx.annotation.OptIn
import androidx.compose.ui.draw.clip

@Composable
fun RadioScreen(
    onPlaySong: (SongResult) -> Unit,
    viewModel: RadioViewModel = hiltViewModel()
) {
    val stations by viewModel.stations.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Header with Apple Music Radio branding
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(
                    androidx.compose.ui.graphics.Brush.verticalGradient(
                        colors = listOf(AppleRed.copy(alpha = 0.3f), Color.Transparent)
                    )
                )
        ) {
            Column(
                modifier = Modifier.fillMaxSize().padding(24.dp),
                verticalArrangement = Arrangement.Bottom
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Radio,
                        contentDescription = "Radio",
                        tint = AppleRed,
                        modifier = Modifier.size(32.dp)
                    )
                    Column {
                        Text(
                            text = "Radio",
                            color = TextPrimary,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Apple Music Radio",
                            color = TextSecondary,
                            fontSize = 14.sp
                        )
                    }
                }
                Spacer(Modifier.height(16.dp))
                Text(
                    text = "Expert-hosted stations and personalized music radio",
                    color = TextSecondary,
                    fontSize = 16.sp
                )
            }
        }

        // Content
        if (isLoading && stations.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(bottom = 96.dp),
                contentAlignment = Alignment.Center
            ) {
                androidx.compose.material3.CircularProgressIndicator(color = AppleRed)
            }
        } else {
            Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
                // Featured Station - Apple Music 1
                FeaturedStationCard(
                    onClick = { /* Play Apple Music 1 */ }
                )

                Spacer(Modifier.height(16.dp))

                // Personalized Stations
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Personalized Stations",
                        color = TextPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (stations.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize().padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Radio,
                                contentDescription = "Radio",
                                tint = TextSecondary.copy(alpha = 0.5f),
                                modifier = Modifier.size(48.dp)
                            )
                            Text(
                                text = "Your personalized stations will appear here",
                                color = TextSecondary,
                                fontSize = 16.sp
                            )
                            Text(
                                text = "Listen to more music to get personalized stations",
                                color = TextSecondary.copy(alpha = 0.7f),
                                fontSize = 14.sp
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(stations, key = { it.id }) { station ->
                            RadioStationCard(
                                station = station,
                                onClick = { onPlaySong(station) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FeaturedStationCard(onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = GlassWhite),
        shape = RoundedCornerShape(24.dp),
        onClick = onClick
    ) {
        Box(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Apple Music 1 artwork placeholder
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(AppleRed.copy(alpha = 0.3f))
                ) {
                    androidx.compose.material3.Icon(
                        imageVector = Icons.Default.Radio,
                        contentDescription = "Apple Music 1",
                        tint = AppleRed,
                        modifier = Modifier
                            .fillMaxSize()
                            .align(Alignment.Center)
                    )
                }
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Apple Music 1",
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Global live radio — now playing",
                        color = TextSecondary,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "Hosted by Zane Lowe, Ebro Darden, and more",
                        color = TextSecondary.copy(alpha = 0.7f),
                        fontSize = 12.sp
                    )
                }
                androidx.compose.material3.Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Play",
                    tint = AppleRed,
                    modifier = Modifier.size(24.dp).padding(end = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun RadioStationCard(
    station: SongResult,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = GlassWhite),
        shape = RoundedCornerShape(12.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            CircularAlbumArt(
                imageUrl = station.thumbnailUrl,
                modifier = Modifier.size(56.dp)
            )
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = station.title,
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = station.artist,
                    color = TextSecondary,
                    fontSize = 13.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            androidx.compose.material3.Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = "Play",
                tint = TextSecondary,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
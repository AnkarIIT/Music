package com.example.music.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.music.data.remote.SongResult
import com.example.music.ui.theme.GlassWhite
import com.example.music.ui.theme.PrimaryRed
import com.example.music.ui.theme.TextPrimary
import com.example.music.ui.theme.TextSecondary

data class RadioStation(
    val id: String,
    val title: String,
    val subtitle: String,
    val coverUrl: String,
    val gradientColors: List<Color>
)

val sampleRadioStations = listOf(
    RadioStation(
        "station_1",
        "Apple Music 1",
        "Global Pop & Hits Live",
        "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=400",
        listOf(Color(0xFFE50914), Color(0xFF8B0000))
    ),
    RadioStation(
        "station_2",
        "Apple Music Country",
        "Today's Country Hits",
        "https://images.unsplash.com/photo-1470225620780-dba8ba36b745?w=400",
        listOf(Color(0xFFD2691E), Color(0xFF8B4513))
    ),
    RadioStation(
        "station_3",
        "Chill Lofi Radio",
        "Beats to relax/study to",
        "https://images.unsplash.com/photo-1514525253161-7a46d19cd819?w=400",
        listOf(Color(0xFF4682B4), Color(0xFF191970))
    )
)

@Composable
fun RadioStationsSection(onSelectStation: (RadioStation) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Radio Stations",
                color = TextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Icon(Icons.Default.Radio, contentDescription = null, tint = PrimaryRed)
        }
        Spacer(modifier = Modifier.height(12.dp))
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(sampleRadioStations) { station ->
                Card(
                    modifier = Modifier
                        .width(180.dp)
                        .height(140.dp)
                        .clickable { onSelectStation(station) },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = GlassWhite)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Brush.linearGradient(station.gradientColors))
                            .padding(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Bottom
                        ) {
                            Text(
                                text = station.title,
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = station.subtitle,
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 12.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }
    }
}

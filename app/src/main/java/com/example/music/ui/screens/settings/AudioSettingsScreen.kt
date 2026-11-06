package com.example.music.ui.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.music.ui.theme.GlassWhite
import com.example.music.ui.theme.PrimaryRed
import com.example.music.ui.theme.TextPrimary
import com.example.music.ui.theme.TextSecondary
import androidx.navigation.NavController

@Composable
fun AudioSettingsScreen(navController: NavController) {
    var losslessEnabled by remember { mutableStateOf(true) }
    var soundCheckEnabled by remember { mutableStateOf(true) }
    var DolbyAtmosEnabled by remember { mutableStateOf(true) }
    var selectedPreset by remember { mutableStateOf("Bass Booster") }

    val presets = listOf("Flat", "Bass Booster", "Electronic", "Acoustic", "Rock")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextPrimary)
            }
            Spacer(Modifier.width(8.dp))
            Text(
                text = "Audio Quality & Equalizer",
                color = TextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(Modifier.height(16.dp))

        // Quality Settings Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = GlassWhite),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                AudioSettingToggleRow(
                    title = "Lossless Audio",
                    subtitle = "Up to 24-bit / 192 kHz",
                    checked = losslessEnabled,
                    onCheckedChange = { losslessEnabled = it }
                )
                AudioSettingToggleRow(
                    title = "Dolby Atmos",
                    subtitle = "Immersive spatial audio experience",
                    checked = DolbyAtmosEnabled,
                    onCheckedChange = { DolbyAtmosEnabled = it }
                )
                AudioSettingToggleRow(
                    title = "Sound Check",
                    subtitle = "Normalize volume level across tracks",
                    checked = soundCheckEnabled,
                    onCheckedChange = { soundCheckEnabled = it }
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Equalizer, contentDescription = null, tint = PrimaryRed)
            Spacer(Modifier.width(8.dp))
            Text(
                text = "Equalizer Presets",
                color = TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(Modifier.height(12.dp))

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            presets.forEach { preset ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (selectedPreset == preset) PrimaryRed.copy(alpha = 0.2f) else GlassWhite
                    ),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                    onClick = { selectedPreset = preset }
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = preset,
                            color = if (selectedPreset == preset) PrimaryRed else TextPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                        if (selectedPreset == preset) {
                            Text(text = "Active", color = PrimaryRed, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AudioSettingToggleRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Medium)
            Text(text = subtitle, color = TextSecondary, fontSize = 12.sp)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(checkedThumbColor = PrimaryRed, checkedTrackColor = PrimaryRed.copy(alpha = 0.5f))
        )
    }
}

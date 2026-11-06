package com.example.music.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ============================================================
// Color Schemes (Apple Music) - Compatible with Compose 1.6.1
// ============================================================

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryRed,                    // #F82C44 - brand red
    secondary = AppleBlue,                   // #0091FF - links, CTAs
    tertiary = AppleBlue,                    // Same as secondary
    background = DarkBackground,             // #262628
    surface = DarkSurface,                   // #1C1C1E
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = DarkOnBackground,         // #FFFFFF
    onSurface = DarkOnSurface,               // #EBEBF5
    onSurfaceVariant = DarkSecondaryLabel,   // 60% white
    outline = DarkOutline,                   // #545458
    inverseSurface = LightSurface,
    inverseOnSurface = LightOnSurface,
    inversePrimary = AppleRed
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryRedLight,               // #FF3B30
    secondary = AppleBlueLight,              // #0088FF
    tertiary = AppleBlueLight,
    background = LightBackground,            // #F5F5F7
    surface = LightSurface,                  // #FFFFFF
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = LightOnBackground,        // #000000
    onSurface = LightOnSurface,              // #3C3C43
    onSurfaceVariant = LightSecondaryLabel,  // 60% black
    outline = LightOutline,                  // #79747E
    inverseSurface = DarkSurface,
    inverseOnSurface = DarkOnSurface,
    inversePrimary = AppleRedSystem
)

// ============================================================
// Main Theme
// ============================================================

@Composable
fun MusicAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val typography = Typography

    // Force status bar / nav bar colors for edge-to-edge
    SideEffect {
        // This runs on composition; actual system bar styling is in MainActivity
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = typography,
        content = content
    )
}

/**
 * Glassmorphic Surface - simplified for Compose 1.6.x
 * Uses simple semi-transparent background for all API levels.
 */
@Composable
fun GlassmorphicSurface(
    modifier: androidx.compose.ui.Modifier = androidx.compose.ui.Modifier,
    content: @Composable () -> Unit
) {
    androidx.compose.foundation.layout.Box(
        modifier = modifier
            .background(GlassWhite)
            .border(1.dp, GlassWhiteStrong, androidx.compose.foundation.shape.RoundedCornerShape(16.dp))
    ) {
        content()
    }
}

/**
 * Glassmorphic Card - simpler card with glass background
 */
@Composable
fun GlassmorphicCard(
    modifier: androidx.compose.ui.Modifier = androidx.compose.ui.Modifier,
    elevation: androidx.compose.material3.CardElevation = androidx.compose.material3.CardDefaults.cardElevation(
        defaultElevation = 0.dp
    ),
    content: @Composable androidx.compose.foundation.layout.ColumnScope.() -> Unit
) {
    androidx.compose.material3.Card(
        modifier = modifier,
        colors = androidx.compose.material3.CardDefaults.cardColors(
            containerColor = GlassWhite
        ),
        elevation = elevation,
        shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
        content = content
    )
}

/**
 * Apple-style rounded corner shapes
 */
object AppleShapes {
    val Xs = androidx.compose.foundation.shape.RoundedCornerShape(3.dp)    // Tiny badges
    val Small = androidx.compose.foundation.shape.RoundedCornerShape(5.dp) // Mini player thumb, chips
    val Medium = androidx.compose.foundation.shape.RoundedCornerShape(7.dp) // Standard cards, buttons
    val Large = androidx.compose.foundation.shape.RoundedCornerShape(10.dp) // Medium cards
    val XLarge = androidx.compose.foundation.shape.RoundedCornerShape(14.dp) // Large cards
    val XXLarge = androidx.compose.foundation.shape.RoundedCornerShape(24.dp) // Now Playing artwork
    val Pill = androidx.compose.foundation.shape.RoundedCornerShape(9999.dp) // Full pill
}
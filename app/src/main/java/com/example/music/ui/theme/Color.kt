package com.example.music.ui.theme

import androidx.compose.ui.graphics.Color

// ============================================================
// Apple Music Dark Theme (Primary)
// Source: com.apple.android.music v7.0.0 APK analysis
// ============================================================

// Backgrounds
val DarkBackground = Color(0xFF262628)           // apple_material_background_dark
val DarkSurface = Color(0xFF1C1C1E)              // secondary_background_color
val DarkSurfaceVariant = Color(0xFF2C2C2E)       // tertiary_background_color
val DarkSurfaceContainer = Color(0xFF1C1C1E)     // surface_container

// Brand Colors
val AppleRed = Color(0xFFF82C44)                 // Primary accent (from info.json)
val AppleRedSystem = Color(0xFFFF3B30)           // system_red - fallback
val AppleBlue = Color(0xFF0091FF)                // key_color_dark / system_blue
val AppleBlueLight = Color(0xFF0088FF)           // key_color_light

// Text Colors (Dark)
val DarkOnBackground = Color(0xFFFFFFFF)         // label_color_dark
val DarkOnSurface = Color(0xFFEBEBF5)            // 99% white
val DarkSecondaryLabel = Color(0x99EBEBF5)       // 60% white - secondary_label_color_dark
val DarkTertiaryLabel = Color(0x4DEBEBF5)        // 30% white - tertiary_label_color_dark
val DarkQuaternaryLabel = Color(0x29EBEBF5)      // 16% white - quaternary_label_color_dark

// Fills & Strokes (Dark)
val DarkQuaternaryFill = Color(0x2E767680)       // quaternary_fill_color dark
val DarkTertiaryFill = Color(0x3D767680)         // tertiary_fill_color dark
val DarkPrimaryFill = Color(0x5C787880)          // primary_fill_color dark
val DarkSeparator = Color(0xFF38383A)            // separator_color
val DarkOutline = Color(0xFF545458)              // outline
val DarkAppleMaterialStroke = Color(0x33FFFFFF)  // apple_material_stroke_dark

// ============================================================
// Apple Music Light Theme
// ============================================================

val LightBackground = Color(0xFFF5F5F7)          // apple_material_background_light
val LightSurface = Color(0xFFFFFFFF)
val LightSurfaceVariant = Color(0xFFEFEFF4)
val LightSurfaceContainer = Color(0xFFFFFFFF)

val LightOnBackground = Color(0xFF000000)
val LightOnSurface = Color(0xFF3C3C43)
val LightSecondaryLabel = Color(0x993C3C43)      // 60% black
val LightTertiaryLabel = Color(0x4D3C3C43)       // 30% black
val LightQuaternaryLabel = Color(0x2E3C3C43)     // 18% black

val LightQuaternaryFill = Color(0x14747480)
val LightTertiaryFill = Color(0x3D767680)        // same value works
val LightPrimaryFill = Color(0x5C787880)
val LightSeparator = Color(0xFFEFEFEF)
val LightOutline = Color(0xFF79747E)
val LightAppleMaterialStroke = Color(0x1A000000)

// ============================================================
// Gradients
// ============================================================

val GradientStart = Color(0xFF2C1010)  // Deep red start
val GradientEnd = Color(0xFF1C1C1E)    // Surface end
val LightGradientStart = Color(0xFFFFEBEE)
val LightGradientEnd = Color(0xFFF5F5F7)

// ============================================================
// Semantic aliases for easy migration
// ============================================================

// Primary brand red
val PrimaryRed = AppleRed
val PrimaryRedLight = AppleRedSystem

// Legacy names (for backward compat during transition)
@Deprecated("Use DarkBackground / LightBackground", ReplaceWith("DarkBackground"))
val SurfaceColor = DarkSurface

@Deprecated("Use DarkOnSurface / LightOnSurface", ReplaceWith("DarkOnSurface"))
val TextPrimary = DarkOnSurface

@Deprecated("Use DarkSecondaryLabel / LightSecondaryLabel", ReplaceWith("DarkSecondaryLabel"))
val TextSecondary = DarkSecondaryLabel

@Deprecated("Use DarkAppleMaterialStroke / LightAppleMaterialStroke", ReplaceWith("DarkAppleMaterialStroke"))
val GlassWhite = DarkAppleMaterialStroke

@Deprecated("Use DarkPrimaryFill / LightPrimaryFill", ReplaceWith("DarkPrimaryFill"))
val GlassWhiteStrong = DarkPrimaryFill
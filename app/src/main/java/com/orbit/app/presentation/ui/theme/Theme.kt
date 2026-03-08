package com.orbit.app.presentation.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val OrbitColorScheme = darkColorScheme(
    primary        = RoseGold,
    onPrimary      = DeepSpace,
    secondary      = NebulaPurple,
    onSecondary    = StarWhite,
    tertiary       = AuroraBlue,
    background     = DeepSpace,
    onBackground   = StarWhite,
    surface        = Midnight,
    onSurface      = StarWhite,
    surfaceVariant = CosmicBlue,
    onSurfaceVariant = StarDim,
    error          = ErrorRed,
    onError        = StarWhite,
)

@Composable
fun OrbitTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = OrbitColorScheme,
        typography  = OrbitTypography,
        content     = content
    )
}

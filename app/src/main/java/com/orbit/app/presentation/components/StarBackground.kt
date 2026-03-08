package com.orbit.app.presentation.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
//import androidx.compose.ui.draw.alpha
//import com.orbit.app.presentation.ui.theme.DeepSpace
import com.orbit.app.presentation.ui.theme.StarGlow
import com.orbit.app.utils.DayPhase
//import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

data class Star(
    val x: Float, val y: Float,
    val radius: Float,
    val twinkleOffset: Float   // phase offset for animation
)

// Pisces constellation — normalised 0..1 coordinates (11 stars)
val PISCES_STARS = listOf(
    Offset(0.12f, 0.30f), Offset(0.15f, 0.22f), Offset(0.20f, 0.18f),
    Offset(0.25f, 0.22f), Offset(0.28f, 0.30f), Offset(0.32f, 0.35f),
    Offset(0.38f, 0.33f), Offset(0.42f, 0.27f), Offset(0.45f, 0.20f),
    Offset(0.48f, 0.24f), Offset(0.50f, 0.32f)
)
val PISCES_LINES = listOf(
    0 to 1, 1 to 2, 2 to 3, 3 to 4, 4 to 5,
    5 to 6, 6 to 7, 7 to 8, 8 to 9, 9 to 10
)

// Gemini constellation — 10 stars (the twins)
val GEMINI_STARS = listOf(
    Offset(0.55f, 0.15f), Offset(0.58f, 0.22f), Offset(0.60f, 0.30f),
    Offset(0.63f, 0.38f), Offset(0.65f, 0.45f), Offset(0.70f, 0.15f),
    Offset(0.72f, 0.23f), Offset(0.74f, 0.31f), Offset(0.76f, 0.39f),
    Offset(0.62f, 0.27f)
)
val GEMINI_LINES = listOf(
    0 to 1, 1 to 2, 2 to 3, 3 to 4,
    5 to 6, 6 to 7, 7 to 8,
    1 to 6, 9 to 2, 9 to 7
)

@Composable
fun StarBackground(
    modifier: Modifier = Modifier,
    phase: DayPhase
) {
    val stars = remember {
        List(120) {
            Star(
                x = Random.nextFloat(),
                y = Random.nextFloat(),
                radius = Random.nextFloat() * 2f + 0.5f,
                twinkleOffset = Random.nextFloat() * 6.28f
            )
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "stars")
    val time by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6.28f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "twinkle"
    )

    // Phase-based sky color tint
    val skyAlpha = when (phase) {
        DayPhase.MORNING   -> 0.7f
        DayPhase.AFTERNOON -> 0.4f
        DayPhase.EVENING   -> 0.85f
        DayPhase.NIGHT     -> 1.0f
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        // Draw stars
        stars.forEach { star ->
            val twinkle = (sin(time + star.twinkleOffset) + 1f) / 2f
            val alpha = (0.3f + twinkle * 0.7f) * skyAlpha
            drawCircle(
                color = StarGlow.copy(alpha = alpha),
                radius = star.radius * (0.8f + twinkle * 0.4f),
                center = Offset(star.x * size.width, star.y * size.height)
            )
        }

        // Draw Pisces constellation
        drawConstellation(
            stars = PISCES_STARS,
            lines = PISCES_LINES,
            color = Color(0xFFE8C4BB),
            alpha = 0.55f * skyAlpha
        )

        // Draw Gemini constellation
        drawConstellation(
            stars = GEMINI_STARS,
            lines = GEMINI_LINES,
            color = Color(0xFFB8C4E8),
            alpha = 0.55f * skyAlpha
        )
    }
}

private fun DrawScope.drawConstellation(
    stars: List<Offset>,
    lines: List<Pair<Int, Int>>,
    color: Color,
    alpha: Float
) {
    // Lines between stars
    lines.forEach { (a, b) ->
        drawLine(
            color = color.copy(alpha = alpha * 0.4f),
            start = Offset(stars[a].x * size.width, stars[a].y * size.height),
            end   = Offset(stars[b].x * size.width, stars[b].y * size.height),
            strokeWidth = 1f
        )
    }
    // Stars themselves
    stars.forEach { star ->
        drawCircle(
            color = color.copy(alpha = alpha),
            radius = 3f,
            center = Offset(star.x * size.width, star.y * size.height)
        )
        // Glow halo
        drawCircle(
            color = color.copy(alpha = alpha * 0.25f),
            radius = 6f,
            center = Offset(star.x * size.width, star.y * size.height)
        )
    }
}

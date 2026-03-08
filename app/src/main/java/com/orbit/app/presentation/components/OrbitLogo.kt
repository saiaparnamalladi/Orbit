package com.orbit.app.presentation.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.orbit.app.presentation.ui.theme.NebulaPurple
import com.orbit.app.presentation.ui.theme.RoseGold
import com.orbit.app.presentation.ui.theme.StarGlow
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun OrbitLogo(
    modifier: Modifier = Modifier,
    size: Dp = 80.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "orbit")

    // Planet 1 (rose gold — you) orbits clockwise
    val angle1 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "planet1"
    )

    // Planet 2 (purple — partner) orbits counter-clockwise
    val angle2 by infiniteTransition.animateFloat(
        initialValue = 360f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "planet2"
    )

    Canvas(modifier = modifier.size(size)) {
        val cx = this.size.width / 2
        val cy = this.size.height / 2
        val orbitRadius = this.size.minDimension * 0.35f
        val planetRadius = this.size.minDimension * 0.10f
        val centerRadius = this.size.minDimension * 0.07f

        // Orbit ring
        drawCircle(
            color = Color.White.copy(alpha = 0.12f),
            radius = orbitRadius,
            center = Offset(cx, cy),
            style = Stroke(width = 1f)
        )

        // Center core (small glowing dot)
        drawCircle(
            color = StarGlow.copy(alpha = 0.9f),
            radius = centerRadius,
            center = Offset(cx, cy)
        )
        drawCircle(
            color = StarGlow.copy(alpha = 0.3f),
            radius = centerRadius * 2f,
            center = Offset(cx, cy)
        )

        // Planet 1 — rose gold (you)
        val rad1 = Math.toRadians(angle1.toDouble())
        val p1 = Offset(
            cx + orbitRadius * cos(rad1).toFloat(),
            cy + orbitRadius * sin(rad1).toFloat()
        )
        drawCircle(color = RoseGold.copy(alpha = 0.3f), radius = planetRadius * 1.6f, center = p1)
        drawCircle(color = RoseGold, radius = planetRadius, center = p1)

        // Planet 2 — nebula purple (partner)
        val rad2 = Math.toRadians(angle2.toDouble())
        val p2 = Offset(
            cx + orbitRadius * cos(rad2).toFloat(),
            cy + orbitRadius * sin(rad2).toFloat()
        )
        drawCircle(color = NebulaPurple.copy(alpha = 0.3f), radius = planetRadius * 1.6f, center = p2)
        drawCircle(color = NebulaPurple, radius = planetRadius, center = p2)
    }
}

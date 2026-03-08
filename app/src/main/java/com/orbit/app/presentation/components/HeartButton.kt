package com.orbit.app.presentation.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.Canvas
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.orbit.app.presentation.ui.theme.HeartRed
import com.orbit.app.presentation.ui.theme.RoseGold
import kotlinx.coroutines.launch

@Composable
fun HeartButton(
    modifier: Modifier = Modifier,
    size: Dp = 64.dp,
    onClick: () -> Unit
) {
    var pressed by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val scale by animateFloatAsState(
        targetValue = if (pressed) 1.35f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "heartScale"
    )

    val pulseAnim = rememberInfiniteTransition(label = "heartPulse")
    val pulseScale by pulseAnim.animateFloat(
        initialValue = 1f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "idlePulse"
    )

    Box(
        modifier = modifier
            .size(size)
            .scale(scale * pulseScale)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                scope.launch {
                    pressed = true
                    kotlinx.coroutines.delay(200)
                    pressed = false
                }
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {
        HeartShape(size = size, tint = RoseGold)
    }
}

@Composable
fun HeartShape(size: Dp, tint: Color) {
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val path = androidx.compose.ui.graphics.Path().apply {
            moveTo(w / 2f, h * 0.85f)
            cubicTo(w * 0.0f, h * 0.55f, w * 0.0f, h * 0.20f, w / 2f, h * 0.35f)
            cubicTo(w * 1.0f, h * 0.20f, w * 1.0f, h * 0.55f, w / 2f, h * 0.85f)
            close()
        }
        drawPath(path, color = tint)
        // Glow
        drawPath(path, color = tint.copy(alpha = 0.25f))
        drawCircle(
            color = tint.copy(alpha = 0.15f),
            radius = w * 0.45f,
            center = Offset(w / 2f, h / 2f)
        )
    }
}

// Shown on partner's screen when they receive a heart
@Composable
fun IncomingHeartOverlay(onDone: () -> Unit) {
    val anim = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        anim.animateTo(1f, animationSpec = tween(300))
        kotlinx.coroutines.delay(1500)
        anim.animateTo(0f, animationSpec = tween(400))
        onDone()
    }

    Box(
        modifier = Modifier
            .size(120.dp)
            .scale(anim.value),
        contentAlignment = Alignment.Center
    ) {
        HeartShape(size = 120.dp, tint = HeartRed.copy(alpha = anim.value))
    }
}

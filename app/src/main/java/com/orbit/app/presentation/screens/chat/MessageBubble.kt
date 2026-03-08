package com.orbit.app.presentation.screens.chat

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.orbit.app.data.model.Message
import com.orbit.app.data.model.MessageType
import com.orbit.app.presentation.ui.theme.*

@Composable
fun MessageBubble(
    message: Message,
    isOwn: Boolean
) {
    val bubbleStyle = bubbleStyleFor(message.type, isOwn)

    // Entrance animation
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }
    val animScale by animateFloatAsState(
        targetValue = if (visible) 1f else 0.85f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "bubbleIn"
    )
    val animAlpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(200),
        label = "bubbleAlpha"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 3.dp)
            .scale(animScale)
            .alpha(animAlpha),
        horizontalArrangement = if (isOwn) Arrangement.End else Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .background(bubbleStyle.background, bubbleStyle.shape)
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Column(horizontalAlignment = if (isOwn) Alignment.End else Alignment.Start) {
                if (bubbleStyle.emoji != null) {
                    Text(
                        text = bubbleStyle.emoji,
                        fontSize = 20.sp,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }
                Text(
                    text = message.text,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontSize = if (message.type == MessageType.HEART) 28.sp else 15.sp
                    ),
                    color = bubbleStyle.textColor,
                    textAlign = if (isOwn) TextAlign.End else TextAlign.Start
                )
            }
        }
    }
}

data class BubbleStyle(
    val background: Brush,
    val textColor: Color,
    val shape: RoundedCornerShape,
    val emoji: String? = null
)

private fun bubbleStyleFor(type: MessageType, isOwn: Boolean): BubbleStyle {
    val ownShape = RoundedCornerShape(18.dp, 18.dp, 4.dp, 18.dp)
    val theirShape = RoundedCornerShape(18.dp, 18.dp, 18.dp, 4.dp)
    val shape = if (isOwn) ownShape else theirShape

    return when (type) {
        MessageType.TEXT -> BubbleStyle(
            background = Brush.linearGradient(
                if (isOwn) listOf(RoseGoldDark, RoseGold.copy(alpha = 0.8f))
                else listOf(CosmicBlue, Midnight)
            ),
            textColor = if (isOwn) StarWhite else StarWhite,
            shape = shape
        )
        MessageType.HEART -> BubbleStyle(
            background = Brush.radialGradient(listOf(HeartRed.copy(alpha = 0.3f), Color.Transparent)),
            textColor = HeartRed,
            shape = shape
        )
        MessageType.SLASH_MISS -> BubbleStyle(
            background = Brush.linearGradient(listOf(NebulaPurple.copy(alpha = 0.6f), RoseGold.copy(alpha = 0.4f))),
            textColor = StarWhite,
            shape = shape,
            emoji = "💛"
        )
        MessageType.SLASH_SOON -> BubbleStyle(
            background = Brush.linearGradient(listOf(AuroraBlue.copy(alpha = 0.5f), NebulaPurple.copy(alpha = 0.4f))),
            textColor = StarWhite,
            shape = shape,
            emoji = "⏳"
        )
        MessageType.SLASH_GOODNIGHT -> BubbleStyle(
            background = Brush.linearGradient(listOf(Color(0xFF0A0A2A), NebulaPurple.copy(alpha = 0.5f))),
            textColor = StarWhite.copy(alpha = 0.9f),
            shape = shape,
            emoji = "🌙"
        )
        MessageType.SLASH_MORNING -> BubbleStyle(
            background = Brush.linearGradient(listOf(Color(0xFF8B4513).copy(alpha = 0.5f), StarGlow.copy(alpha = 0.4f))),
            textColor = StarWhite,
            shape = shape,
            emoji = "🌅"
        )
        MessageType.SLASH_HUG -> BubbleStyle(
            background = Brush.radialGradient(listOf(RoseGold.copy(alpha = 0.5f), NebulaPurple.copy(alpha = 0.3f))),
            textColor = StarWhite,
            shape = shape,
            emoji = "🫂"
        )
    }
}

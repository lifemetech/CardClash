package com.royalcardclash.game.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.royalcardclash.game.model.PlayingCardModel

@Composable
fun PlayingCardView(
    card: PlayingCardModel,
    modifier: Modifier = Modifier,
    width: Dp = 68.dp,
    height: Dp = 98.dp,
    onClick: (() -> Unit)? = null
) {
    val rotationY by animateFloatAsState(
        targetValue = if (card.isFaceUp) 0f else 180f,
        animationSpec = tween(durationMillis = 350),
        label = "cardFlip"
    )

    val offsetY by animateFloatAsState(
        targetValue = if (card.isSelected) (-12).toFloat() else 0f,
        animationSpec = tween(durationMillis = 180),
        label = "cardSelect"
    )

    val shape = RoundedCornerShape(8.dp)

    Box(
        modifier = modifier
            .width(width)
            .height(height)
            .offset(y = offsetY.dp)
            .shadow(elevation = if (card.isSelected) 10.dp else 4.dp, shape = shape)
            .graphicsLayer {
                this.rotationY = rotationY
                cameraDistance = 12 * density
            }
            .background(
                if (rotationY > 90f) {
                    Brush.linearGradient(listOf(Color(0xFF1E3A8A), Color(0xFF0F172A)))
                } else {
                    Brush.linearGradient(listOf(Color(0xFFFAFAFA), Color(0xFFF1F5F9)))
                },
                shape = shape
            )
            .border(
                width = if (card.isSelected) 2.dp else 1.dp,
                color = if (card.isSelected) Color(0xFFFFD700) else Color(0xFFCBD5E1),
                shape = shape
            )
            .clickable(enabled = onClick != null) { onClick?.invoke() },
        contentAlignment = Alignment.Center
    ) {
        if (rotationY <= 90f) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = card.rank.display,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = card.suit.color
                )
                Text(
                    text = card.suit.symbol,
                    fontSize = 22.sp,
                    color = card.suit.color
                )
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(4.dp)
                    .background(Color(0xFF1E293B), RoundedCornerShape(4.dp))
                    .border(1.dp, Color(0xFFFFD700).copy(alpha = 0.4f), RoundedCornerShape(4.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "♠",
                    color = Color(0xFFFFD700).copy(alpha = 0.4f),
                    fontSize = 22.sp
                )
            }
        }
    }
}

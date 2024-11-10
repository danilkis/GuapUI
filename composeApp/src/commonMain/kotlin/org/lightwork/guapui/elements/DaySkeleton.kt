package org.lightwork.guapui.elements

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.unit.dp

@Composable
fun ShimmeringCard(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .padding(8.dp)
            .size(250.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize().shimmerBackground(RoundedCornerShape(12.dp)))
    }
}

fun Modifier.shimmerBackground(shape: Shape = RectangleShape): Modifier = composed {
    val transition = rememberInfiniteTransition()

    val translateAnimation by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            tween(durationMillis = 750, easing = EaseInOut),
            RepeatMode.Reverse
        ),
    )
    val shimmerColors = listOf(
        MaterialTheme.colorScheme.surfaceTint,
        MaterialTheme.colorScheme.background,
    )
    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset(translateAnimation, translateAnimation),
        end = Offset(translateAnimation + 1500f, translateAnimation + 1500f),
        tileMode = TileMode.Repeated,
    )
    return@composed this.then(background(brush, shape))
}

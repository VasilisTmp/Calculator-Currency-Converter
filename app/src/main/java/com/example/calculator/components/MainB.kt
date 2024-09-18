package com.example.calculator.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import kotlinx.coroutines.delay

data class MainBColors(
    val containerC: Color,
    val contentC: Color
)

@Composable
fun MainB(
    modifier: Modifier = Modifier,
    text: String? = null,
    imageVector: ImageVector? = null,
    onClick: () -> Unit,
    colors: MainBColors
) {
    var isPressed by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.9f else 1f,
        animationSpec = tween(durationMillis = 100),
        label = "ButtonScaleAnimation"
    )

    LaunchedEffect(text == "delete" && isPressed) {
        if (isPressed) {
            delay(300)
            while (isPressed) {
                onClick()
                delay(90)
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(65.dp)
            .scale(scale)
            .background(
                color = colors.containerC,
                shape = RoundedCornerShape(15.dp)
            )
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        delay(80)
                        val released =
                            tryAwaitRelease()
                        isPressed = false
                        if (released) {
                            onClick()
                        }
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        if (isPressed) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        MaterialTheme.colorScheme.inverseSurface.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(15.dp)
                    )
            )
        }
        if (imageVector != null) {
            Icon(
                imageVector = imageVector,
                contentDescription = null,
                tint = colors.contentC
            )
        } else {
            Text(
                text = text.orEmpty(),
                fontSize = 27.sp,
                color = colors.contentC
            )
        }
    }
}
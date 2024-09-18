package com.example.calculator.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import kotlinx.coroutines.delay

@Composable
fun CurrencyB(
    text: String,
    onClick: () -> Unit,
) {
    var isPressed by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .width(75.dp)
            .height(45.dp)
            .background(
                color = MaterialTheme.colorScheme.onBackground,
                shape = RoundedCornerShape(7.dp)
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
                        shape = RoundedCornerShape(10.dp)
                    )
            )
        }
        Text(
            text = text,
            fontSize = 23.sp,
            color = MaterialTheme.colorScheme.inverseSurface,
            fontWeight = FontWeight.Light
        )
    }
}
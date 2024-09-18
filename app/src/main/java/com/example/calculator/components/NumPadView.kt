package com.example.calculator.components

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun NumbPadView(
    onNumberInput: (String) -> Unit,
    onSymbolInput: (String) -> Unit,
    onPercent: () -> Unit,
    onClear: () -> Unit,
    onDelete: () -> Unit,
    onEqual: () -> Unit,
    onComma: () -> Unit,
    onSwitchTo: () -> Unit,
    switchIcon: ImageVector,
    modifier: Modifier = Modifier
) {
    val numberColors =
        MainBColors(
            containerC = MaterialTheme.colorScheme.onBackground,
            contentC = MaterialTheme.colorScheme.inverseSurface
        )
    val symbolColors =
        MainBColors(
            containerC = MaterialTheme.colorScheme.onBackground,
            contentC = MaterialTheme.colorScheme.primary
        )
    val equalColors =
        MainBColors(
            containerC = MaterialTheme.colorScheme.primary,
            contentC = MaterialTheme.colorScheme.inverseSurface
        )

    Box(
        modifier = Modifier
            .wrapContentSize()
    ) {
        Column(
            modifier = modifier
                .align(Alignment.BottomCenter),
            verticalArrangement = Arrangement.spacedBy(11.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(11.dp)
            ) {
                MainB(
                    text = "C",
                    onClick = onClear,
                    modifier = Modifier
                        .weight(1f),
                    colors = symbolColors
                )
                MainB(
                    text = "delete",
                    imageVector = Icons.AutoMirrored.Filled.Backspace,
                    onClick = onDelete,
                    modifier = Modifier
                        .weight(1f), colors = symbolColors
                )
                MainB(
                    text = "%",
                    onClick = onPercent,
                    modifier = Modifier
                        .weight(1f), colors = symbolColors
                )
                MainB(
                    text = "/",
                    onClick = { onSymbolInput("/") },
                    modifier = Modifier
                        .weight(1f), colors = symbolColors
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(11.dp)
            ) {
                MainB(
                    text = "7",
                    onClick = { onNumberInput("7") },
                    modifier = Modifier
                        .weight(1f), colors = numberColors
                )
                MainB(
                    text = "8",
                    onClick = { onNumberInput("8") },
                    modifier = Modifier
                        .weight(1f), colors = numberColors
                )
                MainB(
                    text = "9",
                    onClick = { onNumberInput("9") },
                    modifier = Modifier
                        .weight(1f), colors = numberColors
                )
                MainB(
                    text = "*",
                    onClick = { onSymbolInput("*") },
                    modifier = Modifier
                        .weight(1f), colors = symbolColors
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(11.dp)
            ) {
                MainB(
                    text = "4",
                    onClick = { onNumberInput("4") },
                    modifier = Modifier
                        .weight(1f), colors = numberColors
                )
                MainB(
                    text = "5",
                    onClick = { onNumberInput("5") },
                    modifier = Modifier
                        .weight(1f), colors = numberColors
                )
                MainB(
                    text = "6",
                    onClick = { onNumberInput("6") },
                    modifier = Modifier
                        .weight(1f), colors = numberColors
                )
                MainB(
                    text = "-",
                    onClick = { onSymbolInput("-") },
                    modifier = Modifier
                        .weight(1f), colors = symbolColors
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(11.dp)
            ) {
                MainB(
                    text = "1",
                    onClick = { onNumberInput("1") },
                    modifier = Modifier
                        .weight(1f), colors = numberColors
                )
                MainB(
                    text = "2",
                    onClick = { onNumberInput("2") },
                    modifier = Modifier
                        .weight(1f), colors = numberColors
                )
                MainB(
                    text = "3",
                    onClick = { onNumberInput("3") },
                    modifier = Modifier
                        .weight(1f), colors = numberColors
                )
                MainB(
                    text = "+",
                    onClick = { onSymbolInput("+") },
                    modifier = Modifier
                        .weight(1f), colors = symbolColors
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(11.dp)
            ) {
                MainB(
                    imageVector = switchIcon,
                    onClick = onSwitchTo,
                    modifier = Modifier
                        .weight(1f), colors = symbolColors
                )
                MainB(
                    text = "0",
                    onClick = { onNumberInput("0") },
                    modifier = Modifier
                        .weight(1f), colors = numberColors
                )
                MainB(
                    text = ".",
                    onClick = onComma,
                    modifier = Modifier
                        .weight(1f), colors = numberColors
                )
                MainB(
                    text = "=",
                    onClick = onEqual,
                    modifier = Modifier
                        .weight(1f), colors = equalColors
                )
            }
        }
    }
}
package com.example.calculator.ui.calc

import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.example.calculator.components.NumbPadView
import androidx.compose.runtime.*
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalTextInputService
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalFocusManager

@Composable
fun CalculatorView(
    input: TextFieldValue,
    result: String,
    inputIsFocused: Boolean,
    numberInput: (String) -> Unit,
    symbolInput: (String) -> Unit,
    percent: () -> Unit,
    clear: () -> Unit,
    delete: () -> Unit,
    comma: () -> Unit,
    inputChanged: (TextFieldValue) -> Unit,
    inputFocusChanged: (Boolean) -> Unit,
    switchToConverter: () -> Unit,
) {
    val focusRequester = remember { FocusRequester() }
    val scrollStateInput = rememberScrollState()
    val focusManager = LocalFocusManager.current
    var resultState by remember { mutableStateOf(result) }

    fun onResultClick() = focusManager.clearFocus()

    fun onButtonClick() = focusRequester.requestFocus()

    val onNumberInputWithFocus: (String) -> Unit = { str: String ->
        numberInput(str)
        onButtonClick()
    }

    val onSymbolInputWithFocus: (String) -> Unit = { str: String ->
        symbolInput(str)
        if (!resultState.contains("e", ignoreCase = true)) onButtonClick()
    }

    val onCommaWithFocus: () -> Unit = { ->
        comma()
        if (!resultState.contains("e", ignoreCase = true)
            && !resultState.contains(".")
        ) {
            onButtonClick()
        }
    }

    val onPercentWithFocus: () -> Unit = { ->
        percent()
        if (!resultState.contains("e", ignoreCase = true)
            && resultState != "0"
        ) {
            onButtonClick()
        }
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    LaunchedEffect(inputIsFocused) {
        scrollStateInput.scrollTo(scrollStateInput.maxValue)
    }

    LaunchedEffect(result) {
        resultState = result
    }

    LaunchedEffect(input.text) {
        scrollStateInput.scrollTo(scrollStateInput.maxValue)
    }

    CompositionLocalProvider(
        LocalTextInputService provides null
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 27.dp),
                verticalArrangement = Arrangement.Bottom
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(scrollStateInput)
                ) {
                    BasicTextField(
                        value = input,
                        onValueChange = inputChanged,
                        cursorBrush = SolidColor(MaterialTheme.colorScheme.secondary),
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequester)
                            .align(Alignment.BottomCenter)
                            .onFocusChanged { focusState ->
                                inputFocusChanged(focusState.isFocused)
                            },
                        textStyle = TextStyle(
                            textAlign = TextAlign.End,
                            fontSize = if (inputIsFocused) 35.sp else 25.sp,
                            color = if (inputIsFocused) MaterialTheme.colorScheme.inverseSurface
                            else MaterialTheme.colorScheme.tertiary,
                        ),
                        decorationBox = { innerTextField ->
                            Box(
                                modifier = Modifier
                                    .padding(horizontal = 25.dp),
                                contentAlignment = Alignment.CenterEnd,
                            ) {
                                innerTextField()
                            }
                        }
                    )
                }
                Spacer(modifier = Modifier.height(13.dp))
                Box(
                    modifier = Modifier
                        .clickable(
                            onClick = { onResultClick() },
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        )
                        .heightIn(max = 100.dp)
                        .verticalScroll(rememberScrollState()),
                ) {
                    Text(
                        text = "= $result",
                        fontSize = if (inputIsFocused) 25.sp else 35.sp,
                        textAlign = TextAlign.Right,
                        color = if (inputIsFocused) MaterialTheme.colorScheme.tertiary
                        else MaterialTheme.colorScheme.inverseSurface,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 25.dp)
                    )
                }
                Spacer(modifier = Modifier.height(20.dp))
                NumbPadView(
                    onNumberInput = onNumberInputWithFocus,
                    onSymbolInput = onSymbolInputWithFocus,
                    onPercent = onPercentWithFocus,
                    onClear = clear,
                    onDelete = delete,
                    onEqual = ::onResultClick,
                    onComma = onCommaWithFocus,
                    onSwitchTo = switchToConverter,
                    modifier = Modifier.padding(horizontal = 25.dp),
                    switchIcon = Icons.Filled.CurrencyExchange
                )
            }
        }
    }
}
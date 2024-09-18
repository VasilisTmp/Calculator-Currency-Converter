package com.example.calculator.ui.conv

import CurrenciesModal
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalTextInputService
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calculator.components.CurrencyB
import com.example.calculator.components.NumbPadView
import com.example.calculator.components.SwapB
import com.example.calculator.components.TextWithBorder
import com.example.calculator.components.UpdateB
import kotlinx.coroutines.delay

@Composable
fun ConverterView(
    input: TextFieldValue,
    result: String,
    convertedResult: String,
    numberInput: (String) -> Unit,
    symbolInput: (String) -> Unit,
    percent: () -> Unit,
    clear: () -> Unit,
    delete: () -> Unit,
    equal: () -> Unit,
    comma: () -> Unit,
    inputChanged: (TextFieldValue) -> Unit,
    switchToCalculator: () -> Unit,
    symbols: Map<String, String>,
    base: String,
    quote: String,
    getRates: (String) -> Unit,
    changeRate: (String) -> Unit,
    swap: () -> Unit,
    refresh: () -> Unit
) {
    val focusRequester = remember { FocusRequester() }
    val scrollState = rememberScrollState()
    var showModal by remember { mutableStateOf(false) }
    var chooseBase by remember { mutableStateOf(true) }

    val onChangeBase: () -> Unit = { ->
        chooseBase = true
        showModal = true
    }

    val onChangeQuote: () -> Unit = { ->
        chooseBase = false
        showModal = true
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    LaunchedEffect(input.text) {
        delay(50)
        scrollState.scrollTo(scrollState.maxValue)
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
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 25.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CurrencyB(text = base, onClick = onChangeBase)
                        Spacer(modifier = Modifier.height(15.dp))
                        TextWithBorder(
                            text = result,
                        )
                    }
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Spacer(modifier = Modifier.height(8.dp))
                        SwapB(onClick = swap)
                        Spacer(modifier = Modifier.height(28.dp))
                        UpdateB(onClick = refresh)
                    }
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CurrencyB(text = quote, onClick = onChangeQuote)
                        Spacer(modifier = Modifier.height(15.dp))
                        TextWithBorder(
                            text = convertedResult,
                        )
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))

                Box(
                    modifier = Modifier
                        .padding(horizontal = 10.dp)
                        .border(
                            1.dp,
                            MaterialTheme.colorScheme.outline,
                            shape = RoundedCornerShape(10.dp)
                        )
                        .weight(1f)
                        .verticalScroll(scrollState)
                ) {
                    BasicTextField(
                        value = input,
                        onValueChange = inputChanged,
                        cursorBrush = SolidColor(MaterialTheme.colorScheme.secondary),
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequester)
                            .align(Alignment.BottomCenter),
                        textStyle = TextStyle(
                            textAlign = TextAlign.End,
                            fontSize = 35.sp,
                            color = MaterialTheme.colorScheme.inverseSurface,
                        ),
                        decorationBox = { innerTextField ->
                            Box(
                                modifier = Modifier
                                    .padding(
                                        horizontal = 15.dp,
                                        vertical = 5.dp
                                    ),
                                contentAlignment = Alignment.CenterEnd,
                            ) {
                                innerTextField()
                            }
                        }
                    )
                }
                Spacer(modifier = Modifier.height(20.dp))
                NumbPadView(
                    onNumberInput = numberInput,
                    onSymbolInput = symbolInput,
                    onPercent = percent,
                    onClear = clear,
                    onDelete = delete,
                    onEqual = equal,
                    onComma = comma,
                    onSwitchTo = switchToCalculator,
                    modifier = Modifier.padding(horizontal = 25.dp),
                    switchIcon = Icons.Filled.Calculate
                )
                if (showModal) {
                    CurrenciesModal(
                        onDismissRequest = { showModal = false },
                        items = symbols,
                        onItemClick = if (chooseBase) getRates else changeRate,
                        selected = if (chooseBase) base else quote
                    )
                }
            }
        }
    }
}
package com.example.calculator.model.calc

import androidx.compose.ui.text.input.TextFieldValue

data class CalculatorState(
    val input: TextFieldValue,
    val result: String,
    val inputIsFocused: Boolean
)
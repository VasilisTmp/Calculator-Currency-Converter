package com.example.calculator.model.calc

import androidx.compose.ui.text.input.TextFieldValue

sealed class CalculatorIntent {
    data class InputChanged(val textFieldValue: TextFieldValue) : CalculatorIntent()
    data class NumberInput(val str: String) : CalculatorIntent()
    data class SymbolInput(val str: String) : CalculatorIntent()
    object Percent : CalculatorIntent()
    object Clear : CalculatorIntent()
    object Delete : CalculatorIntent()
    object Comma : CalculatorIntent()
    data class InputFocusChanged(val focusValue: Boolean) : CalculatorIntent()
}

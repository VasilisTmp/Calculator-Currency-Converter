package com.example.calculator.model.conv

import androidx.compose.ui.text.input.TextFieldValue

sealed class ConverterIntent {
    data class InputChanged(val textFieldValue: TextFieldValue) : ConverterIntent()
    data class NumberInput(val str: String) : ConverterIntent()
    data class SymbolInput(val str: String) : ConverterIntent()
    object Percent : ConverterIntent()
    object Clear : ConverterIntent()
    object Delete : ConverterIntent()
    object Equal : ConverterIntent()
    object Comma : ConverterIntent()
    data class GetRates(val base: String) : ConverterIntent()
    data class ChangeRate(val quote: String) : ConverterIntent()
    object Refresh : ConverterIntent()
    object Swap : ConverterIntent()
}
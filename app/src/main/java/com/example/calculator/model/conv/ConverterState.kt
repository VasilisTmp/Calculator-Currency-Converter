package com.example.calculator.model.conv

import androidx.compose.ui.text.input.TextFieldValue

data class ConverterState(
    val input: TextFieldValue,
    val result: String,
    val convertedResult: String,
    val symbols: Map<String, String>,
    val rates: Map<String, Double>,
    val base: String,
    val quote: String,
    val rate: Double,
)

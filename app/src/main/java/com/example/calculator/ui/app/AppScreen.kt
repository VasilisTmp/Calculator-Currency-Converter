package com.example.calculator.ui.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.calculator.model.app.AppModel
import com.example.calculator.ui.calc.CalculatorScreen
import com.example.calculator.ui.conv.ConverterScreen

@Composable
fun AppScreen(
    viewModel: AppModel = viewModel(),
) {
    val state = viewModel.state.collectAsState().value

    when (state.calculatorIsActive) {
        true -> CalculatorScreen()
        false -> ConverterScreen()
    }
}
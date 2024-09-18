package com.example.calculator.ui.calc

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import com.example.calculator.model.app.AppModel
import com.example.calculator.model.app.AppIntent
import com.example.calculator.model.calc.CalculatorIntent
import com.example.calculator.model.calc.CalculatorViewModel

@Composable
fun CalculatorScreen(
    viewModel: CalculatorViewModel = viewModel(),
    appModel: AppModel = viewModel()
) {
    val state = viewModel.state.collectAsState().value

    CalculatorView(
        numberInput = { str -> viewModel.processIntent(CalculatorIntent.NumberInput(str)) },
        symbolInput = { str -> viewModel.processIntent(CalculatorIntent.SymbolInput(str)) },
        percent = { viewModel.processIntent(CalculatorIntent.Percent) },
        clear = { viewModel.processIntent(CalculatorIntent.Clear) },
        delete = { viewModel.processIntent(CalculatorIntent.Delete) },
        comma = { viewModel.processIntent(CalculatorIntent.Comma) },
        inputChanged = { textFieldValue ->
            viewModel.processIntent(
                CalculatorIntent.InputChanged(
                    textFieldValue
                )
            )
        },
        input = state.input,
        result = state.result,
        inputIsFocused = state.inputIsFocused,
        inputFocusChanged = { focusVal ->
            viewModel.processIntent(
                CalculatorIntent.InputFocusChanged(
                    focusVal
                )
            )
        },
        switchToConverter = { appModel.processIntent(AppIntent.SwitchToConverter) },
    )
}
package com.example.calculator.ui.conv

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import com.example.calculator.model.app.AppModel
import com.example.calculator.model.app.AppIntent
import com.example.calculator.model.conv.ConverterIntent
import com.example.calculator.model.conv.ConverterViewModel

@Composable
fun ConverterScreen(
    viewModel: ConverterViewModel = viewModel(),
    appModel: AppModel = viewModel()
) {
    val state = viewModel.state.collectAsState().value
    ConverterView(
        numberInput = { str -> viewModel.processIntent(ConverterIntent.NumberInput(str)) },
        symbolInput = { str -> viewModel.processIntent(ConverterIntent.SymbolInput(str)) },
        percent = { viewModel.processIntent(ConverterIntent.Percent) },
        clear = { viewModel.processIntent(ConverterIntent.Clear) },
        delete = { viewModel.processIntent(ConverterIntent.Delete) },
        equal = { viewModel.processIntent(ConverterIntent.Equal) },
        comma = { viewModel.processIntent(ConverterIntent.Comma) },
        inputChanged = { textFieldValue ->
            viewModel.processIntent(
                ConverterIntent.InputChanged(
                    textFieldValue
                )
            )
        },
        input = state.input,
        result = state.result,
        convertedResult = state.convertedResult,
        switchToCalculator = { appModel.processIntent(AppIntent.SwitchToCalculator) },
        symbols = state.symbols,
        base = state.base,
        quote = state.quote,
        getRates = { str -> viewModel.processIntent(ConverterIntent.GetRates(str)) },
        changeRate = { str -> viewModel.processIntent(ConverterIntent.ChangeRate(str)) },
        swap = { viewModel.processIntent(ConverterIntent.Swap) },
        refresh = { viewModel.processIntent(ConverterIntent.Refresh) },
    )
}
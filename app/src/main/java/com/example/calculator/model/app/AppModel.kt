package com.example.calculator.model.app

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class AppModel : ViewModel() {
    private val _state = MutableStateFlow(
        AppState(
            calculatorIsActive = true
        )
    )
    val state: StateFlow<AppState> = _state.asStateFlow()

    fun processIntent(intent: AppIntent) {
        when (intent) {
            is AppIntent.SwitchToCalculator -> {
                _state.update { prev ->
                    prev.copy(calculatorIsActive = true)
                }
            }

            is AppIntent.SwitchToConverter -> {
                _state.update { prev ->
                    prev.copy(calculatorIsActive = false)
                }
            }

        }
    }

}
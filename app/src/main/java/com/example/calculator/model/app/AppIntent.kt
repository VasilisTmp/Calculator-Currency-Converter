package com.example.calculator.model.app


sealed class AppIntent {
    object SwitchToCalculator : AppIntent()
    object SwitchToConverter : AppIntent()
}

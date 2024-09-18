package com.example.calculator.model.calc

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import net.objecthunter.exp4j.ExpressionBuilder

class CalculatorViewModel : ViewModel() {
    private val _state =
        MutableStateFlow(
            CalculatorState(
                input = TextFieldValue(text = "0", selection = TextRange(1)),
                result = "0",
                inputIsFocused = true
            )
        )
    val state: StateFlow<CalculatorState> = _state.asStateFlow()

    fun processIntent(intent: CalculatorIntent) {
        when (intent) {
            is CalculatorIntent.InputChanged -> handleInputChanged(intent.textFieldValue)
            is CalculatorIntent.NumberInput -> handleNumberInput(intent.str)
            is CalculatorIntent.SymbolInput -> handleSymbolInput(intent.str)
            is CalculatorIntent.Percent -> handlePercent()
            is CalculatorIntent.Clear -> handleClear()
            is CalculatorIntent.Delete -> handleDelete()
            is CalculatorIntent.InputFocusChanged -> handleInputFocusChanged(intent.focusValue)
            is CalculatorIntent.Comma -> handleComma()
        }
    }

    private fun handleInputChanged(textFieldValue: TextFieldValue) {
        _state.update { prev ->
            val result = calculateResult(textFieldValue.text)
            prev.copy(
                input = textFieldValue,
                result = result
            )
        }
    }

    private fun handleNumberInput(c: String) {
        _state.update { prev ->
            val prevInputIsFocused = prev.inputIsFocused
            if (prevInputIsFocused) {
                val prevInputText = prev.input.text
                var newCursorIndex = 0
                val prevCursorIndex = prev.input.selection.start
                val newInputText =
                    if (prevInputText == "0") {
                        newCursorIndex = 1
                        if (prevCursorIndex == 1) {
                            c
                        } else {
                            StringBuilder(prevInputText).insert(0, c).toString()
                        }
                    } else {
                        newCursorIndex = prevCursorIndex + 1
                        StringBuilder(prevInputText).insert(prevCursorIndex, c).toString()
                    }
                val newInput = prev.input.copy(
                    text = newInputText,
                    selection = TextRange(newCursorIndex)
                )
                val result = calculateResult(newInputText)
                prev.copy(
                    input = newInput,
                    result = result
                )
            } else {
                val newInput = prev.input.copy(
                    text = c,
                    selection = TextRange(1)
                )
                prev.copy(
                    input = newInput,
                    result = c
                )
            }

        }
    }

    private fun handleSymbolInput(c: String) {
        _state.update { prev ->
            val prevInputIsFocused = prev.inputIsFocused
            if (prevInputIsFocused) {
                val prevInputText = prev.input.text
                var newCursorIndex = 0
                val prevCursorIndex = prev.input.selection.start
                val newInputText =
                    if (prevCursorIndex == 0) {
                        newCursorIndex = 1
                        StringBuilder(prevInputText).insert(0, c).toString()
                    } else {
                        if (prevInputText[prevCursorIndex - 1].isDigit()) {
                            newCursorIndex = prevCursorIndex + 1
                            StringBuilder(prevInputText).insert(prevCursorIndex, c).toString()
                        } else {
                            newCursorIndex = prevCursorIndex
                            val newText =
                                prevInputText.replaceRange(prevCursorIndex - 1, prevCursorIndex, c)
                            newText
                        }
                    }
                val newInput = prev.input.copy(
                    text = newInputText,
                    selection = TextRange(newCursorIndex)
                )
                val result = calculateResult(newInputText)
                prev.copy(
                    input = newInput,
                    result = result,
                )
            } else if (!prev.result.contains("e", ignoreCase = true)) {
                val newInputText = prev.result + c
                val newInput = prev.input.copy(
                    text = newInputText,
                    selection = TextRange(newInputText.length)
                )
                prev.copy(input = newInput)
            } else {
                return
            }
        }
    }

    private fun getLastNumber(expression: String): String? {
        val regex = Regex("""-?\d+(\.\d+)?|(\.\d+)+""")
        val allMatches = regex.findAll(expression)
        return allMatches.lastOrNull()?.value
    }

    private fun isNextNumberADouble(expression: String): Boolean {
        val regex = Regex("^\\d+\\.")
        return regex.containsMatchIn(expression)
    }

    private fun handleComma() {
        _state.update { prev ->
            if (!prev.inputIsFocused) {
                if (prev.result.contains("e", ignoreCase = true)
                    || prev.result.contains(".")
                ) {
                    return@update prev
                }
                val newInputText = prev.result + "."
                val newInput = prev.input.copy(
                    text = newInputText,
                    selection = TextRange(newInputText.length)
                )
                return@update prev.copy(input = newInput)
            }
            val prevInputText = prev.input.text
            val prevCursorIndex = prev.input.selection.start
            if (prevCursorIndex == 0 || !prevInputText[prevCursorIndex - 1].isDigit()
            ) {
                return@update prev
            }
            if (prevCursorIndex < prevInputText.length
                && prevInputText[prevCursorIndex] == '.'
            ) {
                return@update prev
            }
            val lastNumBefore = getLastNumber(prevInputText.substring(0, prevCursorIndex))
            val nextNumberIsDouble =
                isNextNumberADouble(prevInputText.substring(prevCursorIndex))
            if (lastNumBefore == null
                || lastNumBefore.contains(".")
                || nextNumberIsDouble
            ) {
                return@update prev
            }
            val newInputText =
                StringBuilder(prevInputText).insert(prevCursorIndex, ".").toString()
            val newCursorIndex = prevCursorIndex + 1
            val newInput = prev.input.copy(
                text = newInputText,
                selection = TextRange(newCursorIndex)
            )
            val result = calculateResult(newInputText)
            return@update prev.copy(
                input = newInput,
                result = result
            )
        }
    }

    private fun handlePercent() {
        _state.update { prev ->
            if (!prev.inputIsFocused) {
                if (prev.result.contains("e", ignoreCase = true) || prev.result == "0") {
                    return@update prev
                }
                val replacement = calculateReplacement(prev.result)
                val newInput = prev.input.copy(
                    text = replacement,
                    selection = TextRange(replacement.length)
                )
                return@update prev.copy(
                    input = newInput,
                    result = replacement
                )
            }
            val prevCursorIndex = prev.input.selection.start
            if (prevCursorIndex == 0) {
                return@update prev
            }
            val prevInputText = prev.input.text
            val prevCharacter = prevInputText[prevCursorIndex - 1]
            val nextCharacter =
                if (prevCursorIndex < prevInputText.length) prevInputText[prevCursorIndex] else null
            if (!prevCharacter.isDigit() || nextCharacter != null && (nextCharacter == '.' || nextCharacter.isDigit())) {
                return@update prev
            }
            val regex = Regex("""-?\d+(\.\d+)?""")
            val allMatches = regex.findAll(prevInputText.substring(0, prevCursorIndex))
            val lastNumberBefore = allMatches.lastOrNull() ?: return@update prev
            if (lastNumberBefore.value == "0") {
                return@update prev
            }
            val replacement = calculateReplacement(lastNumberBefore.value)
            val matchStart = lastNumberBefore.range.first
            val matchEnd = lastNumberBefore.range.last + 1
            val newInputText =
                prevInputText.substring(0, matchStart) + replacement + prevInputText.substring(
                    matchEnd
                )
            val newCursorIndex = matchStart + replacement.length
            val newInput = prev.input.copy(
                text = newInputText,
                selection = TextRange(newCursorIndex)
            )
            val result = calculateResult(newInputText)
            return@update prev.copy(
                input = newInput,
                result = result
            )
        }
    }


    private fun calculateReplacement(input: String): String {
        val number = input.toDouble()
        val newNumber = number / 100
        val replacement = if (newNumber % 1.0 == 0.0) {
            String.format("%.0f", number)

        } else {
            String.format("%.10f", newNumber).trimEnd('0')
        }
        return replacement
    }

    private fun handleClear() {
        _state.update { prev ->
            prev.copy(
                input = TextFieldValue(
                    text = "0",
                    selection = TextRange(1),
                ),
                result = "0"
            )
        }
    }

    private fun handleDelete() {
        _state.update { prev ->
            if (!prev.inputIsFocused) return
            val prevInputText = prev.input.text
            val prevCursorIndex = prev.input.selection.start
            var newCursorIndex = 0
            if (prevCursorIndex == 0) {
                return
            }
            val newInputText =
                if (prevCursorIndex == 1 && prevInputText.length == 1) {
                    newCursorIndex = prevCursorIndex
                    "0"
                } else {
                    newCursorIndex = prevCursorIndex - 1
                    prevInputText.replaceRange(prevCursorIndex - 1, prevCursorIndex, "")
                }
            val newInput = prev.input.copy(
                text = newInputText,
                selection = TextRange(newCursorIndex)
            )
            val result = calculateResult(newInputText)
            prev.copy(
                input = newInput,
                result = result,
            )
        }
    }

    private fun calculateResult(input: String): String {
        val lastChar = input.last()
        val temp = if (lastChar.isDigit() || lastChar == '.') {
            input
        } else {
            input.dropLast(1)
        }
        var result =
            if (temp.isNotEmpty()) {
                val regex = Regex("\\d*\\.{2,}\\d*|(\\.\\d+\\.)+")
                if (regex.containsMatchIn(temp) || temp == ".") {
                    return "ERROR"
                }
                val regex2 = Regex("(?<=[/+\\-*])\\.(?!\\d)")
                if (regex2.containsMatchIn(temp)) {
                    return "ERROR"
                }
                val expr = ExpressionBuilder(temp).build()
                try {
                    expr.evaluate()
                } catch (e: Throwable) {
                    if (e.toString().contains("zero")) {
                        "Can't divide by zero"
                    } else {
                        "ERROR"
                    }
                }
            } else {
                "ERROR"
            }
        if (Regex("\\d").containsMatchIn(result.toString())) {
            val doubleResult = result.toString().toDouble()
            val formattedResult =
                if (doubleResult % 1.0 == 0.0) {
                    String.format("%.0f", doubleResult)
                } else {
                    String.format("%.10f", doubleResult).trimEnd('0')
                }
            result = formattedResult
        }
        return result.toString()
    }

    private fun handleInputFocusChanged(focusValue: Boolean) {
        _state.update { prev ->
            prev.copy(
                inputIsFocused = focusValue
            )
        }
    }
}
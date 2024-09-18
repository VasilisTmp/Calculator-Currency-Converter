package com.example.calculator.model.conv

import android.util.Log
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import net.objecthunter.exp4j.ExpressionBuilder
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

class ConverterViewModel : ViewModel() {
    private val client = OkHttpClient()
    private val _state =
        MutableStateFlow(
            ConverterState(
                input = TextFieldValue(
                    text = "0",
                    selection = TextRange(1)
                ),
                result = "0",
                convertedResult = "0",
                symbols = emptyMap(),
                rates = emptyMap(),
                base = "-",
                quote = "-",
                rate = 0.0
            )
        )
    val state: StateFlow<ConverterState> = _state.asStateFlow()

    init {
        fetchSymbols()
        handleGetRates("eur")
    }

    fun processIntent(intent: ConverterIntent) {
        when (intent) {
            is ConverterIntent.InputChanged -> handleInputChanged(intent.textFieldValue)
            is ConverterIntent.NumberInput -> handleNumberInput(intent.str)
            is ConverterIntent.SymbolInput -> handleSymbolInput(intent.str)
            is ConverterIntent.Percent -> handlePercent()
            is ConverterIntent.Clear -> handleClear()
            is ConverterIntent.Delete -> handleDelete()
            is ConverterIntent.Equal -> handleEqual()
            is ConverterIntent.Comma -> handleComma()
            is ConverterIntent.GetRates -> handleGetRates(intent.base)
            is ConverterIntent.ChangeRate -> handleChangeRate(intent.quote)
            is ConverterIntent.Refresh -> handleRefresh()
            is ConverterIntent.Swap -> handleSwap()
        }
    }

    private fun handleInputChanged(textFieldValue: TextFieldValue) {
        _state.update { prev ->
            val result = calculateResult(textFieldValue.text)
            val convertedResult = convertResult(result, prev.rate)
            prev.copy(
                input = textFieldValue,
                result = result,
                convertedResult = convertedResult
            )
        }
    }

    private fun handleNumberInput(c: String) {
        _state.update { prev ->
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
            val convertedResult = convertResult(result, prev.rate)
            prev.copy(
                input = newInput,
                result = result,
                convertedResult = convertedResult
            )
        }
    }

    private fun handleSymbolInput(c: String) {
        _state.update { prev ->
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
            val convertedResult = convertResult(result, prev.rate)
            prev.copy(
                input = newInput,
                result = result,
                convertedResult = convertedResult
            )
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
            val convertedResult = convertResult(result, prev.rate)
            return@update prev.copy(
                input = newInput,
                result = result,
                convertedResult = convertedResult
            )
        }
    }

    private fun handlePercent() {
        _state.update { prev ->
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
            val convertedResult = convertResult(result, prev.rate)
            return@update prev.copy(
                input = newInput,
                result = result,
                convertedResult = convertedResult
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
                result = "0",
                convertedResult = "0"
            )
        }
    }

    private fun handleDelete() {
        _state.update { prev ->
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
            val convertedResult = convertResult(result, prev.rate)
            prev.copy(
                input = newInput,
                result = result,
                convertedResult = convertedResult
            )
        }
    }

    private fun handleEqual() {
        _state.update { prev ->
            val newInputText =
                if (prev.result.contains("e", ignoreCase = true)) {
                    prev.input.text
                } else {
                    prev.result
                }
            val newInput =
                prev.input.copy(text = newInputText, selection = TextRange(newInputText.length))
            prev.copy(input = newInput)
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
                    "ERROR"
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

    private fun fetchSymbols() {
        viewModelScope.launch(Dispatchers.IO) {
            val request = Request.Builder()
                .url("https://cdn.jsdelivr.net/npm/@fawazahmed0/currency-api@latest/v1/currencies.json")
                .build()
            try {
                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) {
                        throw IOException("Unexpected code $response")
                    }
                    val responseBody =
                        response.body?.string() ?: throw IOException("Empty response body")
                    val gson = Gson()
                    val mapType = object : TypeToken<Map<String, String>>() {}.type
                    val result: Map<String, String> = gson.fromJson(responseBody, mapType)
                    _state.update { prev ->
                        val newBase =
                            if (prev.base != "-") {
                                prev.base
                            } else {
                                "eur"
                            }
                        val newQuote =
                            if (prev.quote != "-") {
                                prev.quote
                            } else {
                                "usd"
                            }
                        prev.copy(
                            symbols = result,
                            base = newBase,
                            quote = newQuote,
                        )
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
                _state.update { prev ->
                    prev.copy(
                        symbols = emptyMap(),
                        base = "-",
                        quote = "-",
                    )
                }
            }
        }
    }

    private fun handleGetRates(base: String) {
        val basee = if (base == "-") "eur" else base
        viewModelScope.launch(Dispatchers.IO) {
            val request = Request.Builder()
                .url("https://cdn.jsdelivr.net/npm/@fawazahmed0/currency-api@latest/v1/currencies/$basee.json")
                .build()
            try {
                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) {
                        throw IOException("Unexpected code $response")
                    }
                    val responseBody =
                        response.body?.string() ?: throw IOException("Empty response body")
                    val jsonObject = JsonParser.parseString(responseBody).asJsonObject;
                    val responseList = jsonObject.getAsJsonObject(basee).toString();
                    val gson = Gson()
                    val mapType = object : TypeToken<Map<String, Double>>() {}.type
                    val rates: Map<String, Double> = gson.fromJson(responseList, mapType)
                    _state.update { prev ->
                        val newRate =
                            if (prev.rate != 0.0) {
                                rates[prev.quote]
                            } else {
                                rates["usd"]
                            }
                        val convertedResult = convertResult(prev.result, newRate ?: 0.0)
                        prev.copy(
                            rates = rates,
                            rate = newRate ?: 0.0,
                            base = basee,
                            convertedResult = convertedResult
                        )
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
                _state.update { prev ->
                    prev.copy(
                        rates = emptyMap(),
                        rate = 0.0,
                        convertedResult = "ERROR"
                    )
                }
            }
        }
    }

    private fun handleChangeRate(quote: String) {
        _state.update { prev ->
            val newRate = prev.rates[quote]
            val convertedResult = convertResult(prev.result, newRate ?: 0.0)
            prev.copy(
                rate = newRate ?: 0.0,
                convertedResult = convertedResult,
                quote = quote
            )
        }
    }

    private fun handleSwap() {
        val base = _state.value.base
        val quote = _state.value.quote
        if (base == "-" || base == quote) return
        viewModelScope.launch(Dispatchers.IO) {
            val request = Request.Builder()
                .url("https://cdn.jsdelivr.net/npm/@fawazahmed0/currency-api@latest/v1/currencies/$quote.json")
                .build()
            try {
                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) {
                        throw IOException("Unexpected code $response")
                    }
                    val responseBody =
                        response.body?.string() ?: throw IOException("Empty response body")
                    val jsonObject = JsonParser.parseString(responseBody).asJsonObject;
                    val responseList = jsonObject.getAsJsonObject(quote).toString();
                    val gson = Gson()
                    val mapType = object : TypeToken<Map<String, Double>>() {}.type
                    val rates: Map<String, Double> = gson.fromJson(responseList, mapType)
                    _state.update { prev ->
                        val newRate = rates[base]
                        val convertedResult = convertResult(prev.result, newRate ?: 0.0)
                        prev.copy(
                            rates = rates,
                            rate = newRate ?: 0.0,
                            base = quote,
                            quote = base,
                            convertedResult = convertedResult
                        )
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
                _state.update { prev ->
                    prev.copy(
                        rates = emptyMap(),
                        rate = 0.0, base = "-",
                        quote = "-",
                        convertedResult = "ERROR"
                    )
                }
            }
        }
    }

    private fun handleRefresh() {
        fetchSymbols()
        handleGetRates(_state.value.base)
    }

    private fun convertResult(result: String, rate: Double): String {
        return if (result != "ERROR" && rate != 0.0) {
            val doubleResult = result.toDouble() * rate
            val formattedResult =
                if (doubleResult % 1.0 == 0.0) {
                    String.format("%.0f", doubleResult)
                } else {
                    String.format("%.10f", doubleResult).trimEnd('0')
                }
            formattedResult
        } else {
            "ERROR"
        }
    }
}

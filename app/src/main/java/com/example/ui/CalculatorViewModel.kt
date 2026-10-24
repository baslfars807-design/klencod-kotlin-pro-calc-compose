package com.example.ui

import android.app.Application
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.SoundEffectConstants
import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.R
import com.example.data.local.AccentPalette
import com.example.data.local.CalculatorDatabase
import com.example.data.local.ThemeMode
import com.example.data.local.UserPreferences
import com.example.data.local.UserPreferencesRepository
import com.example.data.model.CalculationRecord
import com.example.data.repository.CalculatorRepository
import com.example.domain.evaluator.ExpressionEvaluator
import com.example.domain.model.EvaluationResult
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class CalculatorUiState(
    val expression: String = "",
    val previewResult: String? = null,
    val result: String = "0",
    val errorMessageRes: Int? = null,
    val isEvaluated: Boolean = false,
    val showHistorySheet: Boolean = false,
    val showSettingsDialog: Boolean = false,
    val showClearHistoryDialog: Boolean = false
)

class CalculatorViewModel(
    application: Application,
    private val repository: CalculatorRepository,
    private val preferencesRepository: UserPreferencesRepository
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(CalculatorUiState())
    val uiState: StateFlow<CalculatorUiState> = _uiState.asStateFlow()

    val history: StateFlow<List<CalculationRecord>> = repository.history
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val preferences: StateFlow<UserPreferences> = preferencesRepository.preferencesFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserPreferences()
        )

    private val _snackbarMessage = MutableSharedFlow<String>()
    val snackbarMessage: SharedFlow<String> = _snackbarMessage.asSharedFlow()

    private val vibrator: Vibrator? by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = application.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vibratorManager?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            application.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    }

    fun playFeedback(view: View?) {
        val prefs = preferences.value
        if (prefs.hapticEnabled) {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    vibrator?.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator?.vibrate(20L)
                }
            } catch (e: Exception) {
                // Ignore vibration failure
            }
        }
        if (prefs.soundEnabled && view != null) {
            try {
                view.playSoundEffect(SoundEffectConstants.CLICK)
            } catch (e: Exception) {
                // Ignore sound failure
            }
        }
    }

    fun onDigit(digit: String, view: View? = null) {
        playFeedback(view)
        val currentState = _uiState.value

        val newExpr = if (currentState.isEvaluated) {
            digit
        } else {
            if (currentState.expression == "0" && digit != "0") {
                digit
            } else if (currentState.expression == "0" && digit == "0") {
                "0"
            } else {
                currentState.expression + digit
            }
        }

        updateExpression(newExpr, isEvaluated = false)
    }

    fun onDecimal(view: View? = null) {
        playFeedback(view)
        val currentState = _uiState.value

        if (currentState.isEvaluated) {
            updateExpression("0.", isEvaluated = false)
            return
        }

        val expr = currentState.expression
        if (expr.isEmpty()) {
            updateExpression("0.", isEvaluated = false)
            return
        }

        // Find last number segment
        val lastNumber = extractLastNumber(expr)
        if (!lastNumber.contains(".")) {
            val newExpr = if (expr.endsWith(" ") || expr.endsWith("(") || expr.endsWith("√")) {
                expr + "0."
            } else {
                expr + "."
            }
            updateExpression(newExpr, isEvaluated = false)
        }
    }

    fun onOperator(op: String, view: View? = null) {
        playFeedback(view)
        val currentState = _uiState.value
        val formattedOp = " $op "

        if (currentState.isEvaluated) {
            // Chain calculation starting from previous result if valid
            if (currentState.errorMessageRes == null && currentState.result.isNotEmpty() && currentState.result != "Error") {
                updateExpression(currentState.result + formattedOp, isEvaluated = false)
            } else {
                updateExpression("0$formattedOp", isEvaluated = false)
            }
            return
        }

        val expr = currentState.expression.trimEnd()
        if (expr.isEmpty()) {
            if (op == "−") {
                updateExpression("−", isEvaluated = false)
            }
            return
        }

        // If ends with another operator, replace it
        val endsWithOpRegex = Regex("""[+−×÷^]\s*$""")
        val newExpr = if (endsWithOpRegex.containsMatchIn(expr)) {
            expr.replace(endsWithOpRegex, "") + formattedOp
        } else {
            "$expr$formattedOp"
        }

        updateExpression(newExpr, isEvaluated = false)
    }

    fun onSquareRoot(view: View? = null) {
        playFeedback(view)
        val currentState = _uiState.value
        if (currentState.isEvaluated) {
            updateExpression("√", isEvaluated = false)
            return
        }
        val expr = currentState.expression
        val newExpr = if (expr.isEmpty() || expr.endsWith(" ") || expr.endsWith("(")) {
            expr + "√"
        } else {
            "$expr × √"
        }
        updateExpression(newExpr, isEvaluated = false)
    }

    fun onPercent(view: View? = null) {
        playFeedback(view)
        val currentState = _uiState.value
        if (currentState.isEvaluated) return
        val expr = currentState.expression
        if (expr.isNotEmpty() && (expr.last().isDigit() || expr.endsWith(")"))) {
            updateExpression("$expr%", isEvaluated = false)
        }
    }

    fun onParentheses(view: View? = null) {
        playFeedback(view)
        val currentState = _uiState.value
        if (currentState.isEvaluated) {
            updateExpression("(", isEvaluated = false)
            return
        }

        val expr = currentState.expression
        val openCount = expr.count { it == '(' }
        val closeCount = expr.count { it == ')' }

        val newExpr = if (expr.isEmpty() || expr.endsWith(" ") || expr.endsWith("(") || expr.endsWith("√")) {
            expr + "("
        } else if (openCount > closeCount && (expr.last().isDigit() || expr.endsWith(")") || expr.endsWith("%"))) {
            expr + ")"
        } else {
            "$expr × ("
        }
        updateExpression(newExpr, isEvaluated = false)
    }

    fun onToggleSign(view: View? = null) {
        playFeedback(view)
        val currentState = _uiState.value
        if (currentState.isEvaluated) {
            val res = currentState.result
            if (res.isNotEmpty() && res != "0" && currentState.errorMessageRes == null) {
                val toggled = if (res.startsWith("-") || res.startsWith("−")) {
                    res.substring(1)
                } else {
                    "−$res"
                }
                updateExpression(toggled, isEvaluated = false)
            }
            return
        }

        val expr = currentState.expression
        if (expr.isEmpty()) {
            updateExpression("−", isEvaluated = false)
            return
        }

        // Toggle sign on last number
        val tokens = expr.split(" ")
        if (tokens.isNotEmpty()) {
            val lastToken = tokens.last()
            if (lastToken.isNotEmpty()) {
                val newLastToken = if (lastToken.startsWith("−")) {
                    lastToken.substring(1)
                } else if (lastToken.startsWith("-")) {
                    lastToken.substring(1)
                } else {
                    "−$lastToken"
                }
                val newExpr = (tokens.dropLast(1) + newLastToken).joinToString(" ")
                updateExpression(newExpr, isEvaluated = false)
            }
        }
    }

    fun onBackspace(view: View? = null) {
        playFeedback(view)
        val currentState = _uiState.value
        if (currentState.isEvaluated) {
            _uiState.value = currentState.copy(
                expression = "",
                previewResult = null,
                result = "0",
                errorMessageRes = null,
                isEvaluated = false
            )
            return
        }

        var expr = currentState.expression
        if (expr.isNotEmpty()) {
            if (expr.endsWith(" ")) {
                expr = expr.trimEnd()
            }
            expr = if (expr.isNotEmpty()) expr.substring(0, expr.length - 1) else ""
            if (expr.endsWith(" ")) {
                expr = expr.trimEnd()
            }
            updateExpression(expr, isEvaluated = false)
        }
    }

    fun onClear(view: View? = null) {
        playFeedback(view)
        _uiState.value = CalculatorUiState()
    }

    fun onEquals(view: View? = null) {
        playFeedback(view)
        val currentState = _uiState.value
        val expr = currentState.expression.trim()
        if (expr.isEmpty()) return

        when (val evaluation = ExpressionEvaluator.evaluate(expr)) {
            is EvaluationResult.Success -> {
                val resultStr = evaluation.value
                _uiState.value = currentState.copy(
                    result = resultStr,
                    previewResult = null,
                    errorMessageRes = null,
                    isEvaluated = true
                )
                viewModelScope.launch {
                    repository.saveCalculation(expr, resultStr)
                }
            }
            is EvaluationResult.Error.DivisionByZero -> {
                _uiState.value = currentState.copy(
                    result = "Error",
                    previewResult = null,
                    errorMessageRes = R.string.error_division_by_zero,
                    isEvaluated = true
                )
            }
            is EvaluationResult.Error.DomainError -> {
                _uiState.value = currentState.copy(
                    result = "Error",
                    previewResult = null,
                    errorMessageRes = R.string.error_domain,
                    isEvaluated = true
                )
            }
            is EvaluationResult.Error.Overflow -> {
                _uiState.value = currentState.copy(
                    result = "Error",
                    previewResult = null,
                    errorMessageRes = R.string.error_overflow,
                    isEvaluated = true
                )
            }
            is EvaluationResult.Error.SyntaxError -> {
                _uiState.value = currentState.copy(
                    result = "Error",
                    previewResult = null,
                    errorMessageRes = R.string.error_invalid_expression,
                    isEvaluated = true
                )
            }
        }
    }

    fun onUseHistory(record: CalculationRecord, useAsExpression: Boolean) {
        if (useAsExpression) {
            updateExpression(record.expression, isEvaluated = false)
        } else {
            updateExpression(record.result, isEvaluated = true)
        }
        hideHistorySheet()
    }

    fun onDeleteHistoryRecord(record: CalculationRecord) {
        viewModelScope.launch {
            repository.deleteCalculation(record)
        }
    }

    fun onClearAllHistory() {
        viewModelScope.launch {
            repository.clearHistory()
            _uiState.value = _uiState.value.copy(showClearHistoryDialog = false)
        }
    }

    fun showHistorySheet() {
        _uiState.value = _uiState.value.copy(showHistorySheet = true)
    }

    fun hideHistorySheet() {
        _uiState.value = _uiState.value.copy(showHistorySheet = false)
    }

    fun showSettingsDialog() {
        _uiState.value = _uiState.value.copy(showSettingsDialog = true)
    }

    fun hideSettingsDialog() {
        _uiState.value = _uiState.value.copy(showSettingsDialog = false)
    }

    fun showClearHistoryConfirmation() {
        _uiState.value = _uiState.value.copy(showClearHistoryDialog = true)
    }

    fun hideClearHistoryConfirmation() {
        _uiState.value = _uiState.value.copy(showClearHistoryDialog = false)
    }

    fun copyToClipboard(context: Context, text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Calculator Result", text)
        clipboard.setPrimaryClip(clip)
        viewModelScope.launch {
            _snackbarMessage.emit(context.getString(R.string.copied))
        }
    }

    fun pasteFromClipboard(pastedText: String) {
        val sanitized = pastedText
            .replace("\n", "")
            .replace("\r", "")
            .trim()
        if (sanitized.isNotEmpty()) {
            val currentState = _uiState.value
            val newExpr = if (currentState.isEvaluated) sanitized else currentState.expression + sanitized
            updateExpression(newExpr, isEvaluated = false)
        }
    }

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch {
            preferencesRepository.setThemeMode(mode)
        }
    }

    fun setAccentPalette(palette: AccentPalette) {
        viewModelScope.launch {
            preferencesRepository.setAccentPalette(palette)
        }
    }

    fun setHapticEnabled(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.setHapticEnabled(enabled)
        }
    }

    fun setSoundEnabled(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.setSoundEnabled(enabled)
        }
    }

    private fun updateExpression(newExpr: String, isEvaluated: Boolean) {
        val preview = if (!isEvaluated && newExpr.isNotEmpty()) {
            ExpressionEvaluator.evaluatePreview(newExpr)
        } else null

        _uiState.value = _uiState.value.copy(
            expression = newExpr,
            previewResult = preview,
            errorMessageRes = null,
            isEvaluated = isEvaluated,
            result = if (isEvaluated) _uiState.value.result else (preview ?: _uiState.value.result)
        )
    }

    private fun extractLastNumber(expr: String): String {
        val separators = charArrayOf(' ', '+', '−', '-', '×', '*', '÷', '/', '^', '(', ')')
        val lastIdx = expr.lastIndexOfAny(separators)
        return if (lastIdx == -1) expr else expr.substring(lastIdx + 1)
    }

    class Factory(
        private val application: Application,
        private val repository: CalculatorRepository,
        private val preferencesRepository: UserPreferencesRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(CalculatorViewModel::class.java)) {
                return CalculatorViewModel(application, repository, preferencesRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}

package com.example.ui

import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.key.utf16CodePoint
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.ui.components.CalculatorDisplay
import com.example.ui.components.CalculatorKeypad
import com.example.ui.components.HistorySheet
import com.example.ui.components.SettingsDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorScreen(
    viewModel: CalculatorViewModel
) {
    val context = LocalContext.current
    val view = LocalView.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val history by viewModel.history.collectAsStateWithLifecycle()
    val preferences by viewModel.preferences.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val focusRequester = remember { FocusRequester() }

    // Listen to snackbar messages
    LaunchedEffect(Unit) {
        viewModel.snackbarMessage.collect { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    LaunchedEffect(Unit) {
        try {
            focusRequester.requestFocus()
        } catch (e: Exception) {
            // Ignore focus exception
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Calculate,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = stringResource(id = R.string.app_name),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.showHistorySheet() },
                        modifier = Modifier.testTag("action_history")
                    ) {
                        BadgedBox(
                            badge = {
                                if (history.isNotEmpty()) {
                                    Badge {
                                        Text(
                                            text = if (history.size > 99) "99+" else history.size.toString(),
                                            fontSize = 10.sp
                                        )
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.History,
                                contentDescription = stringResource(id = R.string.history_title)
                            )
                        }
                    }

                    IconButton(
                        onClick = { viewModel.showSettingsDialog() },
                        modifier = Modifier.testTag("action_settings")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = stringResource(id = R.string.settings)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .focusRequester(focusRequester)
                .focusable()
                .onKeyEvent { keyEvent ->
                    if (keyEvent.type != KeyEventType.KeyDown) return@onKeyEvent false

                    when (keyEvent.key) {
                        Key.Zero, Key.NumPad0 -> { viewModel.onDigit("0", view); true }
                        Key.One, Key.NumPad1 -> { viewModel.onDigit("1", view); true }
                        Key.Two, Key.NumPad2 -> { viewModel.onDigit("2", view); true }
                        Key.Three, Key.NumPad3 -> { viewModel.onDigit("3", view); true }
                        Key.Four, Key.NumPad4 -> { viewModel.onDigit("4", view); true }
                        Key.Five, Key.NumPad5 -> { viewModel.onDigit("5", view); true }
                        Key.Six, Key.NumPad6 -> { viewModel.onDigit("6", view); true }
                        Key.Seven, Key.NumPad7 -> { viewModel.onDigit("7", view); true }
                        Key.Eight, Key.NumPad8 -> { viewModel.onDigit("8", view); true }
                        Key.Nine, Key.NumPad9 -> { viewModel.onDigit("9", view); true }
                        Key.Period, Key.NumPadDot -> { viewModel.onDecimal(view); true }
                        Key.Plus, Key.NumPadAdd -> { viewModel.onOperator("+", view); true }
                        Key.Minus, Key.NumPadSubtract -> { viewModel.onOperator("−", view); true }
                        Key.Multiply, Key.NumPadMultiply -> { viewModel.onOperator("×", view); true }
                        Key.Slash, Key.NumPadDivide -> { viewModel.onOperator("÷", view); true }
                        Key.Enter, Key.NumPadEnter, Key.Equals, Key.NumPadEquals -> { viewModel.onEquals(view); true }
                        Key.Backspace -> { viewModel.onBackspace(view); true }
                        Key.Escape -> { viewModel.onClear(view); true }
                        else -> {
                            val char = keyEvent.utf16CodePoint.toChar()
                            when (char) {
                                '(' -> { viewModel.onOperator("(", view); true }
                                ')' -> { viewModel.onOperator(")", view); true }
                                '^' -> { viewModel.onOperator("^", view); true }
                                '%' -> { viewModel.onPercent(view); true }
                                else -> false
                            }
                        }
                    }
                },
            contentAlignment = Alignment.BottomCenter
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .widthIn(max = 520.dp)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                // Calculator Display
                CalculatorDisplay(
                    modifier = Modifier.weight(1f),
                    expression = uiState.expression,
                    previewResult = uiState.previewResult,
                    result = uiState.result,
                    errorMessageRes = uiState.errorMessageRes,
                    isEvaluated = uiState.isEvaluated,
                    onCopy = { textToCopy ->
                        viewModel.copyToClipboard(context, textToCopy)
                    },
                    onPaste = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val item = clipboard.primaryClip?.getItemAt(0)
                        val text = item?.text?.toString() ?: ""
                        if (text.isNotEmpty()) {
                            viewModel.pasteFromClipboard(text)
                        }
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Calculator Keypad
                CalculatorKeypad(
                    modifier = Modifier.fillMaxWidth(),
                    expression = uiState.expression,
                    onDigit = { digit, v -> viewModel.onDigit(digit, v) },
                    onDecimal = { v -> viewModel.onDecimal(v) },
                    onOperator = { op, v -> viewModel.onOperator(op, v) },
                    onSquareRoot = { v -> viewModel.onSquareRoot(v) },
                    onPercent = { v -> viewModel.onPercent(v) },
                    onParentheses = { v -> viewModel.onParentheses(v) },
                    onToggleSign = { v -> viewModel.onToggleSign(v) },
                    onBackspace = { v -> viewModel.onBackspace(v) },
                    onClear = { v -> viewModel.onClear(v) },
                    onEquals = { v -> viewModel.onEquals(v) }
                )
            }
        }
    }

    // History Sheet
    if (uiState.showHistorySheet) {
        HistorySheet(
            historyList = history,
            showClearConfirmation = uiState.showClearHistoryDialog,
            onDismiss = { viewModel.hideHistorySheet() },
            onUseItem = { record, useAsExpr -> viewModel.onUseHistory(record, useAsExpr) },
            onDeleteItem = { record -> viewModel.onDeleteHistoryRecord(record) },
            onClearAllRequest = { viewModel.showClearHistoryConfirmation() },
            onClearAllConfirm = { viewModel.onClearAllHistory() },
            onClearAllDismiss = { viewModel.hideClearHistoryConfirmation() }
        )
    }

    // Settings Dialog
    if (uiState.showSettingsDialog) {
        SettingsDialog(
            preferences = preferences,
            onDismiss = { viewModel.hideSettingsDialog() },
            onThemeModeChange = { mode -> viewModel.setThemeMode(mode) },
            onPaletteChange = { palette -> viewModel.setAccentPalette(palette) },
            onHapticChange = { enabled -> viewModel.setHapticEnabled(enabled) },
            onSoundChange = { enabled -> viewModel.setSoundEnabled(enabled) }
        )
    }
}

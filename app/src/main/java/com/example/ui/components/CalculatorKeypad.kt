package com.example.ui.components

import android.view.View
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CalculatorKeypad(
    modifier: Modifier = Modifier,
    expression: String,
    onDigit: (String, View) -> Unit,
    onDecimal: (View) -> Unit,
    onOperator: (String, View) -> Unit,
    onSquareRoot: (View) -> Unit,
    onPercent: (View) -> Unit,
    onParentheses: (View) -> Unit,
    onToggleSign: (View) -> Unit,
    onBackspace: (View) -> Unit,
    onClear: (View) -> Unit,
    onEquals: (View) -> Unit
) {
    val isAc = expression.isEmpty()

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Scientific Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CalculatorButton(
                modifier = Modifier.weight(1f).height(46.dp),
                text = "(",
                testTag = "btn_open_paren",
                buttonType = ButtonType.SECONDARY,
                onClick = { onOperator("(", it) }
            )
            CalculatorButton(
                modifier = Modifier.weight(1f).height(46.dp),
                text = ")",
                testTag = "btn_close_paren",
                buttonType = ButtonType.SECONDARY,
                onClick = { onOperator(")", it) }
            )
            CalculatorButton(
                modifier = Modifier.weight(1f).height(46.dp),
                text = "√",
                testTag = "btn_sqrt",
                buttonType = ButtonType.SECONDARY,
                onClick = onSquareRoot
            )
            CalculatorButton(
                modifier = Modifier.weight(1f).height(46.dp),
                text = "^",
                testTag = "btn_power",
                buttonType = ButtonType.SECONDARY,
                onClick = { onOperator("^", it) }
            )
            CalculatorButton(
                modifier = Modifier.weight(1f).height(46.dp),
                text = "%",
                testTag = "btn_percent_top",
                buttonType = ButtonType.SECONDARY,
                onClick = onPercent
            )
            CalculatorButton(
                modifier = Modifier.weight(1f).height(46.dp),
                text = "±",
                testTag = "btn_negate_top",
                buttonType = ButtonType.SECONDARY,
                onClick = onToggleSign
            )
        }

        Spacer(modifier = Modifier.height(2.dp))

        // Row 1: AC, ⌫, (), ÷
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            CalculatorButton(
                modifier = Modifier.weight(1f).height(64.dp),
                text = if (isAc) "AC" else "C",
                testTag = "btn_clear",
                buttonType = ButtonType.ACTION,
                onClick = onClear
            )
            CalculatorButton(
                modifier = Modifier.weight(1f).height(64.dp),
                icon = Icons.AutoMirrored.Filled.Backspace,
                text = "Delete",
                testTag = "btn_backspace",
                buttonType = ButtonType.ACTION,
                onClick = onBackspace
            )
            CalculatorButton(
                modifier = Modifier.weight(1f).height(64.dp),
                text = "( )",
                testTag = "btn_smart_paren",
                buttonType = ButtonType.SECONDARY,
                onClick = onParentheses
            )
            CalculatorButton(
                modifier = Modifier.weight(1f).height(64.dp),
                text = "÷",
                testTag = "btn_divide",
                buttonType = ButtonType.OPERATOR,
                onClick = { onOperator("÷", it) }
            )
        }

        // Row 2: 7, 8, 9, ×
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            CalculatorButton(
                modifier = Modifier.weight(1f).height(64.dp),
                text = "7",
                testTag = "btn_7",
                buttonType = ButtonType.NUMBER,
                onClick = { onDigit("7", it) }
            )
            CalculatorButton(
                modifier = Modifier.weight(1f).height(64.dp),
                text = "8",
                testTag = "btn_8",
                buttonType = ButtonType.NUMBER,
                onClick = { onDigit("8", it) }
            )
            CalculatorButton(
                modifier = Modifier.weight(1f).height(64.dp),
                text = "9",
                testTag = "btn_9",
                buttonType = ButtonType.NUMBER,
                onClick = { onDigit("9", it) }
            )
            CalculatorButton(
                modifier = Modifier.weight(1f).height(64.dp),
                text = "×",
                testTag = "btn_multiply",
                buttonType = ButtonType.OPERATOR,
                onClick = { onOperator("×", it) }
            )
        }

        // Row 3: 4, 5, 6, −
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            CalculatorButton(
                modifier = Modifier.weight(1f).height(64.dp),
                text = "4",
                testTag = "btn_4",
                buttonType = ButtonType.NUMBER,
                onClick = { onDigit("4", it) }
            )
            CalculatorButton(
                modifier = Modifier.weight(1f).height(64.dp),
                text = "5",
                testTag = "btn_5",
                buttonType = ButtonType.NUMBER,
                onClick = { onDigit("5", it) }
            )
            CalculatorButton(
                modifier = Modifier.weight(1f).height(64.dp),
                text = "6",
                testTag = "btn_6",
                buttonType = ButtonType.NUMBER,
                onClick = { onDigit("6", it) }
            )
            CalculatorButton(
                modifier = Modifier.weight(1f).height(64.dp),
                text = "−",
                testTag = "btn_subtract",
                buttonType = ButtonType.OPERATOR,
                onClick = { onOperator("−", it) }
            )
        }

        // Row 4: 1, 2, 3, +
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            CalculatorButton(
                modifier = Modifier.weight(1f).height(64.dp),
                text = "1",
                testTag = "btn_1",
                buttonType = ButtonType.NUMBER,
                onClick = { onDigit("1", it) }
            )
            CalculatorButton(
                modifier = Modifier.weight(1f).height(64.dp),
                text = "2",
                testTag = "btn_2",
                buttonType = ButtonType.NUMBER,
                onClick = { onDigit("2", it) }
            )
            CalculatorButton(
                modifier = Modifier.weight(1f).height(64.dp),
                text = "3",
                testTag = "btn_3",
                buttonType = ButtonType.NUMBER,
                onClick = { onDigit("3", it) }
            )
            CalculatorButton(
                modifier = Modifier.weight(1f).height(64.dp),
                text = "+",
                testTag = "btn_add",
                buttonType = ButtonType.OPERATOR,
                onClick = { onOperator("+", it) }
            )
        }

        // Row 5: 0, ., ±, =
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            CalculatorButton(
                modifier = Modifier.weight(1f).height(64.dp),
                text = "0",
                testTag = "btn_0",
                buttonType = ButtonType.NUMBER,
                onClick = { onDigit("0", it) }
            )
            CalculatorButton(
                modifier = Modifier.weight(1f).height(64.dp),
                text = ".",
                testTag = "btn_decimal",
                buttonType = ButtonType.NUMBER,
                onClick = onDecimal
            )
            CalculatorButton(
                modifier = Modifier.weight(1f).height(64.dp),
                text = "±",
                testTag = "btn_negate",
                buttonType = ButtonType.SECONDARY,
                onClick = onToggleSign
            )
            CalculatorButton(
                modifier = Modifier.weight(1f).height(64.dp),
                text = "=",
                testTag = "btn_equals",
                buttonType = ButtonType.EQUALS,
                onClick = onEquals
            )
        }
    }
}

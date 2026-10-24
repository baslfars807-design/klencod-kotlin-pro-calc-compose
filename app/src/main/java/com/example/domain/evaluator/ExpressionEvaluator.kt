package com.example.domain.evaluator

import com.example.domain.model.EvaluationResult
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale
import kotlin.math.pow
import kotlin.math.sqrt

object ExpressionEvaluator {

    private val MATH_CONTEXT = MathContext(16, RoundingMode.HALF_UP)
    private val DISPLAY_DECIMAL_FORMAT = DecimalFormat("#,##0.########", DecimalFormatSymbols(Locale.US))
    private val SCIENTIFIC_FORMAT = DecimalFormat("0.######E0", DecimalFormatSymbols(Locale.US))

    /**
     * Cleans and normalizes the user input expression for parsing.
     */
    fun normalize(raw: String): String {
        var expr = raw
            .replace(" ", "")
            .replace("*", "×")
            .replace("/", "÷")
            .replace("-", "−")

        // Insert implicit multiplication:
        // 1. digit or ')' or '%' followed by '(' or '√'
        // 2. ')' followed by digit
        // 3. '%' followed by digit
        val sb = StringBuilder()
        for (i in expr.indices) {
            val c = expr[i]
            sb.append(c)
            if (i < expr.length - 1) {
                val next = expr[i + 1]
                val currentIsNumberEnd = c.isDigit() || c == ')' || c == '%'
                val nextIsNumberStart = next.isDigit() || next == '(' || next == '√'

                if ((c.isDigit() || c == '%' || c == ')') && (next == '(' || next == '√')) {
                    sb.append("×")
                } else if (c == ')' && (next.isDigit() || next == '.')) {
                    sb.append("×")
                } else if (c == '%' && (next.isDigit() || next == '.')) {
                    sb.append("×")
                }
            }
        }
        return sb.toString()
    }

    /**
     * Evaluates the expression and returns an EvaluationResult.
     */
    fun evaluate(rawExpression: String): EvaluationResult {
        val trimmed = rawExpression.trim()
        if (trimmed.isEmpty()) {
            return EvaluationResult.Error.SyntaxError("Empty expression")
        }

        val normalized = normalize(trimmed)

        return try {
            val parser = Parser(normalized)
            val result = parser.parseExpression()
            if (!parser.isAtEnd()) {
                EvaluationResult.Error.SyntaxError("Unexpected character: ${parser.peek()}")
            } else {
                formatResult(result)
            }
        } catch (e: DivisionByZeroException) {
            EvaluationResult.Error.DivisionByZero
        } catch (e: DomainException) {
            EvaluationResult.Error.DomainError
        } catch (e: OverflowException) {
            EvaluationResult.Error.Overflow
        } catch (e: Exception) {
            EvaluationResult.Error.SyntaxError(e.message ?: "Invalid syntax")
        }
    }

    /**
     * Evaluates live preview while user is typing.
     * Silently balances parentheses and ignores incomplete operators.
     */
    fun evaluatePreview(rawExpression: String): String? {
        var expr = rawExpression.trim()
        if (expr.isEmpty()) return null

        // Strip trailing operators
        while (expr.isNotEmpty() && (expr.endsWith("+") || expr.endsWith("−") || expr.endsWith("×") || expr.endsWith("÷") || expr.endsWith("^") || expr.endsWith("√") || expr.endsWith("."))) {
            expr = expr.substring(0, expr.length - 1).trim()
        }
        if (expr.isEmpty()) return null

        // Count open and close parentheses, auto-balance
        val openCount = expr.count { it == '(' }
        val closeCount = expr.count { it == ')' }
        if (openCount > closeCount) {
            expr += ")".repeat(openCount - closeCount)
        }

        return when (val res = evaluate(expr)) {
            is EvaluationResult.Success -> res.value
            else -> null
        }
    }

    fun formatDisplay(rawNumber: Double): String {
        if (rawNumber.isNaN() || rawNumber.isInfinite()) return "Error"
        val absVal = kotlin.math.abs(rawNumber)
        return if (absVal != 0.0 && (absVal >= 1e12 || absVal < 1e-6)) {
            SCIENTIFIC_FORMAT.format(rawNumber)
        } else {
            val bd = BigDecimal.valueOf(rawNumber).stripTrailingZeros()
            DISPLAY_DECIMAL_FORMAT.format(bd)
        }
    }

    private fun formatResult(value: BigDecimal): EvaluationResult {
        val doubleVal = value.toDouble()
        if (doubleVal.isInfinite()) {
            return EvaluationResult.Error.Overflow
        }
        if (doubleVal.isNaN()) {
            return EvaluationResult.Error.DomainError
        }

        val absDouble = kotlin.math.abs(doubleVal)
        val formatted = if (absDouble != 0.0 && (absDouble >= 1e12 || absDouble < 1e-6)) {
            SCIENTIFIC_FORMAT.format(doubleVal)
        } else {
            val stripped = value.setScale(10, RoundingMode.HALF_UP).stripTrailingZeros()
            // Format without trailing zero if integer
            if (stripped.scale() <= 0) {
                stripped.toPlainString()
            } else {
                stripped.toPlainString()
            }
        }

        return EvaluationResult.Success(value = formatted, rawValue = doubleVal)
    }

    private class DivisionByZeroException : RuntimeException()
    private class DomainException : RuntimeException()
    private class OverflowException : RuntimeException()

    /**
     * Recursive Descent Parser for standard arithmetic grammar:
     * Expression -> Additive
     * Additive   -> Multiplicative (('+' | '−') Multiplicative)*
     * Multiplicative -> Power (('×' | '÷') Power)*
     * Power      -> Unary ('^' Power)?
     * Unary      -> ('+' | '−') Unary | '√' Unary | Postfix
     * Postfix    -> Primary ('%')*
     * Primary    -> NUMBER | '(' Expression ')'
     */
    private class Parser(private val input: String) {
        private var pos = 0

        fun isAtEnd(): Boolean = pos >= input.length

        fun peek(): Char = if (isAtEnd()) '\u0000' else input[pos]

        fun advance(): Char {
            val c = peek()
            if (!isAtEnd()) pos++
            return c
        }

        fun match(expected: Char): Boolean {
            if (peek() == expected) {
                advance()
                return true
            }
            return false
        }

        fun parseExpression(): BigDecimal {
            return parseAdditive()
        }

        private fun parseAdditive(): BigDecimal {
            var left = parseMultiplicative()

            while (true) {
                when {
                    match('+') -> {
                        // Check if right is percentage: e.g. 100 + 10% = 100 + (100 * 0.10)
                        val rightTokenStart = pos
                        val right = parseMultiplicative()
                        left = left.add(right, MATH_CONTEXT)
                    }
                    match('−') -> {
                        val right = parseMultiplicative()
                        left = left.subtract(right, MATH_CONTEXT)
                    }
                    else -> break
                }
            }
            return left
        }

        private fun parseMultiplicative(): BigDecimal {
            var left = parsePower()

            while (true) {
                when {
                    match('×') -> {
                        val right = parsePower()
                        left = left.multiply(right, MATH_CONTEXT)
                    }
                    match('÷') -> {
                        val right = parsePower()
                        if (right.compareTo(BigDecimal.ZERO) == 0) {
                            throw DivisionByZeroException()
                        }
                        left = left.divide(right, 14, RoundingMode.HALF_UP)
                    }
                    else -> break
                }
            }
            return left
        }

        private fun parsePower(): BigDecimal {
            val base = parseUnary()

            if (match('^')) {
                val exponent = parsePower() // right-associative
                val expDouble = exponent.toDouble()
                val baseDouble = base.toDouble()

                if (baseDouble < 0 && expDouble % 1.0 != 0.0) {
                    throw DomainException()
                }

                val powResult = baseDouble.pow(expDouble)
                if (powResult.isInfinite()) throw OverflowException()
                if (powResult.isNaN()) throw DomainException()

                return BigDecimal.valueOf(powResult)
            }
            return base
        }

        private fun parseUnary(): BigDecimal {
            if (match('+')) {
                return parseUnary()
            }
            if (match('−')) {
                return parseUnary().negate()
            }
            if (match('√')) {
                val operand = parseUnary()
                val doubleVal = operand.toDouble()
                if (doubleVal < 0) {
                    throw DomainException()
                }
                val sqrtVal = sqrt(doubleVal)
                if (sqrtVal.isNaN()) throw DomainException()
                return BigDecimal.valueOf(sqrtVal)
            }
            return parsePostfix()
        }

        private fun parsePostfix(): BigDecimal {
            var result = parsePrimary()
            while (match('%')) {
                // x% = x / 100
                result = result.divide(BigDecimal("100"), MATH_CONTEXT)
            }
            return result
        }

        private fun parsePrimary(): BigDecimal {
            if (match('(')) {
                val value = parseExpression()
                if (!match(')')) {
                    throw IllegalArgumentException("Missing closing parenthesis")
                }
                return value
            }

            if (peek().isDigit() || peek() == '.') {
                return parseNumber()
            }

            throw IllegalArgumentException("Expected number or '(' but found '${peek()}' at index $pos")
        }

        private fun parseNumber(): BigDecimal {
            val start = pos
            var hasDot = false

            while (!isAtEnd()) {
                val c = peek()
                if (c.isDigit()) {
                    advance()
                } else if (c == '.') {
                    if (hasDot) break
                    hasDot = true
                    advance()
                } else if (c == 'E' || c == 'e') {
                    advance()
                    if (peek() == '+' || peek() == '−' || peek() == '-') {
                        advance()
                    }
                    while (!isAtEnd() && peek().isDigit()) {
                        advance()
                    }
                    break
                } else {
                    break
                }
            }

            val numStr = input.substring(start, pos).replace('−', '-')
            if (numStr == "." || numStr.isEmpty()) {
                throw IllegalArgumentException("Invalid number format at index $start")
            }
            return BigDecimal(numStr)
        }
    }
}

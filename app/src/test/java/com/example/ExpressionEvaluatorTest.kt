package com.example

import com.example.domain.evaluator.ExpressionEvaluator
import com.example.domain.model.EvaluationResult
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExpressionEvaluatorTest {

    @Test
    fun testBasicArithmetic() {
        val res = ExpressionEvaluator.evaluate("2 + 3 × 4")
        assertTrue(res is EvaluationResult.Success)
        assertEquals("14", (res as EvaluationResult.Success).value)
    }

    @Test
    fun testParenthesesPrecedence() {
        val res = ExpressionEvaluator.evaluate("(2 + 3) × 4")
        assertTrue(res is EvaluationResult.Success)
        assertEquals("20", (res as EvaluationResult.Success).value)
    }

    @Test
    fun testSquareRoot() {
        val res = ExpressionEvaluator.evaluate("√16")
        assertTrue(res is EvaluationResult.Success)
        assertEquals("4", (res as EvaluationResult.Success).value)
    }

    @Test
    fun testPower() {
        val res = ExpressionEvaluator.evaluate("2 ^ 3")
        assertTrue(res is EvaluationResult.Success)
        assertEquals("8", (res as EvaluationResult.Success).value)
    }

    @Test
    fun testPercentage() {
        val res = ExpressionEvaluator.evaluate("50%")
        assertTrue(res is EvaluationResult.Success)
        assertEquals("0.5", (res as EvaluationResult.Success).value)
    }

    @Test
    fun testDecimalPrecision() {
        val res = ExpressionEvaluator.evaluate("0.1 + 0.2")
        assertTrue(res is EvaluationResult.Success)
        assertEquals("0.3", (res as EvaluationResult.Success).value)
    }

    @Test
    fun testDivisionByZero() {
        val res = ExpressionEvaluator.evaluate("10 ÷ 0")
        assertTrue(res is EvaluationResult.Error.DivisionByZero)
    }

    @Test
    fun testNegativeNumbers() {
        val res = ExpressionEvaluator.evaluate("−5 + 12")
        assertTrue(res is EvaluationResult.Success)
        assertEquals("7", (res as EvaluationResult.Success).value)
    }

    @Test
    fun testPreviewEvaluation() {
        val preview = ExpressionEvaluator.evaluatePreview("5 + 3 × ")
        // Trailing operator stripped so evaluates 5 + 3
        assertEquals("8", preview)
    }
}

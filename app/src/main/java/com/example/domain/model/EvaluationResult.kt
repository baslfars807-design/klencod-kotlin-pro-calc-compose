package com.example.domain.model

sealed class EvaluationResult {
    data class Success(val value: String, val rawValue: Double) : EvaluationResult()
    sealed class Error : EvaluationResult() {
        object DivisionByZero : Error()
        object DomainError : Error()
        object Overflow : Error()
        data class SyntaxError(val message: String = "Invalid expression") : Error()
    }
}

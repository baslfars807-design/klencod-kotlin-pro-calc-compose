package com.example.data.repository

import com.example.data.local.CalculationDao
import com.example.data.model.CalculationRecord
import kotlinx.coroutines.flow.Flow

class CalculatorRepository(private val dao: CalculationDao) {
    val history: Flow<List<CalculationRecord>> = dao.getAllCalculations()

    suspend fun saveCalculation(expression: String, result: String): Long {
        return dao.insertCalculation(
            CalculationRecord(
                expression = expression,
                result = result,
                timestamp = System.currentTimeMillis()
            )
        )
    }

    suspend fun deleteCalculation(record: CalculationRecord) {
        dao.deleteCalculation(record)
    }

    suspend fun deleteCalculationById(id: Long) {
        dao.deleteCalculationById(id)
    }

    suspend fun clearHistory() {
        dao.clearAllCalculations()
    }
}

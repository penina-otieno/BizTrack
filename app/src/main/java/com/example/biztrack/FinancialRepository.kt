package com.example.biztrack

import java.util.UUID

enum class TransactionType {
    SALE,
    EXPENSE
}

data class FinancialTransaction(
    val id: String = UUID.randomUUID().toString(),
    val description: String,
    val amount: Double,
    val type: TransactionType,
    val date: Long = System.currentTimeMillis()
)

object FinancialRepository {

    private val transactions = mutableListOf<FinancialTransaction>()

    // Add a transaction
    fun addTransaction(transaction: FinancialTransaction) {
        require(transaction.amount > 0) {
            "Transaction amount must be greater than zero"
        }
        transactions.add(transaction)
    }

    // Retrieve all transactions
    fun getAllTransactions(): List<FinancialTransaction> {
        return transactions.toList()
    }

    // Update an existing transaction
    fun updateTransaction(transaction: FinancialTransaction) {
        val index = transactions.indexOfFirst {
            it.id == transaction.id
        }

        if (index != -1) {
            require(transaction.amount > 0)
            transactions[index] = transaction
        }
    }

    // Delete a transaction
    fun deleteTransaction(id: String) {
        transactions.removeAll {
            it.id == id
        }
    }

    // Calculate total sales
    fun getTotalSales(): Double {
        return transactions
            .filter { it.type == TransactionType.SALE }
            .sumOf { it.amount }
    }

    // Calculate total expenses
    fun getTotalExpenses(): Double {
        return transactions
            .filter { it.type == TransactionType.EXPENSE }
            .sumOf { it.amount }
    }

    // Calculate profit or loss
    fun getProfitLoss(): Double {
        return getTotalSales() - getTotalExpenses()
    }
}

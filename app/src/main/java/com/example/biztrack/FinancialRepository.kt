package com.example.biztrack

//added to ROOM -David
import com.example.biztrack.data.local.BizTrackDatabase
import com.example.biztrack.data.local.dao.BudgetDao
import com.example.biztrack.data.local.dao.TransactionDao
import com.example.biztrack.data.local.entity.BudgetEntity
import com.example.biztrack.data.local.entity.TransactionEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import java.util.UUID

enum class TransactionType {
    SALE,
    EXPENSE,
    TRANSFER
}

data class FinancialTransaction(
    val id: String = UUID.randomUUID().toString(),
    val description: String,
    val amount: Double,
    val type: TransactionType,
    val date: Long = System.currentTimeMillis()
)

object FinancialRepository {

    private var transactionDao: TransactionDao? = null
    private var budgetDao: BudgetDao? = null

    fun init(database: BizTrackDatabase) {
        transactionDao = database.transactionDao()
        budgetDao = database.budgetDao()
    }

    // Add a transaction
    fun addTransaction(transaction: FinancialTransaction) {
        require(transaction.amount > 0) {
            "Transaction amount must be greater than zero"
        }
        runBlocking(Dispatchers.IO) {
            transactionDao?.insertTransaction(transaction.toEntity())
        }
    }

    // Retrieve all transactions
    fun getAllTransactions(): List<FinancialTransaction> {
        return runBlocking(Dispatchers.IO) {
            transactionDao?.getAllTransactionsList()?.map { it.toDomainModel() } ?: emptyList()
        }
    }

    // Update an existing transaction
    fun updateTransaction(transaction: FinancialTransaction) {
        require(transaction.amount > 0) {
            "Transaction amount must be greater than zero"
        }
        runBlocking(Dispatchers.IO) {
            transactionDao?.updateTransaction(transaction.toEntity())
        }
    }

    // Delete a transaction
    fun deleteTransaction(id: String) {
        runBlocking(Dispatchers.IO) {
            transactionDao?.deleteTransactionById(id)
        }
    }

    // Calculate total sales
    fun getTotalSales(): Double {
        return getAllTransactions()
            .filter { it.type == TransactionType.SALE }
            .sumOf { it.amount }
    }

    // Calculate total expenses
    fun getTotalExpenses(): Double {
        return getAllTransactions()
            .filter { it.type == TransactionType.EXPENSE }
            .sumOf { it.amount }
    }

    // Calculate profit or loss
    fun getProfitLoss(): Double {
        return getTotalSales() - getTotalExpenses()
    }

    // Save budget limit to Room
    fun saveBudget(amount: Double) {
        runBlocking(Dispatchers.IO) {
            budgetDao?.saveBudget(BudgetEntity(id = 1, allocatedAmount = amount))
        }
    }

    // Retrieve saved budget limit from Room
    fun getBudget(): Double {
        return runBlocking(Dispatchers.IO) {
            budgetDao?.getMainBudget()?.allocatedAmount ?: 0.0
        }
    }

    private fun FinancialTransaction.toEntity() = TransactionEntity(
        id = id,
        description = description,
        amount = amount,
        type = type.name,
        date = date
    )

    private fun TransactionEntity.toDomainModel() = FinancialTransaction(
        id = id,
        description = description,
        amount = amount,
        type = runCatching { TransactionType.valueOf(type) }.getOrDefault(TransactionType.EXPENSE),
        date = date
    )
}
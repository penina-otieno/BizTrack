
package com.example.biztrack

import android.content.Context
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
    private var sessionManager: SessionManager? = null

    fun init(database: BizTrackDatabase, context: Context) {
        transactionDao = database.transactionDao()
        budgetDao = database.budgetDao()
        sessionManager = SessionManager(context.applicationContext)
    }

    private fun currentUserId(): String =
        sessionManager?.getCurrentUserId()
            ?: error("No active user session")

    // ADD TRANSACTION

    fun addTransaction(transaction: FinancialTransaction) {
        require(transaction.amount > 0) {
            "Transaction amount must be greater than zero"
        }

        val userId = currentUserId()

        runBlocking(Dispatchers.IO) {
            transactionDao?.insertTransaction(
                transaction.toEntity(userId)
            )
        }
    }

    // GET CURRENT USER'S TRANSACTIONS

    fun getAllTransactions(): List<FinancialTransaction> {
        val userId = currentUserId()

        return runBlocking(Dispatchers.IO) {
            transactionDao
                ?.getAllTransactionsList(userId)
                ?.map { it.toDomainModel() }
                ?: emptyList()
        }
    }

    // UPDATE TRANSACTION

    fun updateTransaction(transaction: FinancialTransaction) {
        require(transaction.amount > 0) {
            "Transaction amount must be greater than zero"
        }

        val userId = currentUserId()

        runBlocking(Dispatchers.IO) {
            val existing = transactionDao
                ?.getAllTransactionsList(userId)
                ?.any { it.id == transaction.id } ?: false

            if (existing) {
                transactionDao?.updateTransaction(
                    transaction.toEntity(userId)
                )
            }
        }
    }

    // DELETE TRANSACTION

    fun deleteTransaction(id: String) {
        val userId = currentUserId()

        runBlocking(Dispatchers.IO) {
            transactionDao?.deleteTransactionById(id, userId)
        }
    }

    // TOTAL SALES

    fun getTotalSales(): Double {
        return getAllTransactions()
            .filter { it.type == TransactionType.SALE }
            .sumOf { it.amount }
    }

    // TOTAL EXPENSES

    fun getTotalExpenses(): Double {
        return getAllTransactions()
            .filter { it.type == TransactionType.EXPENSE }
            .sumOf { it.amount }
    }

    // PROFIT OR LOSS

    fun getProfitLoss(): Double {
        return getTotalSales() - getTotalExpenses()
    }

    // SAVE CURRENT USER'S BUDGET

    fun saveBudget(amount: Double) {
        require(amount >= 0) {
            "Budget cannot be negative"
        }

        val userId = currentUserId()

        runBlocking(Dispatchers.IO) {
            budgetDao?.saveBudget(
                BudgetEntity(
                    userId = userId,
                    allocatedAmount = amount
                )
            )
        }
    }

    // GET CURRENT USER'S BUDGET

    fun getBudget(): Double {
        val userId = currentUserId()

        return runBlocking(Dispatchers.IO) {
            budgetDao?.getMainBudget(userId)?.allocatedAmount ?: 0.0
        }
    }

    // CONVERT TO ROOM ENTITY

    private fun FinancialTransaction.toEntity(
        userId: String
    ) = TransactionEntity(
        id = id,
        description = description,
        amount = amount,
        type = type.name,
        date = date,
        userId = userId
    )

    // CONVERT FROM ROOM ENTITY

    private fun TransactionEntity.toDomainModel() =
        FinancialTransaction(
            id = id,
            description = description,
            amount = amount,
            type = runCatching {
                TransactionType.valueOf(type)
            }.getOrDefault(TransactionType.EXPENSE),
            date = date
        )
}

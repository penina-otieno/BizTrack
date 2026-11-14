
package com.example.biztrack.ui.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.LiveData
import com.example.biztrack.data.model.Transaction
import com.example.biztrack.FinancialRepository
import com.example.biztrack.FinancialTransaction
import com.example.biztrack.TransactionType

class TransactionViewModel : ViewModel() {

    private val _transactions =
        MutableLiveData<List<Transaction>>(emptyList())

    val transactions: LiveData<List<Transaction>> = _transactions
    // Load existing transactions from the shared repository.
    init {
        val savedTransactions = FinancialRepository.getAllTransactions()

        val historyTransactions = savedTransactions.mapIndexed { index, item ->
            Transaction(
                id = index + 1,
                type = when (item.type) {
                    TransactionType.SALE -> "Income"
                    TransactionType.EXPENSE -> "Expense"
                    TransactionType.TRANSFER -> "Transfer"
                },
                title = item.description,
                amount = item.amount,
                date = java.text.SimpleDateFormat(
                    "dd MMM yyyy, EEEE",
                    java.util.Locale.getDefault()
                ).format(java.util.Date(item.date)),
                financialTransactionId = item.id
            )
        }

        _transactions.value = historyTransactions
    }

    // CREATE
    fun addTransaction(
        type: String,
        title: String,
        amountStr: String
    ): String? {

        if (title.isBlank()) {
            return "Description is required"
        }

        val amount = amountStr.toDoubleOrNull()

        if (amount == null || amount <= 0) {
            return "Amount must be a valid positive number"
        }

        val financialType = when (type) {
            "Income" -> TransactionType.SALE
            "Expense" -> TransactionType.EXPENSE
            "Transfer" -> TransactionType.TRANSFER
            else -> return "Invalid transaction type"
        }

        // Save in the shared financial repository first.
        val financialTransaction = FinancialTransaction(
            description = title,
            amount = amount,
            type = financialType
        )

        FinancialRepository.addTransaction(financialTransaction)

        // Save in transaction history using the matching ID.
        val currentDate = java.text.SimpleDateFormat(
            "dd MMM yyyy, EEEE",
            java.util.Locale.getDefault()
        ).format(java.util.Date())

        val newTransaction = Transaction(
            type = type,
            title = title,
            amount = amount,
            date = currentDate,
            financialTransactionId = financialTransaction.id
        )

        val currentList =
            _transactions.value?.toMutableList() ?: mutableListOf()

        currentList.add(newTransaction)
        _transactions.value = currentList

        return null
    }

    // DELETE
    fun deleteTransaction(transaction: Transaction) {

        // Delete the matching financial record.
        transaction.financialTransactionId?.let { id ->
            FinancialRepository.deleteTransaction(id)
        }

        // Delete from transaction history.
        val currentList =
            _transactions.value?.toMutableList() ?: mutableListOf()

        currentList.remove(transaction)
        _transactions.value = currentList
    }
}

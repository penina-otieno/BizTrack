package com.example.biztrack.ui.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.LiveData
import com.example.biztrack.data.model.Transaction

class TransactionViewModel : ViewModel() {
    // Mock data for now. David will replace this with the Room Database later.
    private val _transactions = MutableLiveData<List<Transaction>>(emptyList())
    val transactions: LiveData<List<Transaction>> = _transactions

    // CREATE with Validation
    fun addTransaction(type: String, title: String, amountStr: String): String? {
        if (title.isBlank()) return "Description is required"

        val amount = amountStr.toDoubleOrNull()
        if (amount == null || amount <= 0) return "Amount must be a valid positive number"

        val newTransaction = Transaction(type = type, title = title, amount = amount)
        val currentList = _transactions.value?.toMutableList() ?: mutableListOf()
        currentList.add(newTransaction)
        _transactions.value = currentList

        return null // null means success!
    }

    // DELETE
    fun deleteTransaction(transaction: Transaction) {
        val currentList = _transactions.value?.toMutableList() ?: mutableListOf()
        currentList.remove(transaction)
        _transactions.value = currentList
    }
}
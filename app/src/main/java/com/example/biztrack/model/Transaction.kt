package com.example.biztrack.data.model

data class Transaction(
    val id: Int = 0,
    val type: String, // Income, Expense or Transfer
    val title: String,
    val amount: Double,
    val date: String = "27 Jan 2020, Monday",
    val financialTransactionId: String? = null
)

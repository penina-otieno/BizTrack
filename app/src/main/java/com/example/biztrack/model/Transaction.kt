package com.example.biztrack.data.model

data class Transaction(
    val id: Int = 0,
    val type: String, // "Income" or "Expense"
    val title: String,
    val amount: Double,
    val date: String = "27 Jan 2020, Monday"
)
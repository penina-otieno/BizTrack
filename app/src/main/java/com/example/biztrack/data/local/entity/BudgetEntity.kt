package com.example.biztrack.data.local.entity

import androidx.room.Entity

@Entity(
    tableName = "budgets",
    primaryKeys = ["userId", "category", "period"]
)
data class BudgetEntity(
    val userId: String,
    val category: String = "Overall",
    val allocatedAmount: Double,
    val period: String = "Current"
)
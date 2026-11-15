package com.example.biztrack.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "budgets")
data class BudgetEntity(
    @PrimaryKey val id: Int = 1,
    val category: String = "Overall",
    val allocatedAmount: Double,
    val period: String = "Current"
)
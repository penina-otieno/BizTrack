package com.example.biztrack.data.local.dao

import androidx.room.*
import com.example.biztrack.data.local.entity.BudgetEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BudgetDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveBudget(budget: BudgetEntity)

    @Query("SELECT * FROM budgets WHERE id = 1 LIMIT 1")
    suspend fun getMainBudget(): BudgetEntity?

    @Query("SELECT * FROM budgets ORDER BY id DESC")
    fun getAllBudgets(): Flow<List<BudgetEntity>>
}
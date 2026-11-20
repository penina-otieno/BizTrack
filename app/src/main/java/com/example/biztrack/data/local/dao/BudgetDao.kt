package com.example.biztrack.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.biztrack.data.local.entity.BudgetEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BudgetDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveBudget(budget: BudgetEntity)

    @Query("SELECT * FROM budgets WHERE userId = :userId LIMIT 1")
    suspend fun getMainBudget(userId: String): BudgetEntity?

    @Query("SELECT * FROM budgets WHERE userId = :userId")
    fun getAllBudgets(userId: String): Flow<List<BudgetEntity>>
    @Query("""
    UPDATE budgets
    SET userId = :newUserId
    WHERE userId = 'legacy_unclaimed'
""")
    suspend fun claimLegacyBudgets(newUserId: String)
}
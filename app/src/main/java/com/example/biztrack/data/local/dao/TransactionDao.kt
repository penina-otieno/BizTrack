package com.example.biztrack.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Update
import androidx.room.Query
import com.example.biztrack.data.local.entity.TransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity)

    @Update
    suspend fun updateTransaction(transaction: TransactionEntity)

    @Query("""
        DELETE FROM transactions
        WHERE id = :id AND userId = :userId
    """)
    suspend fun deleteTransactionById(id: String, userId: String)

    @Query("""
        SELECT * FROM transactions
        WHERE userId = :userId
        ORDER BY date DESC
    """)
    suspend fun getAllTransactionsList(userId: String): List<TransactionEntity>

    @Query("""
        SELECT * FROM transactions
        WHERE userId = :userId
        ORDER BY date DESC
    """)
    fun getAllTransactions(userId: String): Flow<List<TransactionEntity>>

    @Query("""
        SELECT COALESCE(SUM(amount), 0.0)
        FROM transactions
        WHERE type = 'SALE' AND userId = :userId
    """)
    fun getTotalSales(userId: String): Flow<Double>

    @Query("""
        SELECT COALESCE(SUM(amount), 0.0)
        FROM transactions
        WHERE type = 'EXPENSE' AND userId = :userId
    """)
    fun getTotalExpenses(userId: String): Flow<Double>

    @Query("""
        UPDATE transactions
        SET userId = :newUserId
        WHERE userId = 'legacy_unclaimed'
    """)
    suspend fun claimLegacyTransactions(newUserId: String)
}
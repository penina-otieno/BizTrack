package com.example.biztrack.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.biztrack.data.local.dao.BudgetDao
import com.example.biztrack.data.local.dao.ProductDao
import com.example.biztrack.data.local.dao.TransactionDao
import com.example.biztrack.data.local.entity.BudgetEntity
import com.example.biztrack.data.local.entity.ProductEntity
import com.example.biztrack.data.local.entity.TransactionEntity

@Database(
    entities = [ProductEntity::class, TransactionEntity::class, BudgetEntity::class],
    version = 2,
    exportSchema = false
)
abstract class BizTrackDatabase : RoomDatabase() {

    abstract fun productDao(): ProductDao
    abstract fun transactionDao(): TransactionDao
    abstract fun budgetDao(): BudgetDao

    companion object {
        @Volatile
        private var INSTANCE: BizTrackDatabase? = null

        fun getDatabase(context: Context): BizTrackDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BizTrackDatabase::class.java,
                    "biztrack_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
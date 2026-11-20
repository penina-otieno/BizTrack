package com.example.biztrack.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey val id: String,
    val description: String,
    val amount: Double,
    val type: String,
    val date: Long,

    @ColumnInfo(defaultValue = "'legacy_unclaimed'")
    val userId: String = "legacy_unclaimed"
)
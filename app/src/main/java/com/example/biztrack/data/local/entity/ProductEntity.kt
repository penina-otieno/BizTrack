package com.example.biztrack.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey val id: String,
    val name: String,
    val category: String,
    val quantity: Int,
    val unitPrice: Double,
    val reorderLevel: Int,
    val updatedAt: Long
)
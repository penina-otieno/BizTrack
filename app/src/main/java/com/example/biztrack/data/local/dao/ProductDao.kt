package com.example.biztrack.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Update
import androidx.room.Query
import com.example.biztrack.data.local.entity.ProductEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: ProductEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(products: List<ProductEntity>)

    @Update
    suspend fun updateProduct(product: ProductEntity)

    @Query("""
        DELETE FROM products
        WHERE id = :id AND userId = :userId
    """)
    suspend fun deleteById(id: String, userId: String)

    @Query("""
        SELECT * FROM products
        WHERE id = :id AND userId = :userId
        LIMIT 1
    """)
    suspend fun getProductById(id: String, userId: String): ProductEntity?

    @Query("""
        SELECT * FROM products
        WHERE userId = :userId
        ORDER BY name ASC
    """)
    suspend fun getAllProductsList(userId: String): List<ProductEntity>

    @Query("""
        SELECT * FROM products
        WHERE userId = :userId
        ORDER BY name ASC
    """)
    fun getAllProducts(userId: String): Flow<List<ProductEntity>>

    @Query("""
        UPDATE products
        SET userId = :newUserId
        WHERE userId = 'legacy_unclaimed'
    """)
    suspend fun claimLegacyProducts(newUserId: String)
}
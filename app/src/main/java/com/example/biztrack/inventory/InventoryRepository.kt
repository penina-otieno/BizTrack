
package com.example.biztrack.inventory

import android.content.Context
import com.example.biztrack.SessionManager
import com.example.biztrack.data.local.BizTrackDatabase
import com.example.biztrack.data.local.entity.ProductEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

class InventoryRepository private constructor(context: Context) {

    private val productDao =
        BizTrackDatabase.getDatabase(context).productDao()

    private val sessionManager = SessionManager(context)

    private fun currentUserId(): String =
        sessionManager.getCurrentUserId()
            ?: error("No active user session")

    // READ

    fun getAll(): List<Product> = runBlocking(Dispatchers.IO) {
        val userId = currentUserId()

        // Seed sample products separately for each account.
        if (productDao.getAllProductsList(userId).isEmpty()) {
            productDao.insertAll(
                sampleProducts().map { it.toEntity(userId) }
            )
        }

        productDao.getAllProductsList(userId)
            .map { it.toDomainModel() }
    }

    fun getLowStockCount(): Int =
        getAll().count { it.isLowStock }

    fun getTotalStockValue(): Double =
        getAll().sumOf { it.stockValue }

    // CREATE

    fun add(product: Product) = runBlocking(Dispatchers.IO) {
        productDao.insertProduct(
            product.toEntity(currentUserId())
        )
    }

    // UPDATE

    fun update(product: Product) = runBlocking(Dispatchers.IO) {
        val userId = currentUserId()

        val existing = productDao.getProductById(
            product.id,
            userId
        ) ?: return@runBlocking

        productDao.updateProduct(
            product.copy(
                updatedAt = System.currentTimeMillis()
            ).toEntity(userId)
        )
    }

    // DELETE

    fun delete(id: String) = runBlocking(Dispatchers.IO) {
        productDao.deleteById(id, currentUserId())
    }

    // STOCK ADJUSTMENT

    fun adjustStock(id: String, delta: Int) =
        runBlocking(Dispatchers.IO) {
            val userId = currentUserId()

            val current = productDao.getProductById(id, userId)
                ?: return@runBlocking

            productDao.updateProduct(
                current.copy(
                    quantity = (current.quantity + delta)
                        .coerceAtLeast(0),
                    updatedAt = System.currentTimeMillis()
                )
            )
        }

    // CONVERSION

    private fun Product.toEntity(userId: String) = ProductEntity(
        id = id,
        name = name,
        category = category,
        quantity = quantity,
        unitPrice = unitPrice,
        reorderLevel = reorderLevel,
        updatedAt = updatedAt,
        userId = userId
    )

    private fun ProductEntity.toDomainModel() = Product(
        id = id,
        name = name,
        category = category,
        quantity = quantity,
        unitPrice = unitPrice,
        reorderLevel = reorderLevel,
        updatedAt = updatedAt
    )

    // SAMPLE PRODUCTS

    private fun sampleProducts(): List<Product> = listOf(
        Product(
            name = "Maize Flour (Unga) 2kg",
            category = "Groceries",
            quantity = 40,
            unitPrice = 150.0,
            reorderLevel = 10
        ),
        Product(
            name = "Sugar 1kg",
            category = "Groceries",
            quantity = 25,
            unitPrice = 180.0,
            reorderLevel = 10
        ),
        Product(
            name = "Cooking Oil 1L",
            category = "Groceries",
            quantity = 12,
            unitPrice = 380.0,
            reorderLevel = 5
        ),
        Product(
            name = "Fresh Milk 500ml",
            category = "Dairy",
            quantity = 4,
            unitPrice = 65.0,
            reorderLevel = 10
        ),
        Product(
            name = "Bread 400g",
            category = "Bakery",
            quantity = 18,
            unitPrice = 65.0,
            reorderLevel = 8
        ),
        Product(
            name = "Bar Soap",
            category = "Household",
            quantity = 0,
            unitPrice = 60.0,
            reorderLevel = 6
        )
    )

    companion object {

        @Volatile
        private var instance: InventoryRepository? = null

        fun getInstance(context: Context): InventoryRepository =
            instance ?: synchronized(this) {
                instance ?: InventoryRepository(
                    context.applicationContext
                ).also { instance = it }
            }
    }
}

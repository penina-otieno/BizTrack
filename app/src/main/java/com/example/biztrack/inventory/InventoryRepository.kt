package com.example.biztrack.inventory

import android.content.Context
import com.example.biztrack.data.local.BizTrackDatabase
import com.example.biztrack.data.local.entity.ProductEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

/**
 * INVENTORY MODULE - James Gachago
 *
 * Stores products on the device (SharedPreferences, saved as JSON) so stock
 * is still there after the app is closed and reopened.
 *
 * NOTE FOR DAVID (Database): when the Room database is ready, replace the
 * body of these functions with ProductDao calls. The public functions below
 * are the only thing the rest of the inventory module talks to, so nothing
 * else needs to change.
 */

 /**
 * above comment from james has been noted and Dao calls have been implemented
  * but JSON features from earlier have all been removed.-David
  */
class InventoryRepository private constructor(context: Context) {

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val productDao = BizTrackDatabase.getDatabase(context).productDao()

    init {
        // First launch only: seed sample products into Room so the screen isn't empty.
        if (!prefs.getBoolean(KEY_SEEDED, false)) {
            runBlocking(Dispatchers.IO) {
                productDao.insertAll(sampleProducts().map { it.toEntity() })
            }
            prefs.edit().putBoolean(KEY_SEEDED, true).apply()
        }
    }

    // ---------- READ ----------

    fun getAll(): List<Product> = runBlocking(Dispatchers.IO) {
        productDao.getAllProductsList().map { it.toDomainModel() }
    }

    fun getLowStockCount(): Int = getAll().count { it.isLowStock }

    fun getTotalStockValue(): Double = getAll().sumOf { it.stockValue }

    // ---------- CREATE / UPDATE / DELETE ----------

    fun add(product: Product) = runBlocking(Dispatchers.IO) {
        productDao.insertProduct(product.toEntity())
    }

    fun update(product: Product) = runBlocking(Dispatchers.IO) {
        productDao.updateProduct(product.copy(updatedAt = System.currentTimeMillis()).toEntity())
    }

    fun delete(id: String) = runBlocking(Dispatchers.IO) {
        productDao.deleteById(id)
    }

    /** Changes stock by [delta] (positive = stock in, negative = stock out). */
    fun adjustStock(id: String, delta: Int) = runBlocking(Dispatchers.IO) {
        val current = productDao.getProductById(id) ?: return@runBlocking
        productDao.updateProduct(
            current.copy(
                quantity = (current.quantity + delta).coerceAtLeast(0),
                updatedAt = System.currentTimeMillis()
            )
        )
    }

    private fun Product.toEntity() = ProductEntity(
        id = id,
        name = name,
        category = category,
        quantity = quantity,
        unitPrice = unitPrice,
        reorderLevel = reorderLevel,
        updatedAt = updatedAt
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

    private fun sampleProducts(): List<Product> = listOf(
        Product(name = "Maize Flour (Unga) 2kg", category = "Groceries", quantity = 40, unitPrice = 150.0, reorderLevel = 10),
        Product(name = "Sugar 1kg", category = "Groceries", quantity = 25, unitPrice = 180.0, reorderLevel = 10),
        Product(name = "Cooking Oil 1L", category = "Groceries", quantity = 12, unitPrice = 380.0, reorderLevel = 5),
        Product(name = "Fresh Milk 500ml", category = "Dairy", quantity = 4, unitPrice = 65.0, reorderLevel = 10),
        Product(name = "Bread 400g", category = "Bakery", quantity = 18, unitPrice = 65.0, reorderLevel = 8),
        Product(name = "Bar Soap", category = "Household", quantity = 0, unitPrice = 60.0, reorderLevel = 6)
    )

    companion object {
        private const val PREFS_NAME = "biztrack_inventory"
        private const val KEY_SEEDED = "seeded_room"

        @Volatile
        private var instance: InventoryRepository? = null

        fun getInstance(context: Context): InventoryRepository =
            instance ?: synchronized(this) {
                instance ?: InventoryRepository(context.applicationContext).also { instance = it }
            }
    }
}
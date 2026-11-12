package com.example.biztrack.inventory

import android.content.Context
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

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
class InventoryRepository private constructor(context: Context) {

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val products = mutableListOf<Product>()

    init {
        load()
        // First launch only: add a few sample products so the screen isn't empty.
        if (!prefs.getBoolean(KEY_SEEDED, false)) {
            products.addAll(sampleProducts())
            save()
            prefs.edit().putBoolean(KEY_SEEDED, true).apply()
        }
    }

    // ---------- READ ----------

    @Synchronized
    fun getAll(): List<Product> = products.toList()

    @Synchronized
    fun getLowStockCount(): Int = products.count { it.isLowStock }

    @Synchronized
    fun getTotalStockValue(): Double = products.sumOf { it.stockValue }

    // ---------- CREATE / UPDATE / DELETE ----------

    @Synchronized
    fun add(product: Product) {
        products.add(product)
        save()
    }

    @Synchronized
    fun update(product: Product) {
        val index = products.indexOfFirst { it.id == product.id }
        if (index != -1) {
            products[index] = product.copy(updatedAt = System.currentTimeMillis())
            save()
        }
    }

    @Synchronized
    fun delete(id: String) {
        if (products.removeAll { it.id == id }) save()
    }

    /** Changes stock by [delta] (positive = stock in, negative = stock out). */
    @Synchronized
    fun adjustStock(id: String, delta: Int) {
        val index = products.indexOfFirst { it.id == id }
        if (index != -1) {
            val current = products[index]
            products[index] = current.copy(
                quantity = (current.quantity + delta).coerceAtLeast(0),
                updatedAt = System.currentTimeMillis()
            )
            save()
        }
    }

    // ---------- STORAGE ----------

    private fun save() {
        val array = JSONArray()
        for (p in products) {
            array.put(
                JSONObject()
                    .put("id", p.id)
                    .put("name", p.name)
                    .put("category", p.category)
                    .put("quantity", p.quantity)
                    .put("unitPrice", p.unitPrice)
                    .put("reorderLevel", p.reorderLevel)
                    .put("updatedAt", p.updatedAt)
            )
        }
        prefs.edit().putString(KEY_PRODUCTS, array.toString()).apply()
    }

    private fun load() {
        products.clear()
        val json = prefs.getString(KEY_PRODUCTS, null) ?: return
        try {
            val array = JSONArray(json)
            for (i in 0 until array.length()) {
                val o = array.getJSONObject(i)
                products.add(
                    Product(
                        id = o.getString("id"),
                        name = o.getString("name"),
                        category = o.getString("category"),
                        quantity = o.getInt("quantity"),
                        unitPrice = o.getDouble("unitPrice"),
                        reorderLevel = o.getInt("reorderLevel"),
                        updatedAt = o.getLong("updatedAt")
                    )
                )
            }
        } catch (e: JSONException) {
            // Corrupt data: start clean rather than crash.
            products.clear()
        }
    }

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
        private const val KEY_PRODUCTS = "products_json"
        private const val KEY_SEEDED = "seeded"

        @Volatile
        private var instance: InventoryRepository? = null

        /** One shared copy for the whole app (the dashboard can use it too). */
        fun getInstance(context: Context): InventoryRepository =
            instance ?: synchronized(this) {
                instance ?: InventoryRepository(context.applicationContext).also { instance = it }
            }
    }
}
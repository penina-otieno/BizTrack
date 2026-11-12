package com.example.biztrack.inventory

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel

/**
 * INVENTORY MODULE - James Gachago
 *
 * Holds the inventory screen state and does all validation.
 * Functions that can fail return an error message (String) or null on success,
 * the same pattern used in TransactionViewModel.
 */
class InventoryViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = InventoryRepository.getInstance(application)

    var products by mutableStateOf(repository.getAll())
        private set

    var searchQuery by mutableStateOf("")

    var filter by mutableStateOf(StockFilter.ALL)

    // ---------- Derived values (recomputed automatically when state changes) ----------

    /** Products after applying the search box and the filter chips. */
    val visibleProducts: List<Product>
        get() {
            val query = searchQuery.trim()
            return products
                .filter { p ->
                    query.isEmpty() ||
                            p.name.contains(query, ignoreCase = true) ||
                            p.category.contains(query, ignoreCase = true)
                }
                .filter { p ->
                    when (filter) {
                        StockFilter.ALL -> true
                        StockFilter.LOW -> p.isLowStock
                        StockFilter.OUT -> p.isOutOfStock
                    }
                }
                .sortedBy { it.name.lowercase() }
        }

    val totalProducts: Int get() = products.size
    val lowStockCount: Int get() = products.count { it.isLowStock }
    val totalStockValue: Double get() = products.sumOf { it.stockValue }

    fun findProduct(id: String?): Product? =
        if (id == null) null else products.firstOrNull { it.id == id }

    // ---------- CREATE + UPDATE (with validation) ----------

    /**
     * Adds a new product (existingId == null) or edits an existing one.
     * Returns an error message, or null if saved successfully.
     */
    fun saveProduct(
        existingId: String?,
        name: String,
        category: String,
        quantityText: String,
        priceText: String,
        reorderText: String
    ): String? {
        val cleanName = name.trim()
        if (cleanName.isEmpty()) return "Product name is required"
        if (cleanName.length > Product.MAX_NAME_LENGTH) {
            return "Product name must be ${Product.MAX_NAME_LENGTH} characters or fewer"
        }
        if (products.any { it.id != existingId && it.name.equals(cleanName, ignoreCase = true) }) {
            return "A product named \"$cleanName\" already exists"
        }
        if (category.isBlank()) return "Please choose a category"

        val quantity = quantityText.trim().toIntOrNull()
        if (quantity == null || quantity < 0 || quantity > Product.MAX_QUANTITY) {
            return "Quantity must be a whole number from 0 to ${Product.MAX_QUANTITY}"
        }

        val price = priceText.trim().toDoubleOrNull()
        if (price == null || price.isNaN() || price.isInfinite() || price <= 0.0 || price > Product.MAX_PRICE) {
            return "Price must be a valid amount in KES greater than 0"
        }

        val reorder = reorderText.trim().toIntOrNull()
        if (reorder == null || reorder < 0 || reorder > Product.MAX_QUANTITY) {
            return "Low-stock level must be a whole number (0 or more)"
        }

        if (existingId == null) {
            repository.add(
                Product(
                    name = cleanName,
                    category = category,
                    quantity = quantity,
                    unitPrice = price,
                    reorderLevel = reorder
                )
            )
        } else {
            val existing = findProduct(existingId) ?: return "That product no longer exists"
            repository.update(
                existing.copy(
                    name = cleanName,
                    category = category,
                    quantity = quantity,
                    unitPrice = price,
                    reorderLevel = reorder
                )
            )
        }
        refresh()
        return null
    }

    // ---------- STOCK UPDATE ----------

    /**
     * Adds stock ([adding] = true) or removes stock ([adding] = false).
     * Returns an error message, or null on success.
     */
    fun updateStock(productId: String, amountText: String, adding: Boolean): String? {
        val product = findProduct(productId) ?: return "That product no longer exists"

        val amount = amountText.trim().toIntOrNull()
        if (amount == null || amount <= 0) return "Enter a whole number greater than 0"
        if (amount > Product.MAX_QUANTITY) return "That quantity is too large"

        val newQuantity = if (adding) product.quantity.toLong() + amount else product.quantity.toLong() - amount
        if (newQuantity < 0) return "Not enough stock. Only ${product.quantity} in stock."
        if (newQuantity > Product.MAX_QUANTITY) return "Stock cannot exceed ${Product.MAX_QUANTITY} units"

        repository.adjustStock(productId, if (adding) amount else -amount)
        refresh()
        return null
    }

    // ---------- DELETE ----------

    fun deleteProduct(productId: String) {
        repository.delete(productId)
        refresh()
    }

    // ---------- Reload from storage ----------

    fun refresh() {
        products = repository.getAll()
    }
}
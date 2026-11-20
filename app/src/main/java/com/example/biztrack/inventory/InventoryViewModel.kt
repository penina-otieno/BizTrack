
package com.example.biztrack.inventory

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import java.util.UUID

class InventoryViewModel(application: Application) :
    AndroidViewModel(application) {

    private val repository =
        InventoryRepository.getInstance(application)

    // INVENTORY STATE

    var products by mutableStateOf<List<Product>>(emptyList())
        private set

    var searchQuery by mutableStateOf("")

    var filter by mutableStateOf(StockFilter.ALL)

    // FILTERED PRODUCTS

    val visibleProducts: List<Product>
        get() = products.filter { product ->

            val matchesSearch =
                product.name.contains(searchQuery, ignoreCase = true) ||
                        product.category.contains(searchQuery, ignoreCase = true)

            val matchesFilter = when (filter) {
                StockFilter.ALL -> true
                StockFilter.LOW -> product.isLowStock
                StockFilter.OUT -> product.isOutOfStock
            }

            matchesSearch && matchesFilter
        }

    // SUMMARY VALUES

    val totalProducts: Int
        get() = products.size

    val lowStockCount: Int
        get() = products.count { it.isLowStock }

    val totalStockValue: Double
        get() = products.sumOf { it.stockValue }

    init {
        loadProducts()
    }

    // LOAD PRODUCTS

    fun loadProducts() {
        products = repository.getAll()
    }

    // FIND PRODUCT FOR EDITING

    fun findProduct(id: String?): Product? {
        if (id == null) return null
        return products.find { it.id == id }
    }

    // ADD OR UPDATE PRODUCT

    fun saveProduct(
        existingId: String?,
        name: String,
        category: String,
        quantityText: String,
        priceText: String,
        reorderText: String
    ): String? {

        val cleanName = name.trim()

        if (cleanName.isBlank()) {
            return "Please enter a product name."
        }

        if (cleanName.length > Product.MAX_NAME_LENGTH) {
            return "Product name is too long."
        }

        if (category !in PRODUCT_CATEGORIES) {
            return "Please select a valid category."
        }

        val quantity = quantityText.toIntOrNull()
            ?: return "Enter a valid stock quantity."

        if (quantity !in 0..Product.MAX_QUANTITY) {
            return "Quantity must be between 0 and ${Product.MAX_QUANTITY}."
        }

        val price = priceText.toDoubleOrNull()
            ?: return "Enter a valid selling price."

        if (!price.isFinite() || price <= 0 ||
            price > Product.MAX_PRICE
        ) {
            return "Enter a price greater than zero and within the allowed limit."
        }

        val reorderLevel = reorderText.toIntOrNull()
            ?: return "Enter a valid reorder level."

        if (reorderLevel !in 0..Product.MAX_QUANTITY) {
            return "Enter a valid reorder level."
        }

        val product = Product(
            id = existingId ?: UUID.randomUUID().toString(),
            name = cleanName,
            category = category,
            quantity = quantity,
            unitPrice = price,
            reorderLevel = reorderLevel,
            updatedAt = System.currentTimeMillis()
        )

        if (existingId == null) {
            repository.add(product)
        } else {
            repository.update(product)
        }

        loadProducts()
        return null
    }

    // UPDATE STOCK

    fun updateStock(
        id: String,
        amountText: String,
        adding: Boolean
    ): String? {

        val amount = amountText.toIntOrNull()
            ?: return "Enter a valid quantity."

        if (amount <= 0) {
            return "Quantity must be greater than zero."
        }

        val product = findProduct(id)
            ?: return "Product could not be found."

        val newQuantity = if (adding) {
            product.quantity.toLong() + amount.toLong()
        } else {
            product.quantity.toLong() - amount.toLong()
        }

        if (newQuantity < 0) {
            return "You cannot remove more stock than is available."
        }

        if (newQuantity > Product.MAX_QUANTITY) {
            return "Stock quantity exceeds the allowed limit."
        }

        val difference = if (adding) amount else -amount

        repository.adjustStock(id, difference)

        loadProducts()
        return null
    }

    // DELETE PRODUCT

    fun deleteProduct(id: String) {
        repository.delete(id)
        loadProducts()
    }
}

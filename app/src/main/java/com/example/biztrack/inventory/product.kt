package com.example.biztrack.inventory

import java.util.Locale
import java.util.UUID

/**
 * INVENTORY MODULE - James Gachago
 *
 * A single product in the shop's stock.
 * All prices are in Kenyan Shillings (KES).
 */
data class Product(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val category: String,
    val quantity: Int,          // units currently in stock
    val unitPrice: Double,      // selling price per unit, in KES
    val reorderLevel: Int = DEFAULT_REORDER_LEVEL, // at or below this = low stock
    val updatedAt: Long = System.currentTimeMillis()
) {
    val isOutOfStock: Boolean get() = quantity <= 0

    /** True when stock is at or below the reorder level (includes out-of-stock). */
    val isLowStock: Boolean get() = quantity <= reorderLevel

    /** Value of the stock on hand, in KES. */
    val stockValue: Double get() = quantity * unitPrice

    companion object {
        const val DEFAULT_REORDER_LEVEL = 5
        const val MAX_QUANTITY = 1_000_000
        const val MAX_PRICE = 10_000_000.0
        const val MAX_NAME_LENGTH = 60
    }
}

enum class StockFilter(val label: String) {
    ALL("All"),
    LOW("Low stock"),
    OUT("Out of stock")
}

val PRODUCT_CATEGORIES = listOf(
    "Groceries",
    "Beverages",
    "Dairy",
    "Bakery",
    "Household",
    "Personal Care",
    "Stationery",
    "Other"
)

/** Formats an amount as Kenyan Shillings, e.g. KES 1,250.00 */
fun formatKes(amount: Double): String =
    String.format(Locale.US, "KES %,.2f", amount)
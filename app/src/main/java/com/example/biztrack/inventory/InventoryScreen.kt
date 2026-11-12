package com.example.biztrack.inventory

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * INVENTORY MODULE - James Gachago
 *
 * Main inventory screen: summary, low-stock warning, search, filters,
 * product list, stock update and delete.
 */

// BizTrack colours (same values as res/values/colors.xml)
internal val Navy = Color(0xFF17324D)
internal val BrandGreen = Color(0xFF21966B)
internal val WarningOrange = Color(0xFFE99A32)
internal val LossRed = Color(0xFFD64545)
internal val BackgroundLight = Color(0xFFF5F7FA)
internal val TextPrimary = Color(0xFF243447)
internal val TextSecondary = Color(0xFF718096)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryScreen(
    viewModel: InventoryViewModel,
    onBack: () -> Unit,
    onAddClick: () -> Unit,
    onEditClick: (Product) -> Unit
) {
    val context = LocalContext.current
    var productToDelete by remember { mutableStateOf<Product?>(null) }
    var productToUpdate by remember { mutableStateOf<Product?>(null) }

    val visibleProducts = viewModel.visibleProducts

    Scaffold(
        containerColor = BackgroundLight,
        topBar = {
            TopAppBar(
                title = { Text("Inventory") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Navy,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onAddClick,
                containerColor = BrandGreen,
                contentColor = Color.White,
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Add product") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // ---- Summary tiles ----
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SummaryTile("Products", viewModel.totalProducts.toString(), Navy, Modifier.weight(1f))
                SummaryTile(
                    "Low stock",
                    viewModel.lowStockCount.toString(),
                    if (viewModel.lowStockCount > 0) WarningOrange else BrandGreen,
                    Modifier.weight(1f)
                )
                SummaryTile("Stock value", formatKes(viewModel.totalStockValue), BrandGreen, Modifier.weight(1.6f))
            }

            // ---- Low-stock warning banner ----
            if (viewModel.lowStockCount > 0 && viewModel.filter != StockFilter.LOW) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, top = 12.dp)
                        .background(WarningOrange.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                        .clickable { viewModel.filter = StockFilter.LOW }
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Warning, contentDescription = null, tint = WarningOrange)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "${viewModel.lowStockCount} product(s) running low. Tap to view.",
                        color = TextPrimary,
                        fontSize = 14.sp
                    )
                }
            }

            // ---- Search ----
            OutlinedTextField(
                value = viewModel.searchQuery,
                onValueChange = { viewModel.searchQuery = it },
                placeholder = { Text("Search by name or category") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (viewModel.searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.searchQuery = "" }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear search")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 12.dp)
            )

            // ---- Filter chips ----
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StockFilter.values().forEach { option ->
                    FilterChip(
                        selected = viewModel.filter == option,
                        onClick = { viewModel.filter = option },
                        label = { Text(option.label) }
                    )
                }
            }

            // ---- List ----
            when {
                viewModel.products.isEmpty() -> EmptyState(
                    "No products yet.\nTap \"Add product\" to record your first item.",
                    Modifier.weight(1f)
                )

                visibleProducts.isEmpty() -> EmptyState(
                    "No products match your search or filter.",
                    Modifier.weight(1f)
                )

                else -> LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 96.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(visibleProducts, key = { it.id }) { product ->
                        ProductCard(
                            product = product,
                            onUpdateStock = { productToUpdate = product },
                            onEdit = { onEditClick(product) },
                            onDelete = { productToDelete = product }
                        )
                    }
                }
            }
        }
    }

    // ---- Stock update dialog ----
    productToUpdate?.let { product ->
        StockUpdateDialog(
            product = product,
            onDismiss = { productToUpdate = null },
            onConfirm = { amountText, adding ->
                val error = viewModel.updateStock(product.id, amountText, adding)
                if (error == null) {
                    productToUpdate = null
                    Toast.makeText(context, "Stock updated", Toast.LENGTH_SHORT).show()
                }
                error
            }
        )
    }

    // ---- Delete confirmation dialog ----
    productToDelete?.let { product ->
        AlertDialog(
            onDismissRequest = { productToDelete = null },
            title = { Text("Delete product?") },
            text = { Text("\"${product.name}\" will be permanently removed from your inventory.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteProduct(product.id)
                    productToDelete = null
                    Toast.makeText(context, "Product deleted", Toast.LENGTH_SHORT).show()
                }) {
                    Text("Delete", color = LossRed)
                }
            },
            dismissButton = {
                TextButton(onClick = { productToDelete = null }) { Text("Cancel") }
            }
        )
    }
}

@Composable
private fun SummaryTile(label: String, value: String, valueColor: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(label, color = TextSecondary, fontSize = 12.sp, maxLines = 1)
            Spacer(Modifier.height(4.dp))
            Text(
                text = value,
                color = valueColor,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun EmptyState(message: String, modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        Text(
            text = message,
            color = TextSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(32.dp)
        )
    }
}

@Composable
private fun ProductCard(
    product: Product,
    onUpdateStock: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val statusColor = when {
        product.isOutOfStock -> LossRed
        product.isLowStock -> WarningOrange
        else -> BrandGreen
    }
    val statusText = when {
        product.isOutOfStock -> "Out of stock"
        product.isLowStock -> "Low stock"
        else -> "In stock"
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = product.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = TextPrimary,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "${product.category} • ${formatKes(product.unitPrice)} each",
                        color = TextSecondary,
                        fontSize = 13.sp
                    )
                }
                Spacer(Modifier.width(12.dp))
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = product.quantity.toString(),
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        color = statusColor
                    )
                    Text("in stock", color = TextSecondary, fontSize = 12.sp)
                }
            }

            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Surface(
                    color = statusColor.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(50)
                ) {
                    Text(
                        text = statusText,
                        color = statusColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
                Text(
                    text = "Value: ${formatKes(product.stockValue)}",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }

            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(onClick = onUpdateStock) {
                    Text("Update stock")
                }
                Spacer(Modifier.weight(1f))
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit ${product.name}", tint = Navy)
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete ${product.name}", tint = LossRed)
                }
            }
        }
    }
}

/**
 * Dialog for stock in (restock) / stock out (sold, damaged, expired).
 * [onConfirm] returns an error message to show, or null when it worked.
 */
@Composable
private fun StockUpdateDialog(
    product: Product,
    onDismiss: () -> Unit,
    onConfirm: (amountText: String, adding: Boolean) -> String?
) {
    var adding by remember { mutableStateOf(true) }
    var amountText by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    val amount = amountText.toIntOrNull()
    val preview: Int? = if (amount == null) null else if (adding) product.quantity + amount else product.quantity - amount

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Update stock") },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                Text(product.name, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                Text("Current stock: ${product.quantity}", color = TextSecondary, fontSize = 13.sp)
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = adding,
                        onClick = { adding = true; error = null },
                        label = { Text("Add stock") }
                    )
                    FilterChip(
                        selected = !adding,
                        onClick = { adding = false; error = null },
                        label = { Text("Remove stock") }
                    )
                }
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { input ->
                        if (input.length <= 7 && input.all { it in '0'..'9' }) {
                            amountText = input
                            error = null
                        }
                    },
                    label = { Text("Quantity") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                if (preview != null && preview >= 0) {
                    Spacer(Modifier.height(8.dp))
                    Text("New stock will be: $preview", color = TextSecondary, fontSize = 13.sp)
                }
                error?.let {
                    Spacer(Modifier.height(8.dp))
                    Text(it, color = LossRed, fontSize = 13.sp)
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { error = onConfirm(amountText, adding) }) { Text("Save") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
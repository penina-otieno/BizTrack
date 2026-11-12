package com.example.biztrack.inventory

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale

/**
 * INVENTORY MODULE - James Gachago
 *
 * Product entry form. Used for both adding a new product (product == null)
 * and editing an existing one.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductFormScreen(
    viewModel: InventoryViewModel,
    product: Product?,
    onBack: () -> Unit,
    onSaved: () -> Unit
) {
    val context = LocalContext.current
    val isEditing = product != null

    var name by rememberSaveable(product?.id) { mutableStateOf(product?.name ?: "") }
    var category by rememberSaveable(product?.id) {
        mutableStateOf(product?.category ?: PRODUCT_CATEGORIES.first())
    }
    var quantity by rememberSaveable(product?.id) { mutableStateOf(product?.quantity?.toString() ?: "") }
    var price by rememberSaveable(product?.id) { mutableStateOf(product?.let { priceToText(it.unitPrice) } ?: "") }
    var reorderLevel by rememberSaveable(product?.id) {
        mutableStateOf((product?.reorderLevel ?: Product.DEFAULT_REORDER_LEVEL).toString())
    }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var categoryMenuOpen by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = BackgroundLight,
        topBar = {
            TopAppBar(
                title = { Text(if (isEditing) "Edit Product" else "Add Product") },
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
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { if (it.length <= Product.MAX_NAME_LENGTH) { name = it; errorMessage = null } },
                label = { Text("Product name") },
                placeholder = { Text("e.g., Maize Flour (Unga) 2kg") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            ExposedDropdownMenuBox(
                expanded = categoryMenuOpen,
                onExpandedChange = { categoryMenuOpen = !categoryMenuOpen }
            ) {
                OutlinedTextField(
                    value = category,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Category") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryMenuOpen) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = categoryMenuOpen,
                    onDismissRequest = { categoryMenuOpen = false }
                ) {
                    PRODUCT_CATEGORIES.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                category = option
                                categoryMenuOpen = false
                                errorMessage = null
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = quantity,
                onValueChange = {
                    if (it.length <= 7 && it.all { c -> c in '0'..'9' }) { quantity = it; errorMessage = null }
                },
                label = { Text("Quantity in stock") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = price,
                onValueChange = {
                    val validChars = it.all { c -> c in '0'..'9' || c == '.' }
                    if (it.length <= 10 && validChars && it.count { c -> c == '.' } <= 1) {
                        price = it
                        errorMessage = null
                    }
                },
                label = { Text("Selling price per unit") },
                prefix = { Text("KES ") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = reorderLevel,
                onValueChange = {
                    if (it.length <= 7 && it.all { c -> c in '0'..'9' }) { reorderLevel = it; errorMessage = null }
                },
                label = { Text("Low-stock level") },
                supportingText = { Text("You will be warned when stock falls to this number or below") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            errorMessage?.let {
                Spacer(Modifier.height(8.dp))
                Text(it, color = LossRed, fontSize = 14.sp)
            }

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = {
                    val error = viewModel.saveProduct(
                        existingId = product?.id,
                        name = name,
                        category = category,
                        quantityText = quantity,
                        priceText = price,
                        reorderText = reorderLevel
                    )
                    if (error == null) {
                        Toast.makeText(
                            context,
                            if (isEditing) "Product updated" else "Product added",
                            Toast.LENGTH_SHORT
                        ).show()
                        onSaved()
                    } else {
                        errorMessage = error
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Navy)
            ) {
                Text(if (isEditing) "Save Changes" else "Save Product")
            }
        }
    }
}

/** 150.0 -> "150", 150.5 -> "150.50" (for showing a price in the edit field). */
private fun priceToText(price: Double): String =
    if (price % 1.0 == 0.0) price.toLong().toString() else String.format(Locale.US, "%.2f", price)
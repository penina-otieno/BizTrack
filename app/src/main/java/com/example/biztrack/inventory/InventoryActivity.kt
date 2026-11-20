
package com.example.biztrack.inventory

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.biztrack.LoginActivity
import com.example.biztrack.SessionManager

/*
 * INVENTORY MODULE - James Gachago
 * Entry point of the inventory module.
 */
class InventoryActivity : ComponentActivity() {

    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sessionManager = SessionManager(applicationContext)

        // Prevent access without login.
        if (!sessionManager.isLoggedIn()) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        setContent {
            MaterialTheme(
                colorScheme = lightColorScheme(
                    primary = Navy,
                    onPrimary = Color.White,
                    secondary = BrandGreen,
                    onSecondary = Color.White,
                    background = BackgroundLight,
                    surface = Color.White
                )
            ) {
                InventoryApp(onExit = { finish() })
            }
        }
    }
}

/* Switches between the inventory list and the add/edit form. */
@Composable
fun InventoryApp(
    onExit: () -> Unit,
    viewModel: InventoryViewModel = viewModel()
) {
    var showForm by rememberSaveable {
        mutableStateOf(false)
    }

    var editingId by rememberSaveable {
        mutableStateOf<String?>(null)
    }

    BackHandler(enabled = showForm) {
        showForm = false
        editingId = null
    }

    if (showForm) {
        ProductFormScreen(
            viewModel = viewModel,
            product = viewModel.findProduct(editingId),
            onBack = {
                showForm = false
                editingId = null
            },
            onSaved = {
                showForm = false
                editingId = null
            }
        )
    } else {
        InventoryScreen(
            viewModel = viewModel,
            onBack = onExit,
            onAddClick = {
                editingId = null
                showForm = true
            },
            onEditClick = { product ->
                editingId = product.id
                showForm = true
            }
        )
    }
}

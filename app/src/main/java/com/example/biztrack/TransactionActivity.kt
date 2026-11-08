package com.example.biztrack

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import com.example.biztrack.ui.transactions.AddTransactionScreen
import com.example.biztrack.ui.transactions.TransactionHistoryScreen

class TransactionActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                var showAddScreen by remember { mutableStateOf(false) }

                if (showAddScreen) {
                    AddTransactionScreen(
                        onBack = { showAddScreen = false },
                        onSaveSuccess = { showAddScreen = false }
                    )
                } else {
                    TransactionHistoryScreen(
                        onAddClick = { showAddScreen = true }
                    )
                }
             }
        }
    }
}
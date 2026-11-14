
package com.example.biztrack

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.biztrack.ui.transactions.AddTransactionScreen
import com.example.biztrack.ui.transactions.TransactionHistoryScreen
import com.example.biztrack.ui.transactions.TransactionViewModel

class TransactionActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {

                val transactionViewModel: TransactionViewModel = viewModel()

                var showAddScreen by remember {
                    mutableStateOf(false)
                }

                if (showAddScreen) {
                    AddTransactionScreen(
                        onBack = {
                            showAddScreen = false
                        },
                        onSaveSuccess = {
                            showAddScreen = false
                        },
                        viewModel = transactionViewModel
                    )
                } else {
                    TransactionHistoryScreen(
                        onAddClick = {
                            showAddScreen = true
                        },
                        viewModel = transactionViewModel
                    )
                }
            }
        }
    }
}

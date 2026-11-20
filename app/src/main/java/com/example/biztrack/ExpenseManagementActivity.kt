
package com.example.biztrack

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.biztrack.ui.transactions.TransactionViewModel
import com.example.biztrack.ui.transactions.ExpenseManagementScreen

class ExpenseManagementActivity : ComponentActivity() {

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
            MaterialTheme {
                val transactionViewModel: TransactionViewModel = viewModel()

                ExpenseManagementScreen(
                    viewModel = transactionViewModel,
                    onBack = {
                        finish()
                    }
                )
            }
        }
    }
}

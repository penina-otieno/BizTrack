package com.example.biztrack.budget

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import java.util.Locale

@Composable
fun BudgetScreen(
    budgetViewModel: BudgetViewModel = viewModel()
) {
    val totalBudget by budgetViewModel.totalBudget
    val totalSpent by budgetViewModel.totalSpent

    val remainingBalance = budgetViewModel.remainingBalance
    val spendingProgress = budgetViewModel.spendingProgress

    LaunchedEffect(Unit) {
        budgetViewModel.refreshSpending()
    }

    var budgetInput by remember { mutableStateOf("") }
    var inputError by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Budget Management",
            style = MaterialTheme.typography.headlineMedium
        )

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    "Set Your Budget",
                    style = MaterialTheme.typography.titleLarge
                )

                OutlinedTextField(
                    value = budgetInput,
                    onValueChange = {
                        budgetInput = it
                        inputError = ""
                    },
                    label = { Text("Enter budget amount (KES)") },
                    modifier = Modifier.fillMaxWidth()
                )

                if (inputError.isNotEmpty()) {
                    Text(
                        text = inputError,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                Button(
                    onClick = {
                        val amount = budgetInput.toDoubleOrNull()

                        if (amount == null || amount <= 0) {
                            inputError = "Enter a valid amount greater than zero"
                        } else {
                            budgetViewModel.setBudget(amount)
                            budgetInput = ""
                            inputError = ""
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Save Budget")
                }
            }
        }

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    "Budget Overview",
                    style = MaterialTheme.typography.titleLarge
                )

                Text("Total Budget: ${formatKES(totalBudget)}")
                Text("Total Spent: ${formatKES(totalSpent)}")
                Text("Remaining Balance: ${formatKES(remainingBalance)}")
            }
        }

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    "Spending Progress",
                    style = MaterialTheme.typography.titleLarge
                )

                LinearProgressIndicator(
                    progress = { spendingProgress },
                    modifier = Modifier.fillMaxWidth()
                )

                Text("${(spendingProgress * 100).toInt()}% of budget spent")
            }
        }
    }
}

private fun formatKES(amount: Double): String {
    return String.format(Locale.getDefault(), "KES %,.2f", amount)
}

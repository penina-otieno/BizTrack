
package com.example.biztrack.ui.transactions

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.util.Locale

@Composable
fun ExpenseManagementScreen(
    viewModel: TransactionViewModel,
    onBack: () -> Unit
) {
    val allTransactions by viewModel.transactions.observeAsState(
        initial = emptyList()
    )

    val expenses = allTransactions.filter {
        it.type == "Expense"
    }

    val totalExpenses = expenses.sumOf {
        it.amount
    }

    var showAddForm by remember {
        mutableStateOf(false)
    }

    var description by remember {
        mutableStateOf("")
    }

    var amount by remember {
        mutableStateOf("")
    }

    var errorMessage by remember {
        mutableStateOf<String?>(null)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        TextButton(onClick = onBack) {
            Text("← Back")
        }

        Text(
            text = "Expenses Management",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text("Total Expenses")

                Text(
                    text = "KES ${
                        String.format(Locale.getDefault(), "%,.2f", totalExpenses)
                    }",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.error
                )

                Text("${expenses.size} expense transaction(s)")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                showAddForm = !showAddForm
                errorMessage = null
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                if (showAddForm) "Cancel" else "Add Expense"
            )
        }

        if (showAddForm) {

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = description,
                onValueChange = {
                    description = it
                    errorMessage = null
                },
                label = { Text("Expense Description") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = amount,
                onValueChange = {
                    amount = it
                    errorMessage = null
                },
                label = { Text("Amount (KES)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    errorMessage = viewModel.addTransaction(
                        type = "Expense",
                        title = description,
                        amountStr = amount
                    )

                    if (errorMessage == null) {
                        description = ""
                        amount = ""
                        showAddForm = false
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Expense")
            }

            errorMessage?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Expense History",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (expenses.isEmpty()) {
            Text("No expenses recorded yet.")
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(
                    items = expenses,
                    key = { it.id }
                ) { expense ->

                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = expense.title,
                                    style = MaterialTheme.typography.titleMedium
                                )

                                Text(
                                    text = expense.date,
                                    style = MaterialTheme.typography.bodySmall
                                )

                                Text(
                                    text = "KES ${
                                        String.format(
                                            Locale.getDefault(),
                                            "%,.2f",
                                            expense.amount
                                        )
                                    }",
                                    color = MaterialTheme.colorScheme.error
                                )
                            }

                            TextButton(
                                onClick = {
                                    viewModel.deleteTransaction(expense)
                                }
                            ) {
                                Text("Delete")
                            }
                        }
                    }
                }
            }
        }
    }
}

package com.example.biztrack.ui.transactions

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionScreen(
    onBack: () -> Unit,
    onSaveSuccess: () -> Unit,
    viewModel: TransactionViewModel = viewModel()
) {
    var selectedTab by remember { mutableIntStateOf(1) } // 0: Income, 1: Expense, 2: Transfer
    var title by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(modifier = Modifier.fillMaxSize().background(Color.White)) {
        // Header
        TopAppBar(
            title = { Text("Add Expense", color = Color.White) },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
            },
            actions = {
                IconButton(onClick = { }) {
                    Icon(Icons.Default.BookmarkBorder, contentDescription = "Bookmark", tint = Color.White)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF1E232C))
        )

        // Tabs
        TabRow(selectedTabIndex = selectedTab, containerColor = Color.White, contentColor = Color.Black) {
            Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text("Income") })
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("Expense", color = if (selectedTab == 1) Color.White else Color.Black) },
                modifier = if (selectedTab == 1) Modifier.background(Color(0xFFF84040)) else Modifier
            )
            Tab(selected = selectedTab == 2, onClick = { selectedTab = 2 }, text = { Text("Transfer") })
        }

        // Form
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Description", color = Color.Gray, fontSize = 12.sp)
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                placeholder = { Text("e.g., Unga, Sugar, or Daily Sales") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text("Amount", color = Color.Gray, fontSize = 12.sp)
            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it },
                placeholder = { Text("0.00") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            if (errorMessage != null) {
                Text(text = errorMessage!!, color = Color.Red, modifier = Modifier.padding(top = 8.dp))
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    val type = if (selectedTab == 0) "Income" else "Expense"
                    val error = viewModel.addTransaction(type, title, amount)
                    if (error == null) {
                        onSaveSuccess() // go back to history
                    } else {
                        errorMessage = error
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E232C))
            ) {
                Text("Save Transaction")
            }
        }
    }
}
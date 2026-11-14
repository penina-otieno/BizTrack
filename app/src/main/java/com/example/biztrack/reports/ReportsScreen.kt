
package com.example.biztrack.reports

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.biztrack.FinancialRepository
import com.example.biztrack.FinancialTransaction
import com.example.biztrack.TransactionType
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private data class PeriodSummary(
    val label: String,
    val sales: Double,
    val expenses: Double
) {
    val profit: Double
        get() = sales - expenses
}

@Composable
fun ReportsScreen() {

    val transactions = FinancialRepository.getAllTransactions()

    val totalSales = FinancialRepository.getTotalSales()
    val totalExpenses = FinancialRepository.getTotalExpenses()
    val profitLoss = totalSales - totalExpenses

    val dailySummaries = buildPeriodSummaries(
        transactions,
        "yyyy-MM-dd",
        "dd MMM yyyy"
    )

    val monthlySummaries = buildPeriodSummaries(
        transactions,
        "yyyy-MM",
        "MMMM yyyy"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        item {
            Text(
                text = "Financial Reports",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            ReportCard("Total Sales / Income", totalSales)
        }

        item {
            ReportCard("Total Expenses", totalExpenses)
        }

        item {
            ReportCard("Profit / Loss", profitLoss)
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Daily Summary",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        if (dailySummaries.isEmpty()) {
            item {
                Text("No daily transactions available.")
            }
        } else {
            items(dailySummaries) { summary ->
                PeriodSummaryCard(summary)
            }
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Monthly Summary",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        if (monthlySummaries.isEmpty()) {
            item {
                Text("No monthly transactions available.")
            }
        } else {
            items(monthlySummaries) { summary ->
                PeriodSummaryCard(summary)
            }
        }
    }
}

@Composable
private fun ReportCard(
    title: String,
    amount: Double
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = formatKES(amount),
                style = MaterialTheme.typography.headlineSmall
            )
        }
    }
}

@Composable
private fun PeriodSummaryCard(summary: PeriodSummary) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = summary.label,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Text("Sales: ${formatKES(summary.sales)}")
            Text("Expenses: ${formatKES(summary.expenses)}")
            Text("Profit / Loss: ${formatKES(summary.profit)}")
        }
    }
}

private fun buildPeriodSummaries(
    transactions: List<FinancialTransaction>,
    groupingPattern: String,
    displayPattern: String
): List<PeriodSummary> {

    val groupingFormat = SimpleDateFormat(
        groupingPattern,
        Locale.getDefault()
    )

    val displayFormat = SimpleDateFormat(
        displayPattern,
        Locale.getDefault()
    )

    return transactions
        .groupBy {
            groupingFormat.format(Date(it.date))
        }
        .toSortedMap(compareByDescending { it })
        .map { (period, periodTransactions) ->

            val sales = periodTransactions
                .filter { it.type == TransactionType.SALE }
                .sumOf { it.amount }

            val expenses = periodTransactions
                .filter { it.type == TransactionType.EXPENSE }
                .sumOf { it.amount }

            PeriodSummary(
                label = displayFormat.format(
                    groupingFormat.parse(period)!!
                ),
                sales = sales,
                expenses = expenses
            )
        }
}

private fun formatKES(amount: Double): String {
    return String.format(
        Locale.getDefault(),
        "KES %,.2f",
        amount
    )
}

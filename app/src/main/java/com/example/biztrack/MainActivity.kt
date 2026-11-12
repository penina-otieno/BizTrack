
package com.example.biztrack

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.example.biztrack.inventory.InventoryActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )
            insets
        }

        // Sales navigation
        findViewById<View>(R.id.cardSales).setOnClickListener {
            openSales()
        }

        // Expenses navigation
        findViewById<View>(R.id.cardExpenses).setOnClickListener {
            openModule("Expenses")
        }

        // Inventory navigation
        findViewById<View>(R.id.cardInventory).setOnClickListener {

            startActivity(Intent(this, InventoryActivity::class.java))
        }

        // Budget navigation
        findViewById<View>(R.id.cardBudget).setOnClickListener {
            openModule("Budget")
        }

        // Reports navigation
        findViewById<View>(R.id.cardReports).setOnClickListener {
            openModule("Reports")
        }
    }

    private fun openSales() {
        val intent = Intent(this, SalesActivity::class.java)
        startActivity(intent)
    }

    private fun openModule(moduleName: String) {
        val intent = Intent(this, ModuleActivity::class.java)
        intent.putExtra("MODULE_NAME", moduleName)
        startActivity(intent)
    }

    // Update dashboard financial figures
    private fun updateDashboard() {

        val totalSales = FinancialRepository.getTotalSales()
        val totalExpenses = FinancialRepository.getTotalExpenses()
        val profitLoss = FinancialRepository.getProfitLoss()

        findViewById<TextView>(R.id.tvTotalSales).text =
            String.format(Locale.getDefault(), "KES %,.2f", totalSales)

        findViewById<TextView>(R.id.tvTotalExpenses).text =
            String.format(Locale.getDefault(), "KES %,.2f", totalExpenses)

        val profitText = findViewById<TextView>(R.id.tvProfitLoss)

        profitText.text =
            String.format(Locale.getDefault(), "KES %,.2f", profitLoss)

        if (profitLoss >= 0) {
            profitText.setTextColor(getColor(R.color.profit_green))
        } else {
            profitText.setTextColor(getColor(R.color.loss_red))
        }

        // Update recent activity
        updateRecentActivity()
    }

    private fun updateRecentActivity() {

        val recentTransactions = FinancialRepository
            .getAllTransactions()
            .sortedByDescending { it.date }
            .take(5)

        val activityText = findViewById<TextView>(R.id.tvRecentActivity)

        if (recentTransactions.isEmpty()) {
            activityText.text = "No recent transactions yet."
            return
        }

        val dateFormat = SimpleDateFormat(
            "dd MMM yyyy",
            Locale.getDefault()
        )

        val result = StringBuilder()

        for (transaction in recentTransactions) {

            val type = if (transaction.type == TransactionType.SALE) {
                "SALE"
            } else {
                "EXPENSE"
            }

            val amount = String.format(
                Locale.getDefault(),
                "KES %,.2f",
                transaction.amount
            )

            val date = dateFormat.format(Date(transaction.date))

            result.append("$type: ${transaction.description}\n")
            result.append("$amount  |  $date\n\n")
        }

        activityText.text = result.toString().trim()
    }

    // Refresh dashboard whenever user returns to it
    override fun onResume() {
        super.onResume()
        updateDashboard()
    }
}

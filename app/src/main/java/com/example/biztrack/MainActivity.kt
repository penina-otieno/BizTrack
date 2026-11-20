
package com.example.biztrack

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.biztrack.inventory.InventoryActivity
import com.example.biztrack.budget.BudgetActivity
import com.example.biztrack.reports.ReportsActivity
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var sessionManager: SessionManager
    private lateinit var authRepository: AuthRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sessionManager = SessionManager(applicationContext)
        authRepository = AuthRepository(applicationContext)

        if (!sessionManager.isLoggedIn()) {
            redirectToLogin()
            return
        }

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

        // Load the logged-in user's profile.
        loadUserProfile()

        // Logout
        findViewById<View>(R.id.btnLogout).setOnClickListener {
            logout()
        }

        // Dashboard navigation
        findViewById<View>(R.id.cardSales).setOnClickListener {
            openSales()
        }

        findViewById<View>(R.id.cardExpenses).setOnClickListener {
            startActivity(Intent(this, ExpenseManagementActivity::class.java))
        }

        findViewById<View>(R.id.cardInventory).setOnClickListener {
            startActivity(Intent(this, InventoryActivity::class.java))
        }

        findViewById<View>(R.id.cardBudget).setOnClickListener {
            startActivity(Intent(this, BudgetActivity::class.java))
        }

        findViewById<View>(R.id.cardReports).setOnClickListener {
            startActivity(Intent(this, ReportsActivity::class.java))
        }
    }

    private fun loadUserProfile() {
        lifecycleScope.launch {
            val user = authRepository.getCurrentUser()

            if (user != null) {
                findViewById<TextView>(R.id.tvUserName).text =
                    "Welcome, ${user.fullName}!"

                findViewById<TextView>(R.id.tvBusinessName).text =
                    user.businessName
            } else {
                logout()
            }
        }
    }

    private fun logout() {
        sessionManager.clearSession()

        val intent = Intent(this, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        startActivity(intent)
        finish()
    }

    private fun redirectToLogin() {
        val intent = Intent(this, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        startActivity(intent)
        finish()
    }

    private fun openSales() {
        startActivity(Intent(this, SalesActivity::class.java))
    }

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

        val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        val result = StringBuilder()

        for (transaction in recentTransactions) {
            val type = when (transaction.type) {
                TransactionType.SALE -> "SALE"
                TransactionType.EXPENSE -> "EXPENSE"
                TransactionType.TRANSFER -> "TRANSFER"
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

    override fun onResume() {
        super.onResume()

        if (!::sessionManager.isInitialized) return

        if (!sessionManager.isLoggedIn()) {
            redirectToLogin()
            return
        }

        updateDashboard()
    }
}

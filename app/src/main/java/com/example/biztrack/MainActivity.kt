
package com.example.biztrack

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

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

        // Dashboard navigation
        findViewById<View>(R.id.cardSales).setOnClickListener {
            showComingSoon("Sales")
        }

        findViewById<View>(R.id.cardExpenses).setOnClickListener {
            showComingSoon("Expenses")
        }

        findViewById<View>(R.id.cardInventory).setOnClickListener {
            showComingSoon("Inventory")
        }

        findViewById<View>(R.id.cardBudget).setOnClickListener {
            showComingSoon("Budget")
        }

        findViewById<View>(R.id.cardReports).setOnClickListener {
            showComingSoon("Reports")
        }
    }

    private fun showComingSoon(section: String) {
        Toast.makeText(
            this,
            "$section module will be available soon",
            Toast.LENGTH_SHORT
        ).show()
    }
}

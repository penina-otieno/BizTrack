package com.example.biztrack

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class SalesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sales)

        findViewById<Button>(R.id.btnBack).setOnClickListener {
            finish()
        }

        // === ADDED BY MICHELLE (MM) ===
        // Opens the Compose Transaction History & Add Screen
        findViewById<Button>(R.id.btnOpenTransactions).setOnClickListener {
            val intent = Intent(this, TransactionActivity::class.java)
            startActivity(intent)
        }
    }
}
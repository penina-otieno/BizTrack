
package com.example.biztrack

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class SalesActivity : AppCompatActivity() {

    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sessionManager = SessionManager(applicationContext)

        // Prevent access without login.
        if (!sessionManager.isLoggedIn()) {
            redirectToLogin()
            return
        }

        setContentView(R.layout.activity_sales)

        findViewById<Button>(R.id.btnBack).setOnClickListener {
            finish()
        }

        // Opens transaction history and add screen.
        findViewById<Button>(R.id.btnOpenTransactions).setOnClickListener {
            val intent = Intent(this, TransactionActivity::class.java)
            startActivity(intent)
        }
    }

    private fun redirectToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}

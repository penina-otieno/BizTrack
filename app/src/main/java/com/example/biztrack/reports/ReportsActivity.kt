
package com.example.biztrack.reports

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import com.example.biztrack.LoginActivity
import com.example.biztrack.SessionManager

class ReportsActivity : ComponentActivity() {

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
                ReportsScreen()
            }
        }
    }
}

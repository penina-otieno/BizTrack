
package com.example.biztrack

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ModuleActivity : AppCompatActivity() {

    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sessionManager = SessionManager(applicationContext)

        if (!sessionManager.isLoggedIn()) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        setContentView(R.layout.activity_module)

        val moduleName = intent.getStringExtra("MODULE_NAME") ?: "Module"

        findViewById<TextView>(R.id.tvModuleTitle).text =
            "$moduleName Management"

        findViewById<TextView>(R.id.tvModuleDescription).text =
            "The $moduleName module will be connected to its full functionality."

        findViewById<Button>(R.id.btnBack).setOnClickListener {
            finish()
        }
    }
}

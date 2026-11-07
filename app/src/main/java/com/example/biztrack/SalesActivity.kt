
package com.example.biztrack

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
    }
}

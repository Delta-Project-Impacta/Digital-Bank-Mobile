package com.domleondev.deltabank

import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        val backArrow = findViewById<ImageView>(R.id.back_arrow)

        backArrow.setOnClickListener {
            finish()
        }
    }
}

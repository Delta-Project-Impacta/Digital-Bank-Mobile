package com.domleondev.deltabank

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class PasswordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_password)

        val passwordButtonNext = findViewById<AppCompatButton>(R.id.password_Button_Next)
        val passwordButtonBack = findViewById<ImageView>(R.id.password_Button_Back)

        passwordButtonNext.setOnClickListener {
            intent = Intent(this, AccountPasswordActivity::class.java)
            startActivity(intent)
        }

passwordButtonBack.setOnClickListener {
    finish()
}

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}
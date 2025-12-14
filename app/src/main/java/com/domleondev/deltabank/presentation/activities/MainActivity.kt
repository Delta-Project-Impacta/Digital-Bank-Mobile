package com.domleondev.deltabank.presentation.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.domleondev.deltabank.R
import com.domleondev.deltabank.presentation.util.setupTransparentStatusBarNoPadding

class MainActivity : AppCompatActivity() {

    private lateinit var buttonRegister: AppCompatButton
    private lateinit var buttonLogin: AppCompatButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        setupTransparentStatusBarNoPadding(
            rootViewId = R.id.main,
            darkIcons = true
        )

        buttonRegister = findViewById(R.id.btn_Register)
        buttonLogin = findViewById(R.id.btn_login)

        buttonRegister.setOnClickListener {


            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        buttonLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}
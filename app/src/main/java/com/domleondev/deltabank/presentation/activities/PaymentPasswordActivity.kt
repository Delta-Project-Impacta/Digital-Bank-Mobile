package com.domleondev.deltabank.presentation.activities

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.domleondev.deltabank.R

class PaymentPasswordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_payment_password)

        val backArrow = findViewById<ImageView>(R.id.payment_Password_Arrow_Back)
        backArrow.setOnClickListener {
            finish()
        }

        val paymentConfirmationButtonNext = findViewById<AppCompatButton>(R.id.payment_Password_Button_Next)
        paymentConfirmationButtonNext.setOnClickListener {
            val intent = Intent(this, PaymentConfirmationActivity::class.java)
            startActivity(intent)
        }
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.payment_Password_Container)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}
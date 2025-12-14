package com.domleondev.deltabank.presentation.activities

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.domleondev.deltabank.R
import com.domleondev.deltabank.presentation.util.setupTransparentStatusBarNoPadding

class PaymentConfirmationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_payment_confirmation)

        setupTransparentStatusBarNoPadding(
            rootViewId = R.id.payment_Confirmation_Container,
            darkIcons = true
        )

        val amount = intent.getStringExtra("EXTRA_AMOUNT") ?: ""
        val recipientName = intent.getStringExtra("EXTRA_RECIPIENT_NAME") ?: ""
        val institutionName = intent.getStringExtra("EXTRA_INSTITUTION_NAME") ?: ""
        val paymentType = intent.getStringExtra("EXTRA_PAYMENT_TYPE") ?: ""

        findViewById<TextView>(R.id.payment_Confirmation_Amount_View).text = amount
        findViewById<TextView>(R.id.payment_Confirmation_For_Name_Value).text = recipientName
        findViewById<TextView>(R.id.payment_Confirmation_Institution_Name_Value).text = institutionName

        val nextButton = findViewById<AppCompatButton>(R.id.payment_Confirmation_Button_Next)
        nextButton.setOnClickListener {
            val intent = Intent(this, PaymentSuccessActivity::class.java).apply {
                putExtra("EXTRA_AMOUNT", amount)
                putExtra("EXTRA_RECIPIENT_NAME", recipientName)
                putExtra("EXTRA_INSTITUTION_NAME", institutionName)
                putExtra("EXTRA_PAYMENT_TYPE", paymentType)
            }
            startActivity(intent)
        }

        findViewById<ImageView>(R.id.payment_Confirmation_Close).setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}

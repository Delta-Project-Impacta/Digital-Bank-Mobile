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

    private lateinit var amount: String
    private lateinit var recipientName: String
    private lateinit var institutionName: String
    private lateinit var paymentType: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_payment_password)

        amount = intent.getStringExtra("EXTRA_AMOUNT") ?: ""
        recipientName = intent.getStringExtra("EXTRA_RECIPIENT_NAME") ?: ""
        institutionName = intent.getStringExtra("EXTRA_INSTITUTION_NAME") ?: ""
        paymentType = intent.getStringExtra("EXTRA_PAYMENT_TYPE") ?: ""

        val backArrow = findViewById<ImageView>(R.id.payment_Password_Arrow_Back)
        backArrow.setOnClickListener { finish() }

        val nextButton = findViewById<AppCompatButton>(R.id.payment_Password_Button_Next)
        nextButton.setOnClickListener {

            val intent = Intent(this, PaymentConfirmationActivity::class.java).apply {
                putExtra("EXTRA_AMOUNT", amount)
                putExtra("EXTRA_RECIPIENT_NAME", recipientName)
                putExtra("EXTRA_INSTITUTION_NAME", institutionName)
                putExtra("EXTRA_PAYMENT_TYPE", paymentType)
            }

            startActivity(intent)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.payment_Password_Container)) { v, insets ->
            val sys = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(sys.left, sys.top, sys.right, sys.bottom)
            insets
        }
    }
}

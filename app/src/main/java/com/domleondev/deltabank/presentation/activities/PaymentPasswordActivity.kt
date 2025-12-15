package com.domleondev.deltabank.presentation.activities

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.domleondev.deltabank.R
import com.domleondev.deltabank.presentation.util.setupTransparentStatusBarNoPadding

class PaymentPasswordActivity : AppCompatActivity() {

    private lateinit var amount: String
    private lateinit var recipientName: String
    private lateinit var institutionName: String
    private lateinit var paymentType: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_payment_password)

        setupTransparentStatusBarNoPadding(
            rootViewId = R.id.payment_Password_Container,
            darkIcons = true
        )

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

        val hiddenInput = findViewById<EditText>(R.id.hidden_otp_input)
        val dot1 = findViewById<View>(R.id.pin_dot_1)
        val dot2 = findViewById<View>(R.id.pin_dot_2)
        val dot3 = findViewById<View>(R.id.pin_dot_3)
        val dot4 = findViewById<View>(R.id.pin_dot_4)
        val dots = listOf(dot1, dot2, dot3, dot4)

        hiddenInput.postDelayed({
            hiddenInput.requestFocus()
            val imm =
                getSystemService(INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
            imm.showSoftInput(
                hiddenInput,
                android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT
            )
        }, 200)

        val pinContainer = findViewById<View>(R.id.payment_Password_Dot_Input_Container)
        pinContainer.setOnClickListener {
            hiddenInput.requestFocus()
            val imm =
                getSystemService(INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
            imm.showSoftInput(
                hiddenInput,
                android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT
            )
        }

        hiddenInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val length = s?.length ?: 0

                for (i in dots.indices) {
                    dots[i].isSelected = (i < length)
                }

                if (length == 4) {
                    val intent = Intent(
                        this@PaymentPasswordActivity,
                        PaymentConfirmationActivity::class.java
                    ).apply {
                        putExtra("EXTRA_AMOUNT", amount)
                        putExtra("EXTRA_RECIPIENT_NAME", recipientName)
                        putExtra("EXTRA_INSTITUTION_NAME", institutionName)
                        putExtra("EXTRA_PAYMENT_TYPE", paymentType)
                    }
                    startActivity(intent)
                }
            }
        })
    }
}

package com.domleondev.deltabank.presentation.activities

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.domleondev.deltabank.R

class PixAmountActivity : AppCompatActivity() {

    private var isBalanceVisible = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pix_amount)

        val backArrow = findViewById<ImageView>(R.id.pix_Amount_Arrow_Back)
        backArrow.setOnClickListener {
            finish()
        }

        val pixReviewButtonNext = findViewById<AppCompatButton>(R.id.pix_Amount_Button_Next)
        pixReviewButtonNext.setOnClickListener {
            val intent = Intent(this, PixReviewActivity::class.java)
            startActivity(intent)
        }

        val balanceTextView = findViewById<TextView>(R.id.pix_Amount_Balance_Amount_Text)
        val toggleIcon = findViewById<ImageView>(R.id.pix_Amount_Balance_Toggle_Icon)

        val realBalance = getString(R.string.pix_amount_balance_amount)
        val maskedBalance = "R$ ●●●●●●"

        balanceTextView.text = realBalance

        toggleIcon.setOnClickListener {

            isBalanceVisible = !isBalanceVisible

            if (isBalanceVisible) {
                balanceTextView.text = realBalance
                toggleIcon.setImageResource(R.drawable.ic_eye)
            } else {
                balanceTextView.text = maskedBalance
                toggleIcon.setImageResource(R.drawable.ic_eye_off)
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.pix_Amount_Container)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}
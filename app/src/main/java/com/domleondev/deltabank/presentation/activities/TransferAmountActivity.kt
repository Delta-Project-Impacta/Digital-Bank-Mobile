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

private var isBalanceVisible = true
class TransferAmountActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_tranfer_amount)

        val backArrow = findViewById<ImageView>(R.id.transfer_Amount_Arrow_Back)
        backArrow.setOnClickListener {
            finish()
        }

        val transferReviewButtonNext = findViewById<AppCompatButton>(R.id.transfer_Amount_Button_Next)
        transferReviewButtonNext.setOnClickListener {
            val intent = Intent(this, TransferReviewActivity::class.java)
            startActivity(intent)
        }

        val balanceTextView = findViewById<TextView>(R.id.transfer_Amount_Balance_Amount_Text)
        val toggleIcon = findViewById<ImageView>(R.id.transfer_Amount_Balance_Toggle_Icon)

        val realBalance = getString(R.string.transfer_amount_balance_amount)
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
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}
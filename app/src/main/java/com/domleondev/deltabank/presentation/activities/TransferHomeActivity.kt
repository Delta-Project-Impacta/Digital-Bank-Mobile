package com.domleondev.deltabank.presentation.activities

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.domleondev.deltabank.R
import com.domleondev.deltatransfers.presentation.activities.TransfersDataActivity

class TransferHomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_transfer_home)

        val backArrow = findViewById<ImageView>(R.id.transfer_Home_Arrow_Back)
        backArrow.setOnClickListener {
            finish()
        }

        val transferHomePasswordButtonNext = findViewById<AppCompatButton>(R.id.transfer_Home_Button_Next)
        transferHomePasswordButtonNext.setOnClickListener {
            intent = Intent(this, PixAmountActivity::class.java)
            startActivity(intent)
        }

        val transferHomeLayoutAccount = findViewById<LinearLayout>(R.id.transfer_Home_Layout_Account)
        transferHomeLayoutAccount.setOnClickListener {
            intent = Intent(this, TransfersDataActivity::class.java)
            startActivity(intent)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.transfer_Home_Container)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}
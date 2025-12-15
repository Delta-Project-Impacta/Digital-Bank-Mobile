package com.domleondev.deltabank.presentation.activities

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.domleondev.deltabank.R
import com.domleondev.deltabank.presentation.util.setupTransparentStatusBarNoPadding

class TransferReviewActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_transfer_review)

        setupTransparentStatusBarNoPadding(
            rootViewId = R.id.transfer_Review_Container,
            darkIcons = true
        )

        val backArrow = findViewById<ImageView>(R.id.transfer_Review_Arrow_Back)
        backArrow.setOnClickListener {
            finish()
        }

        val pixPasswordButtonNext = findViewById<AppCompatButton>(R.id.transfer_Review_Button_Next)
        pixPasswordButtonNext.setOnClickListener {
            val intent = Intent(this, TransferPasswordActivity::class.java)
            startActivity(intent)
        }
    }
}
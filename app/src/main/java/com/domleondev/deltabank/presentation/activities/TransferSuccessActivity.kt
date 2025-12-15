package com.domleondev.deltabank.presentation.activities

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.domleondev.deltabank.R
import com.domleondev.deltabank.presentation.util.setupTransparentStatusBarNoPadding

class TransferSuccessActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_transfer_success)

        setupTransparentStatusBarNoPadding(
            rootViewId = R.id.transfer_Success_Container,
            darkIcons = true
        )

        val transferSuccessClose = findViewById<ImageView>(R.id.transfer_Success_Close)

        transferSuccessClose.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            startActivity(intent)
        }
    }
}
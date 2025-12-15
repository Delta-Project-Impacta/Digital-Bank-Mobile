package com.domleondev.deltabank.presentation.activities

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.domleondev.deltabank.R
import com.domleondev.deltabank.presentation.util.setupTransparentStatusBarNoPadding

class PixConfirmationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pix_confirmation)

        setupTransparentStatusBarNoPadding(
            rootViewId = R.id.pix_Confirmation_Container,
            darkIcons = true
        )

        val pixSuccessButtonNext = findViewById<AppCompatButton>(R.id.pix_Confirmation_Button_Next)
        pixSuccessButtonNext.setOnClickListener {
            // Tela desativada temporariamente
            return@setOnClickListener
        }
    }
}

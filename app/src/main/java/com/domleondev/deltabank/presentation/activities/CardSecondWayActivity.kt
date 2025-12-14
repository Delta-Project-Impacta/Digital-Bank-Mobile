package com.domleondev.deltabank.presentation.activities

import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.domleondev.deltabank.R
import com.domleondev.deltabank.presentation.util.setupTransparentStatusBarNoPadding

class CardSecondWayActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_card_second_way)

        setupTransparentStatusBarNoPadding(
            rootViewId = R.id.card_Second_Way_Container,
            darkIcons = true
        )

        val backArrow = findViewById<ImageView>(R.id.card_Second_Way_Arrow_Back)
        backArrow.setOnClickListener {
            finish()
        }
    }
}
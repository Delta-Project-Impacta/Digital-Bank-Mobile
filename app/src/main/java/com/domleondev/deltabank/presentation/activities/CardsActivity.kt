package com.domleondev.deltabank.presentation.activities

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.domleondev.deltabank.R
import com.domleondev.deltabank.presentation.util.setupTransparentStatusBarNoPadding

class CardsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_cards)

        setupTransparentStatusBarNoPadding(
            rootViewId = R.id.cards_Home_Container,
            darkIcons = true
        )

        val backArrow = findViewById<ImageView>(R.id.cards_Home_Arrow_Back)
        backArrow.setOnClickListener {
            finish()
        }


        val optionDisplayPassword = findViewById<androidx.constraintlayout.widget.ConstraintLayout>(R.id.option_Display_Password)
        optionDisplayPassword.setOnClickListener {
            val intent = Intent(this, CardDisplayPasswordActivity::class.java)
            startActivity(intent)
        }

        val optionChangePassword = findViewById<androidx.constraintlayout.widget.ConstraintLayout>(R.id.option_Change_Password)
        optionChangePassword.setOnClickListener {
            val intent = Intent(this, CardChangePasswordActivity::class.java)
            startActivity(intent)
        }

        val optionSecondWay = findViewById<androidx.constraintlayout.widget.ConstraintLayout>(R.id.option_Second_Way)
        optionSecondWay.setOnClickListener {
            val intent = Intent(this, CardSecondWayActivity::class.java)
            startActivity(intent)
        }
    }
}
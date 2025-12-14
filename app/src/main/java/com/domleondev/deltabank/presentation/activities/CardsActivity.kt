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

class CardsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_cards)

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

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.cards_Home_Container)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}
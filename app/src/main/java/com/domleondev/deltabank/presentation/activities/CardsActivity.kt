package com.domleondev.deltabank.presentation.activities

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.domleondev.deltabank.R


class CardsActivity : AppCompatActivity() {

    // Variável de controle para saber o estado atual do cartão
    private var isBlocked = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_cards)

// --- Mapeamento dos componentes de bloqueio ---
        val cardHomeCard = findViewById<LinearLayout>(R.id.card_Home_Card)
        val cardLabel = findViewById<TextView>(R.id.card_For_Label)
        val cardAction = findViewById<TextView>(R.id.card_For_Name_Value)

        // --- Lógica de Bloquear / Desbloquear ---
        cardAction.setOnClickListener {
            isBlocked = !isBlocked // Inverte o estado (se era falso vira verdadeiro, e vice-versa)

            if (isBlocked) {

                cardHomeCard.alpha = 0.4f

                // 2. Mudar textos
                cardLabel.text = getString(R.string.cards_home_card_locked_locked_01) // "Cartão Bloqueado"
                cardAction.text = getString(R.string.cards_home_card_locked_locked_02) // "Desbloquear"

                cardAction.setTextColor(ContextCompat.getColor(this, R.color.green))

            } else {

                cardHomeCard.alpha = 1.0f

                cardLabel.text = getString(R.string.cards_home_card_unlocked_locked_01)
                cardAction.text = getString(R.string.cards_home_card_unlocked_locked_02)

                cardAction.setTextColor(ContextCompat.getColor(this, R.color.red))
            }
        }



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
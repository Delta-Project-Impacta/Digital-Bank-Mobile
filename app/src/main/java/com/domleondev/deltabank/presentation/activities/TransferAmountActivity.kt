package com.domleondev.deltabank.presentation.activities

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
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

        //  Configuração universal de status bar transparente
        val window = window

// LÓGICA DE VERSÕES CORRIGIDA
        when {
            // Android 10 e anteriores (API < 30)
            Build.VERSION.SDK_INT < Build.VERSION_CODES.R -> {
                @Suppress("DEPRECATION")
                window.decorView.systemUiVisibility =
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION // <--- ESSA É A CHAVE!

                @Suppress("DEPRECATION")
                window.statusBarColor = Color.TRANSPARENT
                @Suppress("DEPRECATION")
                window.navigationBarColor = Color.TRANSPARENT // <--- Força a cor aqui
            }

            // Android 11+ (API >= 30)
            else -> {
                // Este comando diz: "Não ajuste o layout pelas barras, deixe passar por trás"
                WindowCompat.setDecorFitsSystemWindows(window, false)

                val controller = WindowInsetsControllerCompat(window, window.decorView)
                controller.isAppearanceLightStatusBars = true
                // controller.isAppearanceLightNavigationBars = true // Descomente se os ícones da navbar sumirem

                @Suppress("DEPRECATION")
                window.statusBarColor = Color.TRANSPARENT
                @Suppress("DEPRECATION")
                window.navigationBarColor = Color.TRANSPARENT // <--- Garante a transparência
            }
        }
        val headerContainer = findViewById<View>(R.id.transfer_Amount)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(bars.left, 0, bars.right, 0)
            headerContainer.setPadding(0, bars.top, 0, 0)
            insets
        }
    }
}
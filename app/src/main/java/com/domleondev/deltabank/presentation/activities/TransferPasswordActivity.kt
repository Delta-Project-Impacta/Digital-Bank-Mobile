package com.domleondev.deltabank.presentation.activities

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.domleondev.deltabank.R
import android.widget.EditText

import android.graphics.Color
import android.os.Build
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat

class TransferPasswordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_transfer_password)


        val transferPasswordArrowBack = findViewById<ImageView>(R.id.transfer_Password_Arrow_Back)
        val transferPasswordButtonNext = findViewById<AppCompatButton>(R.id.transfer_Password_Button_Next)

        transferPasswordArrowBack.setOnClickListener {
            finish()
        }
        transferPasswordButtonNext.setOnClickListener {
            intent = Intent(this, TransferConfirmationActivity::class.java)
            startActivity(intent)
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

            val hiddenInput = findViewById<EditText>(R.id.hidden_otp_input)
            val dot1 = findViewById<View>(R.id.pin_dot_1)
            val dot2 = findViewById<View>(R.id.pin_dot_2)
            val dot3 = findViewById<View>(R.id.pin_dot_3)
            val dot4 = findViewById<View>(R.id.pin_dot_4)
            val dots = listOf(dot1, dot2, dot3, dot4)

            // Força o teclado a abrir e focar no input invisível


        hiddenInput.postDelayed({
            hiddenInput.requestFocus()
            val imm = getSystemService(INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
            imm.showSoftInput(hiddenInput, android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT)
        }, 200)

        val pinContainer = findViewById<View>(R.id.pin_container)
        pinContainer.setOnClickListener {
            hiddenInput.requestFocus()
            val imm = getSystemService(INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
            imm.showSoftInput(hiddenInput, android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT)
        }

            // Listener simples
            hiddenInput.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable?) {
                    val length = s?.length ?: 0

                    // Loop para "acender" ou "apagar" as bolinhas
                    for (i in dots.indices) {
                        // Se o índice for menor que o tamanho do texto, a bolinha está preenchida
                        dots[i].isSelected = (i < length)
                    }

                    if (length == 4) {
                        // Senha completa! Pode habilitar o botão ou validar automaticamente
                        // binding.transferPasswordButtonNext.isEnabled = true
                    }
                }
            })
        val headerContainer = findViewById<View>(R.id.transfer_Password)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.transfer_password_root)) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(bars.left, 0, bars.right, 0)
            headerContainer.setPadding(0, bars.top, 0, 0)
            insets
        }
    }
}
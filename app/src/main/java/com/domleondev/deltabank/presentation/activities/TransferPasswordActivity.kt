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
import com.domleondev.deltabank.presentation.util.setupTransparentStatusBarNoPadding

class TransferPasswordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_transfer_password)

        setupTransparentStatusBarNoPadding(
            rootViewId = R.id.transfer_password_root,
            darkIcons = true
        )

        val transferPasswordArrowBack = findViewById<ImageView>(R.id.transfer_Password_Arrow_Back)
        val transferPasswordButtonNext = findViewById<AppCompatButton>(R.id.transfer_Password_Button_Next)

        transferPasswordArrowBack.setOnClickListener {
            finish()
        }
        transferPasswordButtonNext.setOnClickListener {
            intent = Intent(this, TransferConfirmationActivity::class.java)
            startActivity(intent)
        }

            val hiddenInput = findViewById<EditText>(R.id.hidden_otp_input)
            val dot1 = findViewById<View>(R.id.pin_dot_1)
            val dot2 = findViewById<View>(R.id.pin_dot_2)
            val dot3 = findViewById<View>(R.id.pin_dot_3)
            val dot4 = findViewById<View>(R.id.pin_dot_4)
            val dots = listOf(dot1, dot2, dot3, dot4)

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
    }
}
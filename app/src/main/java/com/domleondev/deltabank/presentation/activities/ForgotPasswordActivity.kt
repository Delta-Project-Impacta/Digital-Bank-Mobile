package com.domleondev.deltabank.presentation.activities

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.domleondev.deltabank.R
import com.domleondev.deltabank.viewModel.ForgotPasswordViewModel
import com.google.android.material.textfield.TextInputEditText

class ForgotPasswordActivity : AppCompatActivity() {

    private val viewModel: ForgotPasswordViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_forgot_password)

        val registerEditEmail = findViewById<TextInputEditText>(R.id.register_Edit_Email)
        val forgotPasswordButtonNext = findViewById<AppCompatButton>(R.id.forgot_Password_Button_Next)
        val backArrow = findViewById<ImageView>(R.id.forgot_Password_Arrow_Back)

        backArrow.setOnClickListener {
            finish()
        }

        // 1. CLIQUE DO BOTÃO (Apenas dispara a ação)
        forgotPasswordButtonNext.setOnClickListener {
            val email = registerEditEmail.text.toString().trim()
            viewModel.sendPasswordResetEmail(email)
        }

        // 2. OBSERVERS (Ficam fora do Listener para não duplicar)
        setupObservers(registerEditEmail, forgotPasswordButtonNext)

        // 3. CONFIGURAÇÃO DE LAYOUT (Insets e Transparência)
        setupLayoutTransparency()
    }

    private fun setupObservers(emailInput: TextInputEditText, button: AppCompatButton) {
        // Observer de Sucesso
        viewModel.resetResult.observe(this) { success ->
            if (success) {
                val intent = Intent(this, EmailVerificationActivity::class.java)
                intent.putExtra("email_enviado", emailInput.text.toString())
                startActivity(intent)
            }
        }

        // Observer de Erro (AQUI ESTÁ A SUA SOLICITAÇÃO)
        viewModel.errorMessage.observe(this) { msg ->
            val mensagemUsuario = when {
                msg.contains("no user record", ignoreCase = true) -> "E-mail não cadastrado em nossa base."
                msg.contains("badly formatted", ignoreCase = true) -> "Formato de e-mail inválido."
                else -> msg // Outros erros (ex: sem internet)
            }
            Toast.makeText(this, mensagemUsuario, Toast.LENGTH_LONG).show()
        }

        // Observer de Loading
        viewModel.isLoading.observe(this) { loading ->
            button.isEnabled = !loading
            button.alpha = if (loading) 0.5f else 1.0f
        }
    }

    private fun setupLayoutTransparency() {
        val window = window
        when {
            Build.VERSION.SDK_INT < Build.VERSION_CODES.R -> {
                @Suppress("DEPRECATION")
                window.decorView.systemUiVisibility =
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                @Suppress("DEPRECATION")
                window.statusBarColor = Color.TRANSPARENT
                @Suppress("DEPRECATION")
                window.navigationBarColor = Color.TRANSPARENT
            }
            else -> {
                WindowCompat.setDecorFitsSystemWindows(window, false)
                val controller = WindowInsetsControllerCompat(window, window.decorView)
                controller.isAppearanceLightStatusBars = true
                @Suppress("DEPRECATION")
                window.statusBarColor = Color.TRANSPARENT
                @Suppress("DEPRECATION")
                window.navigationBarColor = Color.TRANSPARENT
            }
        }

        val headerContainer = findViewById<View>(R.id.forgot_Password)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(bars.left, 0, bars.right, 0)
            headerContainer.setPadding(0, bars.top, 0, 0)
            insets
        }
    }
}
package com.domleondev.deltabank.presentation.activities

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.domleondev.deltabank.R
import com.google.firebase.auth.FirebaseAuth

class EmailVerificationActivity : AppCompatActivity() {

    private lateinit var timerTextView: TextView
    private lateinit var resendButton: TextView
    private var countdownTimer: CountDownTimer? = null
    private var userEmail: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_email_verification)

        // 1. Recebe o e-mail passado pela activity anterior
        userEmail = intent.getStringExtra("email_enviado")

        val backArrow = findViewById<ImageView>(R.id.email_Verification_Arrow_Back)
        val emailVerificationButtonNext = findViewById<AppCompatButton>(R.id.email_Verification_Button_Next)
        timerTextView = findViewById(R.id.forgot_Check_Timer)
        resendButton = findViewById(R.id.email_Verification_Text_Bottom_02)

        backArrow.setOnClickListener { finish() }

        // 2. MUDANÇA IMPORTANTE: O botão agora leva para o LOGIN
        // Como o reset é via link no e-mail, o fluxo termina aqui e volta para o login.
        emailVerificationButtonNext.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            // Limpa a pilha de atividades para o usuário não voltar para cá ao clicar em "voltar"
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        // 3. Lógica do Botão Reenviar
        resendButton.setOnClickListener {
            if (resendButton.isEnabled) {
                reenviarEmailFirebase()
            }
        }

        startOneMinuteTimer()

        // --- SEU CÓDIGO DE LAYOUT (INSETS E TRANSPARÊNCIA) ---
        setupLayoutTransparency()
    }

    private fun reenviarEmailFirebase() {
        if (userEmail.isNullOrBlank()) {
            Toast.makeText(this, "E-mail não encontrado para reenvio.", Toast.LENGTH_SHORT).show()
            return
        }

        // Desabilita visualmente para evitar cliques múltiplos
        resendButton.isEnabled = false
        resendButton.alpha = 0.5f

        FirebaseAuth.getInstance().sendPasswordResetEmail(userEmail!!)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Link reenviado com sucesso!", Toast.LENGTH_SHORT).show()
                    // Reinicia o timer apenas se deu certo
                    startOneMinuteTimer()
                } else {
                    val erro = task.exception?.message ?: "Erro ao reenviar."
                    Toast.makeText(this, erro, Toast.LENGTH_SHORT).show()

                    // Se deu erro, reabilita o botão imediatamente para tentar de novo
                    resendButton.isEnabled = true
                    resendButton.alpha = 1f
                }
            }
    }

    private fun startOneMinuteTimer() {
        timerTextView.visibility = View.VISIBLE
        timerTextView.text = "01:00"

        countdownTimer?.cancel()

        countdownTimer = object : CountDownTimer(60_000, 1_000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsLeft = (millisUntilFinished / 1000).toInt()
                val minutes = secondsLeft / 60
                val seconds = secondsLeft % 60
                timerTextView.text = String.format("%02d:%02d", minutes, seconds)

                if (secondsLeft <= 10) {
                    timerTextView.setTextColor(ContextCompat.getColor(this@EmailVerificationActivity, R.color.red))
                } else {
                    timerTextView.setTextColor(ContextCompat.getColor(this@EmailVerificationActivity, R.color.black))
                }
            }

            override fun onFinish() {
                timerTextView.text = "00:00"
                timerTextView.visibility = View.INVISIBLE

                resendButton.isEnabled = true
                resendButton.alpha = 1f
            }
        }.start()
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
        val headerContainer = findViewById<View>(R.id.email_Verification)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(bars.left, 0, bars.right, 0)
            headerContainer.setPadding(0, bars.top, 0, 0)
            insets
        }
    }

    override fun onDestroy() {
        countdownTimer?.cancel()
        countdownTimer = null
        super.onDestroy()
    }
}
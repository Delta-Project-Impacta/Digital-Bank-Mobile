package com.domleondev.deltabank.presentation.activities

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.domleondev.deltabank.R

class EmailVerificationActivity : AppCompatActivity() {

    private lateinit var timerTextView: TextView
    private lateinit var resendButton: TextView
    private var countdownTimer: CountDownTimer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_email_verification)

        val backArrow = findViewById<ImageView>(R.id.email_Verification_Arrow_Back)
        val emailVerificationButtonNext = findViewById<AppCompatButton>(R.id.email_Verification_Button_Next)
        timerTextView = findViewById(R.id.forgot_Check_Timer)
        resendButton = findViewById(R.id.email_Verification_Text_Bottom_02)

        backArrow.setOnClickListener { finish() }

        emailVerificationButtonNext.setOnClickListener {
            startActivity(Intent(this, ResetPasswordActivity::class.java))
        }

        resendButton.setOnClickListener {

            if (resendButton.isEnabled) {
                resendButton.isEnabled = false
                resendButton.alpha = 0.5f

                // Aqui colocar a chamada para reenviar o e-mail!!
                // resendVerificationCode()

                startOneMinuteTimer()
            }
        }

        startOneMinuteTimer()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun startOneMinuteTimer() {
        timerTextView.visibility = android.view.View.VISIBLE
        timerTextView.text = "01:00"

        countdownTimer?.cancel()

        countdownTimer = object : CountDownTimer(60_000, 1_000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsLeft = (millisUntilFinished / 1000).toInt()
                val minutes = secondsLeft / 60
                val seconds = secondsLeft % 60
                timerTextView.text = String.format("%02d:%02d", minutes, seconds)

                if (secondsLeft <= 10) {
                    timerTextView.setTextColor(resources.getColor(R.color.red, theme))
                } else {
                    timerTextView.setTextColor(resources.getColor(R.color.black, theme))
                }
            }

            override fun onFinish() {
                timerTextView.text = "00:00"
                timerTextView.visibility = android.view.View.INVISIBLE

                resendButton.isEnabled = true
                resendButton.alpha = 1f
            }
        }.start()
    }

    override fun onDestroy() {
        countdownTimer?.cancel()
        countdownTimer = null
        super.onDestroy()
    }
}
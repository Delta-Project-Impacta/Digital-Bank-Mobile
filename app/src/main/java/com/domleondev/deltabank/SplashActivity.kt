package com.domleondev.deltabank

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {

    private val delayBeforeStart = 500L
    private val animDuration = 900L
    private val extraDelay = 300L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val logo = findViewById<ImageView>(R.id.logo)
        val title = findViewById<TextView>(R.id.splash_text)

        logo.visibility = View.INVISIBLE
        title.visibility = View.INVISIBLE

        val logoAnim = AnimationUtils.loadAnimation(this, R.anim.splash_anim)
        val textAnim = AnimationUtils.loadAnimation(this, R.anim.splash_fade_anim)

        Handler(Looper.getMainLooper()).postDelayed({
            logo.visibility = View.VISIBLE
            title.visibility = View.VISIBLE

            logo.startAnimation(logoAnim)
            title.startAnimation(textAnim)
        }, delayBeforeStart)

        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, delayBeforeStart + animDuration + extraDelay)
    }
}

package com.domleondev.deltabank.presentation.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.domleondev.deltabank.R

class PaymentHomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_payment_home)

        val backArrow = findViewById<ImageView>(R.id.payment_Home_Arrow_Back)
        backArrow.setOnClickListener {
            finish()
        }

        setupCustomMenuLogic()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.payment_Home_Container)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupCustomMenuLogic() {

        val cameraIcon = findViewById<ImageView>(R.id.payment_Home_Icon_Camera)
        val menuOverlay = findViewById<FrameLayout>(R.id.menu_overlay)
        val menuContentContainer = findViewById<ConstraintLayout>(R.id.menu_content_container)

        val optionQrCode = menuContentContainer.findViewById<AppCompatTextView>(R.id.option_qr_code)
        val optionBarcode = menuContentContainer.findViewById<AppCompatTextView>(R.id.option_barcode)

        fun setMenuVisible(isVisible: Boolean) {
            val visibility = if (isVisible) View.VISIBLE else View.GONE
            menuOverlay.visibility = visibility
            menuContentContainer.visibility = visibility
        }

        cameraIcon.setOnClickListener {
            setMenuVisible(menuOverlay.visibility != View.VISIBLE)
        }

        menuOverlay.setOnClickListener {
            setMenuVisible(false)
        }

        optionQrCode.setOnClickListener {
            setMenuVisible(false)
            startActivity(Intent(this, QrCodeScannerActivity::class.java))
        }

        optionBarcode.setOnClickListener {
            setMenuVisible(false)
            startActivity(Intent(this, BarCodeScannerActivity::class.java))
        }
    }
}
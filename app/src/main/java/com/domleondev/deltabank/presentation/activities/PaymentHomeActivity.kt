package com.domleondev.deltabank.presentation.activities

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import com.domleondev.deltabank.R
import com.domleondev.deltabank.presentation.util.setupTransparentStatusBarNoPadding
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class PaymentHomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContentView(R.layout.activity_payment_home)

        setupTransparentStatusBarNoPadding(
            rootViewId = R.id.payment_Home_Container,
            darkIcons = true
        )

        val backArrow = findViewById<ImageView>(R.id.payment_Home_Arrow_Back)
        backArrow.setOnClickListener { finish() }

        setupCustomMenuLogic()

        val inputLayout = findViewById<TextInputLayout>(R.id.payment_Home_Input_Pay)
        val editText = findViewById<TextInputEditText>(R.id.payment_Home_Edit_Pay)

        editText.addTextChangedListener { editable ->
            val hasText = !editable.isNullOrBlank()
            val tintColor = if (hasText) {
                ContextCompat.getColor(this, R.color.orange)
            } else {
                ContextCompat.getColor(this, android.R.color.transparent)
            }
            inputLayout.setEndIconTintList(ColorStateList.valueOf(tintColor))
        }

        inputLayout.setEndIconOnClickListener {
            val text = editText.text?.toString()?.trim() ?: ""
            if (text.isBlank()) return@setEndIconOnClickListener
            handleCodeInput(text)
        }
    }

    private fun setupCustomMenuLogic() {

        val cameraIcon = findViewById<ImageView>(R.id.payment_Home_Icon_Camera)
        val menuOverlay = findViewById<FrameLayout>(R.id.menu_overlay)
        val menuContentContainer = findViewById<ConstraintLayout>(R.id.menu_content_container)

        val optionQrCode = menuContentContainer.findViewById<AppCompatTextView>(R.id.option_qr_code)
        val optionBarcode =
            menuContentContainer.findViewById<AppCompatTextView>(R.id.option_barcode)

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

    private fun handleCodeInput(input: String) {
        val clean = input.replace("[^0-9A-Za-z]".toRegex(), "")

        // PIX EMV começa com 000201 e tem mais de 30 chars
        if (clean.startsWith("000201") && clean.length > 30) {
            val intent = Intent(this, PaymentReviewActivity::class.java)
            intent.putExtra("QR_CODE_DATA", input)
            startActivity(intent)
            return
        }

        // Linha digitável 47 dígitos → boleto
        if (clean.length == 47) {
            val intent = Intent(this, PaymentReviewActivity::class.java)
            intent.putExtra("BARCODE_DATA", clean)
            startActivity(intent)
            return
        }

        // Código de barras 44 dígitos → boleto
        if (clean.length == 44) {
            val intent = Intent(this, PaymentReviewActivity::class.java)
            intent.putExtra("BARCODE_DATA", clean)
            startActivity(intent)
            return
        }

        showInvalidInput()
    }

    private fun showInvalidInput() {
        val dialog = androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Código inválido")
            .setMessage("Verifique o código inserido e tente novamente.")
            .setPositiveButton("OK", null)
            .create()

        dialog.show()
    }


}
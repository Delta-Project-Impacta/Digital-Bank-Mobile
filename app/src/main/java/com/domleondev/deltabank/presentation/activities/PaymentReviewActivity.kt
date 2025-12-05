package com.domleondev.deltabank.presentation.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.domleondev.deltabank.R

class PaymentReviewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_payment_review)

        val qrData = intent.getStringExtra("QR_CODE_DATA")

        if (qrData != null) {
            parsePixQrCode(qrData)
        }

        findViewById<ImageView>(R.id.payment_Review_Arrow_Back).setOnClickListener { finish() }

        findViewById<AppCompatButton>(R.id.payment_Review_Button_Next).setOnClickListener {
            startActivity(Intent(this, PaymentPasswordActivity::class.java))
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.payment_Review_Container)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
    private fun parsePixQrCode(payload: String) {
        try {
            val nomeBeneficiario = extractField(payload, "59")?.trim()
                ?: extractField(payload, "58")?.trim()
            val valorRaw = extractField(payload, "54")
            val codigoCompleto = payload.trim()

            val valorFormatado = if (valorRaw.isNullOrBlank()) {
                "R$ 0,00"
            } else {
                val apenasNumeros = valorRaw.replace("[^0-9]".toRegex(), "")
                val valorEmCentavos = apenasNumeros.toLongOrNull() ?: 0L
                val valorDouble = valorEmCentavos / 100.0
                "R$ ${"%.2f".format(valorDouble).replace(".", ",")}"
            }

            findViewById<TextView>(R.id.payment_Review_Description_02_View).text = valorFormatado
            findViewById<TextView>(R.id.payment_Review_Description_15_View).text = valorFormatado

            findViewById<TextView>(R.id.payment_Review_Description_04_View).text =
                nomeBeneficiario ?: "Não informado"

            findViewById<TextView>(R.id.payment_Review_Description_06_View).text = codigoCompleto

            val instituicao = when {
                nomeBeneficiario?.contains("nubank", true) == true -> "Nubank"
                nomeBeneficiario?.contains("inter", true) == true -> "Banco Inter"
                nomeBeneficiario?.contains("c6", true) == true -> "C6 Bank"
                nomeBeneficiario?.contains("itau", true) == true -> "Itaú"
                nomeBeneficiario?.contains("bradesco", true) == true -> "Bradesco"
                nomeBeneficiario?.contains("caixa", true) == true -> "Caixa"
                else -> "Pessoa física"
            }
            findViewById<TextView>(R.id.payment_Review_Description_07_View).text = instituicao

            findViewById<TextView>(R.id.payment_Review_Description_11_View).text = "PIX"

        } catch (e: Exception) {
            Log.e("PIX", "Erro ao parsear", e)
            findViewById<TextView>(R.id.payment_Review_Description_04_View).text = "QR Code inválido"
        }
    }

    private fun extractField(payload: String, tag: String): String? {
        val index = payload.indexOf(tag)
        if (index == -1) return null

        val lengthIndex = index + tag.length
        val lengthStr = payload.substring(lengthIndex, lengthIndex + 2)
        val length = lengthStr.toIntOrNull() ?: return null

        val valueStart = lengthIndex + 2
        val valueEnd = valueStart + length
        if (valueEnd > payload.length) return null

        return payload.substring(valueStart, valueEnd)
    }
}
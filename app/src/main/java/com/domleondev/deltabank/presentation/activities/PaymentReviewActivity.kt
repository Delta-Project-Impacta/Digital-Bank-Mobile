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
        val rawBarcodeData = intent.getStringExtra("BARCODE_DATA")

        when {
            qrData != null -> parsePixQrCode(qrData)

            rawBarcodeData != null -> {
                val clean = rawBarcodeData.replace("[^0-9]".toRegex(), "")
                val barcode44 =
                    if (clean.length == 47) convertLinhaDigitavelToBarcode(clean)
                    else clean

                parseBankSlipBarcode(barcode44)
            }

            else -> showInvalid()
        }

        findViewById<ImageView>(R.id.payment_Review_Arrow_Back).setOnClickListener { finish() }

        val btnNext = findViewById<AppCompatButton>(R.id.payment_Review_Button_Next)
        btnNext.setOnClickListener {

            val amount = findViewById<TextView>(R.id.payment_Review_Description_02_View).text.toString()
            val recipientName = findViewById<TextView>(R.id.payment_Review_Description_04_View).text.toString()
            val institutionName = findViewById<TextView>(R.id.payment_Review_Description_07_View).text.toString()
            val paymentType = findViewById<TextView>(R.id.payment_Review_Description_11_View).text.toString()

            val intent = Intent(this, PaymentPasswordActivity::class.java).apply {
                putExtra("EXTRA_AMOUNT", amount)
                putExtra("EXTRA_RECIPIENT_NAME", recipientName)
                putExtra("EXTRA_INSTITUTION_NAME", institutionName)
                putExtra("EXTRA_PAYMENT_TYPE", paymentType)
            }

            startActivity(intent)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.payment_Review_Container)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    // ==========================================================================================
    //                                     QR CODE PIX
    // ==========================================================================================
    private fun parsePixQrCode(payload: String) {
        try {
            val nomeBeneficiario = extractField(payload, "59")?.trim()
                ?: extractField(payload, "58")?.trim()

            val valorRaw = extractField(payload, "54")
            val codigoCompleto = payload.trim()

            val valorFormatado = if (valorRaw.isNullOrBlank()) {
                "A definir"
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
            findViewById<TextView>(R.id.payment_Review_Description_11_View).text = "PIX"

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

        } catch (e: Exception) {
            Log.e("PIX", "Erro ao parsear", e)
            showInvalid()
        }
    }

    private fun extractField(payload: String, tag: String): String? {
        val index = payload.indexOf(tag)
        if (index == -1) return null

        val lengthIndex = index + tag.length
        val length = payload.substring(lengthIndex, lengthIndex + 2).toIntOrNull() ?: return null

        val valueStart = lengthIndex + 2
        val valueEnd = valueStart + length
        if (valueEnd > payload.length) return null

        return payload.substring(valueStart, valueEnd)
    }

    // ==========================================================================================
    //                                BOLETO (44 ou 47 dígitos)
    // ==========================================================================================
    private fun parseBankSlipBarcode(clean44: String) {
        try {
            if (clean44.length != 44) {
                showInvalid()
                return
            }

            // BOLETOS DE CONVÊNIO — começam com 8
            if (clean44.startsWith("8")) {
                parseConvenio(clean44)
                return
            }

            // BOLETO BANCÁRIO
            val banco = clean44.substring(0, 3)
            val valor = clean44.substring(9, 19) // 10 dígitos do valor (em centavos)
            val valorDouble = valor.toLong() / 100.0
            val valorFormatado = "R$ ${"%.2f".format(valorDouble).replace(".", ",")}"

            val nomeBanco = when (banco) {
                "001" -> "Banco do Brasil"
                "033" -> "Santander"
                "104" -> "Caixa"
                "237" -> "Bradesco"
                "341" -> "Itaú"
                "356", "655" -> "Inter"
                else -> "Banco"
            }

            findViewById<TextView>(R.id.payment_Review_Description_02_View).text = valorFormatado
            findViewById<TextView>(R.id.payment_Review_Description_15_View).text = valorFormatado
            findViewById<TextView>(R.id.payment_Review_Description_04_View).text = nomeBanco
            findViewById<TextView>(R.id.payment_Review_Description_06_View).text = clean44
            findViewById<TextView>(R.id.payment_Review_Description_07_View).text = nomeBanco
            findViewById<TextView>(R.id.payment_Review_Description_11_View).text = "BOLETO"

        } catch (e: Exception) {
            Log.e("BOLETO", "Erro ao parsear", e)
            showInvalid()
        }
    }

    private fun parseConvenio(clean: String) {
        val tipoValor = clean.substring(1, 2).toInt()
        val valorStr = clean.substring(4, 15) // 11 dígitos para convênio
        val valor = if (tipoValor == 6 || tipoValor == 7) 0.0 else valorStr.toLong() / 100.0

        val valorFormatado = "R$ " + "%.2f".format(valor).replace(".", ",")

        findViewById<TextView>(R.id.payment_Review_Description_02_View).text = valorFormatado
        findViewById<TextView>(R.id.payment_Review_Description_15_View).text = valorFormatado
        findViewById<TextView>(R.id.payment_Review_Description_04_View).text = "Concessionária"
        findViewById<TextView>(R.id.payment_Review_Description_06_View).text = clean
        findViewById<TextView>(R.id.payment_Review_Description_07_View).text = "Convênio"
        findViewById<TextView>(R.id.payment_Review_Description_11_View).text = "BOLETO"
    }

    // ==========================================================================================
    //                   CONVERSÃO: Linha Digitável (47) → Código de Barras (44)
    // ==========================================================================================
    private fun convertLinhaDigitavelToBarcode(linha: String): String {
        val c = linha.replace("[^0-9]".toRegex(), "")
        if (c.length != 47) return ""

        // Campos conforme padrão da linha digitável
        val campo1 = c.substring(0, 9)      // 9 primeiros caracteres (campo 1)
        val campo2 = c.substring(10, 20)    // campo 2 (posições 11-20)
        val campo3 = c.substring(21, 31)    // campo 3 (posições 22-31)
        val dvGeral = c.substring(32, 33)   // dígito verificador geral (posição 33)
        val fatorValor = c.substring(33, 47) // fator de vencimento + valor (14 dígitos)

        // Monta o código de barras (44 dígitos) na ordem correta:
        // banco(4) + DV geral(1) + fator+valor(14) + campo1[4..8] + campo2[0..9] + campo3[0..9]
        return c.substring(0, 4) + dvGeral + fatorValor +
                campo1.substring(4) + campo2.substring(0, 10) + campo3.substring(0, 10)
    }

    private fun showInvalid() {
        findViewById<TextView>(R.id.payment_Review_Description_04_View).text = "Código inválido"
        findViewById<TextView>(R.id.payment_Review_Description_11_View).text = "---"
    }
}

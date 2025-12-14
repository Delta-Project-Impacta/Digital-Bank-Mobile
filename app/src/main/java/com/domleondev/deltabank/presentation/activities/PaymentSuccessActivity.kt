package com.domleondev.deltabank.presentation.activities

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.domleondev.deltabank.R
import com.domleondev.deltabank.presentation.util.setupTransparentStatusBarNoPadding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class PaymentSuccessActivity : AppCompatActivity() {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_payment_success)

        setupTransparentStatusBarNoPadding(
            rootViewId = R.id.payment_Success_Container,
            darkIcons = true
        )

        val amount = intent.getStringExtra("EXTRA_AMOUNT") ?: ""
        val recipientName = intent.getStringExtra("EXTRA_RECIPIENT_NAME") ?: ""
        val institutionName = intent.getStringExtra("EXTRA_INSTITUTION_NAME") ?: ""
        val paymentType = intent.getStringExtra("EXTRA_PAYMENT_TYPE") ?: ""

        findViewById<TextView>(R.id.payment_Success_Amount_Value).text = amount
        findViewById<TextView>(R.id.payment_Success_DestinyName_Value).text = recipientName
        findViewById<TextView>(R.id.payment_Success_DestinyInstitution_Value).text = institutionName
        findViewById<TextView>(R.id.payment_Success_TransferType_Value).text = paymentType

        val now = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy - HH:mm:ss", Locale("pt", "BR"))
        var formattedDate = now.format(formatter)
        formattedDate = formattedDate.replaceRange(3, 6, formattedDate.substring(3, 6).uppercase())

        findViewById<TextView>(R.id.payment_Success_Date).text = formattedDate

        val user = auth.currentUser
        if (user != null) {
            val uid = user.uid

            db.collection("users").document(uid).get()
                .addOnSuccessListener { doc ->
                    val fullName = doc.getString("name") ?: "Usuário"
                    val cpf = doc.getString("cpf") ?: "00000000000"

                    findViewById<TextView>(R.id.payment_Success_OriginName_Value).text = fullName

                    val maskedCpf = maskCpf(cpf)
                    findViewById<TextView>(R.id.payment_Success_OriginCpf_Value).text = maskedCpf
                }
        }

        findViewById<ImageView>(R.id.payment_Success_Close).setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun maskCpf(cpf: String): String {
        val digits = cpf.replace("[^0-9]".toRegex(), "")
        if (digits.length != 11) return cpf // fallback

        return "●●●.${digits.substring(3,6)}.${digits.substring(6,9)}-●●"
    }
}

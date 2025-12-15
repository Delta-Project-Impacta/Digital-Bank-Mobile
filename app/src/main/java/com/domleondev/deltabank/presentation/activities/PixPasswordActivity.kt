package com.domleondev.deltabank.presentation.activities

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.domleondev.deltabank.R
import com.domleondev.deltabank.presentation.util.setupTransparentStatusBarNoPadding
import com.domleondev.deltabank.viewModel.TransferPasswordViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.collect

class PixPasswordActivity : AppCompatActivity() {

    private val TAG = "PIX_PASSWORD"
    private val auth = FirebaseAuth.getInstance()

    private val vm: TransferPasswordViewModel by viewModels()

    private lateinit var hiddenInput: EditText
//    private lateinit var pin1: EditText
//    private lateinit var pin2: EditText
//    private lateinit var pin3: EditText
//    private lateinit var pin4: EditText
    private lateinit var btnNext: AppCompatButton

    // extras
    private var recipientUid: String? = null
    private var recipientName: String? = null
    private var recipientCpf: String? = null
    private var amount: Double = 0.0
    private var senderBalance: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pix_password)

        setupTransparentStatusBarNoPadding(
            rootViewId = R.id.pix_Password_Container,
            darkIcons = true
        )

        findViewById<ImageView>(R.id.pix_Password_Arrow_Back).setOnClickListener {
            finish()
        }

        hiddenInput = findViewById(R.id.hidden_otp_input)
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

        // bind inputs
//        pin1 = findViewById(R.id.dot_Input_1)
//        pin2 = findViewById(R.id.dot_Input_2)
//        pin3 = findViewById(R.id.dot_Input_3)
//        pin4 = findViewById(R.id.dot_Input_4)
        btnNext = findViewById(R.id.pix_Password_Button_Next)

        // read extras
        recipientUid = intent.getStringExtra("recipientUid")
        recipientName = intent.getStringExtra("recipientName")
        recipientCpf = intent.getStringExtra("recipientCpf")
        amount = intent.getDoubleExtra("amount", 0.0)
        senderBalance = intent.getDoubleExtra("senderBalance", 0.0)

        Log.d(
            TAG,
            "Extras -> recipientUid=$recipientUid recipientName=$recipientName recipientCpf=$recipientCpf amount=$amount senderBalance=$senderBalance"
        )

        observeViewModel()

        btnNext.setOnClickListener {
            val pin = collectPin()
            Log.d(TAG, "PIN coletado: $pin")

            if (pin.length != 4) {
                Toast.makeText(
                    this,
                    "Digite a senha de transação (4 dígitos)",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            if (auth.currentUser == null) {
                Log.e(TAG, "Usuário não autenticado")
                Toast.makeText(this, "Usuário não autenticado", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val toUid = recipientUid
            if (toUid.isNullOrBlank()) {
                Log.e(TAG, "recipientUid ausente")
                Toast.makeText(this, "Destinatário inválido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            vm.submitPinAndTransfer(
                toUid = toUid,
                amount = amount,
                pin = pin,
                recipientName = recipientName ?: toUid
            )
        }
    }

    private fun observeViewModel() {

        lifecycleScope.launchWhenStarted {
            vm.state.collect { state ->
                Log.d(
                    TAG,
                    "VM state -> loading=${state.loading} success=${state.success} error=${state.error}"
                )

                state.error?.let {
                    Toast.makeText(this@PixPasswordActivity, it, Toast.LENGTH_SHORT).show()
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            vm.events.collect { ev ->
                when (ev) {

                    is TransferPasswordViewModel.Event.TransferSuccess -> {
                        Log.d(TAG, "TransferSuccess -> $ev")

                        val intent = Intent(
                            this@PixPasswordActivity,
                            PixSuccessActivity::class.java
                        ).apply {
                            putExtra("recipientName", recipientName)
                            putExtra("recipientCpf", recipientCpf)
                            putExtra("amount", amount)
                            putExtra("fromTransactionId", ev.fromTransactionId)
                            putExtra("toTransactionId", ev.toTransactionId)
                            putExtra("newFromBalance", ev.newFromBalance)
                            putExtra("newToBalance", ev.newToBalance)
                        }

                        startActivity(intent)
                        finish()
                    }

                    is TransferPasswordViewModel.Event.TransferFailure -> {
                        Log.e(TAG, "TransferFailure -> ${ev.message}")
                        Toast.makeText(
                            this@PixPasswordActivity,
                            "Erro ao efetivar transferência: ${ev.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                    is TransferPasswordViewModel.Event.InvalidPassword -> {
                        Log.w(TAG, "Senha inválida")
                        Toast.makeText(
                            this@PixPasswordActivity,
                            "Senha incorreta",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    private fun collectPin(): String {
        return hiddenInput.text.toString().trim()
    }
}

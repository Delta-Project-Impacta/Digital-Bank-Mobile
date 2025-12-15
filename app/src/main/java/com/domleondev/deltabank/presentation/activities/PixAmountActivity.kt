package com.domleondev.deltabank.presentation.activities

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
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
import com.google.android.material.textfield.TextInputEditText
import java.util.Locale

class PixAmountActivity : AppCompatActivity() {

    private val TAG = "PIX_AMOUNT"
    private val vm: com.domleondev.deltabank.viewModel.TransferAmountViewModel by viewModels()
    // views
    private lateinit var balanceTextView: TextView
    private lateinit var toggleIcon: ImageView
    private var recipientNameTextView: TextView? = null
    private var amountInput: TextInputEditText? = null
    private lateinit var nextButton: AppCompatButton
    private var senderBalance: Double = 0.0

    private var isBalanceVisible = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pix_amount)

        setupTransparentStatusBarNoPadding(
            rootViewId = R.id.pix_Amount_Container,
            darkIcons = true
        )

        val backArrow = findViewById<ImageView>(R.id.pix_Amount_Arrow_Back)
        backArrow.setOnClickListener {
            Log.d(TAG, "Back arrow pressed - finishing activity")
            finish()
        }

        // Views
        balanceTextView = findViewById(R.id.pix_Amount_Balance_Amount_Text)
        toggleIcon = findViewById(R.id.pix_Amount_Balance_Toggle_Icon)
        recipientNameTextView = try { findViewById(R.id.pix_Amount_Name_View) } catch (t: Throwable) {
            Log.w(TAG, "recipientNameTextView não encontrado no layout: ${t.message}")
            null
        }
        amountInput = try { findViewById(R.id.pix_Amount_Edit_Value) } catch (t: Throwable) {
            Log.w(TAG, "amountInput não encontrado no layout: ${t.message}")
            null
        }
        nextButton = findViewById(R.id.pix_Amount_Button_Next)

        // Ler extras (podem ter vindo da TransferHomeActivity)
        val recipientUid = intent.getStringExtra("recipientUid")
        val recipientName = intent.getStringExtra("recipientName")
        val recipientCpf = intent.getStringExtra("recipientCpf")
        senderBalance = intent.getDoubleExtra("senderBalance", 0.0)

        Log.d(TAG, "Extras recebidos -> recipientUid=$recipientUid recipientName=$recipientName recipientCpf=$recipientCpf senderBalance=$senderBalance")

        // Inicializa ViewModel com argumentos
        vm.initFromArgs(recipientUid = recipientUid, recipientName = recipientName, recipientCpf = recipientCpf, senderBalance = senderBalance)

        // TextWatcher: chama VM em tempo real para validar e atualizar state
        amountInput?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val text = s?.toString() ?: ""
                Log.d(TAG, "amountInput mudou -> '$text'")
                vm.onAmountTextChanged(text)
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        // Observa state (para atualizar UI)
        lifecycleScope.launchWhenStarted {
            vm.state.collect { state ->
                // Atualiza nome mostrado, validade do botão, e value input se necessário
                recipientNameTextView?.text = state.recipientName
                // Atualiza visibilidade do balance
                updateBalanceText()
                // Habilitar/desabilitar botão Next via estado
                nextButton.isEnabled = state.isValid
                // Erros de validação (mostramos toast breve)
                state.error?.let {
                    Log.d(TAG, "State error detectado -> $it")
                    Toast.makeText(this@PixAmountActivity, it, Toast.LENGTH_SHORT).show()
                    // Depois de exibir, poderia limpar o erro via VM (opcional)
                }
            }
        }

        // Observa eventos one-shot (navegar / toast)
        lifecycleScope.launchWhenStarted {
            vm.events.collect { ev ->
                when (ev) {
                    // <-- ALTERAÇÃO SOLICITADA: navegar direto para PixPasswordActivity
                    is com.domleondev.deltabank.viewModel.TransferAmountViewModel.Event.NavigateToReview -> {
                        Log.d(TAG, "Evento NavigateToReview recebido -> amount=${ev.amount}")
                        val intent = Intent(this@PixAmountActivity, PixPasswordActivity::class.java).apply {
                            putExtra("recipientUid", ev.recipientUid)
                            putExtra("recipientName", ev.recipientName)
                            putExtra("recipientCpf", ev.recipientCpf)
                            putExtra("amount", ev.amount)
                            putExtra("senderBalance", ev.senderBalance)
                        }
                        startActivity(intent)
                    }
                    is com.domleondev.deltabank.viewModel.TransferAmountViewModel.Event.ShowToast -> {
                        Log.d(TAG, "Evento ShowToast -> ${ev.message}")
                        Toast.makeText(this@PixAmountActivity, ev.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        // Preenche balance inicialmente
        updateBalanceText()

        toggleIcon.setOnClickListener {
            isBalanceVisible = !isBalanceVisible
            Log.d(TAG, "toggleIcon clicado — isBalanceVisible = $isBalanceVisible")
            updateBalanceText()
            toggleIcon.setImageResource(if (isBalanceVisible) R.drawable.ic_eye else R.drawable.ic_eye_off)
        }

        // Next button — delega validação / navegação ao ViewModel
        nextButton.setOnClickListener {
            val rawValue = amountInput?.text?.toString() ?: ""
            Log.d(TAG, "Usuário clicou Next — delegando ao ViewModel — valor raw='$rawValue'")
            vm.onNextClicked(rawValue)
        }
    }

    private fun updateBalanceText() {
        val formatted = java.text.NumberFormat.getCurrencyInstance(Locale("pt", "BR")).format(vm.senderBalance)
        if (isBalanceVisible) {
            balanceTextView.text = formatted
        } else {
            balanceTextView.text = "R$ ●●●●●●"
        }
    }
}

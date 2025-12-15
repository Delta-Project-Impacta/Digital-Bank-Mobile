package com.domleondev.deltabank.presentation.activities

import android.content.Intent
import android.os.Bundle
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
import kotlinx.coroutines.flow.collect
import java.text.NumberFormat
import java.util.Locale

class PixReviewActivity : AppCompatActivity() {

    private val TAG = "PIX_REVIEW"
    private val vm: com.domleondev.deltabank.viewModel.TransferReviewViewModel by viewModels()

    // views
    private lateinit var tvRecipientName: TextView
    private lateinit var tvRecipientCpf: TextView
    private lateinit var tvBankName: TextView
    private lateinit var tvMethod: TextView
    private lateinit var tvAmount: TextView
    private var tvSenderBalance: TextView? = null
    private lateinit var btnNext: AppCompatButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pix_review)

        setupTransparentStatusBarNoPadding(
            rootViewId = R.id.pix_Review_Container,
            darkIcons = true
        )

        val backArrow = findViewById<ImageView>(R.id.pix_Review_Arrow_Back)
        backArrow.setOnClickListener {
            Log.d(TAG, "Back arrow pressed - finishing activity")
            finish()
        }

        // binds (keep defensive try/catch like original)
        tvRecipientName = try {
            findViewById(R.id.pix_Review_Description_04_View)
        } catch (t: Throwable) {
            Log.w(TAG, "tvRecipientName não encontrado: ${t.message}")
            TextView(this)
        }

        tvRecipientCpf = try {
            findViewById(R.id.pix_Review_Description_06_View)
        } catch (t: Throwable) {
            Log.w(TAG, "tvRecipientCpf não encontrado: ${t.message}")
            TextView(this)
        }

        tvBankName = try {
            findViewById(R.id.pix_Review_Description_07_View)
        } catch (t: Throwable) {
            Log.w(TAG, "tvBankName não encontrado: ${t.message}")
            TextView(this)
        }

        tvMethod = try {
            findViewById(R.id.pix_review_description_11_View)
        } catch (t: Throwable) {
            Log.w(TAG, "tvMethod não encontrado: ${t.message}")
            TextView(this)
        }

        tvAmount = try {
            findViewById(R.id.pix_review_description_15_View)
        } catch (t: Throwable) {
            Log.w(TAG, "tvAmount não encontrado: ${t.message}")
            TextView(this)
        }

        tvSenderBalance = try {
            findViewById<TextView>(R.id.pix_Review_Description_02_View)
        } catch (t: Throwable) {
            Log.w(TAG, "tvSenderBalance não encontrado: ${t.message}")
            null
        }

        btnNext = try {
            findViewById(R.id.pix_Review_Button_Next)
        } catch (t: Throwable) {
            Log.e(TAG, "Botão Next não encontrado no layout — verifique o XML", t)
            AppCompatButton(this)
        }

        // Read extras and initialize VM
        val recipientUid = intent.getStringExtra("recipientUid")
        val recipientName = intent.getStringExtra("recipientName")
        val recipientCpf = intent.getStringExtra("recipientCpf")
        val amount = intent.getDoubleExtra("amount", 0.0)
        val senderBalance = intent.getDoubleExtra("senderBalance", 0.0)

        Log.d(TAG, "Extras recebidos -> recipientUid=$recipientUid recipientName=$recipientName recipientCpf=$recipientCpf amount=$amount senderBalance=$senderBalance")

        vm.initFromArgs(
            recipientUid = recipientUid,
            recipientName = recipientName,
            recipientCpf = recipientCpf,
            amount = amount,
            senderBalance = senderBalance
        )

        // Observe state
        lifecycleScope.launchWhenStarted {
            vm.state.collect { state ->
                // update UI fields (safe)
                try { tvBankName.text = state.bankName } catch (t: Throwable) { Log.w(TAG, "Erro setando tvBankName: ${t.message}") }
                try { tvMethod.text = state.method } catch (t: Throwable) { Log.w(TAG, "Erro setando tvMethod: ${t.message}") }
                try { tvRecipientCpf.text = state.recipientCpf ?: "--" } catch (t: Throwable) { Log.w(TAG, "Erro setando tvRecipientCpf: ${t.message}") }
                try { tvRecipientName.text = state.recipientName ?: "--" } catch (t: Throwable) { Log.w(TAG, "Erro setando tvRecipientName: ${t.message}") }

                try { tvAmount.text = state.formattedAmount } catch (t: Throwable) { Log.w(TAG, "Erro setando tvAmount: ${t.message}") }
                try { tvSenderBalance?.text = state.formattedSenderBalance } catch (t: Throwable) { Log.w(TAG, "Erro setando tvSenderBalance: ${t.message}") }

                btnNext.isEnabled = state.isNextEnabled

                state.error?.let { err ->
                    Log.w(TAG, "State error detectado -> $err")
                    Toast.makeText(this@PixReviewActivity, err, Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Observe events
        lifecycleScope.launchWhenStarted {
            vm.events.collect { ev ->
                when (ev) {
                    is com.domleondev.deltabank.viewModel.TransferReviewViewModel.Event.NavigateToPassword -> {
                        Log.d(TAG, "Evento NavigateToPassword recebido -> amount=${ev.amount} recipientUid=${ev.recipientUid}")
                        val intent = Intent(this@PixReviewActivity, PixPasswordActivity::class.java).apply {
                            putExtra("recipientUid", ev.recipientUid)
                            putExtra("recipientName", ev.recipientName)
                            putExtra("recipientCpf", ev.recipientCpf)
                            putExtra("amount", ev.amount)
                            putExtra("senderBalance", ev.senderBalance)
                        }
                        startActivity(intent)
                    }
                    is com.domleondev.deltabank.viewModel.TransferReviewViewModel.Event.ShowToast -> {
                        Log.d(TAG, "Evento ShowToast -> ${ev.message}")
                        Toast.makeText(this@PixReviewActivity, ev.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        // Button click delegates to VM
        btnNext.setOnClickListener {
            Log.d(TAG, "btnNext clicked - delegating final validation to ViewModel")
            vm.onConfirmClicked()
        }
    }
}

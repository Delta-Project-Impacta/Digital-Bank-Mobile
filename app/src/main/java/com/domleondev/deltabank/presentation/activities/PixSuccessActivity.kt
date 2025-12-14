package com.domleondev.deltabank.presentation.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.domleondev.deltabank.R
import kotlinx.coroutines.flow.collect

class PixSuccessActivity : AppCompatActivity() {

    private val TAG = "PIX_SUCCESS"

    private val vm: com.domleondev.deltabank.viewModel.TransferSuccessViewModel by viewModels()

    // Views
    private lateinit var shareIcon: ImageView
    private lateinit var closeIcon: ImageView
    private lateinit var tvDate: TextView
    private lateinit var tvAmountValue: TextView
    private lateinit var tvTransferTypeValue: TextView
    private lateinit var tvDestinyNameValue: TextView
    private lateinit var tvDestinyCpfValue: TextView
    private lateinit var tvDestinyInstitutionValue: TextView
    private lateinit var tvOriginNameValue: TextView
    private lateinit var tvOriginCpfValue: TextView
    private lateinit var tvOriginInstitutionValue: TextView

    private lateinit var tvDeltaTransactionIdValue: TextView

    // Extras
    private var recipientName: String? = null
    private var recipientCpf: String? = null
    private var amount: Double = 0.0
    private var fromTransactionId: String? = null
    private var toTransactionId: String? = null
    private var newFromBalance: Double? = null
    private var newToBalance: Double? = null
    private var originName: String? = null
    private var originCpf: String? = null
    private var recipientUid: String? = null
    private var originUid: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pix_success)

        Log.d(TAG, "onCreate chamado (Activity)")

        // bind views
        closeIcon = findViewById(R.id.pix_Success_Close)
        shareIcon = findViewById(R.id.pix_Success_Share)
        tvDate = findViewById(R.id.pix_Success_Date)
        tvAmountValue = findViewById(R.id.pix_Success_Amount_Value)
        tvTransferTypeValue = findViewById(R.id.pix_Success_TransferType_Value)

        tvDestinyNameValue = findViewById(R.id.pix_Success_DestinyName_Value)
        tvDestinyCpfValue = findViewById(R.id.pix_Success_DestinyCpf_Value)
        tvDestinyInstitutionValue = findViewById(R.id.pix_Success_DestinyInstitution_Value)

        tvOriginNameValue = findViewById(R.id.pix_Success_OriginName_Value)
        tvOriginCpfValue = findViewById(R.id.pix_Success_OriginCpf_Value)
        tvOriginInstitutionValue = findViewById(R.id.pix_Success_OriginInstitution_Value)

        tvDeltaTransactionIdValue = findViewById(R.id.pix_Success_Delta_TransactionId_Value)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // read extras
        recipientName = intent.getStringExtra("recipientName")
        recipientCpf = intent.getStringExtra("recipientCpf")
        amount = intent.getDoubleExtra("amount", 0.0)
        fromTransactionId = intent.getStringExtra("fromTransactionId")
        toTransactionId = intent.getStringExtra("toTransactionId")
        recipientUid = intent.getStringExtra("recipientUid")
        originUid = intent.getStringExtra("originUid") // opcional

        newFromBalance = intent.extras?.let { if (it.containsKey("newFromBalance")) it.getDouble("newFromBalance") else null }
        newToBalance = intent.extras?.let { if (it.containsKey("newToBalance")) it.getDouble("newToBalance") else null }

        originName = intent.getStringExtra("originName")
        originCpf = intent.getStringExtra("originCpf")

        Log.d(TAG, "Extras lidas -> recipientName=$recipientName recipientCpf=$recipientCpf amount=$amount fromTx=$fromTransactionId toTx=$toTransactionId newFrom=$newFromBalance newTo=$newToBalance recipientUid=$recipientUid originUid=$originUid")

        // initialize VM with incoming data
        vm.init(
            originName = originName,
            originCpf = originCpf,
            destinyName = recipientName,
            destinyCpf = recipientCpf,
            amount = amount,
            fromTx = fromTransactionId,
            toTx = toTransactionId,
            newFrom = newFromBalance,
            newTo = newToBalance
        )

        // observe VM state
        lifecycleScope.launchWhenStarted {
            vm.state.collect { state ->
                // populate UI using state values (safe)
                tvDate.text = state.formattedDate
                tvAmountValue.text = state.formattedAmount
                tvTransferTypeValue.text = state.transferType

                tvDestinyNameValue.text = state.destinyName ?: "--"
                tvDestinyCpfValue.text = state.destinyCpf ?: "--"
                tvDestinyInstitutionValue.text = state.destinyInstitution ?: "DeltaBank"

                tvOriginNameValue.text = state.originName ?: "Você"
                tvOriginCpfValue.text = state.originCpf ?: "--"
                tvOriginInstitutionValue.text = state.originInstitution ?: "DeltaBank"

                tvDeltaTransactionIdValue.text = state.transactionId ?: "--"

                if (state.newFromBalance != null || state.newToBalance != null) {
                    Log.d(TAG, "Saldos finais (UI): newFrom=${state.newFromBalance} newTo=${state.newToBalance}")
                }

                state.error?.let { err ->
                    Log.w(TAG, "State error -> $err")
                }
            }
        }

        // sharing uses receiptText produced by VM
        /*shareIcon.setOnClickListener {
            Log.d(TAG, "Usuário clicou em compartilhar comprovante")
            val receiptText = vm.state.value.receiptBody ?: vm.prepareReceiptFallbackText()
            val sendIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, receiptText)
                type = "text/plain"
            }
            val chooser = Intent.createChooser(sendIntent, getString(R.string.pix_success_description))
            startActivity(chooser)
        }*/

        // close -> go to Home clearing stack
        closeIcon.setOnClickListener {
            Log.d(TAG, "Usuário fechou tela de sucesso — voltando ao Home (clear top)")
            val intent = Intent(this, HomeActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            }
            startActivity(intent)
            finish()
        }
    }
}

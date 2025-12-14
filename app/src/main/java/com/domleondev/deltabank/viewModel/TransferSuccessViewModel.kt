package com.domleondev.deltabank.viewModel

import android.text.TextUtils
import android.util.Log
import androidx.lifecycle.ViewModel
import com.domleondev.deltabank.domain.usecase.PrepareReceiptUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class TransferSuccessState(
    val formattedDate: String = SimpleDateFormat(
        "dd/MM/yyyy 'às' HH:mm",
        Locale("pt", "BR")
    ).format(Date()),
    val formattedAmount: String = "--",
    val transferType: String = "PIX",
    val destinyName: String? = null,
    val destinyCpf: String? = null,
    val destinyInstitution: String? = "DeltaBank",
    val originName: String? = null,
    val originCpf: String? = null,
    val originInstitution: String? = "DeltaBank",
    val transactionId: String? = null,
    val newFromBalance: Double? = null,
    val newToBalance: Double? = null,
    val receiptBody: String? = null,
    val error: String? = null
)

class TransferSuccessViewModel(
    private val prepareReceiptUseCase: PrepareReceiptUseCase = PrepareReceiptUseCase()
) : ViewModel() {

    private val TAG = "TransferSuccessVM"

    private val _state = MutableStateFlow(TransferSuccessState())
    val state = _state.asStateFlow()

    /**
     * Inicializa o estado SOMENTE com dados vindos da Cloud Function.
     * Não faz leitura em Firestore.
     * Se faltar dado crítico → erro explícito.
     */
    fun init(
        originName: String?,
        originCpf: String?,
        destinyName: String?,
        destinyCpf: String?,
        amount: Double,
        fromTx: String?,
        toTx: String?,
        newFrom: Double?,
        newTo: Double?
    ) {
        Log.d(TAG, "init chamado -> destinyName=$destinyName amount=$amount fromTx=$fromTx toTx=$toTx")

        if (newFrom == null || newTo == null) {
            Log.e(TAG, "Dados incompletos vindos da Function")
            _state.value = _state.value.copy(
                error = "Dados incompletos para exibir comprovante"
            )
            return
        }

        val nf = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))

        _state.value = _state.value.copy(
            formattedAmount = nf.format(amount),
            destinyName = destinyName,
            destinyCpf = destinyCpf,
            originName = originName,
            originCpf = originCpf,
            transactionId = fromTx ?: toTx,
            newFromBalance = newFrom,
            newToBalance = newTo
        )

        prepareReceiptAndEmit(
            originName,
            originCpf,
            destinyName,
            destinyCpf,
            amount,
            fromTx,
            toTx,
            newFrom,
            newTo
        )
    }

    private fun prepareReceiptAndEmit(
        originName: String?,
        originCpf: String?,
        destinyName: String?,
        destinyCpf: String?,
        amount: Double,
        fromTx: String?,
        toTx: String?,
        newFrom: Double?,
        newTo: Double?
    ) {
        Log.d(TAG, "Preparando receipt via PrepareReceiptUseCase")

        try {
            val receipt = prepareReceiptUseCase.prepare(
                originName = originName,
                originCpf = originCpf,
                destinyName = destinyName,
                destinyCpf = destinyCpf,
                amount = amount,
                fromTransactionId = fromTx,
                toTransactionId = toTx,
                newFromBalance = newFrom,
                newToBalance = newTo
            )

            _state.value = _state.value.copy(
                receiptBody = receipt.bodyText
            )

            Log.d(TAG, "Receipt preparado com sucesso")
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao preparar receipt", e)
            _state.value = _state.value.copy(
                error = "Erro ao preparar comprovante"
            )
        }
    }
}

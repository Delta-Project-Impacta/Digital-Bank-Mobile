package com.domleondev.deltabank.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.domleondev.deltabank.domain.usecase.GetUserByUidUseCase
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

data class TransferReviewState(
    val recipientName: String? = null,
    val recipientCpf: String? = null,
    val bankName: String = "DeltaBank",
    val method: String = "PIX",
    val formattedAmount: String = "--",
    val formattedSenderBalance: String = "--",
    val formattedAfter: String = "--",
    val isNextEnabled: Boolean = true,
    val error: String? = null
)

class TransferReviewViewModel(
    private val getUserByUidUseCase: GetUserByUidUseCase = GetUserByUidUseCase()
) : ViewModel() {

    private val TAG = "TransferReviewVM"

    private val _state = MutableStateFlow(TransferReviewState())
    val state = _state.asStateFlow()

    sealed class Event {
        data class NavigateToPassword(
            val recipientUid: String?,
            val recipientName: String?,
            val recipientCpf: String?,
            val amount: Double,
            val senderBalance: Double
        ) : Event()

        data class ShowToast(val message: String) : Event()
    }

    private val _events = MutableSharedFlow<Event>(extraBufferCapacity = 4, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    val events = _events.asSharedFlow()

    // Internal storage
    private var recipientUid: String? = null
    private var amount: Double = 0.0
    private var senderBalance: Double = 0.0

    /**
     * Inicializa a tela com os extras vindos da Activity anterior.
     * Busca nome do destinatário se necessário.
     */
    fun initFromArgs(recipientUid: String?, recipientName: String?, recipientCpf: String?, amount: Double, senderBalance: Double) {
        Log.d(TAG, "initFromArgs -> uid=$recipientUid name=$recipientName cpf=$recipientCpf amount=$amount senderBalance=$senderBalance")

        this.recipientUid = recipientUid
        this.amount = amount
        this.senderBalance = senderBalance

        // Preenche state básico
        val nf = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
        val formattedAmount = nf.format(amount)
        val formattedSender = nf.format(senderBalance)
        val formattedAfter = nf.format((senderBalance - amount))

        _state.value = _state.value.copy(
            recipientName = recipientName ?: null,
            recipientCpf = recipientCpf ?: null,
            formattedAmount = formattedAmount,
            formattedSenderBalance = formattedSender,
            formattedAfter = formattedAfter,
            isNextEnabled = amount > 0.0 && amount <= senderBalance,
            error = when {
                amount <= 0.0 -> "Valor inválido"
                amount > senderBalance -> "Saldo insuficiente"
                else -> null
            }
        )

        // If recipient name missing, fetch by uid
        if ((recipientName.isNullOrBlank()) && !recipientUid.isNullOrBlank()) {
            Log.d(TAG, "Recipient name ausente — buscando via uid=$recipientUid")
            getUserByUidUseCase.execute(recipientUid!!, object : GetUserByUidUseCase.Callback {
                override fun onSuccess(name: String, cpf: String?) {
                    Log.d(TAG, "GetUserByUidUseCase recebeu -> name=$name cpf=$cpf")
                    viewModelScope.launch {
                        _state.value = _state.value.copy(recipientName = name, recipientCpf = cpf ?: _state.value.recipientCpf)
                    }
                }

                override fun onNotFound() {
                    Log.w(TAG, "Destinatário não encontrado para uid=$recipientUid")
                    viewModelScope.launch { _events.emit(Event.ShowToast("Destinatário não encontrado")) }
                }

                override fun onError(e: Exception) {
                    Log.e(TAG, "Erro ao buscar destinatário", e)
                    viewModelScope.launch { _events.emit(Event.ShowToast("Erro ao buscar destinatário")) }
                }
            })
        } else {
            Log.d(TAG, "Recipient name já informado -> ${recipientName ?: "--"}")
        }
    }

    /**
     * Chamado quando o usuário clica em confirmar (Next).
     * Faz validação final e emite evento de navegação com extras.
     */
    fun onConfirmClicked() {
        Log.d(TAG, "onConfirmClicked -> amount=$amount senderBalance=$senderBalance recipientUid=$recipientUid")

        when {
            amount <= 0.0 -> {
                Log.w(TAG, "Amount inválido no onConfirmClicked")
                viewModelScope.launch { _events.emit(Event.ShowToast("Valor inválido")) }
                return
            }
            amount > senderBalance -> {
                Log.w(TAG, "Saldo insuficiente no onConfirmClicked")
                viewModelScope.launch { _events.emit(Event.ShowToast("Saldo insuficiente")) }
                return
            }
            else -> {
                viewModelScope.launch {
                    _events.emit(
                        Event.NavigateToPassword(
                            recipientUid = recipientUid,
                            recipientName = _state.value.recipientName,
                            recipientCpf = _state.value.recipientCpf,
                            amount = amount,
                            senderBalance = senderBalance
                        )
                    )
                }
            }
        }
    }
}


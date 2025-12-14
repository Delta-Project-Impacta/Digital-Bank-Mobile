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

data class TransferAmountState(
    val recipientName: String = "--",
    val recipientCpf: String = "",
    val amountInput: String = "",
    val isValid: Boolean = false,
    val error: String? = null
)

class TransferAmountViewModel(
    // removi SavedStateHandle do construtor para evitar problemas de factory na inicialização
    private val getUserByUidUseCase: GetUserByUidUseCase = GetUserByUidUseCase()
) : ViewModel() {

    private val TAG = "TransferAmountVM"

    // state
    private val _state = MutableStateFlow(TransferAmountState())
    val state = _state.asStateFlow()

    // events (one-shot)
    sealed class Event {
        data class NavigateToReview(
            val recipientUid: String?,
            val recipientName: String?,
            val recipientCpf: String?,
            val amount: Double,
            val senderBalance: Double
        ) : Event()

        data class ShowToast(val message: String) : Event()
    }

    private val _events =
        MutableSharedFlow<Event>(extraBufferCapacity = 4, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    val events = _events.asSharedFlow()

    // Dados que o Activity pode querer acessar (leitura pública)
    var recipientUid: String? = null
        private set
    var senderBalance: Double = 0.0
        private set

    /**
     * Inicialização a partir das extras (chamada pela Activity após leitura de Intent).
     * Se o nome não for informado, busca via GetUserByUidUseCase.
     */
    fun initFromArgs(recipientUid: String?, recipientName: String?, recipientCpf: String?, senderBalance: Double) {
        Log.d(TAG, "initFromArgs -> uid=$recipientUid name=$recipientName cpf=$recipientCpf senderBalance=$senderBalance")
        this.recipientUid = recipientUid
        this.senderBalance = senderBalance

        // Atualiza estado inicial
        _state.value = _state.value.copy(
            recipientName = recipientName ?: "--",
            recipientCpf = recipientCpf ?: ""
        )

        // Se o nome não veio e temos uid, busca
        if ((recipientName.isNullOrBlank()) && !recipientUid.isNullOrBlank()) {
            Log.d(TAG, "recipientName não informado via Intent — buscando via uid=$recipientUid")
            getUserByUidUseCase.execute(recipientUid!!, object : GetUserByUidUseCase.Callback {
                override fun onSuccess(name: String, cpf: String?) {
                    Log.d(TAG, "GetUserByUidUseCase -> nome=$name cpf=$cpf")
                    _state.value = _state.value.copy(recipientName = name, recipientCpf = cpf ?: "")
                }

                override fun onNotFound() {
                    Log.w(TAG, "GetUserByUidUseCase -> usuário não encontrado para uid=$recipientUid")
                    viewModelScope.launch { _events.emit(Event.ShowToast("Destinatário não encontrado")) }
                }

                override fun onError(e: Exception) {
                    Log.e(TAG, "GetUserByUidUseCase erro", e)
                    viewModelScope.launch { _events.emit(Event.ShowToast("Erro ao buscar destinatário")) }
                }
            })
        } else {
            Log.d(TAG, "recipientName já informado via Intent -> ${recipientName ?: "--"}")
        }
    }

    /**
     * Chamado quando o usuário clica Next. Valida valor, atualiza estado e emite evento de navegação.
     */
    fun onNextClicked(rawValue: String) {
        Log.d(TAG, "onNextClicked -> rawValue='$rawValue' senderBalance=$senderBalance")

        val sanitized = rawValue.replace(Regex("[^0-9,\\.]"), "").replace(",", ".")
        val value = sanitized.toDoubleOrNull()
        val valid = value != null && value > 0.0 && value <= senderBalance

        Log.d(TAG, "Sanitized='$sanitized' parsed=$value")

        when {
            value == null -> {
                Log.w(TAG, "Valor inválido")
                _state.value = _state.value.copy(error = "Digite um valor válido", isValid = false)
                viewModelScope.launch { _events.emit(Event.ShowToast("Digite um valor válido")) }
                return
            }
            value <= 0.0 -> {
                Log.w(TAG, "Valor menor ou igual a zero")
                _state.value = _state.value.copy(error = "Valor deve ser maior que zero", isValid = false)
                viewModelScope.launch { _events.emit(Event.ShowToast("Valor deve ser maior que zero")) }
                return
            }
            value > senderBalance -> {
                Log.w(TAG, "Saldo insuficiente: value=$value senderBalance=$senderBalance")
                _state.value = _state.value.copy(error = "Saldo insuficiente", isValid = false)
                viewModelScope.launch { _events.emit(Event.ShowToast("Saldo insuficiente")) }
                return
            }
            else -> {
                // válido
                _state.value = _state.value.copy(amountInput = rawValue, isValid = true, error = null)
                viewModelScope.launch {
                    _events.emit(
                        Event.NavigateToReview(
                            recipientUid = recipientUid,
                            recipientName = _state.value.recipientName,
                            recipientCpf = _state.value.recipientCpf,
                            amount = value,
                            senderBalance = senderBalance
                        )
                    )
                }
            }
        }
    }

    /**
     * Atualiza estado de validação em tempo real a partir do texto do input.
     * Deve ser chamado por um TextWatcher no Activity.
     */
    fun onAmountTextChanged(rawText: String) {
        Log.d(TAG, "onAmountTextChanged -> raw='$rawText' senderBalance=$senderBalance")

        val sanitized = rawText.replace(Regex("[^0-9,\\.]"), "").replace(",", ".")
        val value = sanitized.toDoubleOrNull()
        val valid = value != null && value > 0.0 && value <= senderBalance

        val errorMsg = when {
            value == null -> null // sem erro enquanto usuário digita (pode estar incompleto)
            value <= 0.0 -> "Valor deve ser maior que zero"
            value > senderBalance -> "Saldo insuficiente"
            else -> null
        }

        _state.value = _state.value.copy(
            amountInput = rawText,
            isValid = valid,
            error = errorMsg
        )
    }
}


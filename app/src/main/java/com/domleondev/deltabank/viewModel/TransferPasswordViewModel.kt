package com.domleondev.deltabank.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.domleondev.deltabank.domain.usecase.PerformPixTransferUseCase
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class TransferPasswordState(
    val loading: Boolean = false,
    val success: Boolean = false,
    val error: String? = null
)

class TransferPasswordViewModel(
    private val performPixTransferUseCase: PerformPixTransferUseCase = PerformPixTransferUseCase()
) : ViewModel() {

    private val TAG = "TransferPasswordVM"

    private val _state = MutableStateFlow(TransferPasswordState())
    val state = _state.asStateFlow()

    sealed class Event {
        data class TransferSuccess(
            val fromTransactionId: String,
            val toTransactionId: String,
            val newFromBalance: Double,
            val newToBalance: Double
        ) : Event()

        data class TransferFailure(val message: String) : Event()
        object InvalidPassword : Event()
    }

    private val _events = MutableSharedFlow<Event>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val events = _events.asSharedFlow()

    fun submitPinAndTransfer(
        toUid: String,
        amount: Double,
        pin: String,
        recipientName: String
    ) {
        Log.d(TAG, "submitPinAndTransfer -> to=$toUid amount=$amount")

        _state.value = TransferPasswordState(loading = true)

        performPixTransferUseCase.execute(
            toUid = toUid,
            amount = amount,
            transactionPassword = pin,
            description = "PIX para $recipientName",
            callback = object : PerformPixTransferUseCase.Callback {

                override fun onSuccess(result: PerformPixTransferUseCase.TransferResult) {
                    Log.d(TAG, "Transferência OK -> $result")

                    _state.value = TransferPasswordState(
                        loading = false,
                        success = true
                    )

                    viewModelScope.launch {
                        _events.emit(
                            Event.TransferSuccess(
                                fromTransactionId = result.fromTransactionId,
                                toTransactionId = result.toTransactionId,
                                newFromBalance = result.newFromBalance,
                                newToBalance = result.newToBalance
                            )
                        )
                    }
                }

                override fun onFailure(e: Exception) {
                    Log.e(TAG, "Erro na transferência", e)

                    _state.value = TransferPasswordState(
                        loading = false,
                        success = false,
                        error = e.message
                    )

                    viewModelScope.launch {
                        // Se o backend disser senha inválida → evento correto
                        if (e.message?.contains("Senha", ignoreCase = true) == true) {
                            _events.emit(Event.InvalidPassword)
                        } else {
                            _events.emit(
                                Event.TransferFailure(
                                    e.message ?: "Erro ao transferir"
                                )
                            )
                        }
                    }
                }
            }
        )
    }
}

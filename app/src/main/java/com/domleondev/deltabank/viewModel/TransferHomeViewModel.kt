package com.domleondev.deltabank.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.domleondev.deltabank.domain.usecase.FindUserByCpfUseCase
import com.domleondev.deltabank.domain.usecase.RecipientResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class TransferHomeViewModel(
    private val findUserByCpfUseCase: FindUserByCpfUseCase = FindUserByCpfUseCase(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) : ViewModel() {

    private val TAG = "TransferHomeViewModel"

    sealed class Event {
        data class NavigateToAmount(val uid: String, val name: String, val cpf: String, val senderBalance: Double) : Event()
        data class ShowToast(val message: String) : Event()
    }

    private val _events = MutableSharedFlow<Event>(extraBufferCapacity = 4, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    val events = _events.asSharedFlow()

    fun onNextClicked(rawCpf: String) {
        val cpf = normalizeCpf(rawCpf)
        Log.d(TAG, "onNextClicked -> raw='$rawCpf' normalized='$cpf'")

        if (cpf.isBlank()) {
            Log.w(TAG, "CPF está em branco após normalização")
            viewModelScope.launch { _events.emit(Event.ShowToast("Insira a chave (CPF) do destinatário")) }
            return
        }

        // Chama o UseCase
        Log.d(TAG, "Chamando FindUserByCpfUseCase.execute(cpf=$cpf)")
        findUserByCpfUseCase.execute(cpf, object : FindUserByCpfUseCase.Callback {
            override fun onSuccess(recipient: RecipientResult) {
                // Aqui o tipo é explicitamente RecipientResult - evita 'overrides nothing'
                Log.d(TAG, "UseCase retornou sucesso -> uid=${recipient.uid} name=${recipient.name}")

                // Obter user atual
                val currentUser = auth.currentUser
                if (currentUser == null) {
                    Log.e(TAG, "Usuário não autenticado ao tentar transferir")
                    viewModelScope.launch { _events.emit(Event.ShowToast("Usuário não autenticado")) }
                    return
                }

                // Pega balance do remetente
                Log.d(TAG, "Buscando balance do remetente uid=${currentUser.uid}")
                db.collection("users").document(currentUser.uid).get()
                    .addOnSuccessListener { senderDoc ->
                        val senderBalance = senderDoc.getDouble("balance") ?: 0.0
                        Log.d(TAG, "Balance do remetente (uid=${currentUser.uid}) = $senderBalance")

                        // Emite evento de navegação com tudo pronto
                        viewModelScope.launch {
                            _events.emit(Event.NavigateToAmount(recipient.uid, recipient.name, recipient.cpf, senderBalance))
                        }
                    }
                    .addOnFailureListener { e ->
                        Log.e(TAG, "Erro ao buscar balance do remetente", e)
                        viewModelScope.launch { _events.emit(Event.ShowToast("Erro ao obter saldo. Tente novamente.")) }
                    }
            }

            override fun onNotFound() {
                Log.w(TAG, "UseCase retornou: usuário não encontrado para cpf=$cpf")
                viewModelScope.launch { _events.emit(Event.ShowToast("Chave PIX não encontrada")) }
            }

            override fun onError(e: Exception) {
                Log.e(TAG, "Erro no UseCase findUserByCpf", e)
                viewModelScope.launch { _events.emit(Event.ShowToast("Erro ao buscar chave PIX. Tente novamente.")) }
            }
        })
    }

    private fun normalizeCpf(input: String): String {
        return input.replace(Regex("[^0-9]"), "")
    }
}

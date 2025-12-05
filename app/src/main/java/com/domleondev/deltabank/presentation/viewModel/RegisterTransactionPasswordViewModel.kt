package com.domleondev.deltabank.presentation.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.domleondev.deltabank.presentation.states.RegisterTransactionPasswordState
import com.domleondev.deltabank.presentation.usecase.RegisterTransactionPasswordUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class RegisterTransactionPasswordViewModel(
    private val registerUseCase: RegisterTransactionPasswordUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<RegisterTransactionPasswordState>(RegisterTransactionPasswordState.Idle)
    val state: StateFlow<RegisterTransactionPasswordState> = _state

    fun register(
        email: String,
        loginPassword: String,
        name: String,
        cpf: String,
        birthDate: String,
        transactionPassword: String,
        confirmTransactionPassword: String) {
        Log.d("RegisterViewModel", "register() called with email=$email, name=$name, cpf=$cpf, birthDate=$birthDate")

        viewModelScope.launch{
            _state.value = RegisterTransactionPasswordState.Loading
            Log.d("RegisterViewModel", "State set to Loading")
            try {
                val result = registerUseCase(email, loginPassword, name, cpf, birthDate, transactionPassword, confirmTransactionPassword)
                Log.d("RegisterViewModel", "UseCase returned result=$result")
                if (result.isSuccess){
                    Log.d("RegisterViewModel", "Registration SUCCESS")
                    _state.value = RegisterTransactionPasswordState.Success
                }else{
                    val ex = result.exceptionOrNull()
                    Log.d("RegisterViewModel", "Registration ERROR: ${ex?.message}")
                    _state.value = RegisterTransactionPasswordState.Error(result.exceptionOrNull()?.message ?: "Erro inesperado")
                }
            } catch (e: Exception){
                Log.d("RegisterViewModel", "Exception caught in register(): ${e.message}")
                _state.value = RegisterTransactionPasswordState.Error(e.message ?: "Erro inesperado")
            }
        }
    }

    fun resetState() {
        Log.d("RegisterViewModel", "resetState() called")
        _state.value = RegisterTransactionPasswordState.Idle
    }
}

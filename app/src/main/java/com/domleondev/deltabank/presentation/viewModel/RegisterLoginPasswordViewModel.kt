package com.domleondev.deltabank.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.domleondev.deltabank.presentation.states.RegisterLoginPasswordState
import com.domleondev.deltabank.presentation.usecase.RegisterLoginPasswordUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RegisterLoginPasswordViewModel(
    private val registerUseCase: RegisterLoginPasswordUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<RegisterLoginPasswordState>(RegisterLoginPasswordState.Idle)
    val state: StateFlow<RegisterLoginPasswordState> = _state

    fun register(
        email: String,
        loginPassword: String,
        confirmPassword: String,
        name: String,
        birth: String
    ) {
        viewModelScope.launch {
            _state.value = RegisterLoginPasswordState.Loading
            try {
                val result = registerUseCase(
                    email = email,
                    loginPassword = loginPassword,
                    confirmPassword = confirmPassword,
                    name = name,
                    birthdate = birth
                )

                if (result.isSuccess) {
                    _state.value = RegisterLoginPasswordState.Success(birth)
                } else {
                    val ex = result.exceptionOrNull()
                    _state.value =
                        RegisterLoginPasswordState.Error(ex?.message ?: "Erro inesperado")
                }

            } catch (e: Exception) {
                _state.value = RegisterLoginPasswordState.Error(e.message ?: "Erro inesperado")
            }
        }
    }

    fun resetState() {
        _state.value = RegisterLoginPasswordState.Idle
    }
}

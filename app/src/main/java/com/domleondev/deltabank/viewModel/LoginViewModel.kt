package com.domleondev.deltabank.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.domleondev.deltabank.domain.usecase.LoginUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.domleondev.deltabank.repository.auth.AuthRepository

class LoginViewModel(
    private val loginUseCase: LoginUseCase,
    private val repository: AuthRepository
) : ViewModel() {

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> get() = _loading

    private val _loginResult = MutableStateFlow<Result<Unit>?>(null)
    val loginResult: StateFlow<Result<Unit>?> get() = _loginResult
    fun login(cpf: String, password: String) {
        viewModelScope.launch {
            Log.d("LOGIN_VIEWMODEL", "Login iniciado: CPF=$cpf, currentUser antes do login: ${repository.getCurrentUserName()}")
            _loading.value = true
            val result = loginUseCase(cpf, password)
            Log.d("LOGIN_VIEWMODEL", "Login concluído: CPF=$cpf, result=${result.isSuccess}, currentUser agora: ${repository.getCurrentUserName()}")
            _loginResult.value = result
            _loading.value = false
        }
    }

    suspend fun checkCpfExists(cpf: String): Boolean {
        Log.d("LOGIN_VIEWMODEL", "Verificando CPF: $cpf, currentUser: ${repository.getCurrentUserName()}")
        val exists = repository.checkCpfExists(cpf)
        Log.d("LOGIN_VIEWMODEL", "Resultado da verificação CPF=$cpf: $exists")
        return exists
    }

}

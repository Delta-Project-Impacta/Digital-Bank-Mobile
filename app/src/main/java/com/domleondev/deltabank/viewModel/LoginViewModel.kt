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
            _loading.value = true
            val result = loginUseCase(cpf, password)
            _loginResult.value = result
            _loading.value = false
        }
    }

    suspend fun checkCpfExists(cpf: String): Boolean {
        return repository.checkCpfExists(cpf)
    }

}

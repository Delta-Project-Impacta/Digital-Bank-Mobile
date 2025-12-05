package com.domleondev.deltabank.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.domleondev.deltabank.presentation.usecase.LoginUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel(private val loginUseCase: LoginUseCase) : ViewModel() {

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> get() = _loading

    private val _loginResult = MutableStateFlow<Result<Unit>?>(null)
    val loginResult: StateFlow<Result<Unit>?> get() = _loginResult

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loading.value = true
            val result = loginUseCase(email, password)
            _loginResult.value = result
            _loading.value = false
        }
    }
}
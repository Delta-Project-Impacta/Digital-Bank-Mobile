package com.domleondev.deltabank.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.domleondev.deltabank.domain.usecase.LoginUseCase
import com.domleondev.deltabank.repository.auth.AuthRepository

class LoginViewModelFactory(
    private val loginUseCase: LoginUseCase,
    private val repository: AuthRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(loginUseCase, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

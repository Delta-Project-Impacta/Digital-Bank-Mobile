package com.domleondev.deltabank.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.domleondev.deltabank.presentation.usecase.LoginUseCase

class LoginViewModelFactory(
    private val loginUseCase: LoginUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return LoginViewModel(loginUseCase) as T
    }
}
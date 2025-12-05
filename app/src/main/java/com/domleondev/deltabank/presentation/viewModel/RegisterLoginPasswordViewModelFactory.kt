package com.domleondev.deltabank.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.domleondev.deltabank.presentation.usecase.RegisterLoginPasswordUseCase


class RegisterLoginPasswordViewModelFactory(
    private val registerLoginPasswordUseCase: RegisterLoginPasswordUseCase
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RegisterLoginPasswordViewModel::class.java)) {
            return RegisterLoginPasswordViewModel(registerLoginPasswordUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

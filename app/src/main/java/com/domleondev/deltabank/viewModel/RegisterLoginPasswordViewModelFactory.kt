package com.domleondev.deltabank.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.domleondev.deltabank.domain.usecase.RegisterLoginPasswordUseCase


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

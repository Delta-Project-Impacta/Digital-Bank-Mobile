package com.domleondev.deltabank.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.domleondev.deltabank.presentation.usecase.RegisterTransactionPasswordUseCase

class RegisterTransactionPasswordViewModelFactory(
    private val useCase: RegisterTransactionPasswordUseCase
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RegisterTransactionPasswordViewModel::class.java)) {
            return RegisterTransactionPasswordViewModel(useCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

package com.domleondev.deltabank.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.domleondev.deltabank.presentation.usecase.*

class RegisterViewModelFactory(
    private val validateName: ValidateNameUseCase,
    private val validateBirth: ValidateBirthDateUseCase,
    private val validateCpf: ValidateCpfUseCase,
    private val validateEmail: ValidateEmailUseCase,
    private val validatePhone: ValidatePhoneUseCase,
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return RegisterViewModel(
            validateName,
            validateCpf,
            validateBirth,
            validateEmail,
            validatePhone
        ) as T
    }
}


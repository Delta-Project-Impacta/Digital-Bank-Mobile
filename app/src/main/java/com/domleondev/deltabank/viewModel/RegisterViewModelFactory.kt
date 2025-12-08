package com.domleondev.deltabank.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.domleondev.deltabank.domain.usecase.ValidateBirthDateUseCase
import com.domleondev.deltabank.domain.usecase.ValidateCpfUseCase
import com.domleondev.deltabank.domain.usecase.ValidateEmailUseCase
import com.domleondev.deltabank.domain.usecase.ValidateNameUseCase
import com.domleondev.deltabank.domain.usecase.ValidatePhoneUseCase

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


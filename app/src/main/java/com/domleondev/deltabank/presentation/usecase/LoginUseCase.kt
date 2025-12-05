package com.domleondev.deltabank.presentation.usecase

import com.domleondev.deltabank.presentation.repository.auth.AuthRepository

class LoginUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(cpf: String, loginPassword: String) = repository.loginWithCpf(cpf, loginPassword)
}
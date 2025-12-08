package com.domleondev.deltabank.domain.usecase

import com.domleondev.deltabank.repository.auth.AuthRepository

class LoginUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(cpf: String, loginPassword: String) = repository.loginWithCpf(cpf, loginPassword)
}
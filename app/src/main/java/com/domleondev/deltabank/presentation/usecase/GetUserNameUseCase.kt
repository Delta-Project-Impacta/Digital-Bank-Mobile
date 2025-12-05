package com.domleondev.deltabank.presentation.usecase

import com.domleondev.deltabank.presentation.repository.auth.AuthRepository

class GetUserNameUseCase (
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(): String? {
        return authRepository.getCurrentUserName()
    }
}
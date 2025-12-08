package com.domleondev.deltabank.domain.usecase

import com.domleondev.deltabank.repository.auth.AuthRepository

class GetUserNameUseCase (
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(): String? {
        return authRepository.getCurrentUserName()
    }
}
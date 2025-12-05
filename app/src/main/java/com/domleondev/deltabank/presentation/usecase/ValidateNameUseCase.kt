package com.domleondev.deltabank.presentation.usecase

class ValidateNameUseCase {
    private val MIN_LENGTH = 8
    operator fun invoke(nome: String): Boolean {
        val trimmedName = nome.trim()

        return trimmedName.isNotEmpty() && trimmedName.length >= MIN_LENGTH && trimmedName.contains(" ")
    }
}
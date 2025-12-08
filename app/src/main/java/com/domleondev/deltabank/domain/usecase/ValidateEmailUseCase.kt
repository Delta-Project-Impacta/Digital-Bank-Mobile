package com.domleondev.deltabank.domain.usecase

import android.util.Log
import android.util.Patterns

class ValidateEmailUseCase {

    operator fun invoke(email: String): Boolean {
        val trimmed = email.trim()

        Log.d("ValidateEmailUseCase", "Email recebido: '$email' | Email tratado: '$trimmed'")

        if (trimmed.isEmpty()) return false

        return Patterns.EMAIL_ADDRESS.matcher(trimmed).matches()
    }
}


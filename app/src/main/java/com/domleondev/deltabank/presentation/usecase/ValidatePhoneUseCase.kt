package com.domleondev.deltabank.presentation.usecase

class ValidatePhoneUseCase {

    operator fun invoke(phone: String): Boolean {
        val trimmedPhone = phone.trim()

        // Verifica se tem exatamente 15 caracteres (com a máscara)
        if (trimmedPhone.length != 15) return false

        // Verifica se o sexto caractere (índice 5) é '9'
        // Exemplo: (11) 9 8765-4321 → índice 5 = '9'
        if (trimmedPhone[5] != '9') return false

        // Verifica se há apenas dígitos e caracteres válidos
        val regex = Regex("^\\(\\d{2}\\) \\d{5}-\\d{4}$")
        return trimmedPhone.matches(regex)
    }
}
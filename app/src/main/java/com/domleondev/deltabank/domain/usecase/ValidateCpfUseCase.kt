package com.domleondev.deltabank.domain.usecase

import kotlin.text.iterator

class ValidateCpfUseCase {

    operator fun invoke(cpf: String): Boolean {
        val cleanCpf = cpf.replace(Regex("[^\\d]"), "")

        if (cleanCpf.length != 11) return false

        if (cleanCpf.all { it == cleanCpf[0] }) return false

        val firstNineDigits = cleanCpf.substring(0, 9)
        val firstVerifier = calculateVerifierDigit(firstNineDigits, 10)
        val secondVerifier = calculateVerifierDigit(firstNineDigits + firstVerifier, 11)

        val calculatedCpf = firstNineDigits + firstVerifier + secondVerifier
        return cleanCpf == calculatedCpf
    }

    private fun calculateVerifierDigit(cpfPart: String, weightStart: Int): Char {
        var sum = 0
        var weight = weightStart

        for (digitChar in cpfPart) {
            sum += (digitChar.digitToInt() * weight--)
        }

        val remainder = sum % 11
        val result = if (remainder < 2) 0 else 11 - remainder
        return result.digitToChar()
    }
}
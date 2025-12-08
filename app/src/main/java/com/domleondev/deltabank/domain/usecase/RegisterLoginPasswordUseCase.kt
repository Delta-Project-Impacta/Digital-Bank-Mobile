package com.domleondev.deltabank.domain.usecase

class RegisterLoginPasswordUseCase {

    companion object {
        private const val PASSWORDS_DONT_MATCH = "As senhas não conferem"
        private const val NOT_NUMERIC = "A senha deve conter apenas números"
        private const val INVALID_LENGTH = "A senha deve ter exatamente 6 dígitos"
        private const val SEQUENTIAL = "Senha sequencial não é permitida"
        private const val REPEATED = "Senha com todos dígitos iguais não é permitida"
        private const val BIRTHDATE = "Senha igual à data de nascimento não é permitida"
    }

    operator fun invoke(
        name: String,
        email: String,
        loginPassword: String,
        confirmPassword: String,
        birthdate: String
    ): Result<Unit> {

        if (loginPassword != confirmPassword) {
            return Result.failure(Exception(PASSWORDS_DONT_MATCH))
        }

        if (loginPassword.length != 6) {
            return Result.failure(Exception(INVALID_LENGTH))
        }

        if (!loginPassword.matches(Regex("^\\d{6}$"))) {
            return Result.failure(Exception(NOT_NUMERIC))
        }

        if (loginPassword.toSet().size == 1) {
            return Result.failure(Exception(REPEATED))
        }

        // Crescente/decrescente padrão
        val sequenciais = listOf(
            "012345", "123456", "234567", "345678", "456789",
            "987654", "876543", "765432", "654321"
        )
        if (sequenciais.contains(loginPassword)) {
            return Result.failure(Exception(SEQUENTIAL))
        }

        // pares sequenciais tipo 112233, 223344 etc
        fun isPairSequence(pwd: String): Boolean {
            if (pwd.length != 6) return false
            return (pwd[0] == pwd[1] &&
                    pwd[2] == pwd[3] &&
                    pwd[4] == pwd[5] &&
                    ((pwd[0] + 1 == pwd[2] && pwd[2] + 1 == pwd[4]) ||
                            (pwd[0] - 1 == pwd[2] && pwd[2] - 1 == pwd[4])))
        }
        if (isPairSequence(loginPassword)) {
            return Result.failure(Exception(SEQUENTIAL))
        }

        // Data de nascimento
        if (birthdate.isNotBlank()) {

            // birth no formato dd/mm/aaaa - INFERNOOOOOO, TESTAR DE NOVOOOOO
            val (day, month, year) = birthdate.split("/")
            val year2 = year.takeLast(2)

            val forbidden = listOf(
                day + month + year,   // DDMMYYYY
                day + month + year2,  // DDMMYY
                month + day + year,   // MMDDYYYY
                month + day + year2,  // MMDDYY
                day + year,           // DDAAAA
                month + year          // MMAAAA
            )

            if (loginPassword in forbidden) {
                return Result.failure(Exception(BIRTHDATE))
            }
        }

        return Result.success(Unit)
    }
}

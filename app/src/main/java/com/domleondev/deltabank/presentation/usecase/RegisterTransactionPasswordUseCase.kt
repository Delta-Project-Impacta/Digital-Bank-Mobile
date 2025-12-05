package com.domleondev.deltabank.presentation.usecase

import android.util.Log
import com.domleondev.deltabank.presentation.repository.auth.AuthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume

class RegisterTransactionPasswordUseCase(private val repository: AuthRepository) {

    companion object {
        private const val NOT_NUMERIC = "A senha deve conter apenas números"
        private const val INVALID_LENGTH = "A senha deve ter exatamente 4 dígitos"
        private const val SEQUENTIAL = "Senha sequencial não é permitida"
        private const val REPEATED = "Senha com todos dígitos iguais não é permitida"
        private const val BIRTHDATE = "Senha igual à data de nascimento não é permitida"
        private const val PASSWORDS_DONT_MATCH = "As senhas não conferem"
    }

    private fun validateTransactionPassword(
        transactionPassword: String,
        confirmTransactionPassword: String,
        birthDate: String
    ): String? {
        Log.d(
            "RegisterUseCase",
            "validateTransactionPassword called with transactionPassword=$transactionPassword, confirmTransactionPassword=$confirmTransactionPassword, birthDate=$birthDate"
        )

        /*if (transactionPassword != confirmTransactionPassword) return PASSWORDS_DONT_MATCH
        if (transactionPassword.length != 4) return INVALID_LENGTH
        if (!transactionPassword.matches(Regex("^\\d{4}$"))) return NOT_NUMERIC
        if (transactionPassword.toSet().size == 1) return REPEATED*/

        if (transactionPassword != confirmTransactionPassword) {
            Log.d("RegisterUseCase", "Validation failed: PASSWORDS_DONT_MATCH")
            return PASSWORDS_DONT_MATCH
        }
        if (transactionPassword.length != 4) {
            Log.d("RegisterUseCase", "Validation failed: INVALID_LENGTH")
            return INVALID_LENGTH
        }
        if (!transactionPassword.matches(Regex("^\\d{4}$"))) {
            Log.d("RegisterUseCase", "Validation failed: NOT_NUMERIC")
            return NOT_NUMERIC
        }
        if (transactionPassword.toSet().size == 1) {
            Log.d("RegisterUseCase", "Validation failed: REPEATED")
            return REPEATED
        }

        val sequentialList = listOf(
            "0123", "1234", "2345", "3456", "4567",
            "5678", "6789", "9876", "8765", "7654", "6543"
        )
        if (transactionPassword in sequentialList) return SEQUENTIAL

        fun isPairSequence(p: String) = p.length == 4 &&
                (p[0] == p[1] && p[2] == p[3] && (p[0] + 1 == p[2] || p[0] - 1 == p[2]))

        if (isPairSequence(transactionPassword)) return SEQUENTIAL

        if (birthDate.isNotBlank()) {
            val (day, month, year) = birthDate.split("/")
            val year2 = year.takeLast(2)
            val forbidden = listOf(day + month, month + day, day + year2, month + year2)
            if (transactionPassword in forbidden) return BIRTHDATE
        }

        return null
    }

    // Agora é suspend igual o que funciona
    suspend operator fun invoke(
        email: String,
        loginPassword: String,
        name: String,
        cpf: String,
        birthDate: String,
        transactionPassword: String,
        confirmTransactionPassword: String
    ): Result<Unit> {
        Log.d(
            "RegisterUseCase",
            "invoke called with email=$email, name=$name, cpf=$cpf, birthDate=$birthDate"
        )

        // Valida primeiro - descomentar dps
        /*validateTransactionPassword(transactionPassword, confirmTransactionPassword, birthDate)?.let {
            return Result.failure(Exception(it))
        }*/

        // Valida primeiro
        validateTransactionPassword(
            transactionPassword,
            confirmTransactionPassword,
            birthDate
        )?.let {
            Log.d("RegisterUseCase", "invoke returning failure due to validation: $it")
            return Result.failure(Exception(it))
        }

        return withContext(Dispatchers.IO) { // ⬅️ Adicione este bloco
            Log.d(
                "RegisterUseCase",
                "Validation passed, calling repository.createUser on IO Dispatcher"
            )

            // A chamada ao createUser (que contém o .await()) é feita no contexto de IO
            val result = repository.createUser(
                email,
                loginPassword,
                name,
                cpf,
                birthDate,
                transactionPassword
            )

            Log.d("RegisterUseCase", "repository.createUser returned result=$result")

            result // Retorna o resultado
        }
    }
}
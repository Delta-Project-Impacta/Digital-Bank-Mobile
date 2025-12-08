package com.domleondev.deltabank.domain.usecase

import android.util.Log
import com.domleondev.deltabank.repository.auth.AuthRepository

private val TAG = "LOGIN_USECASE"

/*class LoginUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(cpf: String, loginPassword: String) = repository.loginWithCpf(cpf, loginPassword
    )
}*/

class LoginUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(cpf: String, loginPassword: String): Result<Unit> {
        Log.d(TAG, "UseCase chamado. cpf=$cpf | password=$loginPassword")
        val r = repository.loginWithCpf(cpf, loginPassword)
        Log.d(TAG, "UseCase recebeu do Repository: $r")
        return r
    }
}

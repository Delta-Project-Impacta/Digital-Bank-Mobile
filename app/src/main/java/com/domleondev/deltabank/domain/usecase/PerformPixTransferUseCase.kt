package com.domleondev.deltabank.domain.usecase

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase

class PerformPixTransferUseCase(
    private val functions: FirebaseFunctions = Firebase.functions
) {

    private val TAG = "PerformPixTransferUC"

    data class TransferResult(
        val fromTransactionId: String,
        val toTransactionId: String,
        val newFromBalance: Double,
        val newToBalance: Double
    )

    interface Callback {
        fun onSuccess(result: TransferResult)
        fun onFailure(e: Exception)
    }

    fun execute(
        toUid: String,
        amount: Double,
        transactionPassword: String,
        description: String?,
        callback: Callback
    ) {
        Log.d(TAG, "execute -> to=$toUid amount=$amount")

        // ============================
        // 🔍 AUTH DEBUG — ESTADO LOCAL
        // ============================
        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        val functions = FirebaseFunctions.getInstance(auth.app) // ⚡ Usa o mesmo app do Auth

        if (currentUser == null) {
            Log.e(TAG, "AUTH DEBUG -> currentUser = null (SEM USUÁRIO LOGADO)")
            callback.onFailure(Exception("Usuário não autenticado"))
            return
        } else {
            Log.d(TAG, "AUTH DEBUG -> currentUser.uid=${currentUser.uid}, email=${currentUser.email}")
        }

        // ============================
        // 🔑 OBTENDO ID TOKEN ANTES DA FUNCTION
        // ============================
        currentUser.getIdToken(true)
            .addOnSuccessListener { result ->
                val token = result.token
                if (token.isNullOrBlank()) {
                    Log.e(TAG, "AUTH DEBUG -> ID TOKEN = NULL ou VAZIO")
                    callback.onFailure(Exception("ID TOKEN vazio"))
                    return@addOnSuccessListener
                } else {
                    Log.d(TAG, "AUTH DEBUG -> ID TOKEN OK (length=${token.length})")
                    Log.d(TAG, "AUTH CHECK -> authApp=${auth.app.name}, functionsApp=${functions}")

                    // ============================
                    // 🔒 VALIDAÇÕES DE NEGÓCIO
                    // ============================
                    if (toUid.isBlank()) {
                        callback.onFailure(Exception("Destinatário inválido"))
                        return@addOnSuccessListener
                    }

                    if (amount <= 0.0) {
                        callback.onFailure(Exception("Valor inválido"))
                        return@addOnSuccessListener
                    }

                    if (transactionPassword.length != 4) {
                        callback.onFailure(Exception("Senha de transação inválida"))
                        return@addOnSuccessListener
                    }

                    val data = mapOf(
                        "toUid" to toUid,
                        "amount" to amount,
                        "description" to description,
                        "transactionPassword" to transactionPassword
                    )

                    Log.d(TAG, "Calling sendPix -> toUid=$toUid amount=$amount")

                    // ============================
                    // ☁️ CHAMADA CLOUD FUNCTION
                    // ============================
                    functions
                        .getHttpsCallable("sendPix")
                        .call(data)
                        .addOnSuccessListener { result ->
                            try {
                                @Suppress("UNCHECKED_CAST")
                                val map = result.data as? Map<String, Any>
                                    ?: throw Exception("Resposta inesperada do servidor")

                                val transferResult = TransferResult(
                                    fromTransactionId = map["fromTransactionId"] as String,
                                    toTransactionId = map["toTransactionId"] as String,
                                    newFromBalance = (map["newFromBalance"] as Number).toDouble(),
                                    newToBalance = (map["newToBalance"] as Number).toDouble()
                                )

                                Log.d(TAG, "PIX realizado com sucesso -> $transferResult")
                                callback.onSuccess(transferResult)

                            } catch (e: Exception) {
                                Log.e(TAG, "Erro ao processar resposta", e)
                                callback.onFailure(e)
                            }
                        }
                        .addOnFailureListener { e ->
                            Log.e(TAG, "Erro ao chamar sendPix", e)
                            callback.onFailure(e)
                        }
                }
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "AUTH DEBUG -> ERRO ao obter ID TOKEN", e)
                callback.onFailure(e)
            }
    }
}

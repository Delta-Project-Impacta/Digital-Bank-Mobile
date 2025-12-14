package com.domleondev.deltabank.domain.usecase

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import java.security.MessageDigest

class ValidateTransactionPasswordUseCase(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    private val TAG = "ValidateTxPassUseCase"

    interface Callback {
        fun onValid()
        fun onInvalid()
        fun onError(e: Exception)
    }

    fun execute(uid: String, pin: String, callback: Callback) {
        Log.d(TAG, "execute -> uid=$uid pin=****")

        try {
            db.collection("users").document(uid)
                .get()
                .addOnSuccessListener { doc ->
                    if (!doc.exists()) {
                        Log.w(TAG, "Usuário não encontrado uid=$uid")
                        callback.onError(Exception("Usuário não encontrado"))
                        return@addOnSuccessListener
                    }

                    val storedHash = doc.getString("transactionPassword") ?: ""
                    Log.d(TAG, "Senha recuperada (oculta) length=${storedHash.length}")

                    if (storedHash.isBlank()) {
                        Log.w(TAG, "transactionPassword vazio para uid=$uid")
                        callback.onInvalid()
                        return@addOnSuccessListener
                    }

                    // calcula sha256 do PIN digitado
                    val computed = sha256Hex(pin)
                    Log.d(TAG, "Computed hash length=${computed.length}")

                    if (computed.equals(storedHash, ignoreCase = true)) {
                        Log.d(TAG, "Validação de senha: OK")
                        callback.onValid()
                    } else {
                        Log.w(TAG, "Validação de senha: inválida")
                        callback.onInvalid()
                    }
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Erro ao buscar transactionPassword", e)
                    callback.onError(e)
                }
        } catch (e: Exception) {
            Log.e(TAG, "Erro inesperado", e)
            callback.onError(e)
        }
    }

    private fun sha256Hex(input: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        val bytes = md.digest(input.toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }
}

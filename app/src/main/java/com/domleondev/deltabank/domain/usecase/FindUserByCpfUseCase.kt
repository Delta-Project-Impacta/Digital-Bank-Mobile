package com.domleondev.deltabank.domain.usecase

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore

data class RecipientResult(
    val uid: String,
    val name: String,
    val cpf: String
)

class FindUserByCpfUseCase(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    private val TAG = "FindUserByCpfUseCase"

    interface Callback {
        fun onSuccess(recipient: RecipientResult)
        fun onNotFound()
        fun onError(e: Exception)
    }

    fun execute(cpf: String, callback: Callback) {
        Log.d(TAG, "execute: procurando CPF = $cpf")

        if (cpf.isBlank()) {
            Log.w(TAG, "execute: CPF vazio")
            callback.onNotFound()
            return
        }

        db.collection("users")
            .whereEqualTo("cpf", cpf)
            .limit(1)
            .get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.isEmpty) {
                    Log.d(TAG, "execute: nenhum usuário encontrado para cpf=$cpf")
                    callback.onNotFound()
                    return@addOnSuccessListener
                }

                val doc = snapshot.documents[0]
                val uid = doc.id
                val name = doc.getString("name") ?: ""
                val foundCpf = doc.getString("cpf") ?: ""

                Log.d(TAG, "execute: usuário encontrado -> uid=$uid name=$name cpf=$foundCpf")
                callback.onSuccess(RecipientResult(uid = uid, name = name, cpf = foundCpf))
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "execute: erro querying users by cpf", e)
                callback.onError(e)
            }
    }
}

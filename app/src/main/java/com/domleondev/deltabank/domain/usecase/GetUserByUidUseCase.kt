package com.domleondev.deltabank.domain.usecase

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore

class GetUserByUidUseCase(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    private val TAG = "GetUserByUidUseCase"

    interface Callback {
        fun onSuccess(name: String, cpf: String?)
        fun onNotFound()
        fun onError(e: Exception)
    }

    fun execute(uid: String, callback: Callback) {
        Log.d(TAG, "execute -> uid=$uid")

        if (uid.isBlank()) {
            Log.w(TAG, "execute -> uid vazio")
            callback.onNotFound()
            return
        }

        db.collection("users").document(uid)
            .get()
            .addOnSuccessListener { doc ->
                if (!doc.exists()) {
                    Log.d(TAG, "execute -> doc não existe uid=$uid")
                    callback.onNotFound()
                    return@addOnSuccessListener
                }
                val name = doc.getString("name") ?: ""
                val cpf = doc.getString("cpf")
                Log.d(TAG, "execute -> encontrado nome=$name cpf=$cpf")
                callback.onSuccess(name, cpf)
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "execute -> erro ao buscar usuário", e)
                callback.onError(e)
            }
    }
}

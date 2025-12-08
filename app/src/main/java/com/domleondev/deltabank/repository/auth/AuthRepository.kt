package com.domleondev.deltabank.repository.auth

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import java.security.MessageDigest
import kotlin.coroutines.resume


class AuthRepository(private val auth: FirebaseAuth) {

    private val db = FirebaseFirestore.getInstance()

    fun getCurrentUserName(): String? {
        val name = auth.currentUser?.displayName
        return name

    }

    /**
     * CRIA USUÁRIO: email + loginPassword + CPF + transactionPassword
     */
    suspend fun createUser(
        email: String,
        loginPassword: String,
        name: String,
        cpf: String,
        birthDate: String,
        transactionPassword: String
    ): Result<Unit> {
        return try {

            // 1. Cria usuário e espera a conclusão
            auth.createUserWithEmailAndPassword(email, loginPassword).await()

            val user = auth.currentUser
            if (user == null) {
                return Result.failure(Exception("Usuário não encontrado após criação."))
            }

            // 2. Atualiza displayName e espera o retorno
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(name).build()
            user.updateProfile(profileUpdates).await()

            fun hash(input: String): String {
                return MessageDigest.getInstance("SHA-256")
                    .digest(input.toByteArray())
                    .joinToString("") { "%02x".format(it) }
            }
            // 3. Salva dados no Firestore e espera o retorno
            val userData = hashMapOf(
                "uid" to user.uid,
                "name" to name,
                "cpf" to cpf,
                "birthDate" to birthDate,
                "email" to email,
                "loginPassword" to hash(loginPassword),
                "transactionPassword" to hash(transactionPassword)
            )

            db.collection("users").document(user.uid)
                .set(userData)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    /**
     * LOGIN POR CPF + SENHA DE 6 DÍGITOS
     */
    suspend fun loginWithCpf(cpf: String, loginPassword: String): Result<Unit> {
        return try {
            val query = db.collection("users")
                .whereEqualTo("cpf", cpf)
                .get()
                .await()

            if (query.isEmpty) return Result.failure(Exception("Credenciais inválidas"))

            val email = query.documents[0].getString("email")
                ?: return Result.failure(Exception("Credenciais inválidas"))

            suspendCancellableCoroutine<Result<Unit>> { cont ->
                auth.signInWithEmailAndPassword(email, loginPassword)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) cont.resume(Result.success(Unit))
                        else cont.resume(Result.failure(Exception("Credenciais inválidas")))
                    }
            }
        } catch (e: Exception) {
            Result.failure(Exception("Credenciais inválidas"))
        }
    }

    /**
     * RETORNA CPF + TRANSACTION PASSWORD DO USUÁRIO ATUAL
     */
    suspend fun getUserByUid(uid: String): Map<String, String>? {
        val doc = db.collection("users").document(uid).get().await()
        if (!doc.exists()) return null
        return mapOf(
            "cpf" to (doc.getString("cpf") ?: ""),
            "transactionPassword" to (doc.getString("transactionPassword") ?: "")
        )
    }
}


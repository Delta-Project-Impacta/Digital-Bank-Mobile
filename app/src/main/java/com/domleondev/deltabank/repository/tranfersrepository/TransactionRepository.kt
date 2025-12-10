package com.domleondev.deltabank.repository.tranfersrepository

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class TransactionRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    fun addTransaction(
        userId: String,
        type: String,
        amount: Double,
        description: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val transactionData = hashMapOf(
            "type" to type,               // "deposit", "pix_sent", "pix_received", "card_purchase"
            "amount" to amount,
            "description" to description,
            "timestamp" to FieldValue.serverTimestamp()
        )

        db.collection("users")
            .document(userId)
            .collection("transactions")
            .add(transactionData)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }
}

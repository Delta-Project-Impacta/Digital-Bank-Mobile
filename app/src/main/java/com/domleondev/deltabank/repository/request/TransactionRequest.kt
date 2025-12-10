package com.domleondev.deltabank.repository.request
import com.google.firebase.Timestamp

data class TransactionRequest(
    val id: String = "",
    val amount: Double = 0.0,
    val description: String = "",
    val timestamp: Timestamp? = null,
    val type: String = ""
)



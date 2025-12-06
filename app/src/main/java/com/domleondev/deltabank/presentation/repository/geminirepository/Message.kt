package com.domleondev.deltabank.presentation.repository.geminirepository

data class Message(
    val text: String,
    val isSentByUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)
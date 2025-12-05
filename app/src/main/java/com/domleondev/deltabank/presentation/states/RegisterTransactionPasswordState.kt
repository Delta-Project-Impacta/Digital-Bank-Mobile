package com.domleondev.deltabank.presentation.states

sealed class RegisterTransactionPasswordState {
    object Idle : RegisterTransactionPasswordState()
    object Loading : RegisterTransactionPasswordState()
    object Success : RegisterTransactionPasswordState()
    data class Error(val message: String) : RegisterTransactionPasswordState()
}

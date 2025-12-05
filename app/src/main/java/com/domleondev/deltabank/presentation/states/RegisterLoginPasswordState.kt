package com.domleondev.deltabank.presentation.states

sealed class RegisterLoginPasswordState {
    object Idle : RegisterLoginPasswordState()
    object Loading : RegisterLoginPasswordState()
    data class Success(val birth: String) : RegisterLoginPasswordState()
    data class Error(val message: String) : RegisterLoginPasswordState()
}
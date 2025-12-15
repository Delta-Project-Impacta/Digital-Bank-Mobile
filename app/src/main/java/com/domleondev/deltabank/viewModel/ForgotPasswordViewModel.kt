package com.domleondev.deltabank.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class ForgotPasswordViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()

    // Estados para a Activity observar
    private val _resetResult = MutableLiveData<Boolean>()
    val resetResult: LiveData<Boolean> = _resetResult

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun sendPasswordResetEmail(email: String) {
        if (email.isBlank()) {
            _errorMessage.value = "Por favor, digite seu e-mail."
            return
        }

        _isLoading.value = true

        // Chama o Firebase
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                _isLoading.value = false
                if (task.isSuccessful) {
                    _resetResult.value = true
                } else {
                    _resetResult.value = false
                    _errorMessage.value = task.exception?.message ?: "Erro ao enviar e-mail."
                }
            }
    }
}
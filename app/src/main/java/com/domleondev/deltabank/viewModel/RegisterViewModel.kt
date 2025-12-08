package com.domleondev.deltabank.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.domleondev.deltabank.domain.usecase.ValidateBirthDateUseCase
import com.domleondev.deltabank.domain.usecase.ValidateCpfUseCase
import com.domleondev.deltabank.domain.usecase.ValidateEmailUseCase
import com.domleondev.deltabank.domain.usecase.ValidateNameUseCase
import com.domleondev.deltabank.domain.usecase.ValidatePhoneUseCase
import com.google.firebase.firestore.FirebaseFirestore


class RegisterViewModel(
    private val validateName: ValidateNameUseCase,
    private val validateCpf: ValidateCpfUseCase,
    private val validateBirth: ValidateBirthDateUseCase,
    private val validateEmail: ValidateEmailUseCase,
    private val validatePhone: ValidatePhoneUseCase,
) : ViewModel() {

    private val db = FirebaseFirestore.getInstance()

    private val _validationState = MutableLiveData<ValidationResult>()
    val validationState: LiveData<ValidationResult> = _validationState

    private val _firebaseCheckState = MutableLiveData<FirebaseCheckResult>()
    val firebaseCheckState: LiveData<FirebaseCheckResult> = _firebaseCheckState

    fun validateFields(
        name: String,
        cpf: String,
        birth: String,
        email: String,
        confirmEmail: String,
        phone: String
    ) {
        if (!validateName(name)) {
            _validationState.value = ValidationResult.Error("invalid_name")
            return
        }
        if (!validateCpf(cpf)) {
            _validationState.value = ValidationResult.Error("invalid_cpf")
            return
        }
        if (!validateBirth(birth)) {
            _validationState.value = ValidationResult.Error("invalid_birth")
            return
        }
        if (!validateEmail(email)) {
            _validationState.value = ValidationResult.Error("invalid_email")
            return
        }
        if (email != confirmEmail) {
            _validationState.value = ValidationResult.Error("email_not_match")
            return
        }
        if (!validatePhone(phone)) {
            _validationState.value = ValidationResult.Error("invalid_phone")
            return
        }

        _validationState.value = ValidationResult.Success
    }

    fun checkCpfAndEmailInFirebase(cpf: String, email: String) {
        db.collection("users")
            .whereEqualTo("cpf", cpf)
            .get()
            .addOnSuccessListener { cpfQuery ->
                if (!cpfQuery.isEmpty) {
                    _firebaseCheckState.value = FirebaseCheckResult.CpfExists
                    return@addOnSuccessListener
                }

                db.collection("users")
                    .whereEqualTo("email", email)
                    .get()
                    .addOnSuccessListener { emailQuery ->
                        if (!emailQuery.isEmpty) {
                            _firebaseCheckState.value = FirebaseCheckResult.EmailExists
                        } else {
                            _firebaseCheckState.value = FirebaseCheckResult.Ok
                        }
                    }
                    .addOnFailureListener {
                        _firebaseCheckState.value = FirebaseCheckResult.Error
                    }
            }
            .addOnFailureListener {
                _firebaseCheckState.value = FirebaseCheckResult.Error
            }
    }
}


sealed class ValidationResult {
    data object Success : ValidationResult()
    data class Error(val errorKey: String) : ValidationResult()
}

sealed class FirebaseCheckResult {
    data object Ok : FirebaseCheckResult()
    data object CpfExists : FirebaseCheckResult()
    data object EmailExists : FirebaseCheckResult()
    data object Error : FirebaseCheckResult()
}



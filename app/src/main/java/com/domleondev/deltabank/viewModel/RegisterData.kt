package com.domleondev.deltabank.viewModel

data class RegisterData(
    val fullName: String,
    val cpf: String,
    val email: String,
    val phone: String,
    val birthDate: String,

    var street: String? = null,
    var number: String? = null,
    var neighborhood: String? = null,
    var city: String? = null,
    var state: String? = null,
    var cep: String? = null,

    var loginPassword: String? = null,
    var transactionPassword: String? = null
)


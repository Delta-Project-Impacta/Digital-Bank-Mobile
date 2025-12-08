package com.domleondev.deltabank.repository.response

data class ViaCepResponse(
    val cep: String?,
    val logradouro: String?,
    val bairro: String?,
    val uf: String?,
    val localidade: String?

)
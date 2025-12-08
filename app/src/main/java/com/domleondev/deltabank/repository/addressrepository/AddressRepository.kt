package com.domleondev.deltabank.repository.addressrepository

import com.domleondev.deltabank.repository.response.StateResponse
import com.domleondev.deltabank.repository.response.ViaCepResponse
import com.domleondev.deltabank.repository.response.CityResponse

class AddressRepository {

    private val viaCepApi = RetrofitClient.viaCepRetrofit
    private val ibgeApi = RetrofitClient.ibgeRetrofit

    suspend fun buscarCep(cep: String): ViaCepResponse? {
        val response = viaCepApi.getEndereco(cep)
        return if (response.isSuccessful) response.body() else null
    }

    suspend fun buscarEstados(): List<StateResponse>? {
        val response = ibgeApi.getEstados()
        return if (response.isSuccessful) response.body() else null
    }

    suspend fun buscarCidades(uf: String): List<CityResponse>? {
        val response = ibgeApi.getCidades(uf)
        return if (response.isSuccessful) response.body() else null
    }
}
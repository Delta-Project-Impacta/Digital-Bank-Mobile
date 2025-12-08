package com.domleondev.deltabank.repository.addressrepository

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import com.domleondev.deltabank.repository.response.ViaCepResponse
interface ViaCepApi {
    @GET("{cep}/json/")
    suspend fun getEndereco(@Path("cep") cep: String): Response<ViaCepResponse>
}
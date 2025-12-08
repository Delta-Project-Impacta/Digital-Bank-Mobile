package com.domleondev.deltabank.repository.addressrepository

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import com.domleondev.deltabank.repository.response.CityResponse
import com.domleondev.deltabank.repository.response.StateResponse

interface IbgeApi {
    @GET("estados")
    suspend fun getEstados(): Response<List<StateResponse>>

    @GET("estados/{uf}/municipios")
    suspend fun getCidades(@Path("uf") uf: String): Response<List<CityResponse>>
}
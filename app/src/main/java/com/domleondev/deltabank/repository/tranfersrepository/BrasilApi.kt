package com.domleondev.deltabank.repository.tranfersrepository

import com.domleondev.deltabank.repository.response.TranfersResponse
import retrofit2.Response
import retrofit2.http.GET

interface BrasilApi {

    // Lista de bancos
    @GET("banks/v1")
    suspend fun getBancos(): Response<List<TranfersResponse>>
}
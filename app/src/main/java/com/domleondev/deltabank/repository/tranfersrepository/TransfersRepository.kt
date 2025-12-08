package com.domleondev.deltabank.repository.tranfersrepository

import com.domleondev.deltabank.repository.response.TranfersResponse

class TransfersRepository {
    private val brasilApi = RetrofitInstance.brasilApiRetrofit

    suspend fun seekBanks(): List<TranfersResponse>? {
        val response = brasilApi.getBancos()
        return if (response.isSuccessful) response.body() else null
    }
}
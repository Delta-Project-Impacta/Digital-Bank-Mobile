package com.domleondev.deltabank.presentation.repository.addressrepository

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    val viaCepRetrofit: ViaCepApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://viacep.com.br/ws/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ViaCepApi::class.java)
    }

    val ibgeRetrofit: IbgeApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://servicodados.ibge.gov.br/api/v1/localidades/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(IbgeApi::class.java)
    }
}
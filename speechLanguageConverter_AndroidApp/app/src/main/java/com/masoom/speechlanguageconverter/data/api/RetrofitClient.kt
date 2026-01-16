package com.masoom.speechlanguageconverter.data.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.getValue

object RetrofitClient {

    private const val BASE_URL = "http://192.168.1.2:3000/"
//    private const val BASE_URL = "http://172.23.100.118:3000/"

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api: ApiInterface by lazy {
        retrofit.create(ApiInterface::class.java)
    }
}
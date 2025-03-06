package com.example.appnews.core.network

import com.example.appnews.register.data.datasource.RegisterService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitHelper {

    private const val BASE_URL = "http://3.209.201.129:3000"

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun <T> createService(service: Class<T>): T {
        return retrofit.create(service)
    }

    val registerService: RegisterService by lazy {
        retrofit.create(RegisterService::class.java)
    }

}
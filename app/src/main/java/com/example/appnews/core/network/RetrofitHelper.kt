package com.example.appnews.core.network

import android.content.Context
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitHelper {
    private const val BASE_URL = "http://3.209.201.129:3000"

    // Método para crear un cliente OkHttp con un token de autorización
    private fun createAuthenticatedClient(context: Context): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor { chain ->
                // Obtener el token de las SharedPreferences
                val sharedPrefs = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
                val authToken = sharedPrefs.getString("auth_token", null)

                val originalRequest = chain.request()
                val newRequest = authToken?.let { token ->
                    originalRequest.newBuilder()
                        .addHeader("Authorization", "Bearer $token")
                        .build()
                } ?: originalRequest

                chain.proceed(newRequest)
            }
            .build()
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun <T> createService(service: Class<T>): T {
        return retrofit.create(service)
    }

    // Método para crear un servicio con autenticación
    fun <T> createAuthenticatedService(
        context: Context,
        service: Class<T>
    ): T {
        val authenticatedRetrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(createAuthenticatedClient(context))
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return authenticatedRetrofit.create(service)
    }

    // Método para guardar el token en SharedPreferences
    fun saveAuthToken(context: Context, token: String) {
        val sharedPrefs = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        sharedPrefs.edit().putString("auth_token", token).apply()
    }

    // Método para obtener el token de SharedPreferences
    fun getAuthToken(context: Context): String? {
        val sharedPrefs = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        return sharedPrefs.getString("auth_token", null)
    }
}
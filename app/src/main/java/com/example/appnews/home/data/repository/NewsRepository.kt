package com.example.appnews.home.data.repository

import android.content.Context
import com.example.appnews.core.network.RetrofitHelper
import com.example.appnews.home.data.datasource.NewsService
import com.example.appnews.home.data.model.NewsDTO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class NewsRepository(
    private val context: Context
) {
    // Obtener el servicio de noticias usando el método de autenticación
    private val newsService = RetrofitHelper.createAuthenticatedService(context, NewsService::class.java)

    // Obtener lista de noticias
    suspend fun getNews(): Result<List<NewsDTO>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = newsService.getNews()
                if (response.isSuccessful) {
                    Result.success(response.body() ?: emptyList())
                } else {
                    Result.failure(Exception(response.errorBody()?.string() ?: "Error desconocido"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}
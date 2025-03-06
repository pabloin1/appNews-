package com.example.appnews.createnews.data.repository

import android.content.Context
import com.example.appnews.core.network.RetrofitHelper
import com.example.appnews.createnews.data.datasource.CreateNewsService
import com.example.appnews.createnews.data.model.CreateNewsRequest
import com.example.appnews.createnews.data.model.CreateNewsResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CreateNewsRepository(
    private val context: Context
) {
    // Crear un servicio autenticado para la creación de noticias
    private val createNewsService = RetrofitHelper.createAuthenticatedService(context, CreateNewsService::class.java)

    // Crear una nueva noticia
    suspend fun createNews(title: String, content: String): Result<CreateNewsResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val request = CreateNewsRequest(title, content)
                val response = createNewsService.createNews(request)

                if (response.isSuccessful) {
                    val newsResponse = response.body()
                    if (newsResponse != null) {
                        Result.success(newsResponse)
                    } else {
                        Result.failure(Exception("No se pudo crear la noticia"))
                    }
                } else {
                    Result.failure(Exception(response.errorBody()?.string() ?: "Error al crear la noticia"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}
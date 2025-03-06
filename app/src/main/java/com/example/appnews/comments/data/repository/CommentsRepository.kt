package com.example.appnews.comments.data.repository

import android.content.Context
import com.example.appnews.comments.data.datasource.CommentsService
import com.example.appnews.comments.data.model.CommentDTO
import com.example.appnews.comments.data.model.CreateCommentRequest
import com.example.appnews.core.network.RetrofitHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CommentsRepository(
    private val context: Context
) {
    // Crear el servicio autenticado para comentarios
    private val commentsService = RetrofitHelper.createAuthenticatedService(context, CommentsService::class.java)

    // Obtener comentarios por ID de noticia
    suspend fun getCommentsByNewsId(newsId: String): Result<List<CommentDTO>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = commentsService.getCommentsByNewsId(newsId)
                if (response.isSuccessful) {
                    Result.success(response.body() ?: emptyList())
                } else {
                    Result.failure(Exception(response.errorBody()?.string() ?: "Error al obtener comentarios"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    // Crear un nuevo comentario
    suspend fun createComment(newsId: String, comment: String): Result<CommentDTO> {
        return withContext(Dispatchers.IO) {
            try {
                val request = CreateCommentRequest(newsId, comment)
                val response = commentsService.createComment(request)
                if (response.isSuccessful) {
                    val commentDTO = response.body()
                    if (commentDTO != null) {
                        Result.success(commentDTO)
                    } else {
                        Result.failure(Exception("No se pudo crear el comentario"))
                    }
                } else {
                    Result.failure(Exception(response.errorBody()?.string() ?: "Error al crear comentario"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}
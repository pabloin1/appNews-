package com.example.appnews.comments.data.datasource

import com.example.appnews.comments.data.model.CommentDTO
import com.example.appnews.comments.data.model.CommentResponse
import com.example.appnews.comments.data.model.CreateCommentRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface CommentsService {
    @GET("api/comments/news/{newsId}")
    suspend fun getCommentsByNewsId(@Path("newsId") newsId: String): Response<List<CommentDTO>>

    @POST("api/comments")
    suspend fun createComment(@Body request: CreateCommentRequest): Response<CommentDTO>
}
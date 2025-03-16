package com.example.appnews.createnews.data.datasource

import com.example.appnews.createnews.data.model.CreateNewsRequest
import com.example.appnews.createnews.data.model.CreateNewsResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface CreateNewsService {
    @POST("api/news")
    suspend fun createNews(@Body request: CreateNewsRequest): Response<CreateNewsResponse>
}
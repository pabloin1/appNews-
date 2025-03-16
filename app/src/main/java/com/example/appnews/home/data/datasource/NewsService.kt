package com.example.appnews.home.data.datasource

import com.example.appnews.home.data.model.NewsDTO
import retrofit2.Response
import retrofit2.http.GET

interface NewsService {
    @GET("api/news")
    suspend fun getNews(): Response<List<NewsDTO>>
}
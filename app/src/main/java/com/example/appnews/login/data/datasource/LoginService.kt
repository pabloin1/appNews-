package com.example.appnews.login.data.datasource

import com.example.appnews.login.data.model.LoginRequest
import com.example.appnews.register.data.model.UserDTO
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginService {
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<UserDTO>
}
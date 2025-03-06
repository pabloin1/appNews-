package com.example.appnews.register.data.datasource

import com.example.appnews.register.data.model.CreateUserRequest
import com.example.appnews.register.data.model.UserDTO
import com.example.appnews.register.data.model.UsernameValidateDTO
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface RegisterService {

    @POST("api/auth/register")
    suspend fun createUser(@Body request: CreateUserRequest): Response<UserDTO>

}
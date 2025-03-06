package com.example.appnews.login.data.repository

import com.example.appnews.core.network.RetrofitHelper
import com.example.appnews.login.data.datasource.LoginService
import com.example.appnews.login.data.model.LoginRequest
import com.example.appnews.register.data.model.UserDTO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LoginRepository {
    private val loginService = RetrofitHelper.createService(LoginService::class.java)

    suspend fun login(request: LoginRequest): Result<UserDTO> {
        return withContext(Dispatchers.IO) {
            try {
                val response = loginService.login(request)
                if (response.isSuccessful) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception(response.errorBody()?.string() ?: "Error de inicio de sesión"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}
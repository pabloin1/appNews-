package com.example.appnews.register.data.repository


import com.example.appnews.register.data.model.CreateUserRequest
import com.example.appnews.register.data.model.UserDTO
import com.example.appnews.register.data.model.UsernameValidateDTO
import com.example.appnews.core.network.RetrofitHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RegisterRepository {

    // Obtener el servicio de registro desde RetrofitHelper
    private val registerService = RetrofitHelper.registerService


    // Crear un nuevo usuario
    suspend fun createUser(request: CreateUserRequest): Result<UserDTO> {
        return withContext(Dispatchers.IO) {
            try {
                val response = registerService.createUser(request)
                if (response.isSuccessful) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception(response.errorBody()?.string()))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}


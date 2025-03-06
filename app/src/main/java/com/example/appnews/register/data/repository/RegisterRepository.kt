package com.example.appnews.register.data.repository

import android.content.Context
import com.example.appnews.register.data.datasource.RegisterService
import com.example.appnews.register.data.model.CreateUserRequest
import com.example.appnews.register.data.model.UserDTO
import com.example.appnews.core.network.RetrofitHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RegisterRepository(
    private val context: Context
) {
    // Obtener el servicio de registro usando createService
    private val registerService = RetrofitHelper.createService(RegisterService::class.java)

    // Crear un nuevo usuario
    suspend fun createUser(request: CreateUserRequest): Result<UserDTO> {
        return withContext(Dispatchers.IO) {
            try {
                val response = registerService.createUser(request)
                if (response.isSuccessful) {
                    Result.success(response.body()!!)
                } else {
                    // Intentar obtener el mensaje de error del cuerpo de la respuesta
                    val errorBody = response.errorBody()?.string() ?: "Error desconocido al registrar usuario"
                    Result.failure(Exception(errorBody))
                }
            } catch (e: Exception) {
                Result.failure(Exception("Error de red: ${e.message}"))
            }
        }
    }
}
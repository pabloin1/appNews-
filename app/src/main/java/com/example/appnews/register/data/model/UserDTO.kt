package com.example.appnews.register.data.model


data class UserDTO(
    val id: String,
    val name: String,
    val email: String,
    val fcmToken: String,
    val token: String // Agregar este campo
)

package com.example.appnews.register.data.model

data class CreateUserRequest(
    val name: String,
    val email: String,
    val password: String,
    val fcmToken: String
)

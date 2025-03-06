package com.example.appnews.register.data.model

data class UsernameValidateDTO(
    val isAvailable: Boolean,
    val message: String? = null
)

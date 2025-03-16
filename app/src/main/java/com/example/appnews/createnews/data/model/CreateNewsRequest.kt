package com.example.appnews.createnews.data.model

import com.google.gson.annotations.SerializedName

data class CreateNewsRequest(
    val title: String,
    val content: String
)

data class CreateNewsResponse(
    @SerializedName("_id") val id: String = "",
    @SerializedName("userId") val userId: String = "",
    val title: String = "",
    val content: String = "",
    @SerializedName("publicationDate") val publicationDate: String = "",
    val createdAt: String = "",
    val updatedAt: String = "",
    @SerializedName("__v") val version: Int = 0
)
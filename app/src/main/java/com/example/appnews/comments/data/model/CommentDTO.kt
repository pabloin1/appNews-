package com.example.appnews.comments.data.model

import com.google.gson.annotations.SerializedName

data class CommentDTO(
    @SerializedName("_id") val id: String = "",
    @SerializedName("newsId") val newsId: String = "",
    @SerializedName("userId") val userId: String = "",
    @SerializedName("userName") val userName: String = "Usuario",
    val comment: String = "",
    val createdAt: String = "",
    val updatedAt: String = "",
    @SerializedName("__v") val version: Int = 0
)

data class CreateCommentRequest(
    val newsId: String,
    val comment: String
)

data class CommentResponse(
    val comments: List<CommentDTO> = emptyList()
)
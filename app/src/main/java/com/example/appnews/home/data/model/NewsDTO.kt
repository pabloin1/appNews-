package com.example.appnews.home.data.model

import com.example.appnews.core.data.local.news.entities.NewsEntity
import com.google.gson.annotations.SerializedName

data class NewsDTO(
    @SerializedName("_id") val id: String,
    @SerializedName("userId") val userId: String,
    val title: String,
    val content: String,
    @SerializedName("publicationDate") val publicationDate: String,
    val createdAt: String,
    val updatedAt: String,
    @SerializedName("__v") val version: Int
)

// Función de extensión para convertir NewsDTO a NewsEntity
fun NewsDTO.toNewsEntity(): NewsEntity {
    return NewsEntity(
        id = id,
        title = title,
        content = content,
        publicationDate = publicationDate,
        userId = userId,
        createdAt = createdAt,
        updatedAt = updatedAt,
        version = version,
        isDownloaded = false // Asegúrate de que el nombre del campo sea correcto
    )
}
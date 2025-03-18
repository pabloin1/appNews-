package com.example.appnews.core.data.local.news.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.appnews.home.data.model.NewsDTO

@Entity(tableName = "news")
data class NewsEntity(
    @PrimaryKey val id: String,
    val title: String,
    val content: String,
    val publicationDate: String,
    val userId: String,
    val createdAt: String,
    val updatedAt: String,
    val version: Int,
    val isDownloaded: Boolean = false
) {
    // Función para convertir NewsEntity a NewsDTO
    fun toNewsDTO(): NewsDTO {
        return NewsDTO(
            id = id,
            userId = userId,
            title = title,
            content = content,
            publicationDate = publicationDate,
            createdAt = createdAt,
            updatedAt = updatedAt,
            version = version
        )
    }
}

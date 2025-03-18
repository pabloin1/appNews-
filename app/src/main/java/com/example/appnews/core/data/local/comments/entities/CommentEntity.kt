package com.example.appnews.core.data.local.comments.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.appnews.core.data.local.news.entities.NewsEntity

@Entity(
    tableName = "comments",
    foreignKeys = [
        ForeignKey(
            entity = NewsEntity::class,
            parentColumns = ["id"],
            childColumns = ["news_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["news_id"])]  // Añadimos un índice para la columna news_id
)
data class CommentEntity(
    @PrimaryKey
    @ColumnInfo(name = "_id")
    val id: String,
    @ColumnInfo(name = "news_id")
    val newsId: String,
    @ColumnInfo(name = "user_id")
    val userId: String,
    @ColumnInfo(name = "user_name")
    val userName: String = "",
    @ColumnInfo(name = "user_email")
    val userEmail: String = "",
    val comment: String,
    val createdAt: String,
    val updatedAt: String
)
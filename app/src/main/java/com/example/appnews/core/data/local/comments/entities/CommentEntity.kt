package com.example.appnews.core.data.local.comments.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.appnews.data.local.news.entities.NewsEntity

@Entity(
    tableName = "comments",
    foreignKeys = [
        ForeignKey(
            entity = NewsEntity::class,
            parentColumns = ["id"],  // Changed from "_id" to "id"
            childColumns = ["news_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
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
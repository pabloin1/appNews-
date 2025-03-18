package com.example.appnews.core.data.local.news.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.example.appnews.core.data.local.comments.entities.CommentEntity
import com.example.appnews.core.data.local.news.entities.NewsEntity

data class NewsWithComments(
    @Embedded val news: NewsEntity,
    @Relation(
        parentColumn = "id",  // Asegúrate que coincida con el nombre de columna en NewsEntity
        entityColumn = "news_id"
    )
    val comments: List<CommentEntity>
)
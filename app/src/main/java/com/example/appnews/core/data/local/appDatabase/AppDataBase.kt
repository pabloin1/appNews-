package com.example.appnews.core.data.local.appDatabase

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.appnews.core.data.local.comments.dao.CommentDao
import com.example.appnews.core.data.local.comments.entities.CommentEntity
import com.example.appnews.core.data.local.news.dao.NewsDao
import com.example.appnews.core.data.local.news.entities.NewsEntity

@Database(
    entities = [
        NewsEntity::class,
        CommentEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun newsDao(): NewsDao
    abstract fun commentDao(): CommentDao
}
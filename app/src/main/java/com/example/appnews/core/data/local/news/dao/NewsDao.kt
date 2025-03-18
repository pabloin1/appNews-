package com.example.appnews.core.data.local.news.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.appnews.core.data.local.news.entities.NewsEntity

@Dao
interface NewsDao {
    @Query("SELECT * FROM news")
    suspend fun getAllNews(): List<NewsEntity>

    @Query("SELECT * FROM news WHERE isDownloaded = 1")
    suspend fun getDownloadedNews(): List<NewsEntity>

    @Query("SELECT * FROM news WHERE id = :newsId LIMIT 1")
    suspend fun getNewsById(newsId: String): NewsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNews(news: List<NewsEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNews(news: NewsEntity)

    @Update
    suspend fun updateNews(news: NewsEntity)

    @Query("UPDATE news SET isDownloaded = :isDownloaded WHERE id = :newsId")
    suspend fun updateDownloadStatus(newsId: String, isDownloaded: Boolean)

    @Query("DELETE FROM news WHERE isDownloaded = 0")
    suspend fun deleteNonDownloadedNews()
}
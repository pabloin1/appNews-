package com.example.appnews.core.data.local.news.repositories

import com.example.appnews.core.data.local.news.dao.NewsDao
import com.example.appnews.core.data.local.news.entities.NewsEntity

class NewsRepository(
    private val newsDao: NewsDao
) {

    // Obtener todas las noticias
    suspend fun getAllNews(): List<NewsEntity> {
        return newsDao.getAllNews()
    }

    // Obtener noticias descargadas
    suspend fun getDownloadedNews(): List<NewsEntity> {
        return newsDao.getDownloadedNews()
    }

    // Insertar noticias
    suspend fun insertNews(news: List<NewsEntity>) {
        newsDao.insertNews(news)
    }

    // Actualizar el estado de descarga de una noticia
    suspend fun updateDownloadStatus(newsId: String, isDownloaded: Boolean) {
        newsDao.updateDownloadStatus(newsId, isDownloaded)
    }

    // Eliminar noticias no descargadas
    suspend fun deleteNonDownloadedNews() {
        newsDao.deleteNonDownloadedNews()
    }
}
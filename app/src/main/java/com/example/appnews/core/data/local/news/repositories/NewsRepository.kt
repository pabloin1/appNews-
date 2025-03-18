package com.example.appnews.core.data.local.news.repositories

import com.example.appnews.core.data.local.news.dao.NewsDao
import com.example.appnews.core.data.local.news.entities.NewsEntity
import android.util.Log

class NewsRepository(
    private val newsDao: NewsDao
) {

    // Obtener todas las noticias
    suspend fun getAllNews(): List<NewsEntity> {
        val allNews = newsDao.getAllNews()
        Log.d("LocalNewsRepository", "Todas las noticias obtenidas: ${allNews.size}")
        return allNews
    }

    // Obtener noticias descargadas
    suspend fun getDownloadedNews(): List<NewsEntity> {
        val news = newsDao.getDownloadedNews()
        Log.d("LocalNewsRepository", "Noticias descargadas obtenidas: ${news.size}")
        if (news.isNotEmpty()) {
            Log.d("LocalNewsRepository", "Primera noticia descargada: ID=${news[0].id}, título=${news[0].title}")
        }
        return news
    }

    // Obtener una noticia por su ID
    suspend fun getNewsById(newsId: String): NewsEntity? {
        val news = newsDao.getNewsById(newsId)
        Log.d("LocalNewsRepository", "Buscando noticia por ID: $newsId - ${if (news != null) "Encontrada" else "No encontrada"}")
        return news
    }

    // Insertar noticia individual
    suspend fun insertNews(news: NewsEntity) {
        Log.d("LocalNewsRepository", "Insertando noticia: ID=${news.id}, título=${news.title}, isDownloaded=${news.isDownloaded}")
        newsDao.insertNews(news)
    }

    // Actualizar noticia
    suspend fun updateNews(news: NewsEntity) {
        Log.d("LocalNewsRepository", "Actualizando noticia: ID=${news.id}, isDownloaded=${news.isDownloaded}")
        newsDao.updateNews(news)
    }

    // Insertar varias noticias
    suspend fun insertNews(news: List<NewsEntity>) {
        Log.d("LocalNewsRepository", "Insertando batch de ${news.size} noticias")
        newsDao.insertNews(news)
    }

    // Actualizar el estado de descarga de una noticia
    suspend fun updateDownloadStatus(newsId: String, isDownloaded: Boolean) {
        try {
            Log.d("LocalNewsRepository", "Actualizando estado de descarga para: $newsId a $isDownloaded")

            // Primero verificar si la noticia existe
            val news = newsDao.getNewsById(newsId)

            if (news != null) {
                newsDao.updateDownloadStatus(newsId, isDownloaded)
                Log.d("LocalNewsRepository", "Estado de descarga actualizado correctamente")
            } else {
                // La noticia no existe en la base de datos
                Log.e("LocalNewsRepository", "La noticia no existe en la base de datos local: $newsId")
                throw Exception("La noticia no existe en la base de datos local")
            }
        } catch (e: Exception) {
            Log.e("LocalNewsRepository", "Error al actualizar estado de descarga", e)
            throw e
        }
    }

    // Eliminar noticias no descargadas
    suspend fun deleteNonDownloadedNews() {
        Log.d("LocalNewsRepository", "Eliminando noticias no descargadas")
        newsDao.deleteNonDownloadedNews()
    }
}
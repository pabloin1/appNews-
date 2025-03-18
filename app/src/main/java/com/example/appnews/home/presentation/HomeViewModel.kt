package com.example.appnews.home.presentation

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appnews.core.data.local.news.repositories.NewsRepository as LocalNewsRepository
import com.example.appnews.core.services.NewsDownloadService
import com.example.appnews.home.data.model.NewsDTO
import com.example.appnews.home.data.model.toNewsEntity
import com.example.appnews.home.data.repository.NewsRepository
import kotlinx.coroutines.launch
import android.util.Log

class HomeViewModel(
    private val context: Context,
    private val remoteNewsRepository: NewsRepository,
    private val localNewsRepository: LocalNewsRepository
) : ViewModel() {

    // Estado de las noticias
    private val _newsList = MutableLiveData<List<NewsDTO>>()
    val newsList: LiveData<List<NewsDTO>> = _newsList

    // Estado de carga
    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    // Estado de error
    private val _errorMessage = MutableLiveData<String?>(null)
    val errorMessage: LiveData<String?> = _errorMessage

    // Estado para feedback de descarga
    private val _downloadStatus = MutableLiveData<String?>(null)
    val downloadStatus: LiveData<String?> = _downloadStatus

    init {
        Log.d(TAG, "Inicializando HomeViewModel")
        loadNews()
    }

    // Función para cargar noticias (desde la API o Room)
    fun loadNews(isOfflineMode: Boolean = false) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _downloadStatus.value = null

            try {
                if (isOfflineMode) {
                    // Obtener noticias descargadas desde Room
                    Log.d(TAG, "Cargando noticias en modo offline")
                    val downloadedNews = localNewsRepository.getDownloadedNews()
                    Log.d(TAG, "Noticias descargadas encontradas: ${downloadedNews.size}")

                    if (downloadedNews.isEmpty()) {
                        Log.d(TAG, "No hay noticias descargadas")
                    } else {
                        Log.d(TAG, "IDs de noticias descargadas: ${downloadedNews.map { it.id }}")
                    }

                    _newsList.value = downloadedNews.map { it.toNewsDTO() }
                } else {
                    // Obtener noticias desde la API
                    Log.d(TAG, "Cargando noticias desde la API")
                    val result = remoteNewsRepository.getNews()
                    if (result.isSuccess) {
                        val newsFromApi = result.getOrNull() ?: emptyList()
                        Log.d(TAG, "Noticias obtenidas de la API: ${newsFromApi.size}")
                        _newsList.value = newsFromApi

                        // Guardar noticias en Room (sin marcarlas como descargadas aún)
                        saveApiNewsToLocalDb(newsFromApi)
                    } else {
                        val errorMsg = result.exceptionOrNull()?.message ?: "Error al cargar noticias"
                        _errorMessage.value = errorMsg
                        Log.e(TAG, "Error al cargar noticias: $errorMsg")
                    }
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Error desconocido"
                Log.e(TAG, "Excepción al cargar noticias", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Función para guardar noticias de la API en la base de datos local
    private suspend fun saveApiNewsToLocalDb(newsFromApi: List<NewsDTO>) {
        try {
            // Primero obtenemos las noticias que ya están marcadas como descargadas
            val downloadedNews = localNewsRepository.getDownloadedNews()
            val downloadedNewsIds = downloadedNews.map { it.id }.toSet()

            Log.d(TAG, "Noticias ya marcadas como descargadas: ${downloadedNewsIds.size}")

            // Convertimos las noticias de la API a entidades, preservando el estado de descarga
            val newsEntities = newsFromApi.map { newsDTO ->
                newsDTO.toNewsEntity().copy(
                    // Mantener estado de descarga si ya estaba descargada
                    isDownloaded = downloadedNewsIds.contains(newsDTO.id)
                )
            }

            // Insertamos todas las noticias
            localNewsRepository.insertNews(newsEntities)
            Log.d(TAG, "Noticias guardadas en la BD local: ${newsEntities.size}")
        } catch (e: Exception) {
            Log.e(TAG, "Error al guardar noticias en la BD local", e)
            throw e
        }
    }

    // Función para descargar una noticia (usando el servicio en primer plano)
    fun downloadNews(newsId: String) {
        Log.d(TAG, "Iniciando descarga de noticia con ID: $newsId")
        // Iniciar servicio en primer plano para la descarga
        NewsDownloadService.startDownload(context, listOf(newsId))

        // Solo mostrar mensaje en la aplicación, pero no el progreso
        _downloadStatus.value = "Descarga iniciada. Verifica la notificación para ver el progreso."
    }

    // Método para descargar todas las noticias visibles
    fun downloadAllVisibleNews() {
        val newsToDownload = _newsList.value?.map { it.id } ?: emptyList()

        if (newsToDownload.isEmpty()) {
            _errorMessage.value = "No hay noticias para descargar"
            return
        }

        Log.d(TAG, "Iniciando descarga de todas las noticias: ${newsToDownload.size}")
        NewsDownloadService.startDownload(context, newsToDownload)
        _downloadStatus.value = "Descarga iniciada. Verifica la notificación para ver el progreso."
    }

    // Cancelar la descarga actual
    fun cancelDownload() {
        Log.d(TAG, "Cancelando descarga")
        NewsDownloadService.stopDownload(context)
        _downloadStatus.value = "Descarga cancelada"
    }

    companion object {
        private const val TAG = "HomeViewModel"
    }
}
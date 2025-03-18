package com.example.appnews.home.presentation

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appnews.core.data.local.news.repositories.NewsRepository
import com.example.appnews.home.data.model.NewsDTO
import com.example.appnews.home.data.model.toNewsEntity
import com.example.appnews.home.data.repository.NewsRepository as RemoteNewsRepository
import kotlinx.coroutines.launch

class HomeViewModel(
    private val remoteNewsRepository: RemoteNewsRepository = RemoteNewsRepository(context),
    private val localNewsRepository: NewsRepository // Repositorio de Room
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

    // Función para cargar noticias (desde la API o Room)
    fun loadNews(isOfflineMode: Boolean = false) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                if (isOfflineMode) {
                    // Obtener noticias descargadas desde Room
                    val downloadedNews = localNewsRepository.getDownloadedNews()
                    _newsList.value = downloadedNews.map { it.toNewsDTO() }
                } else {
                    // Obtener noticias desde la API
                    val result = remoteNewsRepository.getNews()
                    if (result.isSuccess) {
                        val newsFromApi = result.getOrNull() ?: emptyList()
                        _newsList.value = newsFromApi

                        // Guardar noticias en Room (sin marcarlas como descargadas aún)
                        localNewsRepository.insertNews(newsFromApi.map { it.toNewsEntity() })
                    } else {
                        _errorMessage.value = result.exceptionOrNull()?.message ?: "Error al cargar noticias"
                    }
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Error desconocido"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Función para descargar una noticia
    fun downloadNews(newsId: String) {
        viewModelScope.launch {
            try {
                // Marcar la noticia como descargada en Room
                localNewsRepository.updateDownloadStatus(newsId, true)
                loadNews() // Recargar la lista de noticias
            } catch (e: Exception) {
                _errorMessage.value = "Error al descargar la noticia"
            }
        }
    }

    // Inicializar carga de noticias
    init {
        loadNews()
    }
}
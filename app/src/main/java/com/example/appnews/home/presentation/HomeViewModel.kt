package com.example.appnews.home.presentation

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appnews.home.data.model.NewsDTO
import com.example.appnews.home.data.repository.NewsRepository
import kotlinx.coroutines.launch

class HomeViewModel(
    private val context: Context,
    private val newsRepository: NewsRepository = NewsRepository(context)
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

    // Función para cargar noticias
    fun loadNews() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            val result = newsRepository.getNews()

            if (result.isSuccess) {
                _newsList.value = result.getOrNull() ?: emptyList()
            } else {
                _errorMessage.value = result.exceptionOrNull()?.message ?: "Error al cargar noticias"
            }

            _isLoading.value = false
        }
    }

    // Inicializar carga de noticias
    init {
        loadNews()
    }
}
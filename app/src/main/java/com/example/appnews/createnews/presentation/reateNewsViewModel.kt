package com.example.appnews.createnews.presentation

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appnews.createnews.data.model.CreateNewsResponse
import com.example.appnews.createnews.data.repository.CreateNewsRepository
import kotlinx.coroutines.launch

class CreateNewsViewModel(
    private val context: Context,
    private val createNewsRepository: CreateNewsRepository = CreateNewsRepository(context)
) : ViewModel() {

    // Estado para el título de la noticia
    private val _title = MutableLiveData("")
    val title: LiveData<String> = _title

    // Estado para el contenido de la noticia
    private val _content = MutableLiveData("")
    val content: LiveData<String> = _content

    // Estado para habilitar/deshabilitar el botón de publicar
    private val _isPublishEnabled = MutableLiveData(false)
    val isPublishEnabled: LiveData<Boolean> = _isPublishEnabled

    // Estado de publicación
    private val _publishStatus = MutableLiveData<PublishStatus>()
    val publishStatus: LiveData<PublishStatus> = _publishStatus

    // Estado de carga
    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    // Función para actualizar el título
    fun onTitleChanged(newTitle: String) {
        _title.value = newTitle
        validateForm()
    }

    // Función para actualizar el contenido
    fun onContentChanged(newContent: String) {
        _content.value = newContent
        validateForm()
    }

    // Validar el formulario
    private fun validateForm() {
        _isPublishEnabled.value = !(_title.value.isNullOrBlank() || _content.value.isNullOrBlank())
    }

    // Publicar la noticia
    fun publishNews() {
        val currentTitle = _title.value?.trim() ?: ""
        val currentContent = _content.value?.trim() ?: ""

        if (currentTitle.isBlank() || currentContent.isBlank()) {
            _publishStatus.value = PublishStatus.Error("El título y contenido son obligatorios")
            return
        }

        viewModelScope.launch {
            _isLoading.value = true

            val result = createNewsRepository.createNews(currentTitle, currentContent)

            _isLoading.value = false

            if (result.isSuccess) {
                _publishStatus.value = PublishStatus.Success(result.getOrNull() ?: CreateNewsResponse())
                // Limpiar los campos después de publicar
                _title.value = ""
                _content.value = ""
                _isPublishEnabled.value = false
            } else {
                _publishStatus.value = PublishStatus.Error(
                    result.exceptionOrNull()?.message ?: "Error al publicar la noticia"
                )
            }
        }
    }

    // Estados posibles de la publicación
    sealed class PublishStatus {
        data class Success(val news: CreateNewsResponse) : PublishStatus()
        data class Error(val message: String) : PublishStatus()
    }
}
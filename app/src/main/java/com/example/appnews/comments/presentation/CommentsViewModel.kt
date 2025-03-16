package com.example.appnews.comments.presentation

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appnews.comments.data.model.CommentDTO
import com.example.appnews.comments.data.repository.CommentsRepository
import kotlinx.coroutines.launch

class CommentsViewModel(
    private val context: Context,
    private val newsId: String,
    private val commentsRepository: CommentsRepository = CommentsRepository(context)
) : ViewModel() {

    // Estado para la lista de comentarios
    private val _commentsList = MutableLiveData<List<CommentDTO>>(emptyList())
    val commentsList: LiveData<List<CommentDTO>> = _commentsList

    // Estado para el comentario que se está escribiendo
    private val _commentText = MutableLiveData("")
    val commentText: LiveData<String> = _commentText

    // Estado de carga
    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    // Estado para el botón de enviar comentario
    private val _isSendEnabled = MutableLiveData(false)
    val isSendEnabled: LiveData<Boolean> = _isSendEnabled

    // Estado de error
    private val _errorMessage = MutableLiveData<String?>(null)
    val errorMessage: LiveData<String?> = _errorMessage

    // Estado de envío de comentario
    private val _isSending = MutableLiveData(false)
    val isSending: LiveData<Boolean> = _isSending

    init {
        loadComments()
    }

    // Función para cargar comentarios
    fun loadComments() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            val result = commentsRepository.getCommentsByNewsId(newsId)

            if (result.isSuccess) {
                _commentsList.value = result.getOrNull() ?: emptyList()
            } else {
                _errorMessage.value = result.exceptionOrNull()?.message ?: "Error al cargar comentarios"
            }

            _isLoading.value = false
        }
    }

    // Actualizar el texto del comentario
    fun onCommentTextChanged(text: String) {
        _commentText.value = text
        _isSendEnabled.value = text.trim().isNotEmpty()
    }

    // Enviar un nuevo comentario
    fun sendComment() {
        val commentText = _commentText.value?.trim() ?: ""
        if (commentText.isEmpty()) return

        viewModelScope.launch {
            _isSending.value = true
            _errorMessage.value = null

            val result = commentsRepository.createComment(newsId, commentText)

            if (result.isSuccess) {
                // Agregar el nuevo comentario a la lista
                val newComment = result.getOrNull()
                if (newComment != null) {
                    val currentList = _commentsList.value?.toMutableList() ?: mutableListOf()
                    currentList.add(0, newComment)
                    _commentsList.value = currentList
                }
                // Limpiar el campo de texto
                _commentText.value = ""
                _isSendEnabled.value = false
            } else {
                _errorMessage.value = result.exceptionOrNull()?.message ?: "Error al enviar comentario"
            }

            _isSending.value = false
        }
    }
}
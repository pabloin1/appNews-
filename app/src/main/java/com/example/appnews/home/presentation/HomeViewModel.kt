package com.example.appnews.home.presentation


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

data class Item(val id: Int, val title: String)

class HomeViewModel : ViewModel() {
    // LiveData para mantener el estado de los items
    private val _items = MutableLiveData<List<Item>>()
    val items: LiveData<List<Item>> get() = _items

    // LiveData para manejar el estado de carga
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    // LiveData para manejar errores
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    init {
        loadData() // Cargar datos al inicializar el ViewModel
    }

    // Función para cargar datos
    private fun loadData() {
        _isLoading.value = true
        _error.value = null // Reiniciar error

        viewModelScope.launch {
            // Simulación de una carga de datos (puedes reemplazar esto con una llamada a un repositorio)
            try {
                // Simula un retraso de 2 segundos
                kotlinx.coroutines.delay(2000)
                // Carga de datos de ejemplo
                val loadedItems = List(10) { Item(it, "Item #$it") }
                _items.value = loadedItems
            } catch (e: Exception) {
                _error.value = "Error al cargar los datos"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Función para reiniciar el error
    fun clearError() {
        _error.value = null
    }
}

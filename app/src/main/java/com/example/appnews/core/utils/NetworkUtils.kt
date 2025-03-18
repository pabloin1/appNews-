package com.example.appnews.core.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log

object NetworkUtils {
    fun isNetworkAvailable(context: Context): Boolean {
        try {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

            // Para versiones >= Android 6.0
            val network = connectivityManager.activeNetwork
            if (network == null) {
                Log.d("NetworkUtils", "No hay red activa disponible")
                return false
            }

            val capabilities = connectivityManager.getNetworkCapabilities(network)
            if (capabilities == null) {
                Log.d("NetworkUtils", "La red activa no tiene capacidades")
                return false
            }

            val hasInternet = capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                    capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)

            // Detectar tipo de conexión para mejor depuración
            val connectionType = when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> "WiFi"
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> "Datos móviles"
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> "Ethernet"
                else -> "Desconocido"
            }

            Log.d("NetworkUtils", "Conexión a internet: $hasInternet (Tipo: $connectionType)")
            return hasInternet
        } catch (e: Exception) {
            Log.e("NetworkUtils", "Error al verificar la conexión de red", e)
            return false
        }
    }
}
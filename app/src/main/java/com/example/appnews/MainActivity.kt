package com.example.appnews

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.appnews.core.navigation.NavigationWrapper
import com.example.appnews.core.utils.NetworkUtils

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Verificar conexión al iniciar
        val hasConnection = NetworkUtils.isNetworkAvailable(this)
        Log.d("MainActivity", "Estado de conexión al iniciar: $hasConnection")

        setContent {
            NavigationWrapper(initialOnlineState = hasConnection)
        }
    }
}
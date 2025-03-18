package com.example.appnews.offline.presentation

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NewReleases
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun OfflineScreen(
    onRetryConnection: () -> Unit,
    onViewOfflineNews: () -> Unit
) {
    // Log al mostrar la pantalla
    LaunchedEffect(Unit) {
        Log.d("OfflineScreen", "Pantalla offline mostrada")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Icono de "Sin conexión"
        Icon(
            imageVector = Icons.Default.WifiOff,
            contentDescription = "Sin conexión",
            modifier = Modifier.size(100.dp),
            tint = Color(0xFFFF9800) // Color naranja para mayor visibilidad
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Título
        Text(
            text = "No tienes conexión a Internet",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
            color = Color(0xFFEEEEEE) // Color claro para mejor visibilidad
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Descripción
        Text(
            text = "Puedes ver las noticias que hayas descargado anteriormente, pero recuerda que no podrás cargar comentarios ni crear nuevas noticias.",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = Color(0xFFE0E0E0) // Color claro para mejor visibilidad
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Botón para ver noticias descargadas
        Button(
            onClick = {
                Log.d("OfflineScreen", "Botón 'Ver mis noticias descargadas' presionado")
                onViewOfflineNews()
            },
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Icon(
                imageVector = Icons.Default.NewReleases,
                contentDescription = "Ver noticias",
                modifier = Modifier.padding(end = 8.dp)
            )
            Text(
                text = "Ver mis noticias descargadas",
                style = MaterialTheme.typography.bodyLarge
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botón para reintentar conexión
        Button(
            onClick = {
                Log.d("OfflineScreen", "Botón 'Reintentar conexión' presionado")
                onRetryConnection()
            },
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF4CAF50) // Verde para indicar acción positiva
            )
        ) {
            Text(
                text = "Reintentar conexión",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}
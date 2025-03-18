package com.example.appnews.offline.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NewReleases
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun OfflineScreen(
    onRetryConnection: () -> Unit,
    onViewOfflineNews: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Icono de "Sin conexión"
        Icon(
            imageVector = Icons.Default.WifiOff, // Icono de Material 3
            contentDescription = "Sin conexión",
            modifier = Modifier.size(100.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Título
        Text(
            text = "No tienes conexión a Internet",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Descripción
        Text(
            text = "Puedes ver las noticias que hayas descargado anteriormente, pero recuerda que no podrás cargar comentarios ni crear nuevas noticias.",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Botón para ver noticias descargadas
        Button(
            onClick = onViewOfflineNews,
            modifier = Modifier.width(280.dp)
        ) {
            Icon(
                imageVector = Icons.Default.NewReleases, // Icono de Material 3
                contentDescription = "Ver noticias",
                modifier = Modifier.padding(end = 8.dp)
            )
            Text(text = "Ver mis noticias descargadas")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botón para reintentar conexión
        Button(
            onClick = onRetryConnection,
            modifier = Modifier.width(280.dp)
        ) {
            Text(text = "Reintentar conexión")
        }
    }
}

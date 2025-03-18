package com.example.appnews.home.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.appnews.home.data.model.NewsDTO
import com.example.appnews.ui.theme.Purple40
import com.example.appnews.ui.theme.PurpleGrey80
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter



// HomeScreen Composable
@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel,
    onNewsClick: (newsId: String, newsTitle: String) -> Unit = { _, _ -> },
    onCreateNewsClick: () -> Unit = {}
) {
    val newsList by homeViewModel.newsList.observeAsState(emptyList())
    val isLoading by homeViewModel.isLoading.observeAsState(false)
    val errorMessage by homeViewModel.errorMessage.observeAsState()

    // Como no hay estado de modo offline en el ViewModel, lo manejamos localmente en el composable
    var isOfflineMode by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Purple40, PurpleGrey80)
                    )
                )
        ) {
            // Encabezado personalizado
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Purple40)
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (isOfflineMode) "Noticias (Offline)" else "Noticias",
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge
                )

                Row {
                    // Switch para modo offline
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text(
                            text = "Offline",
                            color = Color.White,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(end = 4.dp)
                        )
                        Switch(
                            checked = isOfflineMode,
                            onCheckedChange = {
                                isOfflineMode = it
                                homeViewModel.loadNews(isOfflineMode)
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = PurpleGrey80
                            )
                        )
                    }

                    IconButton(onClick = { homeViewModel.loadNews(isOfflineMode) }) {
                        Icon(
                            Icons.Filled.Refresh,
                            contentDescription = "Recargar noticias",
                            tint = Color.White
                        )
                    }

                    IconButton(onClick = onCreateNewsClick) {
                        Icon(
                            Icons.Filled.Add,
                            contentDescription = "Crear noticia",
                            tint = Color.White
                        )
                    }
                }
            }

            // Contenido principal
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                when {
                    isLoading -> {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                    errorMessage != null -> {
                        ErrorContent(
                            errorMessage = errorMessage!!,
                            onRetry = { homeViewModel.loadNews(isOfflineMode) }
                        )
                    }
                    newsList.isEmpty() -> {
                        Column(
                            modifier = Modifier.align(Alignment.Center),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = if (isOfflineMode)
                                    "No hay noticias descargadas"
                                else
                                    "No hay noticias disponibles",
                                color = Color.White
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            if (!isOfflineMode) {
                                Button(
                                    onClick = onCreateNewsClick,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Purple40
                                    )
                                ) {
                                    Icon(
                                        Icons.Filled.Add,
                                        contentDescription = "Crear noticia"
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Crear noticia")
                                }
                            }
                        }
                    }
                    else -> {
                        NewsList(
                            news = newsList,
                            onNewsClick = onNewsClick,
                            onDownloadClick = { newsId -> homeViewModel.downloadNews(newsId) },
                            isOfflineMode = isOfflineMode
                        )
                    }
                }
            }
        }

        // Botón flotante para crear noticias (solo visible en modo online)
        if (newsList.isNotEmpty() && !isOfflineMode) {
            FloatingActionButton(
                onClick = onCreateNewsClick,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                containerColor = Purple40,
                contentColor = Color.White
            ) {
                Icon(
                    Icons.Filled.Add,
                    contentDescription = "Crear noticia"
                )
            }
        }
    }
}

@Composable
fun NewsList(
    news: List<NewsDTO>,
    onNewsClick: (newsId: String, newsTitle: String) -> Unit,
    onDownloadClick: (String) -> Unit,
    isOfflineMode: Boolean
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(news) { newsItem ->
            NewsItemCard(
                newsItem = newsItem,
                onNewsClick = onNewsClick,
                onDownloadClick = onDownloadClick,
                isOfflineMode = isOfflineMode
            )
        }
    }
}

@Composable
fun NewsItemCard(
    newsItem: NewsDTO,
    onNewsClick: (newsId: String, newsTitle: String) -> Unit,
    onDownloadClick: (String) -> Unit,
    isOfflineMode: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onNewsClick(newsItem.id, newsItem.title) },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = newsItem.title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = newsItem.content,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = formatDate(newsItem.publicationDate),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row {
                    // Botón para descargar noticia (solo visible en modo online)
                    if (!isOfflineMode) {
                        TextButton(
                            onClick = { onDownloadClick(newsItem.id) },
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = Purple40
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Download,
                                contentDescription = "Descargar noticia",
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Descargar")
                        }
                    }

                    // Botón para ver comentarios
                    TextButton(
                        onClick = { onNewsClick(newsItem.id, newsItem.title) },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = Purple40
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Comment,
                            contentDescription = "Ver comentarios",
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Comentarios")
                    }
                }
            }
        }
    }
}

@Composable
fun ErrorContent(
    errorMessage: String,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Error",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.error
            )
            Text(
                text = errorMessage,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            Button(onClick = onRetry) {
                Text("Reintentar")
            }
        }
    }
}

// Función para formatear la fecha
fun formatDate(dateString: String): String {
    return try {
        if (dateString.isBlank()) return ""
        val instant = Instant.parse(dateString)
        val formatter = DateTimeFormatter
            .ofPattern("dd/MM/yyyy HH:mm")
            .withZone(ZoneId.systemDefault())
        formatter.format(instant)
    } catch (e: Exception) {
        dateString // Devolver la fecha original si hay un error
    }
}
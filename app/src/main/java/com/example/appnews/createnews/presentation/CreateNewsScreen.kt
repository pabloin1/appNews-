package com.example.appnews.createnews.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appnews.ui.theme.Purple40
import com.example.appnews.ui.theme.PurpleGrey80

@Composable
fun CreateNewsScreen(
    viewModel: CreateNewsViewModel,
    onBackClick: () -> Unit,
    onNewsCreated: () -> Unit
) {
    val title by viewModel.title.observeAsState("")
    val content by viewModel.content.observeAsState("")
    val isPublishEnabled by viewModel.isPublishEnabled.observeAsState(false)
    val isLoading by viewModel.isLoading.observeAsState(false)
    val publishStatus by viewModel.publishStatus.observeAsState()

    // Estado para Snackbar
    var showSnackbar by remember { mutableStateOf(false) }
    var snackbarMessage by remember { mutableStateOf("") }

    // Observar cambios en el estado de publicación
    LaunchedEffect(publishStatus) {
        when (publishStatus) {
            is CreateNewsViewModel.PublishStatus.Success -> {
                snackbarMessage = "¡Noticia publicada con éxito!"
                showSnackbar = true
                onNewsCreated()
            }
            is CreateNewsViewModel.PublishStatus.Error -> {
                snackbarMessage = (publishStatus as CreateNewsViewModel.PublishStatus.Error).message
                showSnackbar = true
            }
            else -> {}
        }
    }

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
            // Cabecera personalizada
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Purple40)
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Volver",
                        tint = Color.White
                    )
                }

                Text(
                    text = "Crear Noticia",
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge,
                    maxLines = 1
                )
            }

            // Formulario de creación de noticias
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Nueva Publicación",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = Purple40
                    )

                    // Campo de título
                    OutlinedTextField(
                        value = title,
                        onValueChange = { viewModel.onTitleChanged(it) },
                        label = { Text("Título") },
                        placeholder = { Text("Ingresa el título de la noticia") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Purple40,
                            unfocusedBorderColor = PurpleGrey80
                        ),
                        singleLine = true
                    )

                    // Campo de contenido
                    OutlinedTextField(
                        value = content,
                        onValueChange = { viewModel.onContentChanged(it) },
                        label = { Text("Contenido") },
                        placeholder = { Text("Escribe el contenido de la noticia") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Purple40,
                            unfocusedBorderColor = PurpleGrey80
                        ),
                        maxLines = 10
                    )

                    // Botón de publicar
                    Button(
                        onClick = { viewModel.publishNews() },
                        enabled = isPublishEnabled && !isLoading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Purple40,
                            disabledContainerColor = Color.Gray
                        )
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Send,
                                    contentDescription = "Publicar",
                                    tint = Color.White
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Publicar Noticia",
                                    color = Color.White,
                                    fontSize = 16.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Mostrar Snackbar
        if (showSnackbar) {
            Card(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (publishStatus is CreateNewsViewModel.PublishStatus.Success)
                        Color(0xFF4CAF50) else Color(0xFFE53935)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = snackbarMessage,
                        color = Color.White,
                        modifier = Modifier.weight(1f)
                    )
                    TextButton(
                        onClick = { showSnackbar = false },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = Color.White
                        )
                    ) {
                        Text("Cerrar")
                    }
                }
            }
        }
    }
}
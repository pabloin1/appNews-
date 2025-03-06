package com.example.appnews.comments.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appnews.comments.data.model.CommentDTO
import com.example.appnews.ui.theme.Purple40
import com.example.appnews.ui.theme.PurpleGrey80
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun CommentsScreen(
    viewModel: CommentsViewModel,
    newsTitle: String,
    onBackClick: () -> Unit
) {
    val commentsList by viewModel.commentsList.observeAsState(emptyList())
    val isLoading by viewModel.isLoading.observeAsState(false)
    val errorMessage by viewModel.errorMessage.observeAsState()
    val commentText by viewModel.commentText.observeAsState("")
    val isSendEnabled by viewModel.isSendEnabled.observeAsState(false)
    val isSending by viewModel.isSending.observeAsState(false)

    // Estado para el Snackbar
    var showErrorSnackbar by remember { mutableStateOf(false) }
    var snackbarMessage by remember { mutableStateOf("") }

    // Observar cambios en el mensaje de error
    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            snackbarMessage = it
            showErrorSnackbar = true
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
                    text = "Comentarios",
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Subtítulo con el título de la noticia (asegurarse de que no sea null)
            Text(
                text = newsTitle.orEmpty(),
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF6A1B9A))
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            // Contenido principal
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                when {
                    isLoading -> {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                    commentsList.isEmpty() -> {
                        Text(
                            text = "No hay comentarios. ¡Sé el primero en comentar!",
                            modifier = Modifier.align(Alignment.Center),
                            color = Color.White
                        )
                    }
                    else -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(commentsList) { comment ->
                                CommentItem(comment)
                            }
                        }
                    }
                }
            }

            // Área para agregar comentarios
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = commentText.orEmpty(),
                        onValueChange = { viewModel.onCommentTextChanged(it) },
                        placeholder = { Text("Escribe un comentario...") },
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Purple40,
                            unfocusedBorderColor = PurpleGrey80
                        ),
                        maxLines = 3
                    )

                    IconButton(
                        onClick = { viewModel.sendComment() },
                        enabled = isSendEnabled && !isSending,
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                color = if (isSendEnabled && !isSending) Purple40 else Color.Gray,
                                shape = RoundedCornerShape(12.dp)
                            )
                    ) {
                        if (isSending) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Send,
                                contentDescription = "Enviar comentario",
                                tint = Color.White
                            )
                        }
                    }
                }
            }
        }

        // Mostrar Snackbar de error como un componente flotante
        if (showErrorSnackbar) {
            Card(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
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
                        text = snackbarMessage.orEmpty(),
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.weight(1f)
                    )
                    TextButton(
                        onClick = { showErrorSnackbar = false },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.onErrorContainer
                        )
                    ) {
                        Text("Cerrar")
                    }
                }
            }
        }
    }
}

@Composable
fun CommentItem(comment: CommentDTO) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = comment.userName.orEmpty(),
                    fontWeight = FontWeight.Bold,
                    color = Purple40
                )
                Text(
                    text = formatDate(comment.createdAt.orEmpty()),
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = comment.comment.orEmpty(),
                modifier = Modifier.fillMaxWidth()
            )
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
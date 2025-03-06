package com.example.appnews.login.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import com.example.appnews.ui.theme.PurpleGrey80
import androidx.compose.foundation.text.ClickableText
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign


@Composable
fun LoginScreen(
    loginViewModel: LoginViewModel,
    navigateToHome: () -> Unit,
    navigateToRegister: () -> Unit
) {
    val username by loginViewModel.username.observeAsState("")
    val email by loginViewModel.email.observeAsState("")
    val password by loginViewModel.password.observeAsState("")
    var passwordVisible by remember { mutableStateOf(false) }
    val isButtonEnabled by loginViewModel.isButtonEnabled.observeAsState(false)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF4A148C), Color(0xFF7B1FA2))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Iniciar Sesión",
                    fontSize = 26.sp,
                    color = Color(0xFF4A148C),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(20.dp))

                OutlinedTextField(
                    value = username,
                    onValueChange = { loginViewModel.onUsernameChanged(it) },
                    label = { Text("Usuario") },
                    singleLine = true,
                    leadingIcon = { Icon(imageVector = Icons.Filled.Person, contentDescription = "Usuario") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF4A148C),
                        unfocusedBorderColor = PurpleGrey80,
                        cursorColor = Color(0xFF4A148C)
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { loginViewModel.onEmailChanged(it) },
                    label = { Text("Correo Electrónico") },
                    leadingIcon = { Icon(imageVector = Icons.Default.Email, contentDescription = "Correo") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF4A148C),
                        unfocusedBorderColor = PurpleGrey80,
                        cursorColor = Color(0xFF4A148C)
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { loginViewModel.onPasswordChanged(it) },
                    label = { Text("Contraseña") },
                    leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = null) },
                    trailingIcon = {
                        val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(imageVector = image, contentDescription = "Mostrar/Ocultar contraseña")
                        }
                    },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF4A148C),
                        unfocusedBorderColor = PurpleGrey80,
                        cursorColor = Color(0xFF4A148C)
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = { navigateToHome() },
                    enabled = isButtonEnabled,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp)),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4A148C))
                ) {
                    Text(text = "Iniciar Sesión", fontSize = 18.sp, color = Color.White)
                }

                Spacer(modifier = Modifier.height(16.dp))

                ClickableText(
                    text = AnnotatedString("¿No tienes cuenta? Regístrate aquí"),
                    onClick = { navigateToRegister() },
                    style = androidx.compose.ui.text.TextStyle(
                        fontSize = 14.sp,
                        color = Color(0xFF4A148C)
                    )
                )
            }
        }
    }
}

package com.example.appnews

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.appnews.core.navigation.NavigationWrapper


import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.appnews.core.fcm.FCMUtils
import com.example.appnews.ui.theme.AppNewsTheme



class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppNewsTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    TokenScreen(
                        modifier = Modifier.padding(innerPadding),
                        onFetchToken = { callback ->
                            FCMUtils.getToken(this@MainActivity) { token ->
                                val finalToken = token ?: "Error al obtener token"
                                Log.d("FCM_TOKEN", finalToken)
                                callback(finalToken)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun TokenScreen(modifier: Modifier = Modifier, onFetchToken: ((String) -> Unit) -> Unit) {
    var token by remember { mutableStateOf("Presiona el botón para obtener el token") }

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = token, modifier = Modifier.padding(16.dp))
        Button(onClick = { onFetchToken { newToken -> token = newToken } }) {
            Text("Obtener Token FCM")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TokenScreenPreview() {
    AppNewsTheme {
        TokenScreen(onFetchToken = {})
    }
}




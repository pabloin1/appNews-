package com.example.appnews.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.appnews.home.presentation.HomeScreen
import com.example.appnews.home.presentation.HomeViewModel
import com.example.appnews.login.presentation.LoginScreen
import com.example.appnews.login.presentation.LoginViewModel
import com.example.appnews.register.presentation.RegisterScreen
import com.example.appnews.register.presentation.RegisterViewModel


@Composable
fun NavigationWrapper() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "Login") {

        // Pantalla de Login
        composable("Login") {
            LoginScreen(
                loginViewModel = LoginViewModel(),
                navigateToHome = { navController.navigate("Home") },
                navigateToRegister = { navController.navigate("Register") } // Navegar a la pantalla de registro
            )
        }

        // Pantalla Home (puedes personalizarla)
        composable("Home") {
            HomeScreen(
                HomeViewModel = HomeViewModel(),
            )
        }

        // Pantalla de Registro
        composable("Register") {
            RegisterScreen(
                registerViewModel = RegisterViewModel(),
                navigateToHome = { navController.navigate("Home") } // Agregar navegación al hogar después de registrarse
            )
        }
    }
}



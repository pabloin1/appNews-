package com.example.appnews.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.appnews.comments.presentation.CommentsScreen
import com.example.appnews.comments.presentation.CommentsViewModel
import com.example.appnews.createnews.presentation.CreateNewsScreen
import com.example.appnews.createnews.presentation.CreateNewsViewModel
import com.example.appnews.home.presentation.HomeScreen
import com.example.appnews.home.presentation.HomeViewModel
import com.example.appnews.login.presentation.LoginScreen
import com.example.appnews.login.presentation.LoginViewModel
import com.example.appnews.register.presentation.RegisterScreen
import com.example.appnews.register.presentation.RegisterViewModel
import com.example.appnews.register.data.repository.RegisterRepository
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

@Composable
fun NavigationWrapper() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val registerRepository = RegisterRepository(context)

    NavHost(navController = navController, startDestination = "Login") {
        // Pantalla de Login
        composable("Login") {
            LoginScreen(
                loginViewModel = LoginViewModel(context),
                navigateToHome = { navController.navigate("Home") {
                    popUpTo("Login") { inclusive = true }
                }},
                navigateToRegister = { navController.navigate("Register") }
            )
        }

        // Pantalla Home
        composable("Home") {
            HomeScreen(
                homeViewModel = HomeViewModel(context),
                onNewsClick = { newsId, newsTitle ->
                    try {
                        // Navegar a la pantalla de comentarios con el ID y título de la noticia
                        val safeTitle = newsTitle.orEmpty()
                        val encodedTitle = java.net.URLEncoder.encode(safeTitle, StandardCharsets.UTF_8.toString())
                        navController.navigate("Comments/$newsId/$encodedTitle")
                    } catch (e: Exception) {
                        // En caso de error, navegar solo con el ID
                        navController.navigate("Comments/$newsId/Sin%20titulo")
                    }
                },
                onCreateNewsClick = { navController.navigate("CreateNews") }
            )
        }

        // Pantalla de Registro
        composable("Register") {
            RegisterScreen(
                registerViewModel = RegisterViewModel(registerRepository, context),
                navigateToHome = { navController.navigate("Home") {
                    popUpTo("Login") { inclusive = true }
                }}
            )
        }

        // Pantalla de Comentarios
        composable(
            route = "Comments/{newsId}/{newsTitle}",
            arguments = listOf(
                navArgument("newsId") { type = NavType.StringType },
                navArgument("newsTitle") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val newsId = backStackEntry.arguments?.getString("newsId") ?: ""
            val encodedTitle = backStackEntry.arguments?.getString("newsTitle") ?: ""
            val newsTitle = try {
                URLDecoder.decode(encodedTitle, StandardCharsets.UTF_8.toString())
            } catch (e: Exception) {
                "Noticia"
            }

            CommentsScreen(
                viewModel = CommentsViewModel(context, newsId),
                newsTitle = newsTitle,
                onBackClick = { navController.popBackStack() }
            )
        }

        // Pantalla de Crear Noticia
        composable("CreateNews") {
            CreateNewsScreen(
                viewModel = CreateNewsViewModel(context),
                onBackClick = { navController.popBackStack() },
                onNewsCreated = {
                    // Navegar de vuelta a Home para ver la nueva noticia
                    navController.popBackStack()
                }
            )
        }
    }
}
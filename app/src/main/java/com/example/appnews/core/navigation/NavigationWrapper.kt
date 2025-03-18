package com.example.appnews.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.appnews.comments.presentation.CommentsScreen
import com.example.appnews.comments.presentation.CommentsViewModel
import com.example.appnews.core.data.local.appDatabase.DatabaseProvider
import com.example.appnews.core.data.local.news.repositories.NewsRepository as LocalNewsRepository
import com.example.appnews.core.network.RetrofitHelper
import com.example.appnews.core.utils.NetworkUtils
import com.example.appnews.createnews.presentation.CreateNewsScreen
import com.example.appnews.createnews.presentation.CreateNewsViewModel
import com.example.appnews.home.presentation.HomeScreen
import com.example.appnews.home.presentation.HomeViewModel
import com.example.appnews.login.presentation.LoginScreen
import com.example.appnews.login.presentation.LoginViewModel
import com.example.appnews.offline.presentation.OfflineScreen
import com.example.appnews.register.presentation.RegisterScreen
import com.example.appnews.register.presentation.RegisterViewModel
import com.example.appnews.register.data.repository.RegisterRepository
import com.example.appnews.home.data.repository.NewsRepository
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import kotlinx.coroutines.delay
import android.util.Log

@Composable
fun NavigationWrapper(initialOnlineState: Boolean = true) {
    val navController = rememberNavController()
    val context = LocalContext.current
    val registerRepository = RegisterRepository(context)

    // Estado para controlar la conexión a internet con valor inicial pasado desde MainActivity
    val isOnline = remember { mutableStateOf(initialOnlineState) }

    // Log del estado inicial
    Log.d("NavigationWrapper", "Estado inicial de conexión: $initialOnlineState")

    // Obtener las instancias de repositorios necesarias
    val remoteNewsRepository = NewsRepository(context)
    val database = DatabaseProvider.getDatabase(context)
    val localNewsRepository = LocalNewsRepository(database.newsDao())

    // Verificar periódicamente la conexión a internet
    LaunchedEffect(Unit) {
        while(true) {
            val previousState = isOnline.value
            val hasConnection = NetworkUtils.isNetworkAvailable(context)
            isOnline.value = hasConnection

            Log.d("NavigationWrapper", "Verificación periódica de conexión: $hasConnection (anterior: $previousState)")

            // Si cambia el estado de conexión (de conectado a desconectado)
            if (!hasConnection && previousState) {
                Log.d("NavigationWrapper", "Se perdió la conexión, verificando pantalla actual: ${navController.currentDestination?.route}")

                // Solo navegamos a Offline si no estamos ya en Login o en Offline
                val currentRoute = navController.currentDestination?.route
                if (currentRoute != "Login" && currentRoute != "Offline") {
                    Log.d("NavigationWrapper", "Navegando a pantalla Offline desde $currentRoute")
                    navController.navigate("Offline") {
                        // Preservar el stack de navegación para poder volver
                        popUpTo(currentRoute ?: "") { saveState = true }
                    }
                }
            }

            delay(3000) // Verificar cada 3 segundos
        }
    }

    // Para simplicidad, iniciar siempre en Login si hay conexión o en Offline si no hay
    val startDestination = if (initialOnlineState) {
        Log.d("NavigationWrapper", "Pantalla inicial: Login (hay conexión)")
        "Login"
    } else {
        // Si no hay conexión y el usuario está autenticado, mostrar la pantalla offline
        val isAuthenticated = RetrofitHelper.getAuthToken(context) != null
        if (isAuthenticated) {
            Log.d("NavigationWrapper", "Pantalla inicial: Offline (no hay conexión y hay usuario autenticado)")
            "Offline"
        } else {
            Log.d("NavigationWrapper", "Pantalla inicial: Login (no hay conexión pero no hay usuario autenticado)")
            "Login"
        }
    }

    NavHost(navController = navController, startDestination = startDestination) {
        // Pantalla de Login
        composable("Login") {
            LoginScreen(
                loginViewModel = LoginViewModel(context),
                navigateToHome = {
                    if (isOnline.value) {
                        Log.d("NavigationWrapper", "Login exitoso con conexión, navegando a Home")
                        navController.navigate("Home") {
                            popUpTo("Login") { inclusive = true }
                        }
                    } else {
                        Log.d("NavigationWrapper", "Login exitoso sin conexión, navegando a Offline")
                        navController.navigate("Offline") {
                            popUpTo("Login") { inclusive = true }
                        }
                    }
                },
                navigateToRegister = {
                    Log.d("NavigationWrapper", "Navegando a Register desde Login")
                    navController.navigate("Register")
                }
            )
        }

        // Pantalla Home
        composable("Home") {
            HomeScreen(
                homeViewModel = HomeViewModel(
                    context = context,  // Añadir el contexto aquí
                    remoteNewsRepository = remoteNewsRepository,
                    localNewsRepository = localNewsRepository
                ),
                isOffline = !isOnline.value,
                onNewsClick = { newsId, newsTitle ->
                    try {
                        val safeTitle = newsTitle.orEmpty()
                        val encodedTitle = java.net.URLEncoder.encode(safeTitle, StandardCharsets.UTF_8.toString())
                        Log.d("NavigationWrapper", "Navegando a Comments para noticia: $newsId - $safeTitle")
                        navController.navigate("Comments/$newsId/$encodedTitle")
                    } catch (e: Exception) {
                        Log.e("NavigationWrapper", "Error al navegar a Comments", e)
                        navController.navigate("Comments/$newsId/Sin%20titulo")
                    }
                },
                onCreateNewsClick = {
                    if (isOnline.value) {
                        Log.d("NavigationWrapper", "Navegando a CreateNews desde Home")
                        navController.navigate("CreateNews")
                    } else {
                        Log.d("NavigationWrapper", "Intento de crear noticia sin conexión ignorado")
                    }
                }
            )
        }

        // Pantalla Offline
        composable("Offline") {
            OfflineScreen(
                onRetryConnection = {
                    // Verificar conexión y navegar según corresponda
                    Log.d("NavigationWrapper", "Intentando reconexión desde pantalla Offline")
                    val hasConnection = NetworkUtils.isNetworkAvailable(context)
                    isOnline.value = hasConnection

                    if (hasConnection) {
                        Log.d("NavigationWrapper", "Conexión restablecida, navegando a Home")
                        navController.navigate("Home") {
                            popUpTo("Offline") { inclusive = true }
                        }
                    } else {
                        Log.d("NavigationWrapper", "Intento de reconexión fallido")
                    }
                },
                onViewOfflineNews = {
                    // Navegar a Home en modo offline
                    Log.d("NavigationWrapper", "Navegando a Home en modo offline desde pantalla Offline")
                    navController.navigate("Home") {
                        popUpTo("Offline") { inclusive = true }
                    }
                }
            )
        }

        // Pantalla de Registro
        composable("Register") {
            RegisterScreen(
                registerViewModel = RegisterViewModel(registerRepository, context),
                navigateToHome = {
                    if (isOnline.value) {
                        Log.d("NavigationWrapper", "Registro exitoso con conexión, navegando a Home")
                        navController.navigate("Home") {
                            popUpTo("Login") { inclusive = true }
                        }
                    } else {
                        Log.d("NavigationWrapper", "Registro exitoso sin conexión, navegando a Offline")
                        navController.navigate("Offline") {
                            popUpTo("Login") { inclusive = true }
                        }
                    }
                }
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
                onBackClick = {
                    Log.d("NavigationWrapper", "Regresando desde Comments")
                    navController.popBackStack()
                }
            )
        }

        // Pantalla de Crear Noticia
        composable("CreateNews") {
            CreateNewsScreen(
                viewModel = CreateNewsViewModel(context),
                onBackClick = {
                    Log.d("NavigationWrapper", "Regresando desde CreateNews")
                    navController.popBackStack()
                },
                onNewsCreated = {
                    Log.d("NavigationWrapper", "Noticia creada, regresando a Home")
                    navController.popBackStack()
                }
            )
        }
    }
}
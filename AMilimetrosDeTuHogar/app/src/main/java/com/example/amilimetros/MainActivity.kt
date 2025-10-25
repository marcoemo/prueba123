package com.example.amilimetros

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.amilimetros.data.local.storage.UserPreferences
import com.example.amilimetros.navigation.NavGraph
import com.example.amilimetros.navigation.Route
import com.example.amilimetros.ui.components.*
import com.example.amilimetros.ui.theme.AMilimetrosTheme
import kotlinx.coroutines.launch
import com.example.amilimetros.ui.notification.NotificationManager
import com.example.amilimetros.ui.notification.CustomSnackbar

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ✅ Inicializar BD
        val db = com.example.amilimetros.data.local.database.AppDatabase.getInstance(this)
        android.util.Log.d("MainActivity", "✅ Base de datos inicializada")

        enableEdgeToEdge()
        setContent {
            AMilimetrosTheme {
                AppScaffold()
            }
        }
    }
}

@Composable
fun AppScaffold() {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val userPrefs = remember { UserPreferences(context) }

    val isLoggedIn by userPrefs.isLoggedIn.collectAsStateWithLifecycle(false)
    val isAdmin by userPrefs.isAdmin.collectAsStateWithLifecycle(false)
    val notificationState by NotificationManager.notificationState.collectAsStateWithLifecycle()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val drawerItems = defaultDrawerItems(
        onHome = {
            scope.launch {
                navController.navigate(Route.Home.path) {
                    popUpTo(Route.Home.path) { inclusive = true }
                }
                drawerState.close()
            }
        },
        onLogin = {
            scope.launch {
                navController.navigate(Route.Login.path)
                drawerState.close()
            }
        },
        onRegister = {
            scope.launch {
                navController.navigate(Route.Register.path)
                drawerState.close()
            }
        },
        onProducts = {
            scope.launch {
                navController.navigate(Route.Products.path)
                drawerState.close()
            }
        },
        onAnimals = {
            scope.launch {
                navController.navigate(Route.Animals.path)
                drawerState.close()
            }
        },
        onCart = {
            scope.launch {
                navController.navigate(Route.Cart.path)
                drawerState.close()
            }
        },
        onAdmin = {
            scope.launch {
                navController.navigate(Route.Admin.path)
                drawerState.close()
            }
        },
        isLoggedIn = isLoggedIn,
        isAdmin = isAdmin
    )

    val showTopBar = currentRoute !in listOf(Route.Login.path, Route.Register.path)

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AppDrawer(
                currentRoute = currentRoute,
                items = drawerItems
            )
        }
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                Column {
                    if (showTopBar) {
                        AppTopBar(
                            onOpenDrawer = { scope.launch { drawerState.open() } },
                            onHome = {
                                navController.navigate(Route.Home.path) {
                                    popUpTo(Route.Home.path) { inclusive = true }
                                }
                            },
                            onProducts = { navController.navigate(Route.Products.path) },
                            onAnimals = { navController.navigate(Route.Animals.path) },
                            onCart = { navController.navigate(Route.Cart.path) }
                        )
                    }
                    CustomSnackbar(
                        state = notificationState,
                        onDismiss = { NotificationManager.dismiss() }
                    )
                }
            }
        ) { innerPadding ->
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                NavGraph(
                    navController = navController,
                    startDestination = Route.Home.path
                )
            }
        }
    }
}
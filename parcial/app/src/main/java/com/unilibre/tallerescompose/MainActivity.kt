package com.unilibre.tallerescompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.unilibre.tallerescompose.menu.MenuScreen
import com.unilibre.tallerescompose.taller01.WelcomeScreen
import com.unilibre.tallerescompose.taller02.ListsScreen
import com.unilibre.tallerescompose.taller03.WeatherScreen
import com.unilibre.tallerescompose.taller04.TransactionsScreen
import com.unilibre.tallerescompose.taller05.Taller05Main
import dagger.hilt.android.AndroidEntryPoint

/**
 * Main Activity of the application.
 * Annotated with @AndroidEntryPoint to enable Hilt injection in this Activity and its Composables.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Enable edge-to-edge display (transparent status/navigation bars)
        enableEdgeToEdge()
        
        setContent {
            // Main Theme Wrapper
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TalleresNavigation()
                }
            }
        }
    }
}

/**
 * Navigation component that manages all screen transitions in the app.
 */
@Composable
fun TalleresNavigation() {
    val navController = rememberNavController()

    // NavHost defines the navigation graph
    NavHost(
        navController = navController,
        startDestination = "menu" // Initial screen
    ) {
        // Main Menu Route
        composable("menu") {
            MenuScreen(
                onNavigateToTaller = { route ->
                    navController.navigate(route)
                }
            )
        }

        // Taller 01 Route
        composable("taller01") {
            WelcomeScreen()
        }

        // Taller 02 Route
        composable("taller02") {
            ListsScreen()
        }

        // Taller 03 Route
        composable("taller03") {
            WeatherScreen()
        }

        // Taller 04 Route
        composable("taller04") {
            TransactionsScreen()
        }

        // Taller 05 Route
        composable("taller05") {
            Taller05Main()
        }
    }
}

/**
 * Temporary screen to show while individual tallere screens are built.
 */
@Composable
fun PlaceholderScreen(title: String) {
    Scaffold { padding ->
        Text(
            text = title,
            modifier = Modifier.padding(padding)
        )
    }
}

package com.unilibre.tallerescompose.taller03

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition

/**
 * Taller 03: State and Retrofit.
 * Demonstrates Hilt ViewModel, collectAsStateWithLifecycle, and Lottie animations.
 */
@Composable
fun WeatherScreen(
    viewModel: WeatherViewModel = hiltViewModel()
) {
    // Collect UI state from ViewModel safely
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Logic to determine gradient colors based on weatherCode
    val (topColorTarget, bottomColorTarget) = when {
        uiState is WeatherUiState.Success -> {
            val code = (uiState as WeatherUiState.Success).data.currentWeather.weatherCode
            when (code) {
                0, 1 -> Color(0xFFFF9800) to Color(0xFFFFE0B2) // Clear
                2, 3 -> Color(0xFF90A4AE) to Color(0xFFECEFF1) // Cloudy
                in 51..67, in 80..82 -> Color(0xFF1565C0) to Color(0xFFBBDEFB) // Rain
                else -> Color(0xFF6A1B9A) to Color(0xFFE1BEE7) // Default / Night
            }
        }
        else -> Color(0xFF6A1B9A) to Color(0xFFE1BEE7)
    }

    // Animated top color for the gradient
    val animatedTopColor by animateColorAsState(
        targetValue = topColorTarget,
        animationSpec = tween(durationMillis = 1000),
        label = "colorAnim"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(animatedTopColor, bottomColorTarget)
                )
            )
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            when (uiState) {
                is WeatherUiState.Loading -> LoadingView()
                is WeatherUiState.Success -> WeatherDetailView((uiState as WeatherUiState.Success).data, viewModel)
                is WeatherUiState.Error -> ErrorView((uiState as WeatherUiState.Error).message, viewModel)
            }
        }
    }
}

@Composable
fun LoadingView() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        CircularProgressIndicator(color = Color.White)
        Spacer(modifier = Modifier.height(16.dp))
        Text("Obteniendo clima...", color = Color.White)
    }
}

@Composable
fun WeatherDetailView(data: WeatherResponse, viewModel: WeatherViewModel) {
    val weather = data.currentWeather
    
    // Lottie Animation for the sun/cloud
    val composition by rememberLottieComposition(
        LottieCompositionSpec.Url("https://assets9.lottiefiles.com/temp/lf20_StdaX8.json")
    )
    
    LottieAnimation(
        composition = composition,
        iterations = LottieConstants.IterateForever,
        modifier = Modifier.size(220.dp)
    )

    Spacer(modifier = Modifier.height(8.dp))

    Text(
        text = "${weather.temperature}°C",
        fontSize = 80.sp,
        fontWeight = FontWeight.ExtraBold,
        color = Color.White
    )

    Spacer(modifier = Modifier.height(16.dp))

    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        SuggestionChip(
            onClick = {}, 
            label = { Text("💨 ${weather.windSpeed} km/h") },
            colors = SuggestionChipDefaults.suggestionChipColors(labelColor = Color.White)
        )
        SuggestionChip(
            onClick = {}, 
            label = { Text("🌡 Código: ${weather.weatherCode}") },
            colors = SuggestionChipDefaults.suggestionChipColors(labelColor = Color.White)
        )
    }

    Spacer(modifier = Modifier.height(32.dp))

    Button(
        onClick = { viewModel.fetchWeather() },
        colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.2f))
    ) {
        Icon(Icons.Default.Refresh, contentDescription = null, tint = Color.White)
        Spacer(modifier = Modifier.width(8.dp))
        Text("Actualizar", color = Color.White)
    }
}

@Composable
fun ErrorView(message: String, viewModel: WeatherViewModel) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = message, color = Color.White, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { viewModel.fetchWeather() }) {
            Text("Reintentar")
        }
    }
}

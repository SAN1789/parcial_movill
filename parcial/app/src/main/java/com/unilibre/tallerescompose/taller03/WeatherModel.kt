package com.unilibre.tallerescompose.taller03

import com.google.gson.annotations.SerializedName

/**
 * Data model for Weather API response (Open-Meteo).
 */
data class WeatherResponse(
    @SerializedName("current_weather")
    val currentWeather: CurrentWeather
)

data class CurrentWeather(
    @SerializedName("temperature")
    val temperature: Double,
    @SerializedName("windspeed")
    val windSpeed: Double,
    @SerializedName("weathercode")
    val weatherCode: Int
)

/**
 * Sealed interface representing the different UI states.
 */
sealed interface WeatherUiState {
    object Loading : WeatherUiState
    data class Success(val data: WeatherResponse) : WeatherUiState
    data class Error(val message: String) : WeatherUiState
}

package com.unilibre.tallerescompose.taller03

import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Retrofit interface for the Open-Meteo API.
 */
interface WeatherApi {
    @GET("v1/forecast")
    suspend fun getWeatherData(
        @Query("latitude") lat: Double = 4.6097, // Bogotá
        @Query("longitude") lon: Double = -74.0817,
        @Query("current_weather") current: Boolean = true
    ): WeatherResponse
}

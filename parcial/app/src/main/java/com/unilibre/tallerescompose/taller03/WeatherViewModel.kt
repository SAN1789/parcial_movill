package com.unilibre.tallerescompose.taller03

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the Weather screen.
 * Annotated with @HiltViewModel to allow injection into Composables.
 */
@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val api: WeatherApi
) : ViewModel() {

    private val _uiState = MutableStateFlow<WeatherUiState>(WeatherUiState.Loading)
    val uiState: StateFlow<WeatherUiState> = _uiState.asStateFlow()

    init {
        fetchWeather()
    }

    /**
     * Fetches weather data using the Retrofit service within a coroutine.
     */
    fun fetchWeather() {
        viewModelScope.launch {
            _uiState.value = WeatherUiState.Loading
            try {
                val response = api.getWeatherData()
                _uiState.value = WeatherUiState.Success(response)
            } catch (e: Exception) {
                _uiState.value = WeatherUiState.Error("Error al cargar clima: ${e.message}")
            }
        }
    }
}

package com.unilibre.tallerescompose.taller05

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for Taller 05: Recipes with IA and Camera.
 */
@HiltViewModel
class Taller05ViewModel @Inject constructor(
    private val repository: RecipeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(Taller05UiState())
    val uiState = _uiState.asStateFlow()

    /**
     * Updates the list of detected ingredients from the camera analyzer.
     */
    fun onIngredientsDetected(ingredients: List<DetectedIngredient>) {
        _uiState.update { it.copy(detectedIngredients = ingredients) }
    }

    /**
     * Removes an ingredient from the list.
     */
    fun removeIngredient(name: String) {
        _uiState.update { state ->
            state.copy(detectedIngredients = state.detectedIngredients.filter { it.name != name })
        }
    }

    /**
     * Generates recipes using the IA repository based on detected ingredients.
     */
    fun generateRecipes() {
        viewModelScope.launch {
            _uiState.update { it.copy(isGenerating = true, error = null) }
            
            val ingredientsNames = _uiState.value.detectedIngredients.map { it.name }
            
            if (ingredientsNames.isEmpty()) {
                _uiState.update { it.copy(isGenerating = false, error = "No hay ingredientes detectados") }
                return@launch
            }

            try {
                val recipes = repository.generateRecipes(ingredientsNames)
                _uiState.update { it.copy(generatedRecipes = recipes, isGenerating = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isGenerating = false, error = "Error al conectar con la IA") }
            }
        }
    }
}

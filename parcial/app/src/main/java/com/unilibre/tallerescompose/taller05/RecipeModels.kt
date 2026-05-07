package com.unilibre.tallerescompose.taller05

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

/**
 * Data model for a Recipe received from IA.
 */
@Entity(tableName = "favorite_recipes")
data class Recipe(
    @PrimaryKey val id: String,
    @SerializedName("nombre") val title: String,
    @SerializedName("descripcion") val description: String = "",
    val ingredients: List<String> = emptyList(),
    @SerializedName("pasos") val instructions: String,
    @SerializedName("tiempo_minutos") val timeMinutes: Int = 0,
    @SerializedName("dificultad") val difficulty: String = "Media",
    @SerializedName("calorias") val calories: Int = 0,
    val imageUrl: String = "",
    val isFavorite: Boolean = false
)

/**
 * Wrapper for the list of recipes returned by the JSON API.
 */
data class RecipeResponse(
    val recipes: List<Recipe>
)

/**
 * Represents an ingredient detected by the camera.
 */
data class DetectedIngredient(
    val name: String,
    val confidence: Float
)

/**
 * UI State for Taller 05.
 */
data class Taller05UiState(
    val detectedIngredients: List<DetectedIngredient> = emptyList(),
    val generatedRecipes: List<Recipe> = emptyList(),
    val isGenerating: Boolean = false,
    val error: String? = null
)

package com.unilibre.tallerescompose.taller05

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecipeRepository @Inject constructor(
    private val apiService: RecipeApiService
) {
    // Note: In a real app, never hardcode keys. Use BuildConfig or Secrets.
    private val apiKey = "Bearer TU_API_KEY_AQUI" 

    suspend fun generateRecipes(ingredients: List<String>): List<Recipe> {
        val prompt = """
            Tengo estos ingredientes: ${ingredients.joinToString(", ")}.
            Sugiere 3 recetas posibles en formato JSON con exactamente estos campos:
            id (un string unico), nombre, tiempo_minutos, dificultad, pasos (como un solo string con saltos de linea), calorias.
            Responde SOLO el JSON, una lista directa de objetos.
        """.trimIndent()

        val request = AiRequest(
            messages = listOf(
                AiMessage(role = "system", content = "Eres un chef experto que solo responde en JSON."),
                AiMessage(role = "user", content = prompt)
            )
        )

        return try {
            val response = apiService.getAiCompletion(apiKey, request)
            val jsonContent = response.choices.firstOrNull()?.message?.content ?: ""
            
            // Clean JSON if IA adds markdown triple backticks
            val cleanJson = jsonContent.removeSurrounding("```json", "```").trim()
            
            val listType = object : TypeToken<List<Recipe>>() {}.type
            Gson().fromJson(cleanJson, listType)
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}

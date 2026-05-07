package com.unilibre.tallerescompose.taller05

import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

/**
 * Mock Request model for IA (simplified for this structure)
 */
data class AiRequest(
    val model: String = "gpt-3.5-turbo",
    val messages: List<AiMessage>
)

data class AiMessage(
    val role: String,
    val content: String
)

/**
 * Mock Response model for IA
 */
data class AiResponse(
    val choices: List<AiChoice>
)

data class AiChoice(
    val message: AiMessage
)

/**
 * Service to interact with the IA API (OpenAI example structure).
 */
interface RecipeApiService {
    @POST("v1/chat/completions")
    suspend fun getAiCompletion(
        @Header("Authorization") apiKey: String,
        @Body request: AiRequest
    ): AiResponse
}

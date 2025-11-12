package com.runanywhere.sarva

import com.google.gson.annotations.SerializedName
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import java.util.concurrent.TimeUnit

// Data classes for RAG API
data class RagRequest(
    @SerializedName("UserID")
    val userId: String,
    @SerializedName("UserPrompt")
    val userPrompt: String
)

data class RagResponse(
    @SerializedName("user_id")
    val userId: String? = null,
    @SerializedName("user_prompt")
    val userPrompt: String? = null,
    @SerializedName("rag_context")
    val ragContext: String? = null,
    @SerializedName("status")
    val status: String? = null,
    @SerializedName("message")
    val message: String? = null,
    @SerializedName("error")
    val error: String? = null
)

// Retrofit API interface
interface RagApiService {
    @POST(RagConfig.SEARCH_ENDPOINT)
    suspend fun searchRag(@Body request: RagRequest): RagResponse
}

// Repository class for RAG operations
class RagRepository {
    private val apiService: RagApiService

    init {
        // Configure OkHttp client with timeouts
        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(RagConfig.CONNECT_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(RagConfig.READ_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(RagConfig.WRITE_TIMEOUT, TimeUnit.SECONDS)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(RagConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(RagApiService::class.java)
    }

    suspend fun performRagSearch(userId: String, userPrompt: String): Result<String> {
        return try {
            val request = RagRequest(userId = userId, userPrompt = userPrompt)
            val response = apiService.searchRag(request)

            when {
                response.status == "success" && !response.ragContext.isNullOrBlank() -> {
                    Result.success(response.ragContext)
                }

                !response.error.isNullOrBlank() -> {
                    Result.failure(Exception("RAG API Error: ${response.error}"))
                }

                !response.message.isNullOrBlank() -> {
                    Result.failure(Exception("RAG API Message: ${response.message}"))
                }

                else -> {
                    Result.failure(Exception("Unknown RAG API error or empty context"))
                }
            }
        } catch (e: Exception) {
            Result.failure(Exception("Network error during RAG search: ${e.message}", e))
        }
    }
}
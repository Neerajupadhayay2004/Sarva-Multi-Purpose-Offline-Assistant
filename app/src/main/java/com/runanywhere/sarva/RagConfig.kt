package com.runanywhere.sarva

/**
 * Configuration object for RAG (Retrieval-Augmented Generation) API settings.
 * Update these values to match your RAG API deployment.
 */
object RagConfig {
    /**
     * Base URL for your RAG API
     * Example: "https://your-api-domain.com/" or "http://localhost:8000/"
     * Make sure to include the trailing slash
     */
    const val BASE_URL = "http://192.168.106.183:4567/"

    /**
     * API endpoint for RAG search
     * The full URL will be: BASE_URL + SEARCH_ENDPOINT
     */
    const val SEARCH_ENDPOINT = "search"

    /**
     * Timeout settings (in seconds)
     */
    const val CONNECT_TIMEOUT = 30L
    const val READ_TIMEOUT = 60L
    const val WRITE_TIMEOUT = 60L

    /**
     * Default user ID prefix for RAG requests
     */
    const val USER_ID_PREFIX = "ai_user_"
}
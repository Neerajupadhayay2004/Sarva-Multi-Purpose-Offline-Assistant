package com.runanywhere.sarva

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.runanywhere.sdk.models.ModelInfo
import com.runanywhere.sdk.public.RunAnywhere
import com.runanywhere.sdk.public.extensions.listAvailableModels
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import java.util.UUID

// Enhanced Message Data Class for AI Assistant
data class ChatMessage(
    val text: String,
    val isUser: Boolean,
    val hasRagContext: Boolean = false,
    val timestamp: Long = System.currentTimeMillis(),
    val isVoiceMessage: Boolean = false
)

// App States
enum class AppState {
    INITIALIZING,
    FIRST_LAUNCH_SETUP,
    MODEL_DOWNLOADING,
    MODEL_LOADING,
    READY,
    ERROR
}

// Dynamic text configuration for different models
data class ModelTextConfig(
    val welcomeMessage: String,
    val statusMessage: String,
    val placeholderText: String,
    val errorMessage: String,
    val thinkingMessage: String
)

// Chat Session data class
data class ChatSession(
    val id: String,
    val title: String,
    val lastMessage: String,
    val timestamp: Long,
    val messageCount: Int
)

// Modern AI Assistant ViewModel
class ChatViewModel(private val context: Context) : ViewModel() {

    companion object {
        private const val TAG = "ChatViewModel"
        private const val MAX_INIT_RETRIES = 10
        private const val INIT_RETRY_DELAY = 1000L
    }

    // Core state management
    private val _appState = MutableStateFlow(AppState.INITIALIZING)
    val appState: StateFlow<AppState> = _appState

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages

    private val _isProcessing = MutableStateFlow(false)
    val isProcessing: StateFlow<Boolean> = _isProcessing

    // Model management
    private val _availableModels = MutableStateFlow<List<ModelInfo>>(emptyList())
    val availableModels: StateFlow<List<ModelInfo>> = _availableModels

    private val _downloadProgress = MutableStateFlow<Float?>(null)
    val downloadProgress: StateFlow<Float?> = _downloadProgress

    private val _currentModelId = MutableStateFlow<String?>(null)
    val currentModelId: StateFlow<String?> = _currentModelId

    private val _currentModelName = MutableStateFlow<String?>(null)
    val currentModelName: StateFlow<String?> = _currentModelName

    // Chat History Management
    private val _chatHistory = MutableStateFlow<List<ChatSession>>(emptyList())
    val chatHistory: StateFlow<List<ChatSession>> = _chatHistory

    private val _currentChatId = MutableStateFlow<String?>(null)
    val currentChatId: StateFlow<String?> = _currentChatId

    // Status and feedback
    private val _statusMessage = MutableStateFlow("Welcome to AI Assistant")
    val statusMessage: StateFlow<String> = _statusMessage

    private val _ragStatus = MutableStateFlow<String?>(null)
    val ragStatus: StateFlow<String?> = _ragStatus

    // Voice service
    private var enhancedVoiceService: EnhancedVoiceService? = null

    // RAG repository for online research
    private val ragRepository = RagRepository()

    // Settings
    private val _isRagEnabled = MutableStateFlow(true)
    val isRagEnabled: StateFlow<Boolean> = _isRagEnabled

    private val _isVoiceModeEnabled = MutableStateFlow(false)
    val isVoiceModeEnabled: StateFlow<Boolean> = _isVoiceModeEnabled

    // Voice states
    private val _isListening = MutableStateFlow(false)
    val isListening: StateFlow<Boolean> = _isListening

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking

    private val _partialText = MutableStateFlow("")
    val partialText: StateFlow<String> = _partialText

    private val _conversationMode = MutableStateFlow(false)
    val conversationMode: StateFlow<Boolean> = _conversationMode

    // In-memory chat storage
    private val chatSessions = mutableMapOf<String, MutableList<ChatMessage>>()

    // Dynamic text configuration
    private val _currentModelConfig = MutableStateFlow(getDefaultModelConfig())
    val currentModelConfig: StateFlow<ModelTextConfig> = _currentModelConfig

    init {
        initializeApp()
    }

    private fun initializeApp() {
        viewModelScope.launch {
            try {
                _statusMessage.value = "Initializing AI Assistant..."

                // Wait for SDK to be ready
                waitForSDKInitialization()

                // Initialize enhanced voice service
                initializeVoiceService()

                // Load available models from SDK
                loadAvailableModels()

                // Set initial state based on available models
                setupInitialState()

            } catch (e: Exception) {
                Log.e(TAG, "Initialization failed", e)
                _appState.value = AppState.ERROR
                _statusMessage.value = "Initialization failed: ${e.message}"
            }
        }
    }

    private suspend fun waitForSDKInitialization() {
        var retries = 0
        while (retries < MAX_INIT_RETRIES) {
            try {
                // Check if SDK is initialized by trying to list models
                listAvailableModels()
                Log.i(TAG, "SDK is ready")
                return
            } catch (e: Exception) {
                Log.d(TAG, "SDK not ready yet, waiting... (attempt ${retries + 1})")
            }

            delay(INIT_RETRY_DELAY)
            retries++
        }

        // If we get here, SDK might not be ready, but continue anyway
        Log.w(TAG, "SDK initialization check timed out, continuing...")
    }

    private fun initializeVoiceService() {
        try {
            enhancedVoiceService = EnhancedVoiceService(context).apply {
                // Set up callbacks for continuous conversation
                setOnSpeechResult { text ->
                    Log.i(TAG, "🎤🎤🎤 VOICE INPUT RECEIVED: '$text'")
                    Log.i(
                        TAG,
                        "Current state - isProcessing: ${_isProcessing.value}, appState: ${_appState.value}"
                    )
                    Log.i(TAG, "Current chat ID: ${_currentChatId.value}")
                    Log.i(TAG, "Messages count before: ${_messages.value.size}")

                    if (text.isEmpty()) {
                        Log.w(TAG, "❌ Voice text is EMPTY - ignoring")
                        return@setOnSpeechResult
                    }

                    // CRITICAL FIX: Immediately add the user's voice message to chat FIRST
                    viewModelScope.launch {
                        try {
                            Log.i(TAG, "✅ ADDING VOICE MESSAGE TO CHAT IMMEDIATELY")

                            // Ensure we have a chat session
                            val currentChatId = _currentChatId.value
                            if (currentChatId == null) {
                                Log.d(TAG, "No current chat, starting new one")
                                startNewChat()
                                delay(100) // Give it time to initialize
                            }

                            // Add user message to chat IMMEDIATELY so user can see it
                            val chatId = _currentChatId.value
                            if (chatId != null) {
                                val userMessage = ChatMessage(
                                    text = text,
                                    isUser = true,
                                    isVoiceMessage = true
                                )

                                chatSessions[chatId]?.add(userMessage)
                                _messages.value = chatSessions[chatId]?.toList() ?: emptyList()

                                Log.i(
                                    TAG,
                                    "✅ Voice message added to chat, messages count: ${_messages.value.size}"
                                )
                                Log.i(TAG, "📝 User message visible in chat: '$text'")

                                // Clear the partial text since we've added the full message
                                _partialText.value = ""

                                // Now process the AI response
                                delay(200) // Small delay to ensure UI updates

                                if (!_isProcessing.value && _appState.value == AppState.READY) {
                                    Log.i(TAG, "🤖 Now generating AI response for: '$text'")
                                    _isProcessing.value = true

                                    try {
                                        val response = generateAIResponse(text)

                                        if (response.isNotBlank()) {
                                            val responseMessage = ChatMessage(
                                                text = response,
                                                isUser = false,
                                                hasRagContext = _isRagEnabled.value
                                            )

                                            chatSessions[chatId]?.add(responseMessage)
                                            _messages.value =
                                                chatSessions[chatId]?.toList() ?: emptyList()

                                            Log.i(TAG, "✅ AI response added to chat")

                                            // Speak the response if voice is enabled
                                            if (_isVoiceModeEnabled.value && response.isNotEmpty()) {
                                                Log.d(TAG, "🔊 Speaking AI response")
                                                enhancedVoiceService?.speak(response)
                                            }

                                            updateChatHistoryList()
                                        }

                                    } catch (e: Exception) {
                                        Log.e(TAG, "❌ Error generating AI response: ${e.message}")
                                        val errorMessage = ChatMessage(
                                            text = "I apologize, but I encountered an error processing your voice message.",
                                            isUser = false
                                        )
                                        chatSessions[chatId]?.add(errorMessage)
                                        _messages.value =
                                            chatSessions[chatId]?.toList() ?: emptyList()
                                    } finally {
                                        _isProcessing.value = false
                                    }
                                }
                            } else {
                                Log.e(TAG, "❌ Failed to get chat ID after initialization")
                            }

                        } catch (e: Exception) {
                            Log.e(TAG, "❌ Error processing voice message: ${e.message}", e)
                            _isProcessing.value = false
                        }
                    }
                }

                setOnPartialSpeechResult { partialText ->
                    _partialText.value = partialText
                    Log.d(TAG, "🎤 Partial speech: '$partialText'")
                }

                setOnSpeakingStateChanged { isSpeaking ->
                    _isSpeaking.value = isSpeaking
                    Log.d(TAG, "🔊 Speaking state changed: $isSpeaking")

                    // Auto-resume listening in conversation mode after speaking completes
                    if (!isSpeaking && _conversationMode.value && !_isProcessing.value) {
                        viewModelScope.launch {
                            delay(800) // Brief pause for natural conversation flow
                            if (_conversationMode.value && !_isSpeaking.value && !_isProcessing.value) {
                                Log.d(TAG, "🔄 Resuming listening in conversation mode")
                                startVoiceInput()
                            }
                        }
                    }
                }

                setOnListeningStateChanged { isListening ->
                    _isListening.value = isListening
                    Log.d(TAG, "👂 Listening state changed: $isListening")
                }

                setOnError { error ->
                    Log.e(TAG, "❌ Voice service error: $error")
                    if (_conversationMode.value) {
                        // In conversation mode, try to recover from errors
                        viewModelScope.launch {
                            delay(2000)
                            if (_conversationMode.value && !_isListening.value && !_isSpeaking.value && !_isProcessing.value) {
                                Log.d(TAG, "🔄 Recovering from voice error in conversation mode")
                                startVoiceInput()
                            }
                        }
                    }
                }
            }
            Log.i(TAG, "✅ Enhanced voice service initialized successfully")
        } catch (e: Exception) {
            Log.e(TAG, "❌ Failed to initialize voice service", e)
            _isVoiceModeEnabled.value = false
        }
    }

    private suspend fun loadAvailableModels() {
        try {
            _statusMessage.value = "Loading AI models..."

            // Use actual SDK to get models
            val models = listAvailableModels()
            _availableModels.value = models

            Log.i(TAG, "Loaded ${models.size} models from SDK")

            if (models.isNotEmpty()) {
                _statusMessage.value = "Models loaded successfully"
            } else {
                _statusMessage.value = "No models available - please add models"
            }

        } catch (e: Exception) {
            Log.e(TAG, "Failed to load models from SDK", e)
            _statusMessage.value = "Failed to load models: ${e.localizedMessage}"
            _availableModels.value = emptyList()
        }
    }

    private suspend fun setupInitialState() {
        val models = _availableModels.value

        when {
            models.isEmpty() -> {
                _appState.value = AppState.FIRST_LAUNCH_SETUP
                _statusMessage.value = "Welcome! Please download a model to get started."
            }

            models.any { it.isDownloaded } -> {
                // Find first downloaded model and select it
                val downloadedModel = models.first { it.isDownloaded }
                selectModel(downloadedModel.id)
                _appState.value = AppState.READY
                startNewChat()
            }

            else -> {
                _appState.value = AppState.FIRST_LAUNCH_SETUP
                _statusMessage.value =
                    "Models available for download. Please select one to continue."
            }
        }
    }

    fun selectModel(modelId: String) {
        viewModelScope.launch {
            val model = _availableModels.value.find { it.id == modelId }
            if (model != null) {
                try {
                    Log.i(TAG, "🔄 Loading model: ${model.name} (ID: $modelId)")
                    _statusMessage.value = "Loading ${model.name}..."
                    _appState.value = AppState.MODEL_LOADING

                    // Load model using SDK
                    val success = RunAnywhere.loadModel(modelId)

                    if (success) {
                        _currentModelId.value = modelId
                        _currentModelName.value = model.name

                        // Update dynamic text configuration based on selected model
                        _currentModelConfig.value = getModelSpecificConfig(model)

                        _appState.value = AppState.READY
                        Log.i(TAG, "✅ Successfully loaded model: ${model.name}")

                        // Test the model immediately after loading
                        delay(1000) // Give model time to fully load
                        testSDKConnection()

                    } else {
                        _appState.value = AppState.ERROR
                        _statusMessage.value = "❌ Failed to load ${model.name}"
                        Log.e(TAG, "❌ Failed to load model: ${model.name}")
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "❌ Error loading model: ${e.message}", e)
                    _appState.value = AppState.ERROR
                    _statusMessage.value = "Error loading model: ${e.localizedMessage}"
                }
            } else {
                Log.e(TAG, "❌ Model not found with ID: $modelId")
            }
        }
    }

    fun downloadModel(modelId: String) {
        viewModelScope.launch {
            try {
                _appState.value = AppState.MODEL_DOWNLOADING
                _statusMessage.value = "Downloading AI model..."

                // Use actual SDK download with progress tracking
                RunAnywhere.downloadModel(modelId).collect { progress ->
                    _downloadProgress.value = progress
                    _statusMessage.value = "Downloading: ${(progress * 100).toInt()}%"
                }

                _downloadProgress.value = null

                // Refresh models list after download
                loadAvailableModels()

                // Auto-select the downloaded model
                selectModel(modelId)

            } catch (e: Exception) {
                Log.e(TAG, "Download failed", e)
                _appState.value = AppState.ERROR
                _statusMessage.value = "Download failed: ${e.localizedMessage}"
                _downloadProgress.value = null
            }
        }
    }

    // Chat History Management Functions
    fun startNewChat() {
        val newChatId = UUID.randomUUID().toString()
        _currentChatId.value = newChatId
        chatSessions[newChatId] = mutableListOf()

        // Add welcome message for new chat
        val config = _currentModelConfig.value
        addSystemMessage(config.welcomeMessage)

        // Clear current messages
        _messages.value = chatSessions[newChatId]?.toList() ?: emptyList()

        // Update chat history list
        updateChatHistoryList()
    }

    fun selectChat(chatId: String) {
        _currentChatId.value = chatId
        _messages.value = chatSessions[chatId]?.toList() ?: emptyList()
    }

    fun deleteChat(chatId: String) {
        chatSessions.remove(chatId)
        if (_currentChatId.value == chatId) {
            // If deleting current chat, start a new one
            startNewChat()
        } else {
            // Just update the history list
            updateChatHistoryList()
        }
    }

    private fun addSystemMessage(text: String) {
        val currentChatId = _currentChatId.value ?: return
        val systemMessage = ChatMessage(
            text = text,
            isUser = false,
            hasRagContext = false
        )

        chatSessions[currentChatId]?.add(systemMessage)
        _messages.value = chatSessions[currentChatId]?.toList() ?: emptyList()
        updateChatHistoryList()
    }

    private fun updateChatHistoryList() {
        val historyList = chatSessions.map { (chatId, messages) ->
            val title = generateChatTitle(messages)
            val lastMessage = messages.lastOrNull()?.text ?: "New chat"
            val timestamp = messages.lastOrNull()?.timestamp ?: System.currentTimeMillis()

            ChatSession(
                id = chatId,
                title = title,
                lastMessage = lastMessage,
                timestamp = timestamp,
                messageCount = messages.size
            )
        }.sortedByDescending { it.timestamp }

        _chatHistory.value = historyList
    }

    private fun generateChatTitle(messages: List<ChatMessage>): String {
        val userMessages = messages.filter { it.isUser }
        return when {
            userMessages.isEmpty() -> "New Chat"
            userMessages.size == 1 -> {
                val firstMessage = userMessages.first().text
                if (firstMessage.length > 30) {
                    "${firstMessage.take(30)}..."
                } else {
                    firstMessage
                }
            }
            else -> {
                val firstMessage = userMessages.first().text
                val shortTitle = if (firstMessage.length > 20) {
                    "${firstMessage.take(20)}..."
                } else {
                    firstMessage
                }
                "$shortTitle (+${userMessages.size - 1})"
            }
        }
    }

    fun sendMessage(text: String, isVoiceMessage: Boolean = false) {
        if (_appState.value != AppState.READY) {
            _statusMessage.value = "AI assistant not ready. Please wait..."
            Log.w(TAG, "Attempted to send message while app not ready. State: ${_appState.value}")
            return
        }

        val currentChatId = _currentChatId.value
        if (currentChatId == null) {
            Log.d(TAG, "No current chat, starting new one")
            startNewChat()
            return
        }

        // Prevent duplicate requests
        if (_isProcessing.value) {
            Log.w(TAG, "Already processing a message, ignoring new request")
            return
        }

        Log.d(
            TAG,
            "Sending message: '$text', voice: $isVoiceMessage, model: ${_currentModelId.value}"
        )

        // Add user message
        val userMessage = ChatMessage(text, isUser = true, isVoiceMessage = isVoiceMessage)
        chatSessions[currentChatId]?.add(userMessage)
        _messages.value = chatSessions[currentChatId]?.toList() ?: emptyList()

        viewModelScope.launch {
            _isProcessing.value = true
            val config = _currentModelConfig.value
            _statusMessage.value = config.thinkingMessage

            try {
                Log.d(TAG, "Starting AI response generation")

                // Add a small delay to ensure UI updates
                delay(500)

                // Generate response based on current model
                val response = generateAIResponse(text)

                if (response.isBlank()) {
                    Log.e(TAG, "Generated response is blank!")
                    throw Exception("AI generated empty response")
                }

                Log.d(
                    TAG,
                    "Generated response: '${response.take(100)}...' (${response.length} chars)"
                )

                val responseMessage = ChatMessage(
                    text = response,
                    isUser = false,
                    hasRagContext = _isRagEnabled.value,
                    isVoiceMessage = false
                )

                chatSessions[currentChatId]?.add(responseMessage)
                _messages.value = chatSessions[currentChatId]?.toList() ?: emptyList()

                // Voice response if enabled
                if (_isVoiceModeEnabled.value && response.isNotEmpty()) {
                    Log.d(TAG, "Starting voice synthesis for response")
                    enhancedVoiceService?.speak(response)
                }

                _statusMessage.value = config.statusMessage
                updateChatHistoryList()

            } catch (e: Exception) {
                Log.e(TAG, "Error processing message: ${e.message}", e)
                val errorMessage = ChatMessage(
                    "I apologize, but I encountered an error while processing your request. Please try again or switch to a different model.",
                    isUser = false
                )
                chatSessions[currentChatId]?.add(errorMessage)
                _messages.value = chatSessions[currentChatId]?.toList() ?: emptyList()
                _statusMessage.value = "Error: ${e.message}"
            } finally {
                _isProcessing.value = false
                Log.d(TAG, "Message processing completed")
            }
        }
    }

    private suspend fun generateAIResponse(userQuery: String): String {
        val currentModelId = _currentModelId.value

        // First check if we have a model loaded
        if (currentModelId == null) {
            Log.e(TAG, "❌ No model selected! Cannot generate AI response.")
            throw Exception("No AI model selected. Please select a model first.")
        }

        Log.i(TAG, "🤖 Starting AI response generation")
        Log.i(TAG, "📝 User Query: '$userQuery'")
        Log.i(TAG, "🧠 Using Model ID: $currentModelId")
        Log.i(TAG, "📊 Model Name: ${getCurrentModel()?.name}")
        Log.i(TAG, "🔍 RAG Enabled: ${_isRagEnabled.value}")

        // Step 1: Fetch RAG context if enabled
        var ragContext: String? = null
        var enhancedQuery = userQuery

        if (_isRagEnabled.value) {
            try {
                Log.i(TAG, "🔍 Fetching RAG context from API...")
                _ragStatus.value = "🔄 Searching online..."

                // Generate a user ID (you can customize this)
                val userId = "${RagConfig.USER_ID_PREFIX}${System.currentTimeMillis()}"

                // Call RAG API
                val ragResult = ragRepository.performRagSearch(userId, userQuery)

                if (ragResult.isSuccess) {
                    ragContext = ragResult.getOrNull()
                    if (!ragContext.isNullOrBlank()) {
                        Log.i(TAG, "✅ RAG context retrieved successfully!")
                        Log.i(TAG, "📄 RAG context length: ${ragContext.length} characters")
                        Log.i(TAG, "📄 RAG context preview: ${ragContext.take(200)}...")

                        // Combine RAG context with user query for enhanced prompt
                        enhancedQuery = """
                            |Context Information:
                            |$ragContext
                            |
                            |Based on the above context, please answer this question:
                            |$userQuery
                        """.trimMargin()

                        Log.i(TAG, "✅ Enhanced query created with RAG context")
                        _ragStatus.value = "⚡ Online Research Ready"
                    } else {
                        Log.w(TAG, "⚠️ RAG returned empty context")
                        _ragStatus.value = "⚠️ No online data found"
                    }
                } else {
                    val error = ragResult.exceptionOrNull()
                    Log.e(TAG, "❌ RAG search failed: ${error?.message}")
                    _ragStatus.value = "⚠️ Online search unavailable"
                    // Continue with original query even if RAG fails
                }

            } catch (e: Exception) {
                Log.e(TAG, "❌ RAG error: ${e.message}", e)
                _ragStatus.value = "⚠️ RAG error"
                // Continue with original query even if RAG fails
            }
        }

        // Step 2: Generate AI response with enhanced query (includes RAG if available)
        return try {
            Log.i(TAG, "🚀 Calling RunAnywhere.generateStream() with model: $currentModelId")
            Log.i(TAG, "📝 Query length: ${enhancedQuery.length} characters")

            var response = ""
            var tokenCount = 0
            val maxTokens = 500 // Reasonable limit
            var lastLoggedLength = 0

            // Use the actual SDK generation with enhanced query
            RunAnywhere.generateStream(enhancedQuery).collect { token ->
                tokenCount++

                // Log every 10 tokens to track progress
                if (tokenCount % 10 == 0) {
                    Log.d(TAG, "📈 Generated $tokenCount tokens so far...")
                }

                // Safety check for infinite loops
                if (tokenCount > maxTokens) {
                    Log.w(TAG, "⚠️ Reached max token limit ($maxTokens), stopping generation")
                    return@collect
                }

                // Check for empty or whitespace-only tokens
                if (token.isBlank()) {
                    Log.d(TAG, "⚪ Received empty token, skipping...")
                    return@collect
                }

                // Add token to response
                response += token

                // Log response progress every 50 characters
                if (response.length - lastLoggedLength >= 50) {
                    Log.d(TAG, "📝 Current response length: ${response.length} chars")
                    Log.d(TAG, "📄 Current response preview: '${response.takeLast(50)}'")
                    lastLoggedLength = response.length
                }

                // Enhanced spam detection
                if (response.length > 100 && isResponseSpamming(response)) {
                    Log.w(TAG, "🛑 Detected response spamming pattern, stopping generation")
                    return@collect
                }

                // Check for natural stopping points
                if (shouldStopGeneration(response, tokenCount)) {
                    Log.i(TAG, "✅ Natural stopping point reached, ending generation")
                    return@collect
                }
            }

            // Clean up the response
            response = response.trim()

            Log.i(TAG, "🎯 Final response generated!")
            Log.i(TAG, "📊 Total tokens: $tokenCount")
            Log.i(TAG, "📏 Response length: ${response.length} characters")
            Log.i(TAG, "🔍 Used RAG context: ${ragContext != null}")
            Log.i(
                TAG,
                "📝 Response preview: '${response.take(150)}${if (response.length > 150) "..." else ""}'"
            )

            // Validate the response
            if (response.isEmpty()) {
                Log.e(TAG, "❌ SDK returned empty response!")
                throw Exception("AI model generated empty response")
            }

            if (response.length < 10) {
                Log.w(TAG, "⚠️ SDK returned very short response: '$response'")
                // Don't throw error for short responses, they might be valid
            }

            // Update RAG status after successful generation
            if (_isRagEnabled.value && ragContext != null) {
                _ragStatus.value = "✅ Enhanced with online data"
            }

            // Return the actual SDK response
            Log.i(TAG, "✅ Returning genuine AI model response from RunAnywhere SDK")
            response

        } catch (e: Exception) {
            Log.e(TAG, "💥 SDK generation failed completely!", e)
            Log.e(TAG, "❌ Error type: ${e.javaClass.simpleName}")
            Log.e(TAG, "❌ Error message: ${e.message}")
            Log.e(TAG, "📱 Stack trace: ${e.stackTraceToString()}")

            // Reset RAG status on error
            if (_isRagEnabled.value) {
                _ragStatus.value = "⚠️ Generation failed"
            }

            // Only use fallback as absolute last resort
            Log.w(TAG, "🔄 Falling back to mock response due to SDK failure")
            generateMockResponse(userQuery)
        }
    }

    private fun isResponseSpamming(response: String): Boolean {
        if (response.length < 50) return false

        val words = response.split(Regex("\\s+"))
        if (words.size < 10) return false

        val lastTenWords = words.takeLast(10)
        val previousTenWords = words.dropLast(10).takeLast(10)

        // Check if last 10 words repeat the previous 10 words
        if (lastTenWords == previousTenWords) {
            Log.w(TAG, "🔄 Detected word sequence repetition")
            return true
        }

        // Check for single word repetition (like "the the the the")
        val lastFiveWords = words.takeLast(5)
        if (lastFiveWords.distinct().size == 1) {
            Log.w(TAG, "🔄 Detected single word repetition: '${lastFiveWords.first()}'")
            return true
        }

        return false
    }

    private fun shouldStopGeneration(response: String, tokenCount: Int): Boolean {
        // Don't stop too early
        if (response.length < 50 || tokenCount < 10) return false

        // Stop if we have a good amount of content and hit natural endings
        if (response.length > 200) {
            val trimmed = response.trim()

            // Check for sentence endings
            if (trimmed.endsWith(".") || trimmed.endsWith("!") || trimmed.endsWith("?")) {
                val sentences = trimmed.split(Regex("[.!?]+")).filter { it.trim().isNotEmpty() }
                if (sentences.size >= 3) {
                    Log.d(TAG, "🛑 Found natural stopping point with ${sentences.size} sentences")
                    return true
                }
            }
        }

        // Stop if response is getting very long
        if (response.length > 1000) {
            Log.d(TAG, "🛑 Response getting very long (${response.length} chars), stopping")
            return true
        }

        return false
    }

    private fun generateMockResponse(userQuery: String): String {
        Log.w(TAG, "🔄 USING FALLBACK MOCK RESPONSE - SDK not working properly")

        val currentModel = getCurrentModel()
        return when {
            currentModel?.name?.contains("Lightning", ignoreCase = true) == true -> {
                "⚠️ [FALLBACK MODE] Lightning AI simulation: Your query '$userQuery' - This is a mock response because the real AI model is not responding. Please check your SDK setup and model downloads."
            }

            currentModel?.name?.contains("Conversational", ignoreCase = true) == true -> {
                "⚠️ [FALLBACK MODE] Conversational AI simulation: Regarding '$userQuery' - This is a placeholder response. The actual AI model is not working. Please verify your model is downloaded and loaded correctly."
            }

            currentModel?.name?.contains("Precision", ignoreCase = true) == true -> {
                "⚠️ [FALLBACK MODE] Precision AI simulation: Analyzing '$userQuery' - This is a mock response. The real AI model failed to respond. Check your SDK integration and model status."
            }

            currentModel?.name?.contains("Expert", ignoreCase = true) == true -> {
                "⚠️ [FALLBACK MODE] Expert AI simulation: Your question '$userQuery' - This is a fallback response. The actual AI model is not functioning. Please test your SDK connection."
            }

            currentModel?.name?.contains("Code", ignoreCase = true) == true -> {
                "⚠️ [FALLBACK MODE] Code AI simulation: For '$userQuery' - This is a mock response. The real programming AI is not responding. Verify your model setup."
            }

            currentModel?.name?.contains("Creative", ignoreCase = true) == true -> {
                "⚠️ [FALLBACK MODE] Creative AI simulation: Request '$userQuery' - This is a placeholder response. The actual creative AI model is not working properly."
            }

            else -> {
                "⚠️ [FALLBACK MODE] AI Assistant simulation: Regarding '$userQuery' - This is a mock response because the real AI model is not responding. Please check your RunAnywhere SDK setup, ensure models are downloaded and loaded correctly. Use the SDK test button to verify connectivity."
            }
        }
    }

    fun getCurrentModel(): ModelInfo? {
        return _availableModels.value.find { it.id == _currentModelId.value }
    }

    fun getCurrentModelName(): String {
        return getCurrentModel()?.name ?: "AI Assistant"
    }

    // Voice functionality
    fun toggleVoiceMode() {
        _isVoiceModeEnabled.value = !_isVoiceModeEnabled.value

        if (_isVoiceModeEnabled.value) {
            _statusMessage.value = "Voice Assistant enabled"
        } else {
            _statusMessage.value = "Text mode enabled"
            stopAllVoiceOperations()
        }
    }

    fun startVoiceInput() {
        if (_isVoiceModeEnabled.value && _appState.value == AppState.READY && !_isListening.value) {
            enhancedVoiceService?.startListening()
        }
    }

    fun stopVoiceInput() {
        enhancedVoiceService?.stopListening()
    }

    fun stopSpeaking() {
        enhancedVoiceService?.stopSpeaking()
    }

    fun toggleConversationMode() {
        _conversationMode.value = !_conversationMode.value

        if (_conversationMode.value) {
            // Start continuous conversation mode
            _isVoiceModeEnabled.value = true
            _statusMessage.value = "Conversation mode active - I'm listening continuously"
            startVoiceInput()
        } else {
            // Stop continuous conversation
            stopAllVoiceOperations()
            _statusMessage.value = "Conversation mode disabled"
        }
    }

    private fun stopAllVoiceOperations() {
        try {
            stopVoiceInput()
            stopSpeaking()
            _conversationMode.value = false
            _partialText.value = ""
            _isListening.value = false
            _isSpeaking.value = false
        } catch (e: Exception) {
            Log.e(TAG, "Error stopping voice operations", e)
        }
    }

    // Settings
    fun toggleRag() {
        _isRagEnabled.value = !_isRagEnabled.value

        if (_isRagEnabled.value) {
            _statusMessage.value = "✅ Online Research enabled"
            _ragStatus.value = "Online research active"

            // Test RAG functionality
            viewModelScope.launch {
                try {
                    Log.i(TAG, "🔍 Testing RAG functionality...")
                    // TODO: Add actual RAG API test here
                    // For now, just indicate it's enabled
                    _ragStatus.value = "⚡ Online Research Ready"
                    Log.i(TAG, "✅ RAG service appears ready (feature requires backend integration)")
                } catch (e: Exception) {
                    Log.e(TAG, "❌ RAG test failed: ${e.message}")
                    _ragStatus.value = "⚠️ RAG offline (backend needed)"
                }
            }
        } else {
            _statusMessage.value = "Using AI knowledge base only"
            _ragStatus.value = null
            Log.i(TAG, "RAG disabled - using only AI model knowledge")
        }
    }

    fun refreshModels() {
        viewModelScope.launch {
            loadAvailableModels()
            _statusMessage.value = "Refreshing models..."

            // Reset to ready state if we were in error state
            if (_appState.value == AppState.ERROR) {
                _appState.value = AppState.READY
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        try {
            enhancedVoiceService?.cleanup()
        } catch (e: Exception) {
            Log.e(TAG, "Error cleaning up voice service", e)
        }
    }

    private fun getModelSpecificConfig(model: ModelInfo?): ModelTextConfig {
        return when {
            model?.name?.contains("Lightning", ignoreCase = true) == true -> ModelTextConfig(
                welcomeMessage = "⚡ Hi! I'm Lightning AI, optimized for ultra-fast responses and maximum efficiency.",
                statusMessage = "Lightning AI ready • Ultra-fast processing",
                placeholderText = "Ask me anything for instant answers...",
                errorMessage = "Lightning AI encountered a processing issue",
                thinkingMessage = "Lightning AI processing at high speed..."
            )

            model?.name?.contains("Conversational", ignoreCase = true) == true -> ModelTextConfig(
                welcomeMessage = "👋 Hello! I'm Conversational AI, designed for natural, engaging dialogue.",
                statusMessage = "Conversational AI ready • Natural dialogue expert",
                placeholderText = "Let's have a natural conversation...",
                errorMessage = "Conversational AI needs a moment to regroup",
                thinkingMessage = "Conversational AI is thoughtfully responding..."
            )

            model?.name?.contains("Precision", ignoreCase = true) == true -> ModelTextConfig(
                welcomeMessage = "🎯 Greetings! I'm Precision AI, built for accurate analysis and detailed, thorough responses.",
                statusMessage = "Precision AI ready • Deep analysis specialist",
                placeholderText = "Ask for detailed analysis and insights...",
                errorMessage = "Precision AI encountered a complex analysis issue",
                thinkingMessage = "Precision AI conducting thorough analysis..."
            )

            model?.name?.contains("Expert", ignoreCase = true) == true -> ModelTextConfig(
                welcomeMessage = "🧠 Welcome! I'm Expert AI with advanced intelligence and specialized knowledge across domains.",
                statusMessage = "Expert AI ready • Advanced reasoning engine",
                placeholderText = "Request expert-level assistance...",
                errorMessage = "Expert AI needs to recalibrate systems",
                thinkingMessage = "Expert AI applying advanced reasoning..."
            )

            model?.name?.contains("Code", ignoreCase = true) == true -> ModelTextConfig(
                welcomeMessage = "💻 Hey developer! I'm Code AI, your programming assistant for all coding tasks and technical queries.",
                statusMessage = "Code AI ready • Programming specialist",
                placeholderText = "Ask about code, algorithms, or development...",
                errorMessage = "Code AI encountered a compilation issue",
                thinkingMessage = "Code AI analyzing and compiling response..."
            )

            model?.name?.contains("Creative", ignoreCase = true) == true -> ModelTextConfig(
                welcomeMessage = "🎨 Hello there! I'm Creative AI, designed for innovative content generation and creative thinking.",
                statusMessage = "Creative AI ready • Innovation and creativity engine",
                placeholderText = "Let's create something amazing together...",
                errorMessage = "Creative AI hit a creative block",
                thinkingMessage = "Creative AI crafting something special..."
            )

            else -> getDefaultModelConfig()
        }
    }

    private fun getDefaultModelConfig(): ModelTextConfig {
        return ModelTextConfig(
            welcomeMessage = "🤖 Hello! I'm your AI Assistant, ready to help with any questions, tasks, or conversations.",
            statusMessage = "AI Assistant ready • Multi-purpose intelligence",
            placeholderText = "Ask me anything...",
            errorMessage = "AI Assistant encountered an issue",
            thinkingMessage = "AI Assistant is thinking..."
        )
    }

    // Debug function to test SDK functionality
    fun testSDKConnection() {
        viewModelScope.launch {
            try {
                Log.i(TAG, "🔍 Testing RunAnywhere SDK Connection...")

                // Test 1: Check if we can list models
                val models = listAvailableModels()
                Log.i(TAG, "✅ SDK Connection Test 1 - List Models: SUCCESS (${models.size} models)")

                // Test 2: Check current model status
                val currentModelId = _currentModelId.value
                if (currentModelId != null) {
                    Log.i(TAG, "✅ SDK Connection Test 2 - Current Model: $currentModelId")

                    // Test 3: Try a simple generation test
                    try {
                        Log.i(TAG, "🚀 SDK Connection Test 3 - Testing generation...")
                        var testResponse = ""
                        var tokenCount = 0

                        RunAnywhere.generateStream("Hello").collect { token ->
                            testResponse += token
                            tokenCount++
                            if (tokenCount > 5) { // Just test a few tokens
                                Log.i(
                                    TAG,
                                    "✅ SDK Generation Test: SUCCESS - Got response: '$testResponse'"
                                )
                                return@collect
                            }
                        }

                        if (testResponse.isNotEmpty()) {
                            Log.i(TAG, "✅ SDK is working properly! Generated: '$testResponse'")
                            _statusMessage.value =
                                "✅ SDK Connection Verified - Real AI responses active"
                        } else {
                            Log.w(TAG, "⚠️ SDK returned empty response in test")
                            _statusMessage.value = "⚠️ SDK Connection Issues - Check model loading"
                        }

                    } catch (genError: Exception) {
                        Log.e(TAG, "❌ SDK Generation Test Failed: ${genError.message}")
                        _statusMessage.value = "❌ SDK Generation Failed - Using fallback responses"
                    }

                } else {
                    Log.w(TAG, "⚠️ No model currently selected for testing")
                    _statusMessage.value = "⚠️ No model selected - Please select a model first"
                }

            } catch (e: Exception) {
                Log.e(TAG, "❌ SDK Connection Test Failed: ${e.message}")
                _statusMessage.value = "❌ SDK Connection Failed - Check initialization"
            }
        }
    }
}

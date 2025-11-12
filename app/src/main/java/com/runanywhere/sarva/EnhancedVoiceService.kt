package com.runanywhere.sarva

import android.content.Context
import android.media.AudioManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.*
import java.util.*

class EnhancedVoiceService(
    private val context: Context
) : DefaultLifecycleObserver {

    companion object {
        private const val TAG = "EnhancedVoiceService"
    }

    // Core components with safe initialization
    private var textToSpeech: TextToSpeech? = null
    private var speechRecognizer: SpeechRecognizer? = null
    private val audioManager: AudioManager =
        context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    // State management
    private var isInitialized = false
    private var isTtsInitialized = false
    private var isSpeechRecognizerReady = false

    // Coroutine scope for async operations
    private val serviceScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    // Callback interfaces
    private var onSpeechResult: ((String) -> Unit)? = null
    private var onPartialSpeechResult: ((String) -> Unit)? = null
    private var onSpeakingStateChanged: ((Boolean) -> Unit)? = null
    private var onListeningStateChanged: ((Boolean) -> Unit)? = null
    private var onError: ((String) -> Unit)? = null

    // State tracking
    private val isCurrentlySpeaking = mutableStateOf(false)
    private val isCurrentlyListening = mutableStateOf(false)
    private var currentSpeechJob: Job? = null

    init {
        initializeServices()
    }

    private fun initializeServices() {
        try {
            initializeTextToSpeech()
            initializeSpeechRecognizer()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize voice services", e)
            onError?.invoke("Voice services initialization failed: ${e.message}")
        }
    }

    private fun initializeTextToSpeech() {
        try {
            textToSpeech = TextToSpeech(context) { status ->
                if (status == TextToSpeech.SUCCESS) {
                    textToSpeech?.let { tts ->
                        val result = tts.setLanguage(Locale.getDefault())
                        if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                            Log.w(TAG, "Language not supported, falling back to English")
                            tts.setLanguage(Locale.ENGLISH)
                        }

                        // Configure TTS settings for natural human-like voice
                        tts.setSpeechRate(1.2f) // Slightly faster for more natural feel
                        tts.setPitch(1.0f) // Normal pitch

                        // Try to use a higher quality voice engine if available
                        try {
                            val voices = tts.voices
                            val preferredVoice = voices?.firstOrNull { voice ->
                                voice.locale.language == Locale.getDefault().language &&
                                        voice.quality >= 400 && // Higher quality voice
                                        !voice.isNetworkConnectionRequired
                            }

                            if (preferredVoice != null) {
                                tts.voice = preferredVoice
                                Log.i(TAG, "Using high-quality voice: ${preferredVoice.name}")
                            }
                        } catch (e: Exception) {
                            Log.w(TAG, "Could not set preferred voice: ${e.message}")
                        }

                        // Set up utterance callbacks
                        tts.setOnUtteranceProgressListener(object :
                            android.speech.tts.UtteranceProgressListener() {
                            override fun onStart(utteranceId: String?) {
                                Log.d(TAG, "TTS started speaking")
                                isCurrentlySpeaking.value = true
                                onSpeakingStateChanged?.invoke(true)
                            }

                            override fun onDone(utteranceId: String?) {
                                Log.d(TAG, "TTS finished speaking")
                                isCurrentlySpeaking.value = false
                                onSpeakingStateChanged?.invoke(false)
                            }

                            override fun onError(utteranceId: String?) {
                                Log.e(TAG, "TTS error occurred")
                                isCurrentlySpeaking.value = false
                                onSpeakingStateChanged?.invoke(false)
                                onError?.invoke("Speech synthesis error")
                            }

                            override fun onError(utteranceId: String?, errorCode: Int) {
                                Log.e(TAG, "TTS error occurred with code: $errorCode")
                                isCurrentlySpeaking.value = false
                                onSpeakingStateChanged?.invoke(false)
                                onError?.invoke("Speech synthesis error (code: $errorCode)")
                            }
                        })

                        isTtsInitialized = true
                        Log.i(TAG, "TextToSpeech initialized successfully with enhanced quality")
                    }
                } else {
                    Log.e(TAG, "TextToSpeech initialization failed")
                    onError?.invoke("Text-to-speech initialization failed")
                }

                checkFullInitialization()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing TextToSpeech", e)
            onError?.invoke("Text-to-speech setup failed: ${e.message}")
        }
    }

    private fun initializeSpeechRecognizer() {
        try {
            if (SpeechRecognizer.isRecognitionAvailable(context)) {
                speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
                speechRecognizer?.setRecognitionListener(object : RecognitionListener {
                    override fun onReadyForSpeech(params: Bundle?) {
                        Log.d(TAG, "Speech recognizer ready")
                        isCurrentlyListening.value = true
                        onListeningStateChanged?.invoke(true)
                    }

                    override fun onBeginningOfSpeech() {
                        Log.d(TAG, "User started speaking")
                        // Immediately stop any ongoing TTS when user starts speaking
                        if (isCurrentlySpeaking.value) {
                            Log.d(TAG, "Interrupting AI speech - user started speaking")
                            stopSpeaking()
                        }
                    }

                    override fun onRmsChanged(rmsdB: Float) {
                        // Audio level changed - can be used for visual feedback
                    }

                    override fun onBufferReceived(buffer: ByteArray?) {
                        // Audio buffer received
                    }

                    override fun onEndOfSpeech() {
                        Log.d(TAG, "User finished speaking")
                        isCurrentlyListening.value = false
                        onListeningStateChanged?.invoke(false)
                    }

                    override fun onError(error: Int) {
                        Log.e(TAG, "Speech recognition error: $error")
                        isCurrentlyListening.value = false
                        onListeningStateChanged?.invoke(false)

                        val errorMessage = when (error) {
                            SpeechRecognizer.ERROR_AUDIO -> "Audio recording error"
                            SpeechRecognizer.ERROR_CLIENT -> "Client side error"
                            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Insufficient permissions"
                            SpeechRecognizer.ERROR_NETWORK -> "Network error"
                            SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network timeout"
                            SpeechRecognizer.ERROR_NO_MATCH -> "No speech input"
                            SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "Recognition service busy"
                            SpeechRecognizer.ERROR_SERVER -> "Server error"
                            SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "No speech input"
                            else -> "Unknown recognition error"
                        }

                        // Don't treat "no match" and "speech timeout" as critical errors
                        if (error != SpeechRecognizer.ERROR_NO_MATCH && error != SpeechRecognizer.ERROR_SPEECH_TIMEOUT) {
                            onError?.invoke(errorMessage)
                        }
                    }

                    override fun onResults(results: Bundle?) {
                        val matches =
                            results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        val result = matches?.firstOrNull()

                        if (!result.isNullOrBlank()) {
                            Log.d(TAG, "Speech recognition result: $result")
                            onSpeechResult?.invoke(result)
                        }

                        isCurrentlyListening.value = false
                        onListeningStateChanged?.invoke(false)
                    }

                    override fun onPartialResults(partialResults: Bundle?) {
                        val matches =
                            partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        val partialResult = matches?.firstOrNull()

                        if (!partialResult.isNullOrBlank()) {
                            onPartialSpeechResult?.invoke(partialResult)
                        }
                    }

                    override fun onEvent(eventType: Int, params: Bundle?) {
                        // Custom events
                    }
                })

                isSpeechRecognizerReady = true
                Log.i(TAG, "SpeechRecognizer initialized successfully")
            } else {
                Log.w(TAG, "Speech recognition not available on this device")
                onError?.invoke("Speech recognition not available")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing SpeechRecognizer", e)
            onError?.invoke("Speech recognition setup failed: ${e.message}")
        }

        checkFullInitialization()
    }

    private fun checkFullInitialization() {
        if (isTtsInitialized && isSpeechRecognizerReady) {
            isInitialized = true
            Log.i(TAG, "Enhanced voice service fully initialized")
        }
    }

    // Public API methods with error handling

    fun speak(text: String) {
        if (!isInitialized || !isTtsInitialized) {
            Log.w(TAG, "TTS not initialized")
            onError?.invoke("Text-to-speech not ready")
            return
        }

        try {
            // Cancel any ongoing speech job
            currentSpeechJob?.cancel()

            currentSpeechJob = serviceScope.launch {
                try {
                    // Stop any current listening
                    if (isCurrentlyListening.value) {
                        stopListening()
                    }

                    // Generate unique utterance ID
                    val utteranceId = "utterance_${System.currentTimeMillis()}"

                    // Create speech parameters
                    val params = Bundle().apply {
                        putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, utteranceId)
                        putFloat(TextToSpeech.Engine.KEY_PARAM_VOLUME, 1.0f)
                    }

                    textToSpeech?.speak(text, TextToSpeech.QUEUE_FLUSH, params, utteranceId)

                } catch (e: Exception) {
                    Log.e(TAG, "Error during speech synthesis", e)
                    isCurrentlySpeaking.value = false
                    onSpeakingStateChanged?.invoke(false)
                    onError?.invoke("Speech synthesis failed: ${e.message}")
                }
            }

        } catch (e: Exception) {
            Log.e(TAG, "Error initiating speech", e)
            onError?.invoke("Failed to start speech: ${e.message}")
        }
    }

    fun stopSpeaking() {
        try {
            currentSpeechJob?.cancel()
            textToSpeech?.stop()
            isCurrentlySpeaking.value = false
            onSpeakingStateChanged?.invoke(false)
            Log.d(TAG, "Speech stopped")
        } catch (e: Exception) {
            Log.e(TAG, "Error stopping speech", e)
            onError?.invoke("Failed to stop speech: ${e.message}")
        }
    }

    fun startListening() {
        if (!isInitialized || !isSpeechRecognizerReady) {
            Log.w(TAG, "Speech recognizer not initialized")
            onError?.invoke("Speech recognition not ready")
            return
        }

        try {
            // Stop any current speaking
            if (isCurrentlySpeaking.value) {
                stopSpeaking()
                // Add small delay to ensure TTS stops before starting recognition
                serviceScope.launch {
                    delay(500)
                    performStartListening()
                }
            } else {
                performStartListening()
            }

        } catch (e: Exception) {
            Log.e(TAG, "Error starting speech recognition", e)
            onError?.invoke("Failed to start listening: ${e.message}")
        }
    }

    private fun performStartListening() {
        try {
            val intent =
                android.content.Intent(android.speech.RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
                    .apply {
                        putExtra(
                            android.speech.RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                            android.speech.RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                        )
                        putExtra(
                            android.speech.RecognizerIntent.EXTRA_LANGUAGE,
                            Locale.getDefault()
                        )
                        putExtra(android.speech.RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
                        putExtra(android.speech.RecognizerIntent.EXTRA_MAX_RESULTS, 1)
                        putExtra(
                            android.speech.RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS,
                            3000
                        )
                        putExtra(
                            android.speech.RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS,
                            3000
                        )
                    }

            speechRecognizer?.startListening(intent)
            Log.d(TAG, "Started listening for speech")

        } catch (e: Exception) {
            Log.e(TAG, "Error performing speech recognition", e)
            isCurrentlyListening.value = false
            onListeningStateChanged?.invoke(false)
            onError?.invoke("Speech recognition failed: ${e.message}")
        }
    }

    fun stopListening() {
        try {
            speechRecognizer?.stopListening()
            isCurrentlyListening.value = false
            onListeningStateChanged?.invoke(false)
            Log.d(TAG, "Stopped listening")
        } catch (e: Exception) {
            Log.e(TAG, "Error stopping speech recognition", e)
            onError?.invoke("Failed to stop listening: ${e.message}")
        }
    }

    // Callback setters
    fun setOnSpeechResult(callback: (String) -> Unit) {
        onSpeechResult = callback
    }

    fun setOnPartialSpeechResult(callback: (String) -> Unit) {
        onPartialSpeechResult = callback
    }

    fun setOnSpeakingStateChanged(callback: (Boolean) -> Unit) {
        onSpeakingStateChanged = callback
    }

    fun setOnListeningStateChanged(callback: (Boolean) -> Unit) {
        onListeningStateChanged = callback
    }

    fun setOnError(callback: (String) -> Unit) {
        onError = callback
    }

    // State getters
    fun isSpeaking(): Boolean = isCurrentlySpeaking.value
    fun isListening(): Boolean = isCurrentlyListening.value
    fun isReady(): Boolean = isInitialized

    // Lifecycle management
    override fun onPause(owner: LifecycleOwner) {
        try {
            stopSpeaking()
            stopListening()
        } catch (e: Exception) {
            Log.e(TAG, "Error pausing voice service", e)
        }
    }

    override fun onDestroy(owner: LifecycleOwner) {
        cleanup()
    }

    fun cleanup() {
        try {
            Log.i(TAG, "Cleaning up voice service")

            // Cancel any ongoing operations
            currentSpeechJob?.cancel()
            serviceScope.cancel()

            // Stop current operations
            stopSpeaking()
            stopListening()

            // Clean up TTS
            textToSpeech?.apply {
                stop()
                shutdown()
            }
            textToSpeech = null

            // Clean up speech recognizer
            speechRecognizer?.apply {
                cancel()
                destroy()
            }
            speechRecognizer = null

            // Reset state
            isInitialized = false
            isTtsInitialized = false
            isSpeechRecognizerReady = false
            isCurrentlySpeaking.value = false
            isCurrentlyListening.value = false

            Log.i(TAG, "Voice service cleanup completed")

        } catch (e: Exception) {
            Log.e(TAG, "Error during cleanup", e)
        }
    }
}
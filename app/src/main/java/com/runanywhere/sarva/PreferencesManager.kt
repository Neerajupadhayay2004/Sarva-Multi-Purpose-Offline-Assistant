package com.runanywhere.sarva

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class PreferencesManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("medical_assistant_prefs", Context.MODE_PRIVATE)

    private val _downloadedModelId = MutableStateFlow(prefs.getString("downloaded_model_id", null))
    val downloadedModelId: StateFlow<String?> = _downloadedModelId

    private val _isModelDownloaded =
        MutableStateFlow(prefs.getBoolean("is_model_downloaded", false))
    val isModelDownloaded: StateFlow<Boolean> = _isModelDownloaded

    private val _isFirstLaunch = MutableStateFlow(prefs.getBoolean("first_launch", true))
    val isFirstLaunch: StateFlow<Boolean> = _isFirstLaunch

    private val _isRagEnabled = MutableStateFlow(prefs.getBoolean("rag_enabled", true))
    val isRagEnabled: StateFlow<Boolean> = _isRagEnabled

    private val _isVoiceModeEnabled =
        MutableStateFlow(prefs.getBoolean("voice_mode_enabled", false))
    val isVoiceModeEnabled: StateFlow<Boolean> = _isVoiceModeEnabled

    fun setDownloadedModel(modelId: String) {
        prefs.edit()
            .putString("downloaded_model_id", modelId)
            .putBoolean("is_model_downloaded", true)
            .apply()
        _downloadedModelId.value = modelId
        _isModelDownloaded.value = true
    }

    fun setFirstLaunchCompleted() {
        prefs.edit()
            .putBoolean("first_launch", false)
            .apply()
        _isFirstLaunch.value = false
    }

    fun setRagEnabled(enabled: Boolean) {
        prefs.edit()
            .putBoolean("rag_enabled", enabled)
            .apply()
        _isRagEnabled.value = enabled
    }

    fun setVoiceModeEnabled(enabled: Boolean) {
        prefs.edit()
            .putBoolean("voice_mode_enabled", enabled)
            .apply()
        _isVoiceModeEnabled.value = enabled
    }

    fun clearModelData() {
        prefs.edit()
            .remove("downloaded_model_id")
            .putBoolean("is_model_downloaded", false)
            .apply()
        _downloadedModelId.value = null
        _isModelDownloaded.value = false
    }
}
package com.runanywhere.sarva

import android.app.Application
import android.util.Log
import com.runanywhere.sdk.public.RunAnywhere
import com.runanywhere.sdk.data.models.SDKEnvironment
import com.runanywhere.sdk.public.extensions.addModelFromURL
import com.runanywhere.sdk.llm.llamacpp.LlamaCppServiceProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // Initialize SDK asynchronously
        GlobalScope.launch(Dispatchers.IO) {
            initializeSDK()
        }
    }

    private suspend fun initializeSDK() {
        try {
            // Step 1: Initialize SDK
            RunAnywhere.initialize(
                context = this@MyApplication,
                apiKey = "dev",  // Any string works in dev mode
                environment = SDKEnvironment.DEVELOPMENT
            )

            // Step 2: Register LLM Service Provider
            LlamaCppServiceProvider.register()

            // Step 3: Register AI Models
            registerModels()

            // Step 4: Scan for previously downloaded models
            RunAnywhere.scanForDownloadedModels()

            Log.i("MyApp", "AI Assistant SDK initialized successfully")

        } catch (e: Exception) {
            Log.e("MyApp", "AI Assistant SDK initialization failed: ${e.message}")
        }
    }

    private suspend fun registerModels() {
        // Lightning Fast AI - Quick Response Model (374 MB)
        addModelFromURL(
            url = "https://huggingface.co/Triangle104/Qwen2.5-0.5B-Instruct-Q6_K-GGUF/resolve/main/qwen2.5-0.5b-instruct-q6_k.gguf",
            name = "Doctor Model By Sarva",
            type = "LLM"
        )

        // Conversational AI - Natural Dialogue Model (1.2 GB)
        addModelFromURL(
            url = "https://huggingface.co/microsoft/DialoGPT-medium/resolve/main/pytorch_model.bin",
            name = "Conversational AI - Natural Chat",
            type = "LLM"
        )

        // Precision AI - Detailed Analysis Model (892 MB)
        addModelFromURL(
            url = "https://huggingface.co/google/flan-t5-base/resolve/main/pytorch_model.bin",
            name = "Precision AI - Deep Analysis",
            type = "LLM"
        )

        // Expert AI - Advanced Reasoning Model (2.1 GB)
        addModelFromURL(
            url = "https://huggingface.co/microsoft/BioGPT-Large/resolve/main/pytorch_model.bin",
            name = "Expert AI - Advanced Intelligence",
            type = "LLM"
        )

        // Code AI - Programming Assistant Model (687 MB)
        addModelFromURL(
            url = "https://huggingface.co/microsoft/CodeBERT-base/resolve/main/pytorch_model.bin",
            name = "Code AI - Programming Assistant",
            type = "LLM"
        )

        // Creative AI - Content Generation Model (1.5 GB)
        addModelFromURL(
            url = "https://huggingface.co/gpt2-medium/resolve/main/pytorch_model.bin",
            name = "Creative AI - Content Generator",
            type = "LLM"
        )
    }
}

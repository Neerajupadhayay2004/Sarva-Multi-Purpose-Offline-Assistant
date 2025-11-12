package com.runanywhere.sarva

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.runanywhere.sarva.ui.theme.Startup_hackathon20Theme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Startup_hackathon20Theme {
                ModernAIAssistantApp()
            }
        }
    }
}

@Composable
fun ModernAIAssistantApp() {
    val context = LocalContext.current
    val viewModel: ChatViewModel = viewModel { ChatViewModel(context) }

    val appState by viewModel.appState.collectAsState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = TechTheme.backgroundPrimary
    ) {
        when (appState) {
            AppState.INITIALIZING -> ModernInitializingScreen()
            AppState.FIRST_LAUNCH_SETUP -> ModernModelSetupScreen(viewModel)
            AppState.MODEL_DOWNLOADING -> ModernDownloadingScreen(viewModel)
            AppState.MODEL_LOADING -> ModernLoadingScreen()
            AppState.READY -> ModernChatScreen(viewModel)
            AppState.ERROR -> ModernErrorScreen(viewModel)
        }
    }
}

@Composable
fun ModernInitializingScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        TechTheme.backgroundPrimary,
                        TechTheme.backgroundSecondary
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            // Modern AI Icon
            Box(
                modifier = Modifier.size(120.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = TechTheme.accentBlue,
                    strokeWidth = 3.dp,
                    modifier = Modifier.size(120.dp)
                )
                Text(
                    text = "AI",
                    style = MaterialTheme.typography.headlineLarge,
                    color = TechTheme.textPrimary,
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                text = "AI Assistant",
                style = MaterialTheme.typography.headlineMedium,
                color = TechTheme.textPrimary,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Text(
                text = "Initializing multi-model AI system...",
                style = MaterialTheme.typography.bodyLarge,
                color = TechTheme.textSecondary,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun ModernModelSetupScreen(viewModel: ChatViewModel) {
    val availableModels by viewModel.availableModels.collectAsState()
    val statusMessage by viewModel.statusMessage.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        TechTheme.backgroundPrimary,
                        TechTheme.backgroundSecondary
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            // Professional header with branding
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Modern AI logo
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    TechTheme.accentBlue,
                                    TechTheme.accentPurple
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.AutoAwesome,
                        contentDescription = "AI Assistant",
                        tint = Color.White,
                        modifier = Modifier.size(40.dp)
                    )
                }

                Text(
                    text = "AI Assistant Pro",
                    style = MaterialTheme.typography.headlineLarge,
                    color = TechTheme.textPrimary,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "Choose your AI model to get started. Each model is optimized for different types of tasks and conversations.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = TechTheme.textSecondary,
                    textAlign = TextAlign.Center,
                    lineHeight = 24.sp,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Models List with professional cards
            if (availableModels.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = TechTheme.surfacePrimary),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        CircularProgressIndicator(
                            color = TechTheme.accentBlue,
                            strokeWidth = 3.dp,
                            modifier = Modifier.size(48.dp)
                        )
                        Text(
                            text = statusMessage,
                            style = MaterialTheme.typography.bodyLarge,
                            color = TechTheme.textSecondary,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(availableModels) { model ->
                        ProfessionalModelCard(
                            model = model,
                            onDownload = { viewModel.downloadModel(model.id) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Professional footer
            Text(
                text = "You can change models anytime from the settings",
                style = MaterialTheme.typography.bodySmall,
                color = TechTheme.textTertiary,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun ProfessionalModelCard(
    model: com.runanywhere.sdk.models.ModelInfo,
    onDownload: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = TechTheme.surfacePrimary),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            TechTheme.messageBorder
        )
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    // Model icon with gradient background
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(
                                brush = Brush.radialGradient(
                                    colors = when {
                                        model.name.contains("Lightning", ignoreCase = true) ->
                                            listOf(TechTheme.accentYellow, TechTheme.accentOrange)

                                        model.name.contains("Conversational", ignoreCase = true) ->
                                            listOf(TechTheme.accentGreen, TechTheme.accentCyan)

                                        model.name.contains("Precision", ignoreCase = true) ->
                                            listOf(TechTheme.accentBlue, TechTheme.accentPurple)

                                        model.name.contains("Expert", ignoreCase = true) ->
                                            listOf(TechTheme.accentPurple, TechTheme.accentPink)

                                        model.name.contains("Code", ignoreCase = true) ->
                                            listOf(TechTheme.accentCyan, TechTheme.accentBlue)

                                        model.name.contains("Creative", ignoreCase = true) ->
                                            listOf(TechTheme.accentPink, TechTheme.accentOrange)

                                        else -> listOf(TechTheme.accentBlue, TechTheme.accentPurple)
                                    }
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = when {
                                model.name.contains("Lightning", ignoreCase = true) -> "⚡"
                                model.name.contains("Conversational", ignoreCase = true) -> "👋"
                                model.name.contains("Precision", ignoreCase = true) -> "🎯"
                                model.name.contains("Expert", ignoreCase = true) -> "🧠"
                                model.name.contains("Code", ignoreCase = true) -> "💻"
                                model.name.contains("Creative", ignoreCase = true) -> "🎨"
                                else -> "🤖"
                            },
                            fontSize = 20.sp
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = model.name,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = TechTheme.textPrimary
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = getModelDescription(model.name),
                            style = MaterialTheme.typography.bodyMedium,
                            color = TechTheme.textSecondary,
                            lineHeight = 20.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Model specifications row
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Size badge
                            Surface(
                                color = TechTheme.accentCyan.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Storage,
                                        contentDescription = null,
                                        tint = TechTheme.accentCyan,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Text(
                                        text = getModelSize(model.name),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = TechTheme.accentCyan,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }

                            // Performance indicator
                            val (performanceText, performanceColor) = when {
                                model.name.contains("Lightning", ignoreCase = true) ->
                                    "Ultra Fast" to TechTheme.accentYellow

                                model.name.contains("Expert", ignoreCase = true) ->
                                    "Advanced" to TechTheme.accentPurple

                                model.name.contains("Precision", ignoreCase = true) ->
                                    "Detailed" to TechTheme.accentBlue

                                else -> "Balanced" to TechTheme.accentGreen
                            }

                            Surface(
                                color = performanceColor.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = performanceText,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = performanceColor,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Action button
            Button(
                onClick = onDownload,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (model.isDownloaded) TechTheme.success else TechTheme.accentBlue
                ),
                shape = RoundedCornerShape(12.dp),
                enabled = !model.isDownloaded
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = if (model.isDownloaded) Icons.Filled.CheckCircle else Icons.Filled.Download,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = if (model.isDownloaded) "Downloaded" else "Download & Install",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun ModernDownloadingScreen(viewModel: ChatViewModel) {
    val downloadProgress by viewModel.downloadProgress.collectAsState()
    val statusMessage by viewModel.statusMessage.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        TechTheme.backgroundPrimary,
                        TechTheme.backgroundSecondary
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            colors = CardDefaults.cardColors(containerColor = TechTheme.surfacePrimary),
            elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
        ) {
            Column(
                modifier = Modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Setting Up AI Model",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = TechTheme.textPrimary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = statusMessage,
                    style = MaterialTheme.typography.bodyLarge,
                    color = TechTheme.textSecondary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(32.dp))

                downloadProgress?.let { progress ->
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = TechTheme.accentBlue,
                        trackColor = TechTheme.surfaceSecondary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "${(progress * 100).toInt()}%",
                        style = MaterialTheme.typography.titleMedium,
                        color = TechTheme.accentBlue,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun ModernLoadingScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        TechTheme.backgroundPrimary,
                        TechTheme.backgroundSecondary
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            CircularProgressIndicator(
                color = TechTheme.accentBlue,
                strokeWidth = 4.dp,
                modifier = Modifier.size(64.dp)
            )

            Text(
                text = "Loading AI Assistant...",
                style = MaterialTheme.typography.headlineSmall,
                color = TechTheme.textPrimary,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Preparing your personalized AI experience",
                style = MaterialTheme.typography.bodyMedium,
                color = TechTheme.textSecondary
            )
        }
    }
}

@Composable
fun ModernChatScreen(viewModel: ChatViewModel) {
    val context = LocalContext.current
    val messages by viewModel.messages.collectAsState()
    val isProcessing by viewModel.isProcessing.collectAsState()
    val statusMessage by viewModel.statusMessage.collectAsState()
    val ragStatus by viewModel.ragStatus.collectAsState()
    val isRagEnabled by viewModel.isRagEnabled.collectAsState()
    val isVoiceModeEnabled by viewModel.isVoiceModeEnabled.collectAsState()
    val isListening by viewModel.isListening.collectAsState()
    val isSpeaking by viewModel.isSpeaking.collectAsState()
    val partialText by viewModel.partialText.collectAsState()
    val conversationMode by viewModel.conversationMode.collectAsState()
    val currentModelName by viewModel.currentModelName.collectAsState()
    val availableModels by viewModel.availableModels.collectAsState()
    val currentModelId by viewModel.currentModelId.collectAsState()
    val chatHistory by viewModel.chatHistory.collectAsState()
    val currentChatId by viewModel.currentChatId.collectAsState()
    val currentModelConfig by viewModel.currentModelConfig.collectAsState()

    var inputText by remember { mutableStateOf("") }
    var showModelManager by remember { mutableStateOf(false) }
    var showChatHistory by remember { mutableStateOf(false) }
    
    // Auto-scroll state
    val listState = rememberLazyListState()
    
    // Auto-scroll to bottom when new messages arrive or processing changes
    LaunchedEffect(messages.size, isProcessing) {
        if (messages.isNotEmpty()) {
            delay(150) // Increased delay to ensure layout is complete
            listState.animateScrollToItem(
                index = messages.size - 1,
                scrollOffset = 0
            )
        }
    }

    // Check microphone permission
    val hasPermission = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.RECORD_AUDIO
    ) == PackageManager.PERMISSION_GRANTED

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(TechTheme.backgroundPrimary)
            .systemBarsPadding() // Prevents overlap with system bars
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Modern Header
            ModernHeader(
                statusMessage = statusMessage,
                ragStatus = ragStatus,
                currentModel = currentModelName,
                onNewChatClick = { 
                    viewModel.startNewChat()
                    showChatHistory = false
                    showModelManager = false
                },
                onChatHistoryClick = { 
                    showChatHistory = !showChatHistory
                    if (showChatHistory) showModelManager = false
                },
                onModelManagerClick = { 
                    showModelManager = !showModelManager
                    if (showModelManager) showChatHistory = false
                }
            )

            // Messages - Using Box with weight and ensuring it doesn't overlap input
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        end = 16.dp,
                        top = 16.dp,
                        bottom = 200.dp // HUGE bottom padding to ensure nothing is hidden by input/panels
                    ),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    state = listState
                ) {
                    if (messages.isEmpty()) {
                        item {
                            WelcomeScreen(currentModelName ?: "AI Assistant")
                        }
                    }

                    items(messages.size) { index ->
                        ModernMessageBubble(messages[index])
                    }

                    // Typing indicator
                    if (isProcessing) {
                        item {
                            ModernTypingIndicator()
                        }
                    }
                    
                    // Extra large bottom spacing to ensure last message is never hidden
                    item {
                        Spacer(modifier = Modifier.height(200.dp)) // Much larger spacing
                    }
                }
            }
        }

        // Overlay panels - positioned to NOT cover input area
        if (showChatHistory) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(TechTheme.overlayDark)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) { showChatHistory = false }
            ) {
                // Position panel with max height - ABOVE input area
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 160.dp) // Space for input area only
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) { /* Prevent click-through */ }
                ) {
                    ChatHistoryPanel(
                        chatHistory = chatHistory,
                        currentChatId = currentChatId,
                        onChatSelect = { chatId ->
                            viewModel.selectChat(chatId)
                            showChatHistory = false
                        },
                        onDeleteChat = viewModel::deleteChat,
                        onClose = { showChatHistory = false }
                    )
                }
            }
        }

        if (showModelManager) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(TechTheme.overlayDark)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) { showModelManager = false }
            ) {
                // Position panel with max height - ABOVE input area
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 160.dp) // Space for input area only
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) { /* Prevent click-through */ }
                ) {
                    ModernModelManagerPanel(
                        models = availableModels,
                        currentModelId = currentModelId,
                        isRagEnabled = isRagEnabled,
                        isVoiceModeEnabled = isVoiceModeEnabled,
                        onModelSelect = { modelId ->
                            viewModel.selectModel(modelId)
                            showModelManager = false
                        },
                        onModelDownload = viewModel::downloadModel,
                        onToggleRag = viewModel::toggleRag,
                        onToggleVoice = viewModel::toggleVoiceMode,
                        hasPermission = hasPermission,
                        onClose = { showModelManager = false }
                    )
                }
            }
        }

        // Fixed Input Area at bottom - with proper elevation
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        ) {
            ModernInputArea(
                inputText = inputText,
                onInputChange = { inputText = it },
                onSendMessage = {
                    if (inputText.isNotBlank()) {
                        Log.d("MainActivity", "📤 Sending text message: '$inputText'")
                        viewModel.sendMessage(inputText)
                        inputText = ""
                    }
                },
                onVoiceToggle = {
                    if (hasPermission) {
                        Log.d(
                            "MainActivity",
                            "🎤 Voice button clicked - conversationMode=$conversationMode, isListening=$isListening, isSpeaking=$isSpeaking, isVoiceModeEnabled=$isVoiceModeEnabled"
                        )
                        
                        if (conversationMode) {
                            Log.d("MainActivity", "❌ Stopping conversation mode")
                            viewModel.toggleConversationMode()
                        } else if (isListening) {
                            Log.d("MainActivity", "❌ Stopping voice input")
                            viewModel.stopVoiceInput()
                        } else if (isSpeaking) {
                            Log.d("MainActivity", "❌ Stopping AI speaking")
                            viewModel.stopSpeaking()
                        } else {
                            // Enable voice mode and start conversation
                            Log.d("MainActivity", "✅ Starting voice/conversation mode")
                            if (!isVoiceModeEnabled) {
                                Log.d("MainActivity", "🔄 Enabling voice mode first")
                                viewModel.toggleVoiceMode()
                            }
                            Log.d("MainActivity", "🔄 Starting conversation mode")
                            viewModel.toggleConversationMode()
                        }
                    } else {
                        Log.w("MainActivity", "❌ Microphone permission not granted!")
                    }
                },
                isProcessing = isProcessing,
                isVoiceModeEnabled = isVoiceModeEnabled,
                isListening = isListening,
                isSpeaking = isSpeaking,
                partialText = partialText,
                conversationMode = conversationMode,
                onStopSpeaking = viewModel::stopSpeaking,
                currentModelConfig = currentModelConfig
            )
        }
    }
}

@Composable
fun ModernErrorScreen(viewModel: ChatViewModel) {
    val statusMessage by viewModel.statusMessage.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(TechTheme.backgroundPrimary),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            colors = CardDefaults.cardColors(containerColor = TechTheme.surfacePrimary),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Filled.Error,
                    contentDescription = "Error",
                    tint = TechTheme.accentRed,
                    modifier = Modifier.size(64.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "System Error",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = TechTheme.accentRed,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Text(
                    text = statusMessage,
                    style = MaterialTheme.typography.bodyLarge,
                    color = TechTheme.textSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                Button(
                    onClick = { viewModel.refreshModels() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TechTheme.accentBlue
                    )
                ) {
                    Icon(
                        imageVector = Icons.Filled.Refresh,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Retry Setup")
                }
            }
        }
    }
}

@Composable
fun ModernInputArea(
    inputText: String,
    onInputChange: (String) -> Unit,
    onSendMessage: () -> Unit,
    onVoiceToggle: () -> Unit,
    isProcessing: Boolean,
    isVoiceModeEnabled: Boolean,
    isListening: Boolean,
    isSpeaking: Boolean,
    partialText: String,
    conversationMode: Boolean,
    onStopSpeaking: () -> Unit,
    currentModelConfig: ModelTextConfig
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = TechTheme.surfacePrimary,
        shadowElevation = 12.dp
    ) {
        Column(
            modifier = Modifier.padding(
                start = 20.dp,
                end = 20.dp,
                top = 20.dp,
                bottom = 32.dp // Extra bottom padding to avoid navigation bar overlap
            )
        ) {
            // Voice Status Panel with modern design
            if (isListening || isSpeaking || conversationMode) {
                ModernVoiceStatusPanel(
                    isListening = isListening,
                    isSpeaking = isSpeaking,
                    partialText = partialText,
                    conversationMode = conversationMode,
                    onStopSpeaking = onStopSpeaking
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Input Row with modern styling
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                // Text Input with dynamic placeholder based on model
                TextField(
                    value = inputText,
                    onValueChange = onInputChange,
                    modifier = Modifier.weight(1f),
                    placeholder = {
                        Text(
                            text = currentModelConfig.placeholderText,
                            color = TechTheme.textTertiary,
                            fontSize = 15.sp
                        )
                    },
                    enabled = !isProcessing && !isListening,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = TechTheme.inputBackground,
                        unfocusedContainerColor = TechTheme.inputBackground,
                        focusedIndicatorColor = TechTheme.inputBorderFocused,
                        unfocusedIndicatorColor = TechTheme.inputBorder,
                        focusedTextColor = TechTheme.inputText,
                        unfocusedTextColor = TechTheme.inputText,
                        cursorColor = TechTheme.inputBorderFocused,
                        focusedPlaceholderColor = TechTheme.inputPlaceholder,
                        unfocusedPlaceholderColor = TechTheme.inputPlaceholder
                    ),
                    shape = RoundedCornerShape(24.dp),
                    maxLines = 4,
                    textStyle = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 15.sp,
                        lineHeight = 20.sp
                    )
                )

                // Action Buttons with professional styling
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Voice Button with dynamic states and proper icons
                    FloatingActionButton(
                        onClick = onVoiceToggle,
                        containerColor = when {
                            conversationMode -> TechTheme.voiceActive
                            isListening -> TechTheme.voiceListening
                            isSpeaking -> TechTheme.voiceSpeaking
                            isVoiceModeEnabled -> TechTheme.accentBlue
                            else -> TechTheme.surfaceSecondary
                        },
                        contentColor = Color.White,
                        modifier = Modifier.size(56.dp),
                        elevation = FloatingActionButtonDefaults.elevation(
                            defaultElevation = if (conversationMode || isListening || isSpeaking) 8.dp else 4.dp
                        )
                    ) {
                        Icon(
                            imageVector = when {
                                conversationMode -> Icons.Filled.RecordVoiceOver
                                isListening -> Icons.Filled.MicOff
                                isSpeaking -> Icons.Filled.VolumeOff
                                isVoiceModeEnabled -> Icons.Filled.Mic
                                else -> Icons.Filled.Mic
                            },
                            contentDescription = when {
                                conversationMode -> "Exit Conversation Mode"
                                isListening -> "Stop Listening"
                                isSpeaking -> "Stop Speaking"
                                isVoiceModeEnabled -> "Start Voice Input"
                                else -> "Enable Voice Mode"
                            },
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    // Send Button (show when text is available or not in voice-only mode)
                    if (inputText.isNotBlank() || (!isVoiceModeEnabled && !conversationMode)) {
                        FloatingActionButton(
                            onClick = onSendMessage,
                            containerColor = if (inputText.isNotBlank())
                                TechTheme.accentBlue
                            else
                                TechTheme.surfaceSecondary,
                            contentColor = Color.White,
                            modifier = Modifier.size(56.dp),
                            elevation = FloatingActionButtonDefaults.elevation(
                                defaultElevation = if (inputText.isNotBlank()) 6.dp else 2.dp
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Send,
                                contentDescription = "Send Message",
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }

            // Professional hint text for voice features
            if (isVoiceModeEnabled || conversationMode) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = when {
                        conversationMode -> "🔄 Conversation mode active - speak naturally"
                        isListening -> "🎤 Listening... speak your question"
                        isSpeaking -> "🔊 AI is speaking - tap microphone to interrupt"
                        else -> "🎙️ Voice mode ready - tap microphone to speak"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = TechTheme.textTertiary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    fontSize = 13.sp
                )
            }
        }
    }
}
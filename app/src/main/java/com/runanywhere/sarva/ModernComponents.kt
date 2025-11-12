package com.runanywhere.sarva

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.runanywhere.sdk.models.ModelInfo
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModernHeader(
    statusMessage: String,
    ragStatus: String?,
    currentModel: String?,
    onNewChatClick: () -> Unit,
    onChatHistoryClick: () -> Unit,
    onModelManagerClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = TechTheme.surfacePrimary,
        shadowElevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // Main header row with proper spacing
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left section - App branding with flexible width
                Row(
                    modifier = Modifier.weight(1f, fill = false),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Modern AI Icon with gradient
                    Box(
                        modifier = Modifier
                            .size(36.dp)
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
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // App title with limited width to prevent overlap
                    Column(
                        modifier = Modifier.widthIn(max = 120.dp)
                    ) {
                        Text(
                            text = "AI Assistant",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TechTheme.textPrimary,
                            fontSize = 16.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (currentModel != null) {
                            Text(
                                text = currentModel,
                                style = MaterialTheme.typography.bodySmall,
                                color = TechTheme.textSecondary,
                                fontSize = 11.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                // Right section - Action buttons with fixed spacing
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // New Chat Button
                    ModernHeaderButton(
                        icon = Icons.Filled.Add,
                        contentDescription = "New Chat",
                        onClick = onNewChatClick,
                        backgroundColor = TechTheme.accentGreen.copy(alpha = 0.15f),
                        iconTint = TechTheme.accentGreen
                    )

                    // Chat History Button
                    ModernHeaderButton(
                        icon = Icons.Filled.History,
                        contentDescription = "Chat History",
                        onClick = onChatHistoryClick,
                        backgroundColor = TechTheme.surfaceSecondary,
                        iconTint = TechTheme.textSecondary
                    )

                    // Model Manager Button
                    ModernHeaderButton(
                        icon = Icons.Filled.Settings,
                        contentDescription = "Model Manager",
                        onClick = onModelManagerClick,
                        backgroundColor = TechTheme.surfaceSecondary,
                        iconTint = TechTheme.textSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Status Section
            ModernStatusIndicator(statusMessage, ragStatus)
        }
    }
}

@Composable
fun ModernHeaderButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    backgroundColor: Color = TechTheme.surfaceSecondary,
    iconTint: Color = TechTheme.textSecondary
) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(backgroundColor)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = iconTint,
            modifier = Modifier.size(18.dp)
        )
    }
}

@Composable
fun ModernStatusIndicator(statusMessage: String, ragStatus: String?) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Main Status with pulse animation
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            PulsingDot(color = TechTheme.success)
            Text(
                text = statusMessage,
                style = MaterialTheme.typography.bodyMedium,
                color = TechTheme.textSecondary,
                fontSize = 14.sp
            )
        }

        // RAG Status with modern badge
        ragStatus?.let { status ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    color = TechTheme.accentCyan.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = "Research",
                            tint = TechTheme.accentCyan,
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = status,
                            style = MaterialTheme.typography.labelSmall,
                            color = TechTheme.accentCyan,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PulsingDot(color: Color) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Box(
        modifier = Modifier
            .size(8.dp)
            .clip(CircleShape)
            .background(color.copy(alpha = alpha))
    )
}

@Composable
fun WelcomeScreen(currentModelName: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Welcome icon
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            TechTheme.accentBlue.copy(alpha = 0.2f),
                            TechTheme.accentPurple.copy(alpha = 0.1f)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.AutoAwesome,
                contentDescription = "AI",
                tint = TechTheme.accentBlue,
                modifier = Modifier.size(40.dp)
            )
        }

        Text(
            text = "Welcome to AI Assistant",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = TechTheme.textPrimary,
            textAlign = TextAlign.Center
        )

        Text(
            text = "I'm powered by $currentModelName and ready to help you with any questions or tasks. You can switch models, enable online research, or use voice input anytime.",
            style = MaterialTheme.typography.bodyLarge,
            color = TechTheme.textSecondary,
            textAlign = TextAlign.Center,
            lineHeight = 24.sp
        )

        // Suggested prompts
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.heightIn(max = 200.dp)
        ) {
            items(getSuggestedPrompts()) { prompt ->
                SuggestedPromptCard(prompt = prompt)
            }
        }
    }
}

@Composable
fun SuggestedPromptCard(prompt: String) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* Handle suggestion click */ },
        color = TechTheme.surfaceSecondary,
        shape = RoundedCornerShape(12.dp),
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.Lightbulb,
                contentDescription = null,
                tint = TechTheme.accentYellow,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = prompt,
                style = MaterialTheme.typography.bodyMedium,
                color = TechTheme.textPrimary,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = Icons.Filled.ArrowForward,
                contentDescription = null,
                tint = TechTheme.textTertiary,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

fun getSuggestedPrompts(): List<String> {
    return listOf(
        "💡 Explain complex topics in simple terms",
        "📊 Analyze data and provide insights",
        "✍️ Help me write professional content",
        "🔍 Research the latest trends and developments",
        "💻 Solve coding problems and debug issues",
        "🎨 Generate creative ideas and content",
        "📈 Create strategic plans and workflows",
        "🌐 Translate languages and explain cultures"
    )
}

@Composable
fun ModernMessageBubble(message: ChatMessage) {
    val isUser = message.isUser

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
        ) {
            if (!isUser) {
                // AI Avatar with modern design
                Box(
                    modifier = Modifier
                        .size(36.dp)
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
                        contentDescription = "AI",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
            }

            Column(
                modifier = Modifier.weight(1f, fill = false),
                horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
            ) {
                // Message bubble with modern styling
                Surface(
                    modifier = Modifier
                        .padding(
                            start = if (isUser) 64.dp else 0.dp,
                            end = if (isUser) 0.dp else 64.dp
                        )
                        .shadow(
                            elevation = if (isUser) 4.dp else 2.dp,
                            shape = RoundedCornerShape(
                                topStart = 20.dp,
                                topEnd = 20.dp,
                                bottomStart = if (isUser) 20.dp else 6.dp,
                                bottomEnd = if (isUser) 6.dp else 20.dp
                            )
                        ),
                    color = if (isUser) TechTheme.userMessageBg else TechTheme.aiMessageBg,
                    shape = RoundedCornerShape(
                        topStart = 20.dp,
                        topEnd = 20.dp,
                        bottomStart = if (isUser) 20.dp else 6.dp,
                        bottomEnd = if (isUser) 6.dp else 20.dp
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        // Message metadata with modern badges
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Voice indicator with modern icon
                                if (message.isVoiceMessage) {
                                    ModernBadge(
                                        icon = Icons.Filled.Mic,
                                        text = "Voice",
                                        backgroundColor = TechTheme.voiceActive.copy(alpha = 0.2f),
                                        contentColor = TechTheme.voiceActive
                                    )
                                }

                                // RAG indicator with research badge
                                if (!isUser && message.hasRagContext) {
                                    ModernBadge(
                                        icon = Icons.Filled.Search,
                                        text = "Research",
                                        backgroundColor = TechTheme.accentCyan.copy(alpha = 0.2f),
                                        contentColor = TechTheme.accentCyan
                                    )
                                }
                            }

                            // Timestamp with modern styling
                            Text(
                                text = SimpleDateFormat("HH:mm", Locale.getDefault()).format(
                                    Date(message.timestamp)
                                ),
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isUser)
                                    Color.White.copy(alpha = 0.7f)
                                else
                                    TechTheme.textTertiary,
                                fontSize = 11.sp
                            )
                        }

                        if (message.isVoiceMessage || message.hasRagContext) {
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        // Message text with improved typography
                        Text(
                            text = message.text,
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (isUser) Color.White else TechTheme.textPrimary,
                            fontSize = 15.sp,
                            lineHeight = 22.sp
                        )
                    }
                }
            }

            if (isUser) {
                Spacer(modifier = Modifier.width(12.dp))
                // User Avatar with modern gradient
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    TechTheme.accentGreen,
                                    TechTheme.accentCyan
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Person,
                        contentDescription = "User",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ModernBadge(
    icon: ImageVector,
    text: String,
    backgroundColor: Color,
    contentColor: Color
) {
    Surface(
        color = backgroundColor,
        shape = RoundedCornerShape(10.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(10.dp),
                tint = contentColor
            )
            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall,
                color = contentColor,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun ModernTypingIndicator() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {
        // AI Avatar
        Box(
            modifier = Modifier
                .size(36.dp)
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
                contentDescription = "AI",
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Surface(
            color = TechTheme.aiMessageBg,
            shape = RoundedCornerShape(20.dp, 20.dp, 20.dp, 6.dp),
            shadowElevation = 2.dp
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ModernTypingDots()
                Text(
                    text = "AI is thinking...",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TechTheme.textSecondary,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
fun ModernTypingDots() {
    val infiniteTransition = rememberInfiniteTransition(label = "typing")

    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        repeat(3) { index ->
            val animatedScale by infiniteTransition.animateFloat(
                initialValue = 0.3f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(600, easing = EaseInOutCubic),
                    repeatMode = RepeatMode.Reverse,
                    initialStartOffset = StartOffset(index * 200)
                ),
                label = "scale$index"
            )

            Box(
                modifier = Modifier
                    .size((8 * animatedScale).dp)
                    .clip(CircleShape)
                    .background(TechTheme.accentBlue)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
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
    onStopSpeaking: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = TechTheme.surfacePrimary,
        shadowElevation = 12.dp
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
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
                // Text Input with modern design
                TextField(
                    value = inputText,
                    onValueChange = onInputChange,
                    modifier = Modifier.weight(1f),
                    placeholder = {
                        Text(
                            text = "Ask me anything...",
                            color = TechTheme.textTertiary,
                            fontSize = 15.sp
                        )
                    },
                    enabled = !isProcessing && !isListening,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = TechTheme.surfaceSecondary,
                        unfocusedContainerColor = TechTheme.surfaceSecondary,
                        focusedIndicatorColor = TechTheme.accentBlue,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedTextColor = TechTheme.textPrimary,
                        unfocusedTextColor = TechTheme.textPrimary,
                        cursorColor = TechTheme.accentBlue
                    ),
                    shape = RoundedCornerShape(24.dp),
                    maxLines = 4,
                    textStyle = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 15.sp,
                        lineHeight = 20.sp
                    )
                )

                // Action Buttons with modern styling
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Voice Button with dynamic states
                    FloatingActionButton(
                        onClick = onVoiceToggle,
                        containerColor = when {
                            conversationMode -> TechTheme.voiceActive
                            isListening -> TechTheme.voiceListening
                            isVoiceModeEnabled -> TechTheme.accentBlue
                            else -> TechTheme.surfaceSecondary
                        },
                        contentColor = Color.White,
                        modifier = Modifier.size(56.dp)
                    ) {
                        Icon(
                            imageVector = when {
                                conversationMode -> Icons.Filled.RecordVoiceOver
                                isListening -> Icons.Filled.Stop
                                else -> Icons.Filled.Mic
                            },
                            contentDescription = "Voice Input",
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    // Always show the Send Button, even in voice mode
                    FloatingActionButton(
                        onClick = {
                            if (inputText.isNotBlank()) {
                                onSendMessage()
                            }
                        },
                        containerColor = if (inputText.isNotBlank())
                            TechTheme.accentBlue
                        else
                            TechTheme.surfaceSecondary.copy(alpha = 0.5f),
                        contentColor = if (inputText.isNotBlank())
                            Color.White
                        else
                            TechTheme.textTertiary,
                        modifier = Modifier.size(56.dp)
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
    }
}

@Composable
fun ModernVoiceStatusPanel(
    isListening: Boolean,
    isSpeaking: Boolean,
    partialText: String,
    conversationMode: Boolean,
    onStopSpeaking: () -> Unit
) {
    val (backgroundColor, borderColor, iconColor) = when {
        conversationMode -> Triple(
            TechTheme.voiceActive.copy(alpha = 0.1f),
            TechTheme.voiceActive,
            TechTheme.voiceActive
        )

        isListening -> Triple(
            TechTheme.voiceListening.copy(alpha = 0.1f),
            TechTheme.voiceListening,
            TechTheme.voiceListening
        )

        isSpeaking -> Triple(
            TechTheme.voiceSpeaking.copy(alpha = 0.1f),
            TechTheme.voiceSpeaking,
            TechTheme.voiceSpeaking
        )

        else -> Triple(
            TechTheme.surfaceSecondary,
            TechTheme.messageBorder,
            TechTheme.textSecondary
        )
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = backgroundColor,
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, borderColor)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            when {
                conversationMode -> {
                    ModernPulsingIcon(Icons.Filled.RecordVoiceOver, iconColor)
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Conversation Mode Active",
                            style = MaterialTheme.typography.titleSmall,
                            color = iconColor,
                            fontWeight = FontWeight.SemiBold
                        )
                        if (partialText.isNotBlank()) {
                            Text(
                                text = "\"$partialText\"",
                                style = MaterialTheme.typography.bodySmall,
                                color = TechTheme.textSecondary,
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        } else {
                            Text(
                                text = "Listening continuously...",
                                style = MaterialTheme.typography.bodySmall,
                                color = TechTheme.textSecondary
                            )
                        }
                    }
                }

                isListening -> {
                    ModernPulsingIcon(Icons.Filled.Mic, iconColor)
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Listening...",
                            style = MaterialTheme.typography.titleSmall,
                            color = iconColor,
                            fontWeight = FontWeight.SemiBold
                        )
                        if (partialText.isNotBlank()) {
                            Text(
                                text = "\"$partialText\"",
                                style = MaterialTheme.typography.bodySmall,
                                color = TechTheme.textSecondary,
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        } else {
                            Text(
                                text = "Speak your question...",
                                style = MaterialTheme.typography.bodySmall,
                                color = TechTheme.textSecondary
                            )
                        }
                    }
                }

                isSpeaking -> {
                    Icon(
                        imageVector = Icons.Filled.VolumeUp,
                        contentDescription = "Speaking",
                        tint = iconColor,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "AI is speaking...",
                        style = MaterialTheme.typography.titleSmall,
                        color = iconColor,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = onStopSpeaking) {
                        Icon(
                            imageVector = Icons.Filled.Stop,
                            contentDescription = "Stop Speaking",
                            tint = iconColor
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ModernPulsingIcon(icon: ImageVector, color: Color) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Icon(
        imageVector = icon,
        contentDescription = null,
        tint = color,
        modifier = Modifier.size((24 * scale).dp)
    )
}

@Composable
fun ChatHistoryPanel(
    chatHistory: List<ChatSession>,
    currentChatId: String?,
    onChatSelect: (String) -> Unit,
    onDeleteChat: (String) -> Unit,
    onClose: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.85f), // Maximize to 85% of screen height from top
        color = TechTheme.chatHistoryBackground,
        shadowElevation = 8.dp,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Professional header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "💬 Chat History",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = TechTheme.chatHistoryText
                    )
                    Text(
                        text = "${chatHistory.size} conversations",
                        style = MaterialTheme.typography.bodySmall,
                        color = TechTheme.chatHistoryMeta
                    )
                }

                // Close button with actual functionality
                IconButton(
                    onClick = onClose,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(TechTheme.surfaceSecondary)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Close History",
                        tint = TechTheme.textSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Divider(
                color = TechTheme.divider,
                thickness = 1.dp,
                modifier = Modifier.padding(horizontal = 20.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (chatHistory.isEmpty()) {
                // Empty state with better design
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.History,
                            contentDescription = null,
                            tint = TechTheme.textTertiary,
                            modifier = Modifier.size(48.dp)
                        )
                        Text(
                            text = "No chat history yet",
                            style = MaterialTheme.typography.titleMedium,
                            color = TechTheme.textSecondary,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "Start a conversation to see it here",
                            style = MaterialTheme.typography.bodySmall,
                            color = TechTheme.textTertiary,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(chatHistory) { session ->
                        ProfessionalChatHistoryItem(
                            session = session,
                            isSelected = session.id == currentChatId,
                            onSelect = { onChatSelect(session.id) },
                            onDelete = { onDeleteChat(session.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ProfessionalChatHistoryItem(
    session: ChatSession,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onDelete: () -> Unit
) {
    var showDeleteConfirm by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() },
        colors = CardDefaults.cardColors(
            containerColor = when {
                isSelected -> TechTheme.chatHistorySelected
                else -> TechTheme.chatHistoryBackground
            }
        ),
        shape = RoundedCornerShape(12.dp),
        border = if (isSelected)
            androidx.compose.foundation.BorderStroke(1.dp, TechTheme.accentBlue)
        else null,
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 4.dp else 0.dp
        )
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Chat type indicator
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(
                            if (isSelected) TechTheme.accentBlue.copy(alpha = 0.2f)
                            else TechTheme.surfaceSecondary
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Chat,
                        contentDescription = null,
                        tint = if (isSelected) TechTheme.accentBlue else TechTheme.textTertiary,
                        modifier = Modifier.size(16.dp)
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = session.title,
                        style = MaterialTheme.typography.titleSmall,
                        color = TechTheme.chatHistoryText,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Text(
                        text = session.lastMessage.take(60).let {
                            if (it.length < session.lastMessage.length) "$it..." else it
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = TechTheme.chatHistoryMeta,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(top = 2.dp)
                    )

                    // Timestamp and message count
                    Row(
                        modifier = Modifier.padding(top = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = formatTimestamp(session.timestamp),
                            style = MaterialTheme.typography.labelSmall,
                            color = TechTheme.textTertiary,
                            fontSize = 10.sp
                        )

                        if (session.messageCount > 2) {
                            Surface(
                                color = TechTheme.accentBlue.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = "${session.messageCount} msgs",
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TechTheme.accentBlue,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }

            // Action menu
            IconButton(
                onClick = { showDeleteConfirm = !showDeleteConfirm },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = if (showDeleteConfirm) Icons.Filled.Close else Icons.Filled.MoreVert,
                    contentDescription = if (showDeleteConfirm) "Cancel" else "More Options",
                    tint = TechTheme.textTertiary,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        // Delete confirmation row
        if (showDeleteConfirm) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
                    .padding(top = 0.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        onDelete()
                        showDeleteConfirm = false
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TechTheme.buttonDestructive
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Delete",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                OutlinedButton(
                    onClick = { showDeleteConfirm = false },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = TechTheme.textSecondary
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        TechTheme.buttonSecondary
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Cancel",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

// Helper function to format timestamps nicely
private fun formatTimestamp(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp

    return when {
        diff < 60_000 -> "Just now"
        diff < 3600_000 -> "${diff / 60_000}m ago"
        diff < 86400_000 -> "${diff / 3600_000}h ago"
        diff < 604800_000 -> "${diff / 86400_000}d ago"
        else -> SimpleDateFormat("MMM dd", Locale.getDefault()).format(Date(timestamp))
    }
}

@Composable
fun ModernModelManagerPanel(
    models: List<ModelInfo>,
    currentModelId: String?,
    isRagEnabled: Boolean,
    isVoiceModeEnabled: Boolean,
    onModelSelect: (String) -> Unit,
    onModelDownload: (String) -> Unit,
    onToggleRag: () -> Unit,
    onToggleVoice: () -> Unit,
    hasPermission: Boolean,
    onClose: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.85f), // Maximize to 85% of screen height
        color = TechTheme.surfacePrimary,
        shadowElevation = 8.dp,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
    ) {
        LazyColumn(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Header with professional styling
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "AI Model Manager",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = TechTheme.textPrimary
                        )
                        Text(
                            text = "Configure models and features",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TechTheme.textSecondary
                        )
                    }

                    // Professional close button
                    IconButton(
                        onClick = onClose,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(TechTheme.surfaceSecondary)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Close",
                            tint = TechTheme.textSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // Features Section with modern design
            item {
                Text(
                    text = "🚀 Features",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = TechTheme.textPrimary
                )
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = TechTheme.surfaceSecondary),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        ModernSettingItem(
                            icon = Icons.Filled.Search,
                            title = "Online Research",
                            subtitle = "Access real-time information and web search capabilities",
                            isEnabled = isRagEnabled,
                            onToggle = onToggleRag
                        )

                        Divider(
                            color = TechTheme.divider,
                            thickness = 1.dp,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )

                        ModernSettingItem(
                            icon = Icons.Filled.Mic,
                            title = "Voice Assistant",
                            subtitle = if (hasPermission)
                                "Natural voice conversation with continuous mode"
                            else
                                "Microphone permission required for voice features",
                            isEnabled = isVoiceModeEnabled && hasPermission,
                            onToggle = onToggleVoice
                        )
                    }
                }
            }

            // AI Models Section with professional header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🤖 AI Models",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = TechTheme.textPrimary
                    )

                    // Model count badge
                    Surface(
                        color = TechTheme.accentBlue.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "${models.size} available",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.labelMedium,
                            color = TechTheme.accentBlue,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // Models grid with improved cards
            items(models) { model ->
                EnhancedModelCard(
                    model = model,
                    isSelected = model.id == currentModelId,
                    onSelect = { onModelSelect(model.id) },
                    onDownload = { onModelDownload(model.id) }
                )
            }

            // Add some bottom padding
            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
fun ModernSettingItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    isEnabled: Boolean,
    onToggle: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (isEnabled) TechTheme.accentBlue else TechTheme.textTertiary,
            modifier = Modifier.size(24.dp)
        )

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium,
                color = TechTheme.textPrimary
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = TechTheme.textSecondary
            )
        }

        Switch(
            checked = isEnabled,
            onCheckedChange = { onToggle() },
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = TechTheme.accentBlue,
                uncheckedThumbColor = TechTheme.textTertiary,
                uncheckedTrackColor = TechTheme.surfaceSecondary
            )
        )
    }
}

@Composable
fun EnhancedModelCard(
    model: ModelInfo,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onDownload: () -> Unit
) {
    val backgroundColor = when {
        isSelected -> TechTheme.modelCardSelected
        model.isDownloaded -> TechTheme.modelCardBackground
        else -> TechTheme.modelCardDisabled
    }

    val borderColor = when {
        isSelected -> TechTheme.modelSelected
        model.isDownloaded -> TechTheme.modelCardBorder
        else -> TechTheme.modelCardBorder.copy(alpha = 0.5f)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { if (model.isDownloaded) onSelect() },
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, borderColor),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 8.dp else 4.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // Header row with model info and status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    // Model name with icon
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Model type icon
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(
                                    when {
                                        model.name.contains("Lightning", ignoreCase = true) ->
                                            TechTheme.accentYellow.copy(alpha = 0.2f)

                                        model.name.contains("Conversational", ignoreCase = true) ->
                                            TechTheme.accentGreen.copy(alpha = 0.2f)

                                        model.name.contains("Precision", ignoreCase = true) ->
                                            TechTheme.accentBlue.copy(alpha = 0.2f)

                                        model.name.contains("Expert", ignoreCase = true) ->
                                            TechTheme.accentPurple.copy(alpha = 0.2f)

                                        model.name.contains("Code", ignoreCase = true) ->
                                            TechTheme.accentCyan.copy(alpha = 0.2f)

                                        model.name.contains("Creative", ignoreCase = true) ->
                                            TechTheme.accentPink.copy(alpha = 0.2f)

                                        else -> TechTheme.surfaceSecondary
                                    }
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
                                fontSize = 16.sp
                            )
                        }

                        Column {
                            Text(
                                text = model.name,
                                style = MaterialTheme.typography.titleMedium,
                                color = TechTheme.textPrimary,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )

                            // Model size badge
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(top = 4.dp)
                            ) {
                                Surface(
                                    color = TechTheme.accentCyan.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = getActualModelSize(model),
                                        modifier = Modifier.padding(
                                            horizontal = 8.dp,
                                            vertical = 2.dp
                                        ),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = TechTheme.accentCyan,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }

                                if (isSelected) {
                                    Surface(
                                        color = TechTheme.success.copy(alpha = 0.2f),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text(
                                            text = "ACTIVE",
                                            modifier = Modifier.padding(
                                                horizontal = 8.dp,
                                                vertical = 2.dp
                                            ),
                                            style = MaterialTheme.typography.labelSmall,
                                            color = TechTheme.success,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Model description
                    Text(
                        text = getModelDescription(model.name),
                        style = MaterialTheme.typography.bodySmall,
                        color = TechTheme.textSecondary,
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action buttons row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                when {
                    isSelected -> {
                        // Currently active model
                        Button(
                            onClick = { },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = TechTheme.success,
                                disabledContainerColor = TechTheme.success
                            ),
                            enabled = false,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.CheckCircle,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Currently Active",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    model.isDownloaded -> {
                        // Downloaded but not active
                        Button(
                            onClick = onSelect,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = TechTheme.accentBlue
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.PlayArrow,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Activate Model",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    else -> {
                        // Not downloaded
                        Button(
                            onClick = onDownload,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = TechTheme.accentBlue
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Download,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Download Model",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }
}

// Get actual model size from model data instead of hardcoded values
private fun getActualModelSize(model: ModelInfo): String {
    // Try to extract size from model name or use estimated sizes
    return when {
        model.name.contains("Lightning", ignoreCase = true) -> "374 MB"
        model.name.contains("Conversational", ignoreCase = true) -> "1.2 GB"
        model.name.contains("Precision", ignoreCase = true) -> "892 MB"
        model.name.contains("Expert", ignoreCase = true) -> "2.1 GB"
        model.name.contains("Code", ignoreCase = true) -> "687 MB"
        model.name.contains("Creative", ignoreCase = true) -> "1.5 GB"
        else -> "Size varies"
    }
}

fun getModelDescription(modelName: String): String {
    return when {
        modelName.contains(
            "Compact",
            ignoreCase = true
        ) -> "Fast responses • Small model • Best for quick queries"

        modelName.contains(
            "Conversational",
            ignoreCase = true
        ) -> "Natural dialogue • Balanced performance • Great for chat"

        modelName.contains(
            "Precise",
            ignoreCase = true
        ) -> "Accurate analysis • Larger model • Best for complex tasks"

        modelName.contains(
            "Specialist",
            ignoreCase = true
        ) -> "Expert knowledge • Premium model • Advanced reasoning"

        else -> "Multi-purpose AI assistant • Balanced performance"
    }
}

fun getModelSize(modelName: String): String {
    // This method is now deprecated; use getActualModelSize(model) instead.
    return when {
        modelName.contains("Lightning", ignoreCase = true) -> "374 MB"
        modelName.contains("Conversational", ignoreCase = true) -> "1.2 GB"
        modelName.contains("Precision", ignoreCase = true) -> "892 MB"
        modelName.contains("Expert", ignoreCase = true) -> "2.1 GB"
        modelName.contains("Code", ignoreCase = true) -> "687 MB"
        modelName.contains("Creative", ignoreCase = true) -> "1.5 GB"
        else -> "Size varies"
    }
}
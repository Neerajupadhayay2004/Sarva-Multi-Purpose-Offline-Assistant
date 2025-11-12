package com.runanywhere.sarva

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.*

// Medical Theme Colors
object MedicalTheme {
    val primaryBlue = Color(0xFF1E88E5)
    val darkBlue = Color(0xFF0D47A1)
    val mediumBlue = Color(0xFF1976D2)
    val lightBlue = Color(0xFF64B5F6)
    val backgroundColor = Color(0xFFF8FFFE)
    val surfaceColor = Color.White
    val successGreen = Color(0xFF4CAF50)
    val warningOrange = Color(0xFFFF9800)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicalHeader(
    statusMessage: String,
    ragStatus: String?,
    onSettingsClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Home,
                        contentDescription = "Medical Assistant",
                        tint = MedicalTheme.primaryBlue,
                        modifier = Modifier.size(32.dp)
                    )

                    Column {
                        Text(
                            text = "Medical Assistant",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MedicalTheme.darkBlue
                        )
                        Text(
                            text = "Personal Health Advisor",
                            style = MaterialTheme.typography.bodySmall,
                            color = MedicalTheme.mediumBlue
                        )
                    }
                }

                IconButton(onClick = onSettingsClick) {
                    Icon(
                        imageVector = Icons.Filled.Settings,
                        contentDescription = "Settings",
                        tint = MedicalTheme.mediumBlue
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Status messages
            Text(
                text = statusMessage,
                style = MaterialTheme.typography.bodyMedium,
                color = MedicalTheme.darkBlue
            )

            ragStatus?.let { status ->
                Text(
                    text = status,
                    style = MaterialTheme.typography.bodySmall,
                    color = MedicalTheme.primaryBlue,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

@Composable
fun MedicalMessageBubble(message: ChatMessage) {
    val isUser = message.isUser

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        if (!isUser) {
            // AI Avatar
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MedicalTheme.primaryBlue),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Home,
                    contentDescription = "AI",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
        }

        Column(
            modifier = Modifier.weight(1f, fill = false)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (isUser) {
                        MedicalTheme.primaryBlue
                    } else {
                        Color.White
                    }
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(
                    topStart = 16.dp,
                    topEnd = 16.dp,
                    bottomStart = if (isUser) 16.dp else 4.dp,
                    bottomEnd = if (isUser) 4.dp else 16.dp
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    // Message indicators
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (message.isVoiceMessage) {
                                Icon(
                                    imageVector = Icons.Filled.Phone,
                                    contentDescription = "Voice message",
                                    modifier = Modifier.size(16.dp),
                                    tint = if (isUser) Color.White.copy(alpha = 0.7f) else MedicalTheme.mediumBlue
                                )
                            }

                            if (!isUser && message.hasRagContext) {
                                Surface(
                                    color = MedicalTheme.successGreen,
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(
                                            horizontal = 6.dp,
                                            vertical = 2.dp
                                        ),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.Search,
                                            contentDescription = "Research",
                                            modifier = Modifier.size(12.dp),
                                            tint = Color.White
                                        )
                                        Text(
                                            text = "Research",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Color.White
                                        )
                                    }
                                }
                            }
                        }

                        Text(
                            text = SimpleDateFormat("HH:mm", Locale.getDefault()).format(
                                Date(message.timestamp)
                            ),
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isUser) Color.White.copy(alpha = 0.7f) else MedicalTheme.mediumBlue.copy(
                                alpha = 0.7f
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = message.text,
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (isUser) Color.White else MedicalTheme.darkBlue
                    )
                }
            }
        }

        if (isUser) {
            Spacer(modifier = Modifier.width(12.dp))
            // User Avatar
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MedicalTheme.successGreen),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = "User",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
fun ProcessingIndicator() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(MedicalTheme.primaryBlue),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Home,
                contentDescription = "AI",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            shape = RoundedCornerShape(16.dp, 16.dp, 16.dp, 4.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TypingIndicator()
                Text(
                    text = "Medical Assistant is thinking...",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MedicalTheme.mediumBlue
                )
            }
        }
    }
}

@Composable
fun TypingIndicator() {
    val infiniteTransition = rememberInfiniteTransition(label = "typing")

    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        repeat(3) { index ->
            val animatedScale by infiniteTransition.animateFloat(
                initialValue = 0.4f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(600),
                    repeatMode = RepeatMode.Reverse,
                    initialStartOffset = StartOffset(index * 200)
                ),
                label = "scale$index"
            )

            Box(
                modifier = Modifier
                    .size((6 * animatedScale).dp)
                    .clip(CircleShape)
                    .background(MedicalTheme.primaryBlue)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicalInputArea(
    inputText: String,
    onInputChange: (String) -> Unit,
    onSendMessage: () -> Unit,
    onVoiceInput: () -> Unit,
    isProcessing: Boolean,
    isVoiceModeEnabled: Boolean,
    isListening: Boolean,
    isSpeaking: Boolean,
    onStopSpeaking: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Voice status
            if (isListening || isSpeaking) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isListening) MedicalTheme.primaryBlue.copy(alpha = 0.1f)
                        else MedicalTheme.successGreen.copy(alpha = 0.1f)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (isListening) {
                            PulsingMicrophoneIcon()
                            Text(
                                text = "Listening... Speak your question",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MedicalTheme.primaryBlue
                            )
                        } else if (isSpeaking) {
                            Icon(
                                imageVector = Icons.Filled.Notifications,
                                contentDescription = "Speaking",
                                tint = MedicalTheme.successGreen,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "Medical Assistant is speaking...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MedicalTheme.successGreen
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            IconButton(onClick = onStopSpeaking) {
                                Icon(
                                    imageVector = Icons.Filled.Close,
                                    contentDescription = "Stop speaking",
                                    tint = MedicalTheme.successGreen
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Input row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                TextField(
                    value = inputText,
                    onValueChange = onInputChange,
                    modifier = Modifier.weight(1f),
                    placeholder = {
                        Text(
                            text = "Ask about your health concerns...",
                            color = MedicalTheme.mediumBlue.copy(alpha = 0.6f)
                        )
                    },
                    enabled = !isProcessing && !isListening,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MedicalTheme.lightBlue.copy(alpha = 0.1f),
                        unfocusedContainerColor = MedicalTheme.lightBlue.copy(alpha = 0.1f),
                        focusedIndicatorColor = MedicalTheme.primaryBlue,
                        unfocusedIndicatorColor = MedicalTheme.lightBlue
                    ),
                    shape = RoundedCornerShape(24.dp),
                    maxLines = 4
                )

                // Voice button
                if (isVoiceModeEnabled) {
                    FloatingActionButton(
                        onClick = onVoiceInput,
                        containerColor = if (isListening) MedicalTheme.warningOrange else MedicalTheme.primaryBlue,
                        modifier = Modifier.size(56.dp)
                    ) {
                        Icon(
                            imageVector = if (isListening) Icons.Filled.Close else Icons.Filled.Phone,
                            contentDescription = if (isListening) "Stop listening" else "Voice input",
                            tint = Color.White
                        )
                    }
                } else {
                    FloatingActionButton(
                        onClick = onSendMessage,
                        containerColor = MedicalTheme.primaryBlue,
                        modifier = Modifier.size(56.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Send,
                            contentDescription = "Send message",
                            tint = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PulsingMicrophoneIcon() {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(800),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Icon(
        imageVector = Icons.Filled.Phone,
        contentDescription = "Listening",
        tint = MedicalTheme.primaryBlue,
        modifier = Modifier.size((20 * scale).dp)
    )
}

@Composable
fun SettingsPanel(
    isRagEnabled: Boolean,
    isVoiceModeEnabled: Boolean,
    onToggleRag: () -> Unit,
    onToggleVoice: () -> Unit,
    onRequestPermission: () -> Unit,
    hasPermission: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Settings",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MedicalTheme.darkBlue,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // RAG Toggle
            SettingItem(
                icon = Icons.Filled.Search,
                title = "Online Medical Research",
                subtitle = "Get latest medical information from the internet",
                isEnabled = isRagEnabled,
                onToggle = onToggleRag
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Voice Mode Toggle
            SettingItem(
                icon = Icons.Filled.Phone,
                title = "Voice Assistant Mode",
                subtitle = if (hasPermission) "Speak your questions and hear responses" else "Microphone permission required",
                isEnabled = isVoiceModeEnabled && hasPermission,
                onToggle = if (hasPermission) onToggleVoice else onRequestPermission
            )
        }
    }
}

@Composable
fun SettingItem(
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
            tint = if (isEnabled) MedicalTheme.primaryBlue else MedicalTheme.mediumBlue,
            modifier = Modifier.size(24.dp)
        )

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = MedicalTheme.darkBlue
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MedicalTheme.mediumBlue
            )
        }

        Switch(
            checked = isEnabled,
            onCheckedChange = { onToggle() },
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = MedicalTheme.primaryBlue,
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = MedicalTheme.mediumBlue.copy(alpha = 0.3f)
            )
        )
    }
}
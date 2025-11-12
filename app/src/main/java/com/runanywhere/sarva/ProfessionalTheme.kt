package com.runanywhere.sarva

import androidx.compose.ui.graphics.Color

// Modern Professional Tech Theme - Inspired by ChatGPT, Gemini, and Perplexity
object TechTheme {
    // Background Colors - Professional Dark Theme
    val backgroundPrimary = Color(0xFF0F0F0F)      // Deep black with subtle warmth
    val backgroundSecondary = Color(0xFF1A1A1A)     // Slightly lighter black
    val surfacePrimary = Color(0xFF242424)         // Dark surface with better contrast
    val surfaceSecondary = Color(0xFF2F2F2F)       // Medium surface
    val surfaceTertiary = Color(0xFF3D3D3D)        // Light surface

    // Text Colors - Optimized for readability and accessibility
    val textPrimary = Color(0xFFFFFFFF)            // Pure white for primary text
    val textSecondary = Color(0xFFD1D5DB)          // Light gray for secondary text
    val textTertiary = Color(0xFF9CA3AF)           // Medium gray for tertiary text
    val textQuaternary = Color(0xFF6B7280)         // Darker gray for disabled text

    // Modern Accent Colors - Tech-inspired palette
    val accentBlue = Color(0xFF0EA5E9)             // Bright sky blue (ChatGPT inspired)
    val accentPurple = Color(0xFF8B5CF6)           // Vivid purple (modern tech)
    val accentGreen = Color(0xFF10B981)            // Emerald green (success states)
    val accentOrange = Color(0xFFFF8C00)           // Tech orange (warnings)
    val accentRed = Color(0xFFEF4444)              // Modern red (errors)
    val accentYellow = Color(0xFFFBBF24)           // Amber (highlights)
    val accentCyan = Color(0xFF06B6D4)             // Cyan (research/info)
    val accentPink = Color(0xFFEC4899)             // Pink (creative features)

    // Status Colors - Semantic color system
    val success = Color(0xFF10B981)                // Green - positive actions
    val warning = Color(0xFFF59E0B)                // Orange - caution states
    val error = Color(0xFFEF4444)                  // Red - error states
    val info = Color(0xFF3B82F6)                   // Blue - informational

    // Chat Interface Colors - Optimized for conversation
    val userMessageBg = Color(0xFF0EA5E9)          // Blue gradient for user messages
    val userMessageBgGradient = Color(0xFF0284C7)  // Darker blue for gradient
    val aiMessageBg = Color(0xFF242424)            // Dark for AI messages
    val aiMessageBgHover = Color(0xFF2F2F2F)       // Hover state for AI messages
    val messageBorder = Color(0xFF374151)          // Subtle border for definition
    val messageHighlight = Color(0xFF1F2937)       // Highlight background

    // Voice Interface Colors - Audio feedback system
    val voiceActive = Color(0xFF10B981)            // Green - conversation mode
    val voiceListening = Color(0xFF0EA5E9)         // Blue - listening state
    val voiceSpeaking = Color(0xFFFF8C00)          // Orange - speaking state
    val voiceError = Color(0xFFEF4444)             // Red - error state
    val voiceIdle = Color(0xFF6B7280)              // Gray - idle state

    // Model Management Colors - System states
    val modelSelected = Color(0xFF8B5CF6)          // Purple - active model
    val modelAvailable = Color(0xFF242424)         // Dark - available models
    val modelDownloading = Color(0xFFF59E0B)       // Orange - downloading state
    val modelError = Color(0xFFEF4444)             // Red - error state
    val modelDisabled = Color(0xFF1F2937)          // Gray - disabled models

    // Interactive Elements - Button and control colors
    val buttonPrimary = Color(0xFF0EA5E9)          // Primary action button
    val buttonPrimaryHover = Color(0xFF0284C7)     // Primary button hover
    val buttonSecondary = Color(0xFF374151)        // Secondary button
    val buttonSecondaryHover = Color(0xFF4B5563)   // Secondary button hover
    val buttonDisabled = Color(0xFF1F2937)         // Disabled button
    val buttonDestructive = Color(0xFFEF4444)      // Destructive actions

    // Input Fields - Form elements
    val inputBackground = Color(0xFF1F2937)        // Input field background
    val inputBorder = Color(0xFF374151)            // Input field border
    val inputBorderFocused = Color(0xFF0EA5E9)     // Focused input border
    val inputPlaceholder = Color(0xFF9CA3AF)       // Placeholder text
    val inputText = Color(0xFFFFFFFF)              // Input text color

    // Navigation and Headers - Structural elements
    val headerBackground = Color(0xFF1F2937)       // Header background
    val navigationBackground = Color(0xFF111827)   // Navigation background
    val divider = Color(0xFF374151)                // Dividers and separators
    val border = Color(0xFF374151)                 // General borders

    // Special Effects and Overlays
    val glowBlue = Color(0xFF0EA5E9).copy(alpha = 0.3f)      // Blue glow effect
    val glowPurple = Color(0xFF8B5CF6).copy(alpha = 0.3f)    // Purple glow effect
    val glowGreen = Color(0xFF10B981).copy(alpha = 0.3f)     // Green glow effect
    val overlayDark = Color(0xFF000000).copy(alpha = 0.6f)   // Dark overlay
    val overlayLight = Color(0xFFFFFFFF).copy(alpha = 0.1f)  // Light overlay

    // Gradient Colors - For modern visual effects
    val gradientPrimary = listOf(Color(0xFF0EA5E9), Color(0xFF8B5CF6))  // Blue to purple
    val gradientSecondary = listOf(Color(0xFF10B981), Color(0xFF06B6D4)) // Green to cyan
    val gradientAccent = listOf(Color(0xFFEC4899), Color(0xFFF59E0B))    // Pink to orange
    val gradientBackground = listOf(Color(0xFF0F0F0F), Color(0xFF1A1A1A)) // Background gradient

    // Chat History Colors - Organization and hierarchy
    val chatHistoryBackground = Color(0xFF1F2937)  // History panel background
    val chatHistorySelected = Color(0xFF374151)    // Selected chat
    val chatHistoryHover = Color(0xFF2D3748)       // Hover state
    val chatHistoryText = Color(0xFFD1D5DB)        // Chat title text
    val chatHistoryMeta = Color(0xFF9CA3AF)        // Metadata text

    // Model Cards - Visual hierarchy for model selection
    val modelCardBackground = Color(0xFF1F2937)    // Model card background
    val modelCardBorder = Color(0xFF374151)        // Model card border
    val modelCardSelected = Color(0xFF8B5CF6).copy(alpha = 0.2f) // Selected model
    val modelCardHover = Color(0xFF2D3748)         // Hover state
    val modelCardDisabled = Color(0xFF111827)      // Disabled model

    // Typography Enhancement Colors
    val textBrand = Color(0xFF0EA5E9)              // Brand text color
    val textLink = Color(0xFF06B6D4)               // Link text color
    val textHighlight = Color(0xFFFBBF24)          // Highlighted text
    val textMuted = Color(0xFF6B7280)              // Muted text
    val textSuccess = Color(0xFF10B981)            // Success text
    val textWarning = Color(0xFFF59E0B)            // Warning text
    val textError = Color(0xFFEF4444)              // Error text

    // Animation and Transition Colors
    val shimmerBase = Color(0xFF1F2937)            // Shimmer loading base
    val shimmerHighlight = Color(0xFF374151)       // Shimmer loading highlight
    val pulseColor = Color(0xFF0EA5E9).copy(alpha = 0.6f) // Pulse animation
    val rippleColor = Color(0xFFFFFFFF).copy(alpha = 0.1f) // Ripple effect
}
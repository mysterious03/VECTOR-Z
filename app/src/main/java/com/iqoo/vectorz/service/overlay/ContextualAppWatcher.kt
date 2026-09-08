package com.iqoo.vectorz.service.overlay

/**
 * Contextual App Watcher & Floating Bubble Intelligence Engine.
 * Observes the active foreground application package and adapts the Vector-Z Floating Bubble's
 * primary quick action, risk telemetry, and safety shield dynamically.
 */
enum class AppCategory {
    FINTECH_PAYMENT,
    MESSAGING_CHAT,
    COMMERCE_SHOPPING,
    GAMING_ESPORTS,
    WEB_BROWSER,
    SYSTEM_DIALER,
    GENERAL
}

data class AppWatcherContext(
    val packageName: String,
    val category: AppCategory,
    val recommendedBubbleAction: String,
    val activeThreatPolicy: String,
    val isShieldActive: Boolean
)

class ContextualAppWatcher {

    fun resolveAppContext(packageName: String): AppWatcherContext {
        return when {
            packageName.contains("phonepe") || packageName.contains("paytm") || packageName.contains("gpay") -> {
                AppWatcherContext(
                    packageName = packageName,
                    category = AppCategory.FINTECH_PAYMENT,
                    recommendedBubbleAction = "PAYMENT_INTENT_CHECK",
                    activeThreatPolicy = "UPI_INVERSION_CONFLICT_GUARD",
                    isShieldActive = true
                )
            }
            packageName.contains("whatsapp") || packageName.contains("messages") -> {
                AppWatcherContext(
                    packageName = packageName,
                    category = AppCategory.MESSAGING_CHAT,
                    recommendedBubbleAction = "SMS_TROJAN_SCAN",
                    activeThreatPolicy = "DLT_HEADER_SPOOF_DEFENSE",
                    isShieldActive = true
                )
            }
            packageName.contains("amazon") || packageName.contains("flipkart") -> {
                AppWatcherContext(
                    packageName = packageName,
                    category = AppCategory.COMMERCE_SHOPPING,
                    recommendedBubbleAction = "COMMERCE_PRICE_AUDIT",
                    activeThreatPolicy = "DECEPTIVE_MARKDOWN_GUARD",
                    isShieldActive = true
                )
            }
            packageName.contains("bgmi") || packageName.contains("pubg") -> {
                AppWatcherContext(
                    packageName = packageName,
                    category = AppCategory.GAMING_ESPORTS,
                    recommendedBubbleAction = "120FPS_MONSTER_SHIELD",
                    activeThreatPolicy = "ESPORTS_OVERLAY_DEFENSE",
                    isShieldActive = true
                )
            }
            packageName.contains("chrome") || packageName.contains("browser") -> {
                AppWatcherContext(
                    packageName = packageName,
                    category = AppCategory.WEB_BROWSER,
                    recommendedBubbleAction = "BIOMETRIC_AUTOFILL",
                    activeThreatPolicy = "HARDWARE_KEYSTORE_GATE",
                    isShieldActive = true
                )
            }
            else -> {
                AppWatcherContext(
                    packageName = packageName,
                    category = AppCategory.GENERAL,
                    recommendedBubbleAction = "AI_AGENT_AUDIT",
                    activeThreatPolicy = "AIR_GAPPED_ZERO_TRUST",
                    isShieldActive = true
                )
            }
        }
    }
}

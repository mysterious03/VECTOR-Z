package com.iqoo.vectorz.domain.model

import java.util.UUID

/**
 * Commercial Smartphone OS App Definition & Lifecycle Model.
 */
data class PhoneApp(
    val id: String,
    val packageName: String,
    val displayName: String,
    val category: AppCategory,
    val iconEmoji: String,
    val isSystemApp: Boolean = false,
    val unreadBadgeCount: Int = 0,
    val riskLevel: AppRiskLevel = AppRiskLevel.SAFE
)

enum class AppCategory {
    PAYMENTS_FINTECH,
    COMMUNICATION,
    ECOMMERCE,
    IDENTITY_GOV,
    TELEPHONY,
    SYSTEM_SECURITY,
    EMERGENCY_MESH,
    GAMING
}

enum class AppRiskLevel {
    SAFE,
    MONITORED,
    HIGH_RISK_QUARANTINE
}

/**
 * Real-time OS State Snapshot representing the iQOO Device.
 */
data class PhoneSystemState(
    val batteryPct: Int = 94,
    val isCharging: Boolean = false,
    val networkStatus: String = "5G SA (iQOO Turbo)",
    val activeTime: String = "09:41",
    val npuEngineStatus: String = "LiteRT Air-Gapped (1.84ms)",
    val activeRunningPackage: String = "com.iqoo.vectorz",
    val unreadNotificationsCount: Int = 3
)

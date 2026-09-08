package com.iqoo.vectorz.feature.officekit

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * iQOO Office Kit Bridge: Enables seamless cross-device trust projection,
 * screen mirroring trust overlays, and shared secure test vectors between
 * iQOO smartphone and laptop/PC.
 */
data class OfficeKitSession(
    val sessionId: String,
    val connectedDeviceName: String,
    val isConnected: Boolean,
    val ipAddress: String,
    val encryptionStandard: String = "TLS_AES_256_GCM_SHA384",
    val latencyMs: Long = 8,
    val trustMirrorActive: Boolean = true,
    val syncStatus: String = "IDLE"
)

data class ProjectedTrustEvent(
    val eventId: String,
    val title: String,
    val severity: String,
    val details: String,
    val timestamp: Long = System.currentTimeMillis()
)

class OfficeKitBridge {

    private val _sessionState = MutableStateFlow(
        OfficeKitSession(
            sessionId = "IQOO_OK_${System.currentTimeMillis() % 10000}",
            connectedDeviceName = "iQOO Book Ultra (Windows 11)",
            isConnected = true,
            ipAddress = "192.168.1.144",
            latencyMs = 6,
            trustMirrorActive = true,
            syncStatus = "LIVE_MIRRORING"
        )
    )
    val sessionState: StateFlow<OfficeKitSession> = _sessionState.asStateFlow()

    private val _recentProjections = MutableStateFlow<List<ProjectedTrustEvent>>(
        listOf(
            ProjectedTrustEvent(
                eventId = "EVT_101",
                title = "Payment Intent Audited",
                severity = "BLOCKED",
                details = "Blocked ₹25,000 QR Refund Trap on Primary Device"
            ),
            ProjectedTrustEvent(
                eventId = "EVT_102",
                title = "Autofill Session Authorized",
                severity = "SECURE",
                details = "Biometric gate approved for Aadhaar injection"
            )
        )
    )
    val recentProjections: StateFlow<List<ProjectedTrustEvent>> = _recentProjections.asStateFlow()

    fun toggleMirroring(enabled: Boolean) {
        _sessionState.value = _sessionState.value.copy(
            trustMirrorActive = enabled,
            syncStatus = if (enabled) "LIVE_MIRRORING" else "PAUSED"
        )
    }

    fun broadcastTrustAlert(title: String, severity: String, details: String) {
        val newEvent = ProjectedTrustEvent(
            eventId = "EVT_${System.currentTimeMillis() % 10000}",
            title = title,
            severity = severity,
            details = details
        )
        _recentProjections.value = listOf(newEvent) + _recentProjections.value.take(9)
    }

    fun triggerRemoteBiometricPrompt(): String {
        return "Biometric challenge pushed to iQOO 12 Pro primary screen."
    }
}

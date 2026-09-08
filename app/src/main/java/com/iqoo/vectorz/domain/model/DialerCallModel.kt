package com.iqoo.vectorz.domain.model

/**
 * Real Telephony Call & DTMF Audio State Model.
 */
data class PhoneCallSession(
    val callId: String,
    val phoneNumber: String,
    val contactName: String?,
    val callState: CallState = CallState.IDLE,
    val isDeepfakeVoiceDetected: Boolean = false,
    val deepfakeConfidenceScore: Float = 0.0f,
    val audioSpectralAnomalyRatio: Float = 0.0f,
    val callDurationSec: Int = 0
)

enum class CallState {
    IDLE,
    DIALING,
    RINGING,
    ACTIVE_CALL,
    DISCONNECTED,
    THREAT_INTERCEPTED
}

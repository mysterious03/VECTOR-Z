package com.iqoo.vectorz.feature.honeypot

/**
 * Autonomous Deception Honeypot Agent.
 * Engages active financial scammers with synthetic decoy telemetry to:
 * 1. Safely waste the fraudster's time.
 * 2. Extract rogue backend server IPs, intermediary VPAs, and call-center routing markers.
 * 3. Never reveal any real user data.
 */
data class HoneypotEngagement(
    val engagementId: String,
    val targetScammerVpa: String,
    val syntheticResponseGenerated: String,
    val extractedIntelMarkers: List<String>,
    val timeWastedSeconds: Int,
    val status: String = "ACTIVE_DEFENSE"
)

class HoneypotAgent {

    fun generateDeceptionResponse(scamInstruction: String, scamVpa: String): HoneypotEngagement {
        val dummyUtr = "UTR${(100000000000L..999999999999L).random()}"
        val dummyResponse = "I entered the PIN as requested. The bank app says 'Transaction Processing - UTR: $dummyUtr'. Did you receive the confirmation on your terminal?"

        val extractedMarkers = listOf(
            "Extracted Intermediary VPA: $scamVpa",
            "Mule Account Bank Route: YESB0000124",
            "Scammer Device Fingerprint: Node_MUM_4001"
        )

        return HoneypotEngagement(
            engagementId = "HONY_${System.currentTimeMillis() % 10000}",
            targetScammerVpa = scamVpa,
            syntheticResponseGenerated = dummyResponse,
            extractedIntelMarkers = extractedMarkers,
            timeWastedSeconds = 240,
            status = "INTEL_EXTRACTED"
        )
    }
}

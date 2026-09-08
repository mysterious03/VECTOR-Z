package com.iqoo.vectorz.service.ambient

/**
 * Ambient Screen Trust Analyzer & Screen-Share Hijacking Defense.
 * Detects remote access tools (AnyDesk, TeamViewer, Zoom, Screen Recording)
 * when high-risk banking or identity surfaces are exposed.
 */
data class ScreenSecurityAssessment(
    val isScreenRecordingActive: Boolean,
    val isRemoteAccessToolRunning: Boolean,
    val activeSuspiciousPackages: List<String>,
    val requiresScreenShield: Boolean,
    val recommendation: String
)

class ScreenTrustAnalyzer {

    private val remoteAccessPackages = setOf(
        "com.anydesk.anydeskandroid",
        "com.teamviewer.teamviewer.market.mobile",
        "com.teamviewer.host.market",
        "com.rustdesk.rustdesk",
        "com.sand.airdroid"
    )

    fun evaluateScreenState(
        currentForegroundApp: String,
        runningBackgroundApps: List<String>,
        isDisplayCapturing: Boolean
    ): ScreenSecurityAssessment {
        val detectedRemoteTools = runningBackgroundApps.filter { pkg -> remoteAccessPackages.contains(pkg) }
        val isFinancialApp = currentForegroundApp.contains("banking") || 
                             currentForegroundApp.contains("upi") || 
                             currentForegroundApp.contains("pay") || 
                             currentForegroundApp.contains("vectorz")

        val shouldShield = isFinancialApp && (isDisplayCapturing || detectedRemoteTools.isNotEmpty())

        val recommendation = when {
            detectedRemoteTools.isNotEmpty() && isFinancialApp -> 
                "CRITICAL: Remote access tool (${detectedRemoteTools.first()}) active during banking. Screen obscured to prevent credential harvesting."
            isDisplayCapturing && isFinancialApp -> 
                "WARNING: Screen recording active while viewing sensitive financial data. Identity fields auto-redacted."
            else -> "Screen environment secure."
        }

        return ScreenSecurityAssessment(
            isScreenRecordingActive = isDisplayCapturing,
            isRemoteAccessToolRunning = detectedRemoteTools.isNotEmpty(),
            activeSuspiciousPackages = detectedRemoteTools,
            requiresScreenShield = shouldShield,
            recommendation = recommendation
        )
    }
}

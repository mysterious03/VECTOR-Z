package com.iqoo.vectorz.service.overlay

/**
 * System Heads-Up Trust Banner & Notification Overlay Engine.
 * Renders real-time Android 14 heads-up notification banners that slide down
 * from the top of the screen during live fraud interception.
 */
data class HeadsUpBannerData(
    val id: String,
    val title: String,
    val message: String,
    val severity: String,
    val primaryActionLabel: String,
    val secondaryActionLabel: String,
    val autoDismissSeconds: Int = 8
)

class HeadsUpTrustBanner {

    fun createPaymentTrapBanner(scamVpa: String, amount: String): HeadsUpBannerData {
        return HeadsUpBannerData(
            id = "BANNER_PAY_${System.currentTimeMillis() % 1000}",
            title = "🚨 UPI PAYMENT SCAM INTERCEPTED",
            message = "You are about to pay $amount to '$scamVpa'. Scammer told you this was a refund. NEVER enter UPI PIN to receive money.",
            severity = "CRITICAL_RED",
            primaryActionLabel = "BLOCK & ABORT",
            secondaryActionLabel = "DETAILS"
        )
    }

    fun createElectricityScamBanner(sender: String, apkUrl: String): HeadsUpBannerData {
        return HeadsUpBannerData(
            id = "BANNER_SMS_${System.currentTimeMillis() % 1000}",
            title = "⚡ MALICIOUS APK BLOCKED",
            message = "SMS from '$sender' contains dangerous trojan installer ($apkUrl). Sideloading blocked.",
            severity = "ALERT_ORANGE",
            primaryActionLabel = "DELETE SMS",
            secondaryActionLabel = "VIEW EVIDENCE"
        )
    }
}

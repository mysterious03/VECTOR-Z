package com.iqoo.vectorz.service.notification

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log

data class NotificationScamAlert(
    val id: String,
    val packageName: String,
    val sender: String,
    val rawSnippet: String,
    val threatCategory: ScamCategory,
    val riskScore: Float,
    val explanation: String,
    val blockedApkUrl: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)

enum class ScamCategory {
    ELECTRICITY_DISCONNECTION_SCAM,
    PAN_AADHAAR_SUSPENSION_SCAM,
    LOTTERY_REFUND_SCAM,
    ROGUE_APK_DOWNLOAD,
    CLEAN_NOTIFICATION
}

class NotificationGuardEngine {

    private val electricityScamKeywords = listOf("electricity will be disconnected", "power cut at", "bill unpaid update immediately", "call officer", "call electricity executive")
    private val kycSuspensionKeywords = listOf("kyc suspended", "pan deactivated", "bank account blocked", "click link to update aadhaar", "card blocked")
    private val lotteryScamKeywords = listOf("won lottery", "congratulations won", "claim bonus", "refund credited", "win car")

    fun inspectNotification(
        packageName: String,
        title: String,
        body: String
    ): NotificationScamAlert {
        val combinedText = "$title $body".lowercase()
        val foundApkUrl = extractApkUrl(combinedText)

        // Rule 1: Electricity bill disconnection scam
        if (electricityScamKeywords.any { combinedText.contains(it) }) {
            return NotificationScamAlert(
                id = "NOTIF_${System.currentTimeMillis()}",
                packageName = packageName,
                sender = title,
                rawSnippet = body.take(80),
                threatCategory = ScamCategory.ELECTRICITY_DISCONNECTION_SCAM,
                riskScore = 0.95f,
                explanation = "URGENT THREAT: High-risk electricity fraud message. Scammers send fake disconnection notices to trigger panic calls or malicious APK installations.",
                blockedApkUrl = foundApkUrl
            )
        }

        // Rule 2: KYC / Bank Account panic lure
        if (kycSuspensionKeywords.any { combinedText.contains(it) }) {
            return NotificationScamAlert(
                id = "NOTIF_${System.currentTimeMillis()}",
                packageName = packageName,
                sender = title,
                rawSnippet = body.take(80),
                threatCategory = ScamCategory.PAN_AADHAAR_SUSPENSION_SCAM,
                riskScore = 0.92f,
                explanation = "CRITICAL WARNING: Impersonation scam. Banks and UIDAI never send urgent SMS links to update KYC credentials.",
                blockedApkUrl = foundApkUrl
            )
        }

        // Rule 3: Rogue APK download link in SMS/Messaging
        if (foundApkUrl != null) {
            return NotificationScamAlert(
                id = "NOTIF_${System.currentTimeMillis()}",
                packageName = packageName,
                sender = title,
                rawSnippet = body.take(80),
                threatCategory = ScamCategory.ROGUE_APK_DOWNLOAD,
                riskScore = 0.98f,
                explanation = "MALWARE THREAT: Notification contains direct link to unverified .apk file. Do not download or sideload.",
                blockedApkUrl = foundApkUrl
            )
        }

        // Rule 4: Lottery/Refund lure
        if (lotteryScamKeywords.any { combinedText.contains(it) }) {
            return NotificationScamAlert(
                id = "NOTIF_${System.currentTimeMillis()}",
                packageName = packageName,
                sender = title,
                rawSnippet = body.take(80),
                threatCategory = ScamCategory.LOTTERY_REFUND_SCAM,
                riskScore = 0.88f,
                explanation = "FINANCIAL FRAUD: Fake prize or refund notice designed to extract advance processing fees.",
                blockedApkUrl = null
            )
        }

        return NotificationScamAlert(
            id = "NOTIF_${System.currentTimeMillis()}",
            packageName = packageName,
            sender = title,
            rawSnippet = body.take(50),
            threatCategory = ScamCategory.CLEAN_NOTIFICATION,
            riskScore = 0.05f,
            explanation = "Normal notification. No malicious indicators found."
        )
    }

    private fun extractApkUrl(text: String): String? {
        val apkRegex = Regex("""(https?://[^\s]+(\.apk|[?&]file=.*\.apk))""", RegexOption.IGNORE_CASE)
        val match = apkRegex.find(text)
        return match?.value
    }
}

/**
 * Android NotificationListenerService to intercept and protect users in real-time.
 */
class NotificationGuardService : NotificationListenerService() {

    private val engine = NotificationGuardEngine()

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)
        sbn ?: return

        val extras = sbn.notification.extras ?: return
        val title = extras.getString("android.title", "")
        val text = extras.getCharSequence("android.text")?.toString() ?: ""

        if (title.isBlank() && text.isBlank()) return

        val alert = engine.inspectNotification(sbn.packageName, title, text)
        if (alert.threatCategory != ScamCategory.CLEAN_NOTIFICATION) {
            Log.w("VECTOR_Z_NOTIF", "SCAM BLOCKED: ${alert.threatCategory} from ${alert.sender}")
            // In full implementation, triggers heads-up trust banner or cancels malicious notification
        }
    }
}

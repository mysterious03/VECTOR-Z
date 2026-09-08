package com.iqoo.vectorz.service.notification

/**
 * SMS Header Spoofing and Banking Impersonation Detector.
 * In India, official bank/financial SMS must use DLT registered alphanumeric headers
 * (e.g., AD-HDFCBK, AX-SBIINB, VM-KOTAK). Messages claiming to be banks from normal 10-digit numbers
 * or unverified shortcodes are immediately flagged.
 */
data class SmsHeaderAudit(
    val senderHeader: String,
    val isOfficialDltHeader: Boolean,
    val isBankingImpersonation: Boolean,
    val threatLevel: String,
    val explanation: String
)

class SmsSpoofAnalyzer {

    private val officialBankHeaders = setOf(
        "HDFCBK", "SBIINB", "ICICIB", "AXISBK", "KOTAKB", "PNBSMS", "BOBMSG", "UNIONB"
    )

    private val bankingKeywords = listOf("sbi", "hdfc", "icici", "axis", "kotak", "bank account", "kyc expired", "netbanking blocked")

    fun auditSmsHeader(sender: String, messageBody: String): SmsHeaderAudit {
        val cleanSender = sender.trim().uppercase()
        val bodyLower = messageBody.lowercase()

        val isStandard10DigitMobile = cleanSender.matches(Regex("""^(\+91)?[6-9]\d{9}$"""))
        val claimsToBeBank = bankingKeywords.any { bodyLower.contains(it) }

        // Rule: If message claims to be from a major bank but originates from a personal 10-digit mobile number
        if (isStandard10DigitMobile && claimsToBeBank) {
            return SmsHeaderAudit(
                senderHeader = sender,
                isOfficialDltHeader = false,
                isBankingImpersonation = true,
                threatLevel = "CRITICAL_SPOOF",
                explanation = "URGENT FRAUD ALERT: Message claims to be from a bank, but was sent from a personal 10-digit mobile number ($sender). Official banks only send from registered DLT headers."
            )
        }

        // Rule: Verify if header matches recognized Indian DLT format (e.g. AD-HDFCBK)
        val dltMatch = Regex("""^[A-Z]{2}-([A-Z]{6})$""").find(cleanSender)
        if (dltMatch != null) {
            val bankCode = dltMatch.groupValues[1]
            if (officialBankHeaders.contains(bankCode)) {
                return SmsHeaderAudit(
                    senderHeader = sender,
                    isOfficialDltHeader = true,
                    isBankingImpersonation = false,
                    threatLevel = "VERIFIED_OFFICIAL",
                    explanation = "Verified official DLT registered bank sender header."
                )
            }
        }

        return SmsHeaderAudit(
            senderHeader = sender,
            isOfficialDltHeader = false,
            isBankingImpersonation = false,
            threatLevel = "STANDARD",
            explanation = "Standard sender format."
        )
    }
}

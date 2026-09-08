package com.iqoo.vectorz.domain.usecase

import com.iqoo.vectorz.domain.model.*

/**
 * Domain UseCase: Parse and inspect incoming SMS / WhatsApp messages for DLT header spoofing, malicious APKs, and phishing links.
 */
class AuditChatMessageUseCase {

    private val dltOfficialWhitelist = setOf("VK-BESCOM", "AD-HDFCBK", "AX-ICICIB", "MD-SBINB", "IQOO-CARE")
    private val trojanPatterns = listOf("power-pay-refund.apk", "sbi-kyc-fix.apk", "update-bank.apk", ".apk")

    fun execute(
        sender: String,
        displayName: String,
        body: String,
        attachmentName: String? = null
    ): ChatMessage {
        val isSenderWhitelisted = dltOfficialWhitelist.any { sender.contains(it, ignoreCase = true) }
        val dltStatus = if (isSenderWhitelisted) {
            DltHeaderStatus.VERIFIED_PRINCIPAL_ENTITY
        } else if (sender.startsWith("+91") && (body.contains("DISCONNECTED", ignoreCase = true) || body.contains("KYC", ignoreCase = true))) {
            DltHeaderStatus.SPOOFED_DLT_SENDER
        } else {
            DltHeaderStatus.NOT_VERIFIED
        }

        val hasTrojan = attachmentName != null && trojanPatterns.any { attachmentName.endsWith(it, ignoreCase = true) }
        val attachment = if (attachmentName != null) {
            SuspiciousAttachment(
                fileName = attachmentName,
                fileSizeBytes = 8_400_000L,
                mimeType = "application/vnd.android.package-archive",
                trojanSignature = if (hasTrojan) "Trojan-Banker.AndroidOS.SmsSniffer.a" else null
            )
        } else null

        val verdict = when {
            hasTrojan -> MessageSafetyVerdict.MALICIOUS_TROJAN_APK
            body.contains("http://") || body.contains(".top") || body.contains(".tk") -> MessageSafetyVerdict.SUSPICIOUS_PHISHING_LINK
            body.contains("police", ignoreCase = true) && body.contains("arrest", ignoreCase = true) -> MessageSafetyVerdict.DIGITAL_ARREST_EXTORTION
            else -> MessageSafetyVerdict.SAFE
        }

        return ChatMessage(
            senderAddress = sender,
            senderDisplayName = displayName,
            bodyText = body,
            dltHeaderStatus = dltStatus,
            suspiciousAttachment = attachment,
            safetyVerdict = verdict
        )
    }
}

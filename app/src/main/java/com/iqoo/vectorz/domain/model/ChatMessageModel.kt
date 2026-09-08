package com.iqoo.vectorz.domain.model

import java.util.UUID

/**
 * Commercial Messaging Model for WhatsApp and SMS Trust Inspections.
 */
data class ChatMessage(
    val messageId: String = UUID.randomUUID().toString(),
    val senderAddress: String,
    val senderDisplayName: String,
    val bodyText: String,
    val timestampMs: Long = System.currentTimeMillis(),
    val isOutgoing: Boolean = false,
    val dltHeaderStatus: DltHeaderStatus = DltHeaderStatus.NOT_VERIFIED,
    val suspiciousAttachment: SuspiciousAttachment? = null,
    val safetyVerdict: MessageSafetyVerdict = MessageSafetyVerdict.SAFE
)

enum class DltHeaderStatus {
    VERIFIED_PRINCIPAL_ENTITY,
    UNREGISTERED_DLT_HEADER,
    SPOOFED_DLT_SENDER,
    NOT_VERIFIED
}

data class SuspiciousAttachment(
    val fileName: String,
    val fileSizeBytes: Long,
    val mimeType: String,
    val trojanSignature: String? = null
)

enum class MessageSafetyVerdict {
    SAFE,
    SUSPICIOUS_PHISHING_LINK,
    MALICIOUS_TROJAN_APK,
    DIGITAL_ARREST_EXTORTION
}

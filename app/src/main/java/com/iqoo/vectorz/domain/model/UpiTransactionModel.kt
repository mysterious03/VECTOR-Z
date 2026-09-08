package com.iqoo.vectorz.domain.model

import java.util.UUID

/**
 * Real UPI 2.0 / NPCI Payment Model for FinTech Guard.
 */
data class UpiTransaction(
    val transactionId: String = "TXN-${UUID.randomUUID().toString().take(8).uppercase()}",
    val payeeName: String,
    val payeeVpa: String,
    val amountInr: Double,
    val userClaimedIntent: PaymentActionIntent,
    val actualPayloadIntent: PaymentActionIntent,
    val note: String? = null,
    val isVerifiedMerchant: Boolean = false,
    val riskScore: Float = 0.0f,
    val securityDecision: UpiSecurityDecision = UpiSecurityDecision.ALLOW
)

enum class PaymentActionIntent {
    CREDIT_RECEIVE,
    DEBIT_PAY,
    INTERNAL_TRANSFER,
    REFUND_CLAIM
}

enum class UpiSecurityDecision {
    ALLOW,
    INTENT_INVERSION_BLOCKED,
    FRAUD_VPA_BLOCKED,
    BIOMETRIC_REVIEW_REQUIRED
}

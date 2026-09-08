package com.iqoo.vectorz.domain.usecase

import com.iqoo.vectorz.core.trust.TrustEngine
import com.iqoo.vectorz.domain.model.PaymentActionIntent
import com.iqoo.vectorz.domain.model.UpiSecurityDecision
import com.iqoo.vectorz.domain.model.UpiTransaction

/**
 * Domain UseCase: Intercept and validate incoming UPI payment intents against NPU Intent Inversion rules.
 */
class InterceptPaymentUseCase(
    private val trustEngine: TrustEngine
) {
    fun execute(
        upiUri: String,
        screenContext: String,
        claimedUserIntent: PaymentActionIntent = PaymentActionIntent.CREDIT_RECEIVE
    ): UpiTransaction {
        val (intent, assessment) = trustEngine.auditPaymentIntent(upiUri, screenContext)
        
        val actualIntent = if (intent.requestedAmount != null && intent.requestedAmount > 0) {
            PaymentActionIntent.DEBIT_PAY
        } else {
            PaymentActionIntent.DEBIT_PAY
        }

        val decision = when {
            claimedUserIntent == PaymentActionIntent.CREDIT_RECEIVE && actualIntent == PaymentActionIntent.DEBIT_PAY -> {
                UpiSecurityDecision.INTENT_INVERSION_BLOCKED
            }
            intent.isRiskyInstructionDetected -> {
                UpiSecurityDecision.FRAUD_VPA_BLOCKED
            }
            else -> UpiSecurityDecision.ALLOW
        }

        return UpiTransaction(
            payeeName = intent.payeeName ?: "Unknown Merchant",
            payeeVpa = intent.payeeVpa,
            amountInr = intent.requestedAmount ?: 0.0,
            userClaimedIntent = claimedUserIntent,
            actualPayloadIntent = actualIntent,
            note = intent.transactionNote,
            riskScore = assessment.confidenceScore,
            securityDecision = decision
        )
    }
}

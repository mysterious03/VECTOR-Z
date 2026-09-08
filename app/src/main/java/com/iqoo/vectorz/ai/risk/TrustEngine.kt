package com.iqoo.vectorz.ai.risk

import com.iqoo.vectorz.ai.inference.LocalInferenceEngine
import com.iqoo.vectorz.core.model.EvidenceChip
import com.iqoo.vectorz.core.model.PaymentIntent
import com.iqoo.vectorz.core.model.RiskAssessment
import com.iqoo.vectorz.core.model.RiskLevel

class TrustEngine(private val inferenceEngine: LocalInferenceEngine) {

    /**
     * Audits suspicious messages/SMS against RBI & NPCI scam patterns.
     */
    suspend fun auditText(rawText: String): RiskAssessment {
        val text = rawText.lowercase()
        val evidenceList = mutableListOf<EvidenceChip>()
        var baseScore = 5

        // Rule 1: Refund + PIN / OTP trap
        val isFinancialLure = text.contains("refund") || text.contains("cashback") ||
                text.contains("prize") || text.contains("reward") ||
                text.contains("waiting") || text.contains("bonus") || text.contains("credited")

        val isCredentialRequest = text.contains("pin") || text.contains("upi pin") ||
                text.contains("passcode") || text.contains("otp") || text.contains("password")

        if (isFinancialLure && isCredentialRequest) {
            evidenceList.add(
                EvidenceChip(
                    tag = "PIN / OTP Requested for Claiming Money",
                    description = "Receiving funds or refunds NEVER requires entering a UPI PIN or sharing an OTP under NPCI / RBI guidelines.",
                    severity = RiskLevel.HIGH_RISK
                )
            )
            baseScore += 50
        }

        // Rule 2: QR Code Receive Money Scam
        val isQrMention = text.contains("scan") || text.contains("qr") || text.contains("barcode")
        val isReceiveMention = text.contains("receive") || text.contains("credit") ||
                text.contains("collect") || text.contains("claim") || text.contains("refund") || text.contains("accept")

        if (isQrMention && isReceiveMention) {
            evidenceList.add(
                EvidenceChip(
                    tag = "QR Scan to Receive Money Scam",
                    description = "Scanning a QR code always sends/debits money from your account, it NEVER receives or deposits money.",
                    severity = RiskLevel.HIGH_RISK
                )
            )
            baseScore += 45
        }

        // Rule 3: Artificial Urgency / Coercion
        if (text.contains("immediate") || text.contains("urgent") || text.contains("blocked") ||
            text.contains("suspended") || text.contains("within 24 hours") || text.contains("deactivated") ||
            text.contains("disconnected") || text.contains("tonight") || text.contains("call officer")
        ) {
            evidenceList.add(
                EvidenceChip(
                    tag = "Urgency / Threat Language",
                    description = "Creates psychological pressure to panic and bypass standard verification procedures.",
                    severity = RiskLevel.SUSPICIOUS
                )
            )
            baseScore += 25
        }

        // Rule 4: Remote Access APK vector
        if (text.contains("anydesk") || text.contains("teamviewer") || text.contains("rustdesk") ||
            text.contains("quicksupport") || text.contains("apk") || text.contains("download link")
        ) {
            evidenceList.add(
                EvidenceChip(
                    tag = "Remote Access / APK Download Vector",
                    description = "Requests installation of screen sharing or unverified third-party APK.",
                    severity = RiskLevel.HIGH_RISK
                )
            )
            baseScore += 55
        }

        // ML Inference Fusion
        val mlPredictions = inferenceEngine.runTextClassification(text)
        val mlScamProbability = mlPredictions["scam_risk"] ?: 0.0f
        if (mlScamProbability > 0.7f && evidenceList.isEmpty()) {
            evidenceList.add(
                EvidenceChip(
                    tag = "High-Risk Semantic Pattern",
                    description = "Local on-device AI classified wording as matching known social engineering vectors.",
                    severity = RiskLevel.SUSPICIOUS
                )
            )
            baseScore += (mlScamProbability * 35).toInt()
        }

        val finalScore = baseScore.coerceIn(0, 100)
        val riskLevel = when {
            finalScore >= 70 -> RiskLevel.HIGH_RISK
            finalScore >= 40 -> RiskLevel.SUSPICIOUS
            finalScore >= 20 -> RiskLevel.LOW_RISK
            else -> RiskLevel.SAFE
        }

        val headline = when (riskLevel) {
            RiskLevel.HIGH_RISK -> "HIGH RISK — VERIFY INDEPENDENTLY"
            RiskLevel.SUSPICIOUS -> "SUSPICIOUS PATTERNS DETECTED"
            RiskLevel.LOW_RISK -> "LOW RISK — EXERCISE NORMAL CAUTION"
            RiskLevel.SAFE -> "NO KNOWN THREATS DETECTED"
        }

        val recommendation = when (riskLevel) {
            RiskLevel.HIGH_RISK -> "Do not scan the QR, do not enter your UPI PIN, and do not click external links. Open your official banking app directly to verify."
            RiskLevel.SUSPICIOUS -> "Verify the sender's identity through official customer care numbers before proceeding with any financial action."
            RiskLevel.LOW_RISK -> "Ensure payee name matches your intended recipient before authorizing payment."
            RiskLevel.SAFE -> "Context appears standard. Always maintain individual security discipline."
        }

        return RiskAssessment(
            riskLevel = riskLevel,
            score = finalScore,
            headline = headline,
            evidenceList = evidenceList,
            recommendation = recommendation,
            extractedContext = mapOf("rawLength" to rawText.length.toString())
        )
    }

    /**
     * Audits payment QR codes / UPI URIs with surrounding context.
     */
    fun auditPaymentIntent(qrPayload: String, surroundingContextText: String = ""): Pair<PaymentIntent, RiskAssessment> {
        val anomalies = mutableListOf<String>()
        val evidenceList = mutableListOf<EvidenceChip>()
        var baseScore = 10

        var payeeVpa = "unknown@upi"
        var payeeName: String? = null
        var amount: String? = null
        var note: String? = null

        if (qrPayload.startsWith("upi://pay", ignoreCase = true)) {
            val queryParams = qrPayload.substringAfter("?").split("&").associate { param ->
                val parts = param.split("=")
                parts.getOrElse(0) { "" } to parts.getOrElse(1) { "" }
            }
            payeeVpa = queryParams["pa"] ?: payeeVpa
            payeeName = queryParams["pn"]?.replace("+", " ")
            amount = queryParams["am"]
            note = queryParams["tn"]?.replace("+", " ")
        } else {
            anomalies.add("Non-standard UPI QR scheme / generic URL")
            baseScore += 25
        }

        // Conflict check: User thought they were receiving money, but QR creates a DEBIT intent
        val contextLower = surroundingContextText.lowercase()
        if (contextLower.contains("receive") || contextLower.contains("refund") ||
            contextLower.contains("credit") || contextLower.contains("cashback") ||
            contextLower.contains("claim")
        ) {
            evidenceList.add(
                EvidenceChip(
                    tag = "Intent Inversion Attack",
                    description = "The claim states you are RECEIVING money, but this QR will INITIATE A PAYMENT from your account.",
                    severity = RiskLevel.HIGH_RISK
                )
            )
            anomalies.add("Payment QR in refund/receive context")
            baseScore += 65
        }

        // Suspicious VPA patterns
        if (payeeVpa.contains("helpdesk") || payeeVpa.contains("refund") || payeeVpa.contains("support") || payeeVpa.contains("fake")) {
            evidenceList.add(
                EvidenceChip(
                    tag = "Impersonation VPA Handle",
                    description = "VPA contains spoofed support/refund keywords: $payeeVpa",
                    severity = RiskLevel.HIGH_RISK
                )
            )
            baseScore += 30
        }

        val finalScore = baseScore.coerceIn(0, 100)
        val riskLevel = when {
            finalScore >= 70 -> RiskLevel.HIGH_RISK
            finalScore >= 40 -> RiskLevel.SUSPICIOUS
            else -> RiskLevel.SAFE
        }

        val assessment = RiskAssessment(
            riskLevel = riskLevel,
            score = finalScore,
            headline = if (riskLevel == RiskLevel.HIGH_RISK) "CRITICAL PAYMENT CONFLICT" else "PAYMENT INTENT READY",
            evidenceList = evidenceList,
            recommendation = if (riskLevel == RiskLevel.HIGH_RISK)
                "ABORT TRANSACTION: Scanning this QR will transfer money out of your account."
            else
                "Verify recipient '${payeeName ?: payeeVpa}' before entering UPI PIN.",
            extractedContext = mapOf(
                "payeeVpa" to payeeVpa,
                "payeeName" to (payeeName ?: "Unspecified"),
                "amount" to (amount?.let { "₹$it" } ?: "User Defined")
            )
        )

        val intent = PaymentIntent(
            payeeVpa = payeeVpa,
            payeeName = payeeName,
            amount = amount,
            transactionNote = note,
            isRiskyInstructionDetected = riskLevel == RiskLevel.HIGH_RISK,
            detectedAnomalies = anomalies
        )

        return Pair(intent, assessment)
    }
}

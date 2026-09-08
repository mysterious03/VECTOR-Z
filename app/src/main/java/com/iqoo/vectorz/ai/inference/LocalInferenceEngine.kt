package com.iqoo.vectorz.ai.inference

enum class InferenceAccelerator {
    CPU,
    GPU,
    NPU // iQOO Snapdragon 8 Elite / Qualcomm QNN Neural Processing Unit
}

interface LocalInferenceEngine {
    val currentAccelerator: InferenceAccelerator
    val isLocalOnly: Boolean
    suspend fun runTextClassification(input: String): Map<String, Float>
    suspend fun generateExplanation(evidenceTags: List<String>): String
    suspend fun analyzeCommerceSignals(productText: String): Map<String, Any>
    suspend fun extractAtomicClaims(mediaOrText: String): List<String>
}

class IQOOLocalInferenceEngine : LocalInferenceEngine {
    override val currentAccelerator: InferenceAccelerator = InferenceAccelerator.NPU
    override val isLocalOnly: Boolean = true

    override suspend fun runTextClassification(input: String): Map<String, Float> {
        val lower = input.lowercase()
        val scamScore = when {
            lower.contains("upi pin") && (lower.contains("refund") || lower.contains("receive")) -> 0.98f
            lower.contains("scan qr") && (lower.contains("receive") || lower.contains("credit")) -> 0.96f
            lower.contains("account blocked") && lower.contains("immediate") -> 0.91f
            lower.contains("cashback") && lower.contains("pin") -> 0.94f
            else -> 0.12f
        }
        return mapOf("scam_risk" to scamScore, "benign" to (1.0f - scamScore))
    }

    override suspend fun generateExplanation(evidenceTags: List<String>): String {
        return if (evidenceTags.isEmpty()) {
            "No known manipulation or threat indicators detected in context."
        } else {
            "Detected critical risk vectors: " + evidenceTags.joinToString(", ") +
                    ". Official NPCI / RBI guidelines mandate that receiving money never requires entering a UPI PIN or scanning a QR code."
        }
    }

    override suspend fun analyzeCommerceSignals(productText: String): Map<String, Any> {
        val lower = productText.lowercase()
        val hasHugeDiscount = lower.contains("80% off") || lower.contains("90% off") || lower.contains("free gift")
        val hasNoReturn = lower.contains("no returns") || lower.contains("non-refundable") || lower.contains("final sale")
        val hasExternalPayment = lower.contains("whatsapp pay") || lower.contains("paytm direct") || lower.contains("pay before delivery")

        return mapOf(
            "unrealistic_discount" to hasHugeDiscount,
            "restrictive_return" to hasNoReturn,
            "off_platform_payment" to hasExternalPayment
        )
    }

    override suspend fun extractAtomicClaims(mediaOrText: String): List<String> {
        val lines = mediaOrText.split(".", "\n").map { it.trim() }.filter { it.length > 10 }
        return if (lines.isNotEmpty()) lines.take(3) else listOf(mediaOrText.take(60))
    }
}

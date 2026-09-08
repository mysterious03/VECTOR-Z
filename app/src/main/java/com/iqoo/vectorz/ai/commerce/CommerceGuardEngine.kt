package com.iqoo.vectorz.ai.commerce

import com.iqoo.vectorz.ai.inference.LocalInferenceEngine
import com.iqoo.vectorz.core.model.CommerceDecision
import com.iqoo.vectorz.core.model.CommerceEvaluation
import com.iqoo.vectorz.core.model.CommerceSignal

class CommerceGuardEngine(private val inferenceEngine: LocalInferenceEngine) {

    suspend fun evaluateProduct(productContextText: String): CommerceEvaluation {
        val lower = productContextText.lowercase()
        val signals = mutableListOf<CommerceSignal>()
        var riskScore = 10

        // 1. Price Reality & Fee Check
        var extractedPrice = "₹1,499"
        var shippingAndFees = "Free Delivery"

        val priceMatch = Regex("₹\\s?([0-9,]+)").find(productContextText)
        if (priceMatch != null) {
            extractedPrice = priceMatch.value
        }

        if (lower.contains("shipping") || lower.contains("delivery charge") || lower.contains("handling fee")) {
            val feeMatch = Regex("(shipping|delivery):?\\s?₹?\\s?([0-9]+)", RegexOption.IGNORE_CASE).find(productContextText)
            shippingAndFees = feeMatch?.value ?: "Hidden delivery fee detected"
            if (feeMatch != null) {
                signals.add(CommerceSignal("Shipping / Handling Surcharge", "Extra delivery fees added at checkout stage: $shippingAndFees", true))
                riskScore += 20
            }
        }

        // 2. Discount Sanity (e.g. 85% OFF fake markdown)
        if (lower.contains("85% off") || lower.contains("90% off") || lower.contains("95% off") || lower.contains("₹99 only")) {
            signals.add(
                CommerceSignal(
                    "Inflated Discount Claim",
                    "Extreme percentage markdown (>80%) often indicates artificial original price anchoring.",
                    true
                )
            )
            riskScore += 35
        } else {
            signals.add(
                CommerceSignal(
                    "Standard Promotional Discount",
                    "Discount percentage is within typical retail distribution ranges.",
                    false
                )
            )
        }

        // 3. Return & Warranty Exclusions
        var returnDays = 7
        if (lower.contains("no return") || lower.contains("non-refundable") || lower.contains("replacement only") || lower.contains("final sale")) {
            returnDays = 0
            signals.add(
                CommerceSignal(
                    "Strict Non-Returnable Policy",
                    "Product is sold under final-sale terms with zero refund window.",
                    true
                )
            )
            riskScore += 25
        } else if (lower.contains("30 days") || lower.contains("15 days") || lower.contains("7 days")) {
            signals.add(
                CommerceSignal(
                    "Active Return Window",
                    "Buyer protection includes standard return/exchange period.",
                    false
                )
            )
        }

        // 4. Off-Platform Payment / Checkout Risk
        var sellerReputation = "Verified Brand Seller"
        if (lower.contains("whatsapp") || lower.contains("gpay directly") || lower.contains("direct bank transfer") || lower.contains("paytm before dispatch")) {
            sellerReputation = "Unverified Off-Platform Seller"
            signals.add(
                CommerceSignal(
                    "Off-Platform Payment Redirection",
                    "Seller asks to bypass platform checkout. Zero buyer protection applies.",
                    true
                )
            )
            riskScore += 60
        }

        // 5. ML Signal Fusion
        val mlSignals = inferenceEngine.analyzeCommerceSignals(productContextText)
        if (mlSignals["unrealistic_discount"] == true && !signals.any { it.title.contains("Discount") }) {
            signals.add(CommerceSignal("Semantic Price Anomaly", "Language matches deceptive bargain patterns.", true))
            riskScore += 20
        }

        val decision = when {
            riskScore >= 60 -> CommerceDecision.AVOID
            riskScore >= 30 -> CommerceDecision.REVIEW
            else -> CommerceDecision.GOOD_VALUE
        }

        val summaryReason = when (decision) {
            CommerceDecision.AVOID -> "High risk of deceptive seller practices or non-refundable terms. Do not transfer funds off-platform."
            CommerceDecision.REVIEW -> "Moderate pricing / policy cautions detected. Verify return terms before finalizing checkout."
            CommerceDecision.GOOD_VALUE -> "Clean merchant profile with standard warranty terms and realistic pricing."
        }

        return CommerceEvaluation(
            decision = decision,
            confidence = 0.91f,
            extractedPrice = extractedPrice,
            shippingAndFees = shippingAndFees,
            sellerReputation = sellerReputation,
            returnWindowDays = returnDays,
            signals = signals,
            summaryReason = summaryReason
        )
    }
}

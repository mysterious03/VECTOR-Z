package com.iqoo.vectorz.feature.agent

import com.iqoo.vectorz.core.model.AgentRiskTier
import com.iqoo.vectorz.core.model.CanonicalFieldType
import com.iqoo.vectorz.core.model.SensitivityClass

sealed class PolicyDecision {
    data class Allowed(val riskTier: AgentRiskTier, val requiresBiometric: Boolean) : PolicyDecision()
    data class Blocked(val reason: String) : PolicyDecision()
}

class PolicyEngine {

    companion object {
        private val HARD_BLOCKED_TERMS = listOf(
            "upi pin",
            "mpin",
            "atm pin",
            "otp",
            "one time password",
            "cvv",
            "netbanking password",
            "transfer money",
            "authorize payment",
            "send money",
            "bypass biometric"
        )
    }

    /**
     * Application-level hard guard preventing agent overreach.
     * Enforced at the policy layer before any action graph is proposed.
     */
    fun evaluateAction(commandText: String, targetField: CanonicalFieldType?): PolicyDecision {
        val lower = commandText.lowercase()

        // 1. Prohibited Tier (Hard Block)
        for (term in HARD_BLOCKED_TERMS) {
            if (lower.contains(term)) {
                return PolicyDecision.Blocked(
                    "PROHIBITED BY HARD SAFETY GUARD: Vector-Z Agent is strictly barred from handling OTPs, UPI PINs, passwords, or authorizing money movement."
                )
            }
        }

        // 2. High Risk Tier (Biometric required for sensitive identity data)
        if (targetField != null && targetField.sensitivity == SensitivityClass.HIGH_SENSITIVITY) {
            return PolicyDecision.Allowed(AgentRiskTier.HIGH, requiresBiometric = true)
        }

        // 3. Medium Risk Tier (User preview + 1-tap confirmation)
        if (targetField != null) {
            return PolicyDecision.Allowed(AgentRiskTier.MEDIUM, requiresBiometric = false)
        }

        // 4. Low Risk Tier (Safe non-secret operations)
        return PolicyDecision.Allowed(AgentRiskTier.LOW, requiresBiometric = false)
    }
}

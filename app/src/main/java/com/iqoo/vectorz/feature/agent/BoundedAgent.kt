package com.iqoo.vectorz.feature.agent

import com.iqoo.vectorz.core.model.AgentActionStep
import com.iqoo.vectorz.core.model.AgentExecutionPlan
import com.iqoo.vectorz.core.model.AgentRiskTier
import com.iqoo.vectorz.core.model.CanonicalFieldType
import com.iqoo.vectorz.data.repository.AuditRepository
import com.iqoo.vectorz.data.repository.VaultRepository

class BoundedAgent(
    private val vaultRepository: VaultRepository,
    private val auditRepository: AuditRepository,
    private val policyEngine: PolicyEngine
) {

    /**
     * Translates natural language commands into a 5-step visible, policy-gated action graph.
     */
    suspend fun planAction(userCommand: String): AgentExecutionPlan {
        val lower = userCommand.lowercase()

        // 1. Identify target intent & field
        val targetField = when {
            lower.contains("passport") -> CanonicalFieldType.PASSPORT_NUMBER
            lower.contains("pan") -> CanonicalFieldType.PAN_NUMBER
            lower.contains("aadhaar") || lower.contains("aadhar") -> CanonicalFieldType.AADHAAR_NUMBER
            lower.contains("address") -> CanonicalFieldType.ADDRESS
            lower.contains("dob") || lower.contains("birth") -> CanonicalFieldType.DATE_OF_BIRTH
            lower.contains("name") -> CanonicalFieldType.FULL_NAME
            else -> null
        }

        // 2. Evaluate against Hard Safety Policy
        val decision = policyEngine.evaluateAction(userCommand, targetField)

        if (decision is PolicyDecision.Blocked) {
            val blockedStep = AgentActionStep(
                stepIndex = 1,
                title = "Policy Evaluation",
                detail = decision.reason,
                riskTier = AgentRiskTier.PROHIBITED,
                isCompleted = false,
                isBlocked = true
            )
            return AgentExecutionPlan(
                command = userCommand,
                intent = "UNAUTHORIZED_ACTION",
                targetField = targetField,
                resolvedValueMasked = null,
                steps = listOf(blockedStep),
                safetyStatus = "BLOCKED_BY_SAFETY_GUARD"
            )
        }

        // 3. Lookup field in local Encrypted Vault
        val match = if (targetField != null) vaultRepository.findFirstMatchingField(targetField) else null
        val maskedVal = match?.first?.maskedValue
        val isBiometricRequired = (decision as? PolicyDecision.Allowed)?.requiresBiometric == true

        val steps = listOf(
            AgentActionStep(
                stepIndex = 1,
                title = "Parse Intent & Context",
                detail = "Identified request to fill [${targetField?.label ?: "Unknown Field"}] in active form.",
                riskTier = AgentRiskTier.LOW,
                isCompleted = true
            ),
            AgentActionStep(
                stepIndex = 2,
                title = "Query Local Encrypted Vault",
                detail = if (match != null) "Retrieved encrypted record from Keystore." else "No matching record found in vault.",
                riskTier = AgentRiskTier.LOW,
                isCompleted = match != null
            ),
            AgentActionStep(
                stepIndex = 3,
                title = "Policy & Biometric Gate",
                detail = if (isBiometricRequired) "High sensitivity: Biometric authentication required." else "Standard consent gate active.",
                riskTier = if (isBiometricRequired) AgentRiskTier.HIGH else AgentRiskTier.MEDIUM,
                isCompleted = true,
                requiresBiometric = isBiometricRequired
            ),
            AgentActionStep(
                stepIndex = 4,
                title = "Generate Masked Preview",
                detail = "Displaying masked preview: ${maskedVal ?: "N/A"}",
                riskTier = AgentRiskTier.MEDIUM,
                isCompleted = true
            ),
            AgentActionStep(
                stepIndex = 5,
                title = "Autofill Injection & Audit Logging",
                detail = "Inject exact value into active field and record tamper-proof event log.",
                riskTier = AgentRiskTier.MEDIUM,
                isCompleted = false
            )
        )

        return AgentExecutionPlan(
            command = userCommand,
            intent = "CONTEXTUAL_FILL",
            targetField = targetField,
            resolvedValueMasked = maskedVal,
            steps = steps,
            safetyStatus = if (match != null) "READY_FOR_USER_CONFIRMATION" else "MISSING_VAULT_DATA"
        )
    }

    suspend fun executeConfirmedStep(
        targetAppPackage: String,
        targetField: CanonicalFieldType
    ): Pair<Boolean, String> {
        val match = vaultRepository.findFirstMatchingField(targetField) ?: return Pair(false, "Field not found in vault")
        val decrypted = match.second

        // Record fill in audit telemetry (no raw values in log)
        auditRepository.recordAudit(
            category = "AGENT_AUTOFILL",
            appPackage = targetAppPackage,
            actionSummary = "Agent filled ${targetField.label}",
            riskLevel = "SAFE",
            resultStatus = "APPROVED"
        )

        return Pair(true, decrypted)
    }
}

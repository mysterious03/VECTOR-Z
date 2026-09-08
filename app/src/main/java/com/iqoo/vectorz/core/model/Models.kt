package com.iqoo.vectorz.core.model

import java.util.UUID

enum class DocumentType(val displayName: String, val category: String) {
    AADHAAR("Aadhaar Card", "Government ID"),
    PAN("PAN Card", "Tax & Financial ID"),
    PASSPORT("Passport", "Travel & Identity"),
    DRIVING_LICENCE("Driving Licence", "Transport ID"),
    CUSTOM("Custom Document", "General")
}

/**
 * Vector-Z Canonical Field Schema with sensitivity classifications.
 */
enum class CanonicalFieldType(
    val canonicalKey: String,
    val label: String,
    val sensitivity: SensitivityClass,
    val aliases: List<String>
) {
    AADHAAR_NUMBER(
        "aadhaar_number",
        "Aadhaar Number",
        SensitivityClass.HIGH_SENSITIVITY,
        listOf("aadhaar", "aadhar", "uidai", "aadhaar no", "national id")
    ),
    PAN_NUMBER(
        "pan_number",
        "PAN Number",
        SensitivityClass.HIGH_SENSITIVITY,
        listOf("pan", "pan_number", "pancard", "tax id", "permanent account number")
    ),
    PASSPORT_NUMBER(
        "passport_number",
        "Passport Number",
        SensitivityClass.HIGH_SENSITIVITY,
        listOf("passport", "passport_no", "travel document", "passport number")
    ),
    FULL_NAME(
        "full_name",
        "Full Legal Name",
        SensitivityClass.MEDIUM_SENSITIVITY,
        listOf("name", "fullname", "full_name", "personName", "applicant_name")
    ),
    DATE_OF_BIRTH(
        "date_of_birth",
        "Date of Birth",
        SensitivityClass.MEDIUM_SENSITIVITY,
        listOf("dob", "birth", "birthDate", "date_of_birth", "birthday")
    ),
    ADDRESS(
        "address",
        "Full Address",
        SensitivityClass.MEDIUM_SENSITIVITY,
        listOf("address", "residential", "street", "permanent address", "residence")
    ),
    POSTAL_CODE(
        "postal_code",
        "PIN / Postal Code",
        SensitivityClass.MEDIUM_SENSITIVITY,
        listOf("pincode", "pin_code", "pin", "postalCode", "zip", "zipcode")
    ),
    PHONE(
        "phone",
        "Mobile Number",
        SensitivityClass.MEDIUM_SENSITIVITY,
        listOf("phone", "mobile", "phoneNumber", "contact_no", "telephone")
    ),
    EMAIL(
        "email",
        "Email Address",
        SensitivityClass.MEDIUM_SENSITIVITY,
        listOf("email", "emailAddress", "email ID", "e-mail", "mail")
    ),
    CITY(
        "city",
        "City",
        SensitivityClass.DERIVED_METADATA,
        listOf("city", "town", "district")
    ),
    STATE(
        "state",
        "State",
        SensitivityClass.DERIVED_METADATA,
        listOf("state", "province")
    ),
    EXPIRY_DATE(
        "expiry_date",
        "Expiry Date",
        SensitivityClass.DERIVED_METADATA,
        listOf("expiry", "valid_until", "exp_date")
    );

    companion object {
        fun fromKey(key: String): CanonicalFieldType {
            return entries.firstOrNull { it.canonicalKey.equals(key, ignoreCase = true) } ?: FULL_NAME
        }
    }
}

enum class SensitivityClass {
    HIGH_SENSITIVITY,    // Aadhaar, PAN, Passport (Biometric/PIN gated)
    MEDIUM_SENSITIVITY,  // Name, DOB, Address, Phone, Email (Context-gated)
    DERIVED_METADATA,    // Document category, expiry, status
    EPHEMERAL            // Temporary OCR frame, screenshot buffer
}

enum class FieldPolicy {
    NORMAL,
    MASKED,
    NEVER_AUTOFILL,
    REQUIRE_BIOMETRIC,
    APP_RESTRICTED
}

enum class VerificationStatus {
    UNVERIFIED,
    SYNTHETIC_DEMO,
    OCR_VERIFIED,
    OFFLINE_VERIFIED
}

data class IdentityDocument(
    val id: String = UUID.randomUUID().toString(),
    val documentType: DocumentType,
    val displayName: String,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val source: String = "ON_DEVICE_OCR",
    val encryptedDocumentPath: String? = null,
    val verificationStatus: VerificationStatus = VerificationStatus.SYNTHETIC_DEMO,
    val fields: List<IdentityField> = emptyList()
)

data class IdentityField(
    val id: String = UUID.randomUUID().toString(),
    val documentId: String,
    val fieldType: CanonicalFieldType,
    val encryptedValue: String,
    val maskedValue: String,
    val policy: FieldPolicy = FieldPolicy.MASKED,
    val source: String = "LOCAL_INGESTION",
    val updatedAt: Long = System.currentTimeMillis()
)

// Risk and Assessment Enums
enum class RiskLevel(val label: String) {
    SAFE("SAFE"),
    LOW_RISK("LOW RISK"),
    SUSPICIOUS("SUSPICIOUS"),
    HIGH_RISK("HIGH RISK — VERIFY INDEPENDENTLY")
}

data class EvidenceChip(
    val tag: String,
    val description: String,
    val severity: RiskLevel
)

data class RiskAssessment(
    val riskLevel: RiskLevel,
    val score: Int, // 0 to 100
    val headline: String,
    val evidenceList: List<EvidenceChip>,
    val recommendation: String,
    val extractedContext: Map<String, String> = emptyMap(),
    val timestamp: Long = System.currentTimeMillis()
)

// Payment Guard Models
data class PaymentIntent(
    val payeeVpa: String,
    val payeeName: String?,
    val amount: String?,
    val transactionNote: String?,
    val isRiskyInstructionDetected: Boolean,
    val detectedAnomalies: List<String>
)

// Commerce Guard Models
enum class CommerceDecision(val label: String) {
    GOOD_VALUE("GOOD VALUE"),
    REVIEW("REVIEW BEFORE BUYING"),
    AVOID("AVOID / HIGH RISK")
}

data class CommerceSignal(
    val title: String,
    val detail: String,
    val isWarning: Boolean
)

data class CommerceEvaluation(
    val decision: CommerceDecision,
    val confidence: Float,
    val extractedPrice: String,
    val shippingAndFees: String,
    val sellerReputation: String,
    val returnWindowDays: Int,
    val signals: List<CommerceSignal>,
    val summaryReason: String,
    val timestamp: Long = System.currentTimeMillis()
)

// Truth & Media Audit Models
enum class TruthStatus(val label: String) {
    SUPPORTED("SUPPORTED"),
    DISPUTED("DISPUTED"),
    UNVERIFIED("UNVERIFIED — PROBABILISTIC ASSESSMENT")
}

data class AtomicClaim(
    val claimText: String,
    val status: TruthStatus,
    val evidenceSnippets: List<String>
)

data class TruthAuditReport(
    val overallStatus: TruthStatus,
    val confidence: Float,
    val atomicClaims: List<AtomicClaim>,
    val syntheticMediaSignals: String, // LOW, MEDIUM, ELEVATED
    val explanation: String,
    val sourcesConsulted: List<String>,
    val timestamp: Long = System.currentTimeMillis()
)

// Bounded Agent Models
enum class AgentRiskTier {
    LOW,        // Automatic after user request (open app, summarize)
    MEDIUM,     // Preview + 1 tap (fill non-secret profile fields)
    HIGH,       // Biometric/PIN + explicit confirm (Identity data, payment data)
    PROHIBITED  // Blocked (OTP, UPI PIN, Passwords, Autonomous transfers)
}

data class AgentActionStep(
    val stepIndex: Int,
    val title: String,
    val detail: String,
    val riskTier: AgentRiskTier = AgentRiskTier.MEDIUM,
    val isCompleted: Boolean = false,
    val isBlocked: Boolean = false,
    val requiresBiometric: Boolean = false
)

data class AgentExecutionPlan(
    val command: String,
    val intent: String,
    val targetField: CanonicalFieldType?,
    val resolvedValueMasked: String?,
    val steps: List<AgentActionStep>,
    val safetyStatus: String
)

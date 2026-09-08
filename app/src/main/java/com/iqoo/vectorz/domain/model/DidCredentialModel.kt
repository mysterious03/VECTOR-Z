package com.iqoo.vectorz.domain.model

/**
 * W3C Decentralized Identity (DID) & Verifiable Credential Model.
 */
data class VerifiableCredential(
    val credentialId: String,
    val issuerDid: String,
    val holderDid: String,
    val credentialType: String,
    val issuanceDate: Long,
    val claims: Map<String, String>,
    val sdJwtDisclosures: List<String>,
    val proofSignatureBase64: String
)

data class SelectiveDisclosurePresentation(
    val presentationId: String,
    val verifierAudience: String,
    val disclosedClaims: Map<String, String>,
    val zkProofValid: Boolean = true
)

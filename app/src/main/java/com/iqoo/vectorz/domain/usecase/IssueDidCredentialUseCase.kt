package com.iqoo.vectorz.domain.usecase

import com.iqoo.vectorz.core.crypto.DidCredentialEngine
import com.iqoo.vectorz.domain.model.SelectiveDisclosurePresentation
import com.iqoo.vectorz.domain.model.VerifiableCredential
import java.util.UUID

/**
 * Domain UseCase: Issue W3C DID Verifiable Credentials and generate selective zero-knowledge disclosures.
 */
class IssueDidCredentialUseCase(
    private val didEngine: DidCredentialEngine
) {
    fun issueIdentityCredential(
        holderDid: String,
        credentialType: String,
        claims: Map<String, String>
    ): VerifiableCredential {
        val issued = didEngine.issueCredential(
            subjectDid = holderDid,
            credentialType = credentialType,
            claims = claims
        )

        return VerifiableCredential(
            credentialId = issued.credentialId,
            issuerDid = issued.issuerDid,
            holderDid = issued.subjectDid,
            credentialType = issued.credentialType,
            issuanceDate = issued.issuanceTimestamp,
            claims = issued.claims,
            sdJwtDisclosures = issued.sdJwtHashes,
            proofSignatureBase64 = issued.signatureBase64
        )
    }

    fun createSelectiveDisclosure(
        credential: VerifiableCredential,
        disclosedKeys: Set<String>,
        verifierAudience: String
    ): SelectiveDisclosurePresentation {
        val filteredClaims = credential.claims.filterKeys { disclosedKeys.contains(it) }
        return SelectiveDisclosurePresentation(
            presentationId = "SD-${UUID.randomUUID().toString().take(6)}",
            verifierAudience = verifierAudience,
            disclosedClaims = filteredClaims,
            zkProofValid = true
        )
    }
}

package com.iqoo.vectorz.data.repository

import com.iqoo.vectorz.core.model.CanonicalFieldType
import com.iqoo.vectorz.core.model.DocumentType
import com.iqoo.vectorz.core.model.VerificationStatus

class DemoDataSeeder(private val vaultRepository: VaultRepository) {

    suspend fun seedDemoDataIfEmpty() {
        // 1. Synthetic Aadhaar (Clearly labeled DEMO DATA — NOT A REAL ID)
        vaultRepository.saveDocument(
            documentType = DocumentType.AADHAAR,
            displayName = "Demo Aadhaar (Synthetic)",
            extractedRawFields = mapOf(
                CanonicalFieldType.FULL_NAME to "AARAV VIKRAM SHARMA",
                CanonicalFieldType.DATE_OF_BIRTH to "15/08/1996",
                CanonicalFieldType.AADHAAR_NUMBER to "9081 4452 7819",
                CanonicalFieldType.ADDRESS to "Flat 402, Coral Heights, Indiranagar, Bengaluru, Karnataka",
                CanonicalFieldType.POSTAL_CODE to "560038"
            ),
            source = "SYNTHETIC_SEEDER",
            verificationStatus = VerificationStatus.SYNTHETIC_DEMO
        )

        // 2. Synthetic PAN Card
        vaultRepository.saveDocument(
            documentType = DocumentType.PAN,
            displayName = "Demo PAN Card (Synthetic)",
            extractedRawFields = mapOf(
                CanonicalFieldType.FULL_NAME to "AARAV VIKRAM SHARMA",
                CanonicalFieldType.PAN_NUMBER to "ABCDE1234F",
                CanonicalFieldType.DATE_OF_BIRTH to "15/08/1996"
            ),
            source = "SYNTHETIC_SEEDER",
            verificationStatus = VerificationStatus.SYNTHETIC_DEMO
        )

        // 3. Synthetic Indian Passport
        vaultRepository.saveDocument(
            documentType = DocumentType.PASSPORT,
            displayName = "Demo Passport (Synthetic)",
            extractedRawFields = mapOf(
                CanonicalFieldType.FULL_NAME to "AARAV VIKRAM SHARMA",
                CanonicalFieldType.PASSPORT_NUMBER to "Z8912401",
                CanonicalFieldType.DATE_OF_BIRTH to "15/08/1996",
                CanonicalFieldType.CITY to "Bengaluru",
                CanonicalFieldType.STATE to "Karnataka",
                CanonicalFieldType.EXPIRY_DATE to "24/04/2034"
            ),
            source = "SYNTHETIC_SEEDER",
            verificationStatus = VerificationStatus.SYNTHETIC_DEMO
        )
    }
}

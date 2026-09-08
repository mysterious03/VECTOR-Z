package com.iqoo.vectorz.feature.ocr

import com.iqoo.vectorz.core.model.CanonicalFieldType
import com.iqoo.vectorz.core.model.DocumentType

data class ParsedDocumentResult(
    val detectedType: DocumentType,
    val confidence: Float,
    val extractedFields: Map<CanonicalFieldType, String>,
    val rawText: String
)

class DocumentParser {

    companion object {
        private val PAN_REGEX = Regex("[A-Z]{5}[0-9]{4}[A-Z]")
        private val AADHAAR_REGEX = Regex("\\b\\d{4}[ -]?\\d{4}[ -]?\\d{4}\\b")
        private val PASSPORT_REGEX = Regex("\\b[A-PR-WYZ][0-9]{7}\\b")
        private val DOB_REGEX = Regex("\\b\\d{2}[/-]\\d{2}[/-]\\d{4}\\b")
        private val PINCODE_REGEX = Regex("\\b\\d{6}\\b")
    }

    /**
     * Parses OCR text into CanonicalFieldType mapped dictionary with deterministic regex validation.
     */
    fun parseOcrText(rawText: String): ParsedDocumentResult {
        val upperText = rawText.uppercase()
        val fields = mutableMapOf<CanonicalFieldType, String>()

        // 1. Detect PAN Card
        val panMatch = PAN_REGEX.find(upperText)?.value
        if (panMatch != null || upperText.contains("INCOME TAX DEPARTMENT") || upperText.contains("PERMANENT ACCOUNT NUMBER")) {
            panMatch?.let { fields[CanonicalFieldType.PAN_NUMBER] = it }
            extractCommonFields(upperText, rawText, fields)
            return ParsedDocumentResult(
                detectedType = DocumentType.PAN,
                confidence = if (panMatch != null) 0.98f else 0.85f,
                extractedFields = fields,
                rawText = rawText
            )
        }

        // 2. Detect Aadhaar Card
        val aadhaarMatch = AADHAAR_REGEX.find(upperText)?.value
        if (aadhaarMatch != null || upperText.contains("GOVERNMENT OF INDIA") || upperText.contains("UNIQUE IDENTIFICATION") || upperText.contains("MERA AADHAAR")) {
            aadhaarMatch?.let { fields[CanonicalFieldType.AADHAAR_NUMBER] = it.replace("-", " ") }
            extractCommonFields(upperText, rawText, fields)
            return ParsedDocumentResult(
                detectedType = DocumentType.AADHAAR,
                confidence = if (aadhaarMatch != null) 0.99f else 0.88f,
                extractedFields = fields,
                rawText = rawText
            )
        }

        // 3. Detect Passport
        val passportMatch = PASSPORT_REGEX.find(upperText)?.value
        if (passportMatch != null || upperText.contains("REPUBLIC OF INDIA") || upperText.contains("PASSPORT")) {
            passportMatch?.let { fields[CanonicalFieldType.PASSPORT_NUMBER] = it }
            extractCommonFields(upperText, rawText, fields)
            return ParsedDocumentResult(
                detectedType = DocumentType.PASSPORT,
                confidence = if (passportMatch != null) 0.97f else 0.82f,
                extractedFields = fields,
                rawText = rawText
            )
        }

        // Fallback generic extraction
        extractCommonFields(upperText, rawText, fields)
        return ParsedDocumentResult(
            detectedType = DocumentType.CUSTOM,
            confidence = 0.50f,
            extractedFields = fields,
            rawText = rawText
        )
    }

    private fun extractCommonFields(
        upperText: String,
        rawText: String,
        fields: MutableMap<CanonicalFieldType, String>
    ) {
        // Date of Birth
        DOB_REGEX.find(upperText)?.value?.let {
            fields[CanonicalFieldType.DATE_OF_BIRTH] = it
        }

        // PIN Code
        PINCODE_REGEX.find(upperText)?.value?.let {
            fields[CanonicalFieldType.POSTAL_CODE] = it
        }

        // Name Heuristic
        val lines = rawText.lines().map { it.trim() }.filter { it.isNotEmpty() }
        for (line in lines) {
            val upper = line.uppercase()
            if (upper.matches(Regex("^[A-Z ]{3,35}$")) &&
                !upper.contains("INDIA") &&
                !upper.contains("GOVERNMENT") &&
                !upper.contains("DEPARTMENT") &&
                !upper.contains("ACCOUNT") &&
                !upper.contains("PASSPORT") &&
                !upper.contains("AADHAAR") &&
                !upper.contains("MALE") &&
                !upper.contains("FEMALE")
            ) {
                fields[CanonicalFieldType.FULL_NAME] = upper
                break
            }
        }
    }
}

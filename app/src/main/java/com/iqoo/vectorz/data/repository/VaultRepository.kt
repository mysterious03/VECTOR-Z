package com.iqoo.vectorz.data.repository

import com.iqoo.vectorz.core.database.DocumentEntity
import com.iqoo.vectorz.core.database.FieldEntity
import com.iqoo.vectorz.core.database.VaultDao
import com.iqoo.vectorz.core.model.*
import com.iqoo.vectorz.core.security.CryptoEngine
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID

class VaultRepository(
    private val vaultDao: VaultDao,
    private val cryptoEngine: CryptoEngine
) {

    fun getDocumentsFlow(): Flow<List<IdentityDocument>> {
        return vaultDao.getAllDocumentsFlow().map { docEntities ->
            docEntities.map { doc ->
                val fieldEntities = vaultDao.getFieldsForDocument(doc.id)
                IdentityDocument(
                    id = doc.id,
                    documentType = try { DocumentType.valueOf(doc.documentType) } catch (e: Exception) { DocumentType.CUSTOM },
                    displayName = doc.displayName,
                    createdAt = doc.createdAt,
                    updatedAt = doc.updatedAt,
                    source = doc.source,
                    encryptedDocumentPath = doc.encryptedDocumentPath,
                    verificationStatus = try { VerificationStatus.valueOf(doc.verificationStatus) } catch (e: Exception) { VerificationStatus.SYNTHETIC_DEMO },
                    fields = fieldEntities.map { field ->
                        IdentityField(
                            id = field.id,
                            documentId = field.documentId,
                            fieldType = try { CanonicalFieldType.valueOf(field.fieldType) } catch (e: Exception) { CanonicalFieldType.FULL_NAME },
                            encryptedValue = field.encryptedValue,
                            maskedValue = field.maskedValue,
                            policy = try { FieldPolicy.valueOf(field.policy) } catch (e: Exception) { FieldPolicy.MASKED },
                            source = field.source,
                            updatedAt = field.updatedAt
                        )
                    }
                )
            }
        }
    }

    suspend fun saveDocument(
        documentType: DocumentType,
        displayName: String,
        extractedRawFields: Map<CanonicalFieldType, String>,
        source: String = "ON_DEVICE_OCR",
        verificationStatus: VerificationStatus = VerificationStatus.SYNTHETIC_DEMO
    ): String {
        val docId = UUID.randomUUID().toString()
        val now = System.currentTimeMillis()

        val docEntity = DocumentEntity(
            id = docId,
            documentType = documentType.name,
            displayName = displayName,
            createdAt = now,
            updatedAt = now,
            source = source,
            encryptedDocumentPath = null,
            verificationStatus = verificationStatus.name
        )

        val fieldEntities = extractedRawFields.map { (fieldType, rawValue) ->
            val encrypted = cryptoEngine.encrypt(rawValue)
            val masked = cryptoEngine.maskValue(fieldType, rawValue)
            val defaultPolicy = when (fieldType.sensitivity) {
                SensitivityClass.HIGH_SENSITIVITY -> FieldPolicy.REQUIRE_BIOMETRIC
                SensitivityClass.MEDIUM_SENSITIVITY -> FieldPolicy.MASKED
                else -> FieldPolicy.NORMAL
            }

            FieldEntity(
                id = UUID.randomUUID().toString(),
                documentId = docId,
                fieldType = fieldType.name,
                encryptedValue = encrypted,
                maskedValue = masked,
                policy = defaultPolicy.name,
                source = source,
                updatedAt = now
            )
        }

        vaultDao.insertDocument(docEntity)
        vaultDao.insertFields(fieldEntities)
        return docId
    }

    suspend fun getDecryptedFieldValue(field: IdentityField): String {
        return cryptoEngine.decrypt(field.encryptedValue)
    }

    suspend fun findFirstMatchingField(fieldType: CanonicalFieldType): Pair<IdentityField, String>? {
        val fields = vaultDao.getFieldsByType(fieldType.name)
        if (fields.isEmpty()) return null
        val chosen = fields.first()
        val decrypted = cryptoEngine.decrypt(chosen.encryptedValue)
        val domainField = IdentityField(
            id = chosen.id,
            documentId = chosen.documentId,
            fieldType = fieldType,
            encryptedValue = chosen.encryptedValue,
            maskedValue = chosen.maskedValue,
            policy = try { FieldPolicy.valueOf(chosen.policy) } catch (e: Exception) { FieldPolicy.MASKED },
            source = chosen.source,
            updatedAt = chosen.updatedAt
        )
        return Pair(domainField, decrypted)
    }

    suspend fun deleteDocument(docId: String) {
        vaultDao.deleteDocument(docId)
    }

    suspend fun clearVault() {
        vaultDao.clearAll()
    }
}

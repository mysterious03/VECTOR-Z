package com.iqoo.vectorz.core.database

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "identity_documents")
data class DocumentEntity(
    @PrimaryKey val id: String,
    val documentType: String,
    val displayName: String,
    val createdAt: Long,
    val updatedAt: Long,
    val source: String,
    val encryptedDocumentPath: String?,
    val verificationStatus: String
)

@Entity(
    tableName = "identity_fields",
    foreignKeys = [
        ForeignKey(
            entity = DocumentEntity::class,
            parentColumns = ["id"],
            childColumns = ["documentId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("documentId"), Index("fieldType")]
)
data class FieldEntity(
    @PrimaryKey val id: String,
    val documentId: String,
    val fieldType: String,
    val encryptedValue: String,
    val maskedValue: String,
    val policy: String,
    val source: String,
    val updatedAt: Long
)

@Entity(tableName = "audit_events")
data class AuditEventEntity(
    @PrimaryKey val id: String,
    val timestamp: Long,
    val category: String, // AUTOFILL, PAYMENT_GUARD, COMMERCE_GUARD, TRUTH_AUDIT, AGENT, VAULT_ACCESS
    val appPackage: String,
    val actionSummary: String,
    val riskLevel: String,
    val resultStatus: String, // APPROVED, BLOCKED, REVIEW_REQUIRED, REJECTED
    val evidenceDetailsJson: String
)

@Entity(tableName = "app_policies")
data class AppPolicyEntity(
    @PrimaryKey val appPackage: String,
    val appName: String,
    val isAutofillAllowed: Boolean = true,
    val requireBiometricAlways: Boolean = false,
    val updatedAt: Long = System.currentTimeMillis()
)

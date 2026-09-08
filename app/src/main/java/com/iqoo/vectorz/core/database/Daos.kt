package com.iqoo.vectorz.core.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface VaultDao {
    @Query("SELECT * FROM identity_documents ORDER BY createdAt DESC")
    fun getAllDocumentsFlow(): Flow<List<DocumentEntity>>

    @Query("SELECT * FROM identity_documents WHERE id = :docId")
    suspend fun getDocumentById(docId: String): DocumentEntity?

    @Query("SELECT * FROM identity_fields WHERE documentId = :docId")
    suspend fun getFieldsForDocument(docId: String): List<FieldEntity>

    @Query("SELECT * FROM identity_fields WHERE fieldType = :fieldType")
    suspend fun getFieldsByType(fieldType: String): List<FieldEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDocument(document: DocumentEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFields(fields: List<FieldEntity>)

    @Query("DELETE FROM identity_documents WHERE id = :docId")
    suspend fun deleteDocument(docId: String)

    @Query("DELETE FROM identity_documents")
    suspend fun clearAll()
}

@Dao
interface AuditDao {
    @Query("SELECT * FROM audit_events ORDER BY timestamp DESC")
    fun getAllAuditsFlow(): Flow<List<AuditEventEntity>>

    @Query("SELECT * FROM audit_events ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentAuditsFlow(limit: Int = 10): Flow<List<AuditEventEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAudit(event: AuditEventEntity)

    @Query("SELECT COUNT(*) FROM audit_events WHERE riskLevel = 'HIGH_RISK'")
    fun getHighRiskCountFlow(): Flow<Int>

    @Query("DELETE FROM audit_events")
    suspend fun clearAudits()
}

@Dao
interface AppPolicyDao {
    @Query("SELECT * FROM app_policies")
    fun getAllAppPoliciesFlow(): Flow<List<AppPolicyEntity>>

    @Query("SELECT * FROM app_policies WHERE appPackage = :packageName")
    suspend fun getPolicyForApp(packageName: String): AppPolicyEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdatePolicy(policy: AppPolicyEntity)

    @Query("DELETE FROM app_policies")
    suspend fun clearPolicies()
}

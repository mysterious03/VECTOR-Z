package com.iqoo.vectorz.data.repository

import com.iqoo.vectorz.core.database.AppPolicyDao
import com.iqoo.vectorz.core.database.AppPolicyEntity
import com.iqoo.vectorz.core.database.AuditDao
import com.iqoo.vectorz.core.database.AuditEventEntity
import com.iqoo.vectorz.core.model.CanonicalFieldType
import com.iqoo.vectorz.core.model.RiskAssessment
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class AuditRepository(
    private val auditDao: AuditDao,
    private val appPolicyDao: AppPolicyDao
) {

    fun getAuditHistoryFlow(): Flow<List<AuditEventEntity>> = auditDao.getAllAuditsFlow()

    fun getRecentAuditsFlow(limit: Int = 10): Flow<List<AuditEventEntity>> = auditDao.getRecentAuditsFlow(limit)

    fun getAllAppPoliciesFlow(): Flow<List<AppPolicyEntity>> = appPolicyDao.getAllAppPoliciesFlow()

    suspend fun getPolicyForApp(packageName: String): AppPolicyEntity {
        return appPolicyDao.getPolicyForApp(packageName) ?: AppPolicyEntity(
            appPackage = packageName,
            appName = packageName.substringAfterLast("."),
            isAutofillAllowed = true,
            requireBiometricAlways = false
        )
    }

    suspend fun setAppPolicy(policy: AppPolicyEntity) {
        appPolicyDao.insertOrUpdatePolicy(policy)
    }

    suspend fun recordAudit(
        category: String,
        appPackage: String,
        actionSummary: String,
        riskLevel: String,
        resultStatus: String,
        evidenceDetailsJson: String = ""
    ) {
        // Privacy rule: NEVER store raw secrets or passwords in telemetry
        val entity = AuditEventEntity(
            id = UUID.randomUUID().toString(),
            timestamp = System.currentTimeMillis(),
            category = category,
            appPackage = appPackage,
            actionSummary = if (actionSummary.length > 100) actionSummary.take(100) + "..." else actionSummary,
            riskLevel = riskLevel,
            resultStatus = resultStatus,
            evidenceDetailsJson = evidenceDetailsJson
        )
        auditDao.insertAudit(entity)
    }

    suspend fun purgeEphemeralArtifacts() {
        // Purge audit cache & temporary execution buffers
        auditDao.clearAudits()
    }
}

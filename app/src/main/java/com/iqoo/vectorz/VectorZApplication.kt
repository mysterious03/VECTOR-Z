package com.iqoo.vectorz

import android.app.Application
import com.iqoo.vectorz.ai.commerce.CommerceGuardEngine
import com.iqoo.vectorz.ai.inference.IQOOLocalInferenceEngine
import com.iqoo.vectorz.ai.risk.TrustEngine
import com.iqoo.vectorz.ai.truth.TruthAuditEngine
import com.iqoo.vectorz.core.database.VectorZDatabase
import com.iqoo.vectorz.core.security.CryptoEngine
import com.iqoo.vectorz.data.repository.AuditRepository
import com.iqoo.vectorz.data.repository.DemoDataSeeder
import com.iqoo.vectorz.data.repository.VaultRepository
import com.iqoo.vectorz.feature.agent.BoundedAgent
import com.iqoo.vectorz.feature.agent.PolicyEngine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class VectorZApplication : Application() {

    val database by lazy { VectorZDatabase.getInstance(this) }
    val cryptoEngine by lazy { CryptoEngine(this) }
    val vaultRepository by lazy { VaultRepository(database.vaultDao(), cryptoEngine) }
    val auditRepository by lazy { AuditRepository(database.auditDao(), database.appPolicyDao()) }

    val inferenceEngine by lazy { IQOOLocalInferenceEngine() }
    val trustEngine by lazy { TrustEngine(inferenceEngine) }
    val commerceGuardEngine by lazy { CommerceGuardEngine(inferenceEngine) }
    val truthAuditEngine by lazy { TruthAuditEngine(inferenceEngine) }
    val policyEngine by lazy { PolicyEngine() }
    val boundedAgent by lazy { BoundedAgent(vaultRepository, auditRepository, policyEngine) }

    private val applicationScope = CoroutineScope(Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        applicationScope.launch {
            val seeder = DemoDataSeeder(vaultRepository)
            seeder.seedDemoDataIfEmpty()
        }
    }
}

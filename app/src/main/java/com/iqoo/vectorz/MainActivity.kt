package com.iqoo.vectorz

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.iqoo.vectorz.core.database.AppPolicyEntity
import com.iqoo.vectorz.core.model.*
import com.iqoo.vectorz.core.theme.VectorZBlack
import com.iqoo.vectorz.core.theme.VectorZTheme
import com.iqoo.vectorz.data.repository.DemoDataSeeder
import com.iqoo.vectorz.feature.agent.AgentScreen
import com.iqoo.vectorz.feature.commerce.CommerceGuardScreen
import com.iqoo.vectorz.feature.dashboard.HomeScreen
import com.iqoo.vectorz.feature.dashboard.PrivacyDashboardScreen
import com.iqoo.vectorz.feature.ocr.OcrScannerScreen
import com.iqoo.vectorz.feature.payment.PaymentGuardScreen
import com.iqoo.vectorz.feature.simulator.KycFormSimulatorScreen
import com.iqoo.vectorz.feature.truth.TruthAuditScreen
import com.iqoo.vectorz.feature.vault.VaultScreen
import com.iqoo.vectorz.service.overlay.FloatingBubbleService
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val app by lazy { application as VectorZApplication }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            VectorZTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = VectorZBlack
                ) {
                    val navController = rememberNavController()
                    val coroutineScope = rememberCoroutineScope()

                    // Reactive State
                    val documents by app.vaultRepository.getDocumentsFlow().collectAsState(initial = emptyList())
                    val recentAudits by app.auditRepository.getRecentAuditsFlow(10).collectAsState(initial = emptyList())
                    val appPolicies by app.auditRepository.getAllAppPoliciesFlow().collectAsState(initial = emptyList())

                    var isBubbleActive by remember { mutableStateOf(false) }
                    var currentPaymentIntent by remember { mutableStateOf<PaymentIntent?>(null) }
                    var currentPaymentAssessment by remember { mutableStateOf<RiskAssessment?>(null) }
                    var currentCommerceEvaluation by remember { mutableStateOf<CommerceEvaluation?>(null) }
                    var currentTruthReport by remember { mutableStateOf<TruthAuditReport?>(null) }
                    var currentAgentPlan by remember { mutableStateOf<AgentExecutionPlan?>(null) }

                    NavHost(navController = navController, startDestination = "home") {
                        composable("home") {
                            HomeScreen(
                                onNavigateToVault = { navController.navigate("vault") },
                                onNavigateToPaymentGuard = { navController.navigate("payment") },
                                onNavigateToCommerceGuard = { navController.navigate("commerce") },
                                onNavigateToTruthAudit = { navController.navigate("truth") },
                                onNavigateToAgent = { navController.navigate("agent") },
                                onNavigateToSimulator = { navController.navigate("simulator") },
                                onNavigateToPrivacyDashboard = { navController.navigate("privacy") },
                                onToggleBubbleService = { enable ->
                                    isBubbleActive = enable
                                    if (enable) {
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this@MainActivity)) {
                                            val intent = Intent(
                                                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                                Uri.parse("package:$packageName")
                                            )
                                            startActivity(intent)
                                        } else {
                                            val serviceIntent = Intent(this@MainActivity, FloatingBubbleService::class.java)
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                                startForegroundService(serviceIntent)
                                            } else {
                                                startService(serviceIntent)
                                            }
                                        }
                                    } else {
                                        stopService(Intent(this@MainActivity, FloatingBubbleService::class.java))
                                    }
                                },
                                isBubbleActive = isBubbleActive
                            )
                        }

                        composable("vault") {
                            VaultScreen(
                                documents = documents,
                                onAddDocumentClick = { navController.navigate("ocr") },
                                onDeleteDocument = { docId ->
                                    coroutineScope.launch {
                                        app.vaultRepository.deleteDocument(docId)
                                    }
                                },
                                onBack = { navController.popBackStack() }
                            )
                        }

                        composable("ocr") {
                            OcrScannerScreen(
                                onSaveExtracted = { docType, name, fields ->
                                    coroutineScope.launch {
                                        app.vaultRepository.saveDocument(
                                            documentType = docType,
                                            displayName = name,
                                            extractedRawFields = fields,
                                            source = "ON_DEVICE_OCR"
                                        )
                                        app.auditRepository.recordAudit(
                                            category = "VAULT_ACCESS",
                                            appPackage = packageName,
                                            actionSummary = "Saved ${docType.displayName} to vault",
                                            riskLevel = "SAFE",
                                            resultStatus = "STORED"
                                        )
                                    }
                                },
                                onBack = { navController.popBackStack() }
                            )
                        }

                        composable("payment") {
                            PaymentGuardScreen(
                                currentIntent = currentPaymentIntent,
                                currentAssessment = currentPaymentAssessment,
                                onScanOrAuditQr = { uri, contextText ->
                                    val (intent, assessment) = app.trustEngine.auditPaymentIntent(uri, contextText)
                                    currentPaymentIntent = intent
                                    currentPaymentAssessment = assessment
                                    coroutineScope.launch {
                                        app.auditRepository.recordAudit(
                                            category = "PAYMENT_GUARD",
                                            appPackage = packageName,
                                            actionSummary = "Inspected payment intent: ${intent.payeeName ?: intent.payeeVpa}",
                                            riskLevel = assessment.riskLevel.name,
                                            resultStatus = if (intent.isRiskyInstructionDetected) "REVIEW_REQUIRED" else "APPROVED"
                                        )
                                    }
                                },
                                onBack = { navController.popBackStack() }
                            )
                        }

                        composable("commerce") {
                            CommerceGuardScreen(
                                currentEvaluation = currentCommerceEvaluation,
                                onEvaluateProduct = { text ->
                                    coroutineScope.launch {
                                        val eval = app.commerceGuardEngine.evaluateProduct(text)
                                        currentCommerceEvaluation = eval
                                        app.auditRepository.recordAudit(
                                            category = "COMMERCE_GUARD",
                                            appPackage = packageName,
                                            actionSummary = "Audited product listing (${eval.extractedPrice})",
                                            riskLevel = if (eval.decision == CommerceDecision.AVOID) "HIGH_RISK" else "SAFE",
                                            resultStatus = eval.decision.name
                                        )
                                    }
                                },
                                onBack = { navController.popBackStack() }
                            )
                        }

                        composable("truth") {
                            TruthAuditScreen(
                                currentReport = currentTruthReport,
                                onAuditContent = { text ->
                                    coroutineScope.launch {
                                        val report = app.truthAuditEngine.auditContent(text)
                                        currentTruthReport = report
                                        app.auditRepository.recordAudit(
                                            category = "TRUTH_AUDIT",
                                            appPackage = packageName,
                                            actionSummary = "Truth check: ${report.overallStatus.name}",
                                            riskLevel = if (report.overallStatus == TruthStatus.DISPUTED) "HIGH_RISK" else "SAFE",
                                            resultStatus = report.overallStatus.name
                                        )
                                    }
                                },
                                onBack = { navController.popBackStack() }
                            )
                        }

                        composable("agent") {
                            AgentScreen(
                                currentPlan = currentAgentPlan,
                                onRunCommand = { cmd ->
                                    coroutineScope.launch {
                                        currentAgentPlan = app.boundedAgent.planAction(cmd)
                                    }
                                },
                                onConfirmExecution = {
                                    coroutineScope.launch {
                                        currentAgentPlan?.targetField?.let { field ->
                                            app.boundedAgent.executeConfirmedStep("com.demo.travelapp", field)
                                            navController.navigate("simulator")
                                        }
                                    }
                                },
                                onBack = { navController.popBackStack() }
                            )
                        }

                        composable("simulator") {
                            KycFormSimulatorScreen(
                                onTriggerAutofill = { fieldType, onFilled ->
                                    coroutineScope.launch {
                                        val match = app.vaultRepository.findFirstMatchingField(fieldType)
                                        if (match != null) {
                                            onFilled(match.second)
                                            app.auditRepository.recordAudit(
                                                category = "AUTOFILL",
                                                appPackage = "com.demo.kycapp",
                                                actionSummary = "Autofilled ${fieldType.label}",
                                                riskLevel = "SAFE",
                                                resultStatus = "APPROVED"
                                            )
                                        }
                                    }
                                },
                                onBack = { navController.popBackStack() }
                            )
                        }

                        composable("privacy") {
                            PrivacyDashboardScreen(
                                recentAudits = recentAudits,
                                appPolicies = appPolicies,
                                onToggleAppPolicy = { pkg, allowed ->
                                    coroutineScope.launch {
                                        app.auditRepository.setAppPolicy(
                                            AppPolicyEntity(
                                                appPackage = pkg,
                                                appName = pkg.substringAfterLast("."),
                                                isAutofillAllowed = allowed
                                            )
                                        )
                                    }
                                },
                                onPurgeEphemeralCache = {
                                    coroutineScope.launch {
                                        app.auditRepository.purgeEphemeralArtifacts()
                                    }
                                },
                                onResetDemoData = {
                                    coroutineScope.launch {
                                        app.vaultRepository.clearVault()
                                        DemoDataSeeder(app.vaultRepository).seedDemoDataIfEmpty()
                                    }
                                },
                                onBack = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}

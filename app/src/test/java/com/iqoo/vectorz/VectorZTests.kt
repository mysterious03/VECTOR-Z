package com.iqoo.vectorz

import com.iqoo.vectorz.ai.commerce.CommerceGuardEngine
import com.iqoo.vectorz.ai.inference.IQOOLocalInferenceEngine
import com.iqoo.vectorz.ai.risk.TrustEngine
import com.iqoo.vectorz.ai.truth.TruthAuditEngine
import com.iqoo.vectorz.core.model.*
import com.iqoo.vectorz.feature.agent.PolicyDecision
import com.iqoo.vectorz.feature.agent.PolicyEngine
import com.iqoo.vectorz.feature.ocr.DocumentParser
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test

class VectorZTests {

    private val inferenceEngine = IQOOLocalInferenceEngine()
    private val parser = DocumentParser()
    private val trustEngine = TrustEngine(inferenceEngine)
    private val commerceEngine = CommerceGuardEngine(inferenceEngine)
    private val truthEngine = TruthAuditEngine(inferenceEngine)
    private val policyEngine = PolicyEngine()

    @Test
    fun `test canonical PAN and Aadhaar extraction`() {
        val panOcr = "INCOME TAX DEPARTMENT\nAARAV VIKRAM SHARMA\n15/08/1996\nABCDE1234F"
        val panResult = parser.parseOcrText(panOcr)
        assertEquals(DocumentType.PAN, panResult.detectedType)
        assertEquals("ABCDE1234F", panResult.extractedFields[CanonicalFieldType.PAN_NUMBER])
        assertEquals("15/08/1996", panResult.extractedFields[CanonicalFieldType.DATE_OF_BIRTH])

        val aadhOcr = "GOVERNMENT OF INDIA\nAARAV VIKRAM SHARMA\nDOB: 15/08/1996\n9081 4452 7819\nBengaluru 560038"
        val aadhResult = parser.parseOcrText(aadhOcr)
        assertEquals(DocumentType.AADHAAR, aadhResult.detectedType)
        assertEquals("9081 4452 7819", aadhResult.extractedFields[CanonicalFieldType.AADHAAR_NUMBER])
        assertEquals("560038", aadhResult.extractedFields[CanonicalFieldType.POSTAL_CODE])
    }

    @Test
    fun `test refund and UPI PIN scam triggers HIGH RISK`() = runBlocking {
        val scamMessage = "Your refund of ₹4,999 is pending. Scan QR and enter UPI PIN immediately to receive."
        val assessment = trustEngine.auditText(scamMessage)
        assertEquals(RiskLevel.HIGH_RISK, assessment.riskLevel)
        assertTrue(assessment.score >= 70)
        assertTrue(assessment.evidenceList.any { it.tag.contains("PIN") })
    }

    @Test
    fun `test Payment Intent Guard flags intent inversion conflict`() {
        val qrUri = "upi://pay?pa=refund-desk@fakebank&pn=Refund+Desk&am=2500"
        val context = "Customer care says: Scan this QR to RECEIVE your refund."
        val (intent, assessment) = trustEngine.auditPaymentIntent(qrUri, context)
        assertTrue(intent.isRiskyInstructionDetected)
        assertEquals(RiskLevel.HIGH_RISK, assessment.riskLevel)
    }

    @Test
    fun `test Commerce Guard detects non-refundable off-platform scam`() = runBlocking {
        val scamProduct = "Earbuds 90% OFF from ₹9999 to ₹999. Final Sale - strictly Non-refundable. WhatsApp pay before dispatch."
        val eval = commerceEngine.evaluateProduct(scamProduct)
        assertEquals(CommerceDecision.AVOID, eval.decision)
        assertTrue(eval.signals.any { it.title.contains("Off-Platform") || it.title.contains("Non-Returnable") })
    }

    @Test
    fun `test Truth Audit extracts atomic claims and identifies disputed rumors`() = runBlocking {
        val rumor = "Government is giving free electricity relief of ₹50,000. Claim your RBI lottery bonus now."
        val report = truthEngine.auditContent(rumor)
        assertEquals(TruthStatus.DISPUTED, report.overallStatus)
        assertTrue(report.atomicClaims.isNotEmpty())
    }

    @Test
    fun `test Agent policy engine hard blocks UPI PIN and OTP commands`() {
        val blockedPin = policyEngine.evaluateAction("Enter my UPI PIN and transfer money", null)
        assertTrue(blockedPin is PolicyDecision.Blocked)

        val blockedOtp = policyEngine.evaluateAction("Read OTP and authorize transaction", null)
        assertTrue(blockedOtp is PolicyDecision.Blocked)

        val allowedPassport = policyEngine.evaluateAction("Fill the passport number", CanonicalFieldType.PASSPORT_NUMBER)
        assertTrue(allowedPassport is PolicyDecision.Allowed)
        assertTrue((allowedPassport as PolicyDecision.Allowed).requiresBiometric)
    }

    @Test
    fun `test Notification Guard intercepts electricity and rogue APK scams`() {
        val notifEngine = com.iqoo.vectorz.service.notification.NotificationGuardEngine()
        
        val elecScam = notifEngine.inspectNotification(
            "com.google.android.apps.messaging",
            "POWER BOARD",
            "Dear consumer, your electricity will be disconnected tonight at 9:30 PM. Download update: http://power-board.in/update.apk"
        )
        assertEquals(com.iqoo.vectorz.service.notification.ScamCategory.ELECTRICITY_DISCONNECTION_SCAM, elecScam.threatCategory)
        assertTrue(elecScam.riskScore >= 0.90f)
        assertNotNull(elecScam.blockedApkUrl)

        val cleanNotif = notifEngine.inspectNotification(
            "com.whatsapp",
            "Mom",
            "Hi, please pick up groceries on your way back."
        )
        assertEquals(com.iqoo.vectorz.service.notification.ScamCategory.CLEAN_NOTIFICATION, cleanNotif.threatCategory)
    }

    @Test
    fun `test Multi-Modal Inspector flags synthetic voice clones and visual deepfakes`() {
        val mediaEngine = com.iqoo.vectorz.ai.multimodal.MediaInspectorEngine()

        val voiceResult = mediaEngine.analyzeVoiceSample(
            audioDurationMs = 5000,
            hasHarmonicDistortion = true,
            ambientReverbRatio = 0.02f,
            detectedKeywords = listOf("urgent transfer", "hospital emergency")
        )
        assertEquals(com.iqoo.vectorz.ai.multimodal.MediaRiskLevel.DEEPFAKE_ALERT, voiceResult.riskLevel)
        assertTrue(voiceResult.syntheticLikelihood >= 0.70f)

        val deepfakeImage = mediaEngine.analyzeVisualMedia(
            facialBoundaryBlurScore = 0.85f,
            eyeBlinkIrregularity = true,
            lightingInconsistency = true
        )
        assertEquals(com.iqoo.vectorz.ai.multimodal.MediaRiskLevel.DEEPFAKE_ALERT, deepfakeImage.riskLevel)
    }

    @Test
    fun `test Granular App Permission Matrix restricts unauthorized field access`() {
        val matrix = com.iqoo.vectorz.core.policy.AppPermissionMatrix()
        
        // Amazon Shopping should have access to address, but NOT PAN
        assertTrue(matrix.isFieldAllowedForApp("in.amazon.mShop.android.shopping", "address"))
        assertFalse(matrix.isFieldAllowedForApp("in.amazon.mShop.android.shopping", "pan_number"))
        
        // Suspicious app should be blocked completely
        assertFalse(matrix.isFieldAllowedForApp("com.unverified.loanapp", "phone"))
    }

    @Test
    fun `test NPU Hardware Benchmark reports sub-5ms latency and zero cloud calls`() {
        val benchmark = com.iqoo.vectorz.ai.benchmark.NpuBenchmarkEngine()
        val metrics = benchmark.benchmarkOperation("PAYMENT_INTENT_AUDIT")
        assertTrue(metrics.latencyMs < 5.0)
        assertEquals(0, metrics.cloudCallsCount)
        assertEquals("100% ON-DEVICE", metrics.privacyScore)
    }

    @Test
    fun `test Test Vector Library covers all 15 Indian threat archetypes`() {
        val vectors = com.iqoo.vectorz.feature.simulator.TestVectorLibrary.vectors
        assertEquals(15, vectors.size)
        assertTrue(vectors.any { it.title.contains("FedEx") })
        assertTrue(vectors.any { it.title.contains("Electricity") })
        assertTrue(vectors.any { it.title.contains("Swiggy") && it.expectedThreatLevel == "CLEAN" })
    }

    @Test
    fun `test Screen Trust Analyzer shields banking apps during remote screen share`() {
        val analyzer = com.iqoo.vectorz.service.ambient.ScreenTrustAnalyzer()
        
        val riskScreen = analyzer.evaluateScreenState(
            currentForegroundApp = "com.demo.banking",
            runningBackgroundApps = listOf("com.anydesk.anydeskandroid"),
            isDisplayCapturing = true
        )
        assertTrue(riskScreen.requiresScreenShield)
        assertTrue(riskScreen.isRemoteAccessToolRunning)

        val safeScreen = analyzer.evaluateScreenState(
            currentForegroundApp = "com.google.android.calculator",
            runningBackgroundApps = emptyList(),
            isDisplayCapturing = false
        )
        assertFalse(safeScreen.requiresScreenShield)
    }

    @Test
    fun `test Panic Shield wipes memory buffers and freezes Keystore keys`() {
        val panicEngine = com.iqoo.vectorz.core.security.PanicShieldEngine()
        val lockdownState = panicEngine.triggerEmergencyLockdown()
        assertTrue(lockdownState.isLockdownActive)
        assertTrue(lockdownState.memoryBuffersWipedCount > 0)
        assertEquals(15, lockdownState.keystoreCooldownMinutesRemaining)

        val restored = panicEngine.liftEmergencyLockdown(biometricProofConfirmed = true)
        assertTrue(restored)
        assertFalse(panicEngine.state.value.isLockdownActive)
    }

    @Test
    fun `test Threat Intel Engine matches known Indian scam VPAs and domains`() {
        val intel = com.iqoo.vectorz.ai.threat.ThreatIntelEngine()
        val vpaResult = intel.queryVpa("refund-desk@fakebank")
        assertTrue(vpaResult.isKnownMalicious)
        assertEquals("VERIFIED_UPI_SCAM_VPA", vpaResult.threatCategory)

        val domainResult = intel.queryDomain("power-board.in")
        assertTrue(domainResult.isKnownMalicious)
        assertEquals("VERIFIED_PHISHING_DOMAIN", domainResult.threatCategory)
    }

    @Test
    fun `test SMS Spoof Analyzer detects 10-digit mobile banking impersonation`() {
        val spoofAnalyzer = com.iqoo.vectorz.service.notification.SmsSpoofAnalyzer()
        
        val fakeBankSms = spoofAnalyzer.auditSmsHeader(
            sender = "+919876543210",
            messageBody = "Your SBI netbanking account is blocked. Update immediately."
        )
        assertTrue(fakeBankSms.isBankingImpersonation)
        assertEquals("CRITICAL_SPOOF", fakeBankSms.threatLevel)

        val legitBankSms = spoofAnalyzer.auditSmsHeader(
            sender = "AD-HDFCBK",
            messageBody = "₹1,200 spent on your card ending 9182."
        )
        assertTrue(legitBankSms.isOfficialDltHeader)
        assertFalse(legitBankSms.isBankingImpersonation)
    }
}




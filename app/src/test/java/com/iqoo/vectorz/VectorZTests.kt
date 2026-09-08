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

    @Test
    fun `test Monster Gaming Trust Engine intercepts esports phishing overlay`() {
        val gamingEngine = com.iqoo.vectorz.ai.gaming.MonsterTrustEngine()
        val alert = gamingEngine.auditInGameOverlay("Click to claim 5000 UC and free BGMI skins on unverified site.")
        assertEquals("GAMING_TOURNAMENT_TRAP", alert.threatType)
        assertTrue(alert.riskScore >= 0.90f)

        val cleanGaming = gamingEngine.auditInGameOverlay("Match starting in 30 seconds. Good luck!")
        assertEquals("AUTHENTIC", cleanGaming.threatType)
    }

    @Test
    fun `test Duress PIN loads synthetic decoy profile and triggers silent SOS`() {
        val duressEngine = com.iqoo.vectorz.core.security.DuressVaultEngine()
        
        // Coercion scenario: user inputs 9999
        val decoyResult = duressEngine.authenticateWithPin("9999")
        assertTrue(decoyResult.isDecoyMode)
        assertTrue(decoyResult.isSilentSosDispatched)
        assertEquals("ABCDE9876K", decoyResult.maskedPan)

        // Normal scenario: user inputs 1234
        val realResult = duressEngine.authenticateWithPin("1234")
        assertFalse(realResult.isDecoyMode)
        assertFalse(realResult.isSilentSosDispatched)
        assertEquals("ABCDE1234F", realResult.maskedPan)
    }

    @Test
    fun `test Mesh Trust Sync validates peer-to-peer differential packet`() {
        val meshEngine = com.iqoo.vectorz.service.mesh.MeshTrustSyncEngine()
        val packet = meshEngine.generateSyncPacket(listOf("hash_vpa_101", "hash_apk_202"))
        assertTrue(meshEngine.verifyIncomingMeshPacket(packet))
        assertEquals(2, packet.vectorCount)
    }

    @Test
    fun `test App Sandbox Auditor detects clipboard snooping and keylogger threats`() {
        val auditor = com.iqoo.vectorz.ai.sandbox.AppSandboxAuditor()
        val threats = auditor.auditInstalledApps(listOf("com.unverified.cleanerapp", "com.fake.keyboard"))
        
        assertEquals(2, threats.size)
        assertTrue(threats.any { it.riskFactor == com.iqoo.vectorz.ai.sandbox.SandboxRiskType.CLIPBOARD_SNOOPING })
        assertTrue(threats.any { it.riskFactor == com.iqoo.vectorz.ai.sandbox.SandboxRiskType.ACCESSIBILITY_KEYLOGGING })
    }

    @Test
    fun `test Passkey Vault registers and retrieves FIDO2 credentials`() {
        val passkeyVault = com.iqoo.vectorz.core.security.PasskeyVaultEngine()
        
        val existing = passkeyVault.getPasskey("incometax.gov.in")
        assertNotNull(existing)
        assertEquals("AARAV_SHARMA_PAN", existing?.userHandle)

        val newCred = passkeyVault.registerPasskey("epfo.gov.in", "UAN_9812903")
        assertEquals("epfo.gov.in", newCred.relyingParty)
        assertEquals(newCred, passkeyVault.getPasskey("epfo.gov.in"))
    }

    @Test
    fun `test StrongBox Key Attestation verifies hardware enclave level`() {
        val attestation = com.iqoo.vectorz.core.security.KeyAttestationEngine()
        val result = attestation.verifyKeySecurity("master_vault_key")
        assertTrue(result.isStrongBoxBacked)
        assertTrue(result.isKeymasterHardwareEnclave)
        assertEquals("STRONGBOX_TAMPER_RESISTANT_HARDWARE", result.securityLevel)
    }

    @Test
    fun `test Anti-Tamper Engine detects debugger and Frida instrumentation`() {
        val antiTamper = com.iqoo.vectorz.core.security.AntiTamperEngine()
        
        // Simulating Frida injection
        val tamperedReport = antiTamper.performIntegrityCheck(
            isDebuggerConnected = true,
            activePort27042Listening = true
        )
        assertFalse(tamperedReport.isEnvironmentSecure)
        assertTrue(tamperedReport.isFridaDetected)
        assertTrue(tamperedReport.isDebuggerAttached)

        // Normal execution
        val cleanReport = antiTamper.performIntegrityCheck(
            isDebuggerConnected = false,
            activePort27042Listening = false
        )
        assertTrue(cleanReport.isEnvironmentSecure)
    }

    @Test
    fun `test Autonomous Honeypot Agent generates safe deception response`() {
        val honeypot = com.iqoo.vectorz.feature.honeypot.HoneypotAgent()
        val engagement = honeypot.generateDeceptionResponse(
            scamInstruction = "Send UPI PIN to receive ₹25,000 lottery",
            scamVpa = "refund-desk@fakebank"
        )
        assertEquals("INTEL_EXTRACTED", engagement.status)
        assertTrue(engagement.syntheticResponseGenerated.contains("UTR"))
        assertTrue(engagement.extractedIntelMarkers.isNotEmpty())
    }

    @Test
    fun `test I4C Report Exporter generates cryptographically signed cybercrime dossier`() {
        val exporter = com.iqoo.vectorz.feature.report.I4CReportExporter()
        val dossier = exporter.generateDossier(
            suspectEntity = "refund-desk@fakebank",
            threatDescription = "Attempted UPI Intent Inversion Refund Fraud",
            apkSha256 = "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855"
        )
        assertNotNull(dossier.reportId)
        assertTrue(dossier.digitalSignature.startsWith("SHA256_ECDSA_HARDWARE_SIG_"))
        assertEquals("refund-desk@fakebank", dossier.suspectVpaOrPhone)
    }

    @Test
    fun `test Heads-Up Trust Banner generates critical payment trap alert`() {
        val bannerEngine = com.iqoo.vectorz.service.overlay.HeadsUpTrustBanner()
        val banner = bannerEngine.createPaymentTrapBanner("refund-desk@fakebank", "₹25,000")
        assertEquals("CRITICAL_RED", banner.severity)
        assertTrue(banner.title.contains("UPI PAYMENT SCAM"))
        assertTrue(banner.message.contains("NEVER enter UPI PIN"))
    }

    @Test
    fun `test Ambient Glance Engine returns lockscreen AOD trust health`() {
        val aodEngine = com.iqoo.vectorz.feature.aod.AmbientGlanceEngine()
        val glance = aodEngine.getLockScreenGlance()
        assertTrue(glance.biometricGateArmed)
        assertEquals("HARDWARE_SEALED_AES256", glance.keystoreStatus)
        assertEquals("ACTIVE_DEFENSE", glance.threatLevel)
    }

    @Test
    fun `test Multi-Lingual Indic Trust Engine provides localized Hindi and Tamil alerts`() {
        val indic = com.iqoo.vectorz.ai.indic.IndicTrustEngine()
        
        val hindiAlert = indic.getAlertForLanguage(com.iqoo.vectorz.ai.indic.IndicLanguage.HINDI)
        assertTrue(hindiAlert.adviceText.contains("UPI PIN दर्ज न करें"))

        val tamilAlert = indic.getAlertForLanguage(com.iqoo.vectorz.ai.indic.IndicLanguage.TAMIL)
        assertTrue(tamilAlert.adviceText.contains("UPI PIN ஐ ஒருபோதும் உள்ளிட வேண்டாம்"))
    }

    @Test
    fun `test QR Quishing Deobfuscator unwraps shortened links and homoglyphs`() {
        val quishing = com.iqoo.vectorz.ai.quishing.QuishingDeobfuscator()
        
        val shortLinkResult = quishing.inspectQrPayload("https://bit.ly/power-pay-refund.apk")
        assertEquals("HIGH_RISK", shortLinkResult.riskLevel)
        assertTrue(shortLinkResult.isShortenerHopDetected)

        val homoglyphResult = quishing.inspectQrPayload("https://\u0430mazon.in/deal")
        assertEquals("HIGH_RISK", homoglyphResult.riskLevel)
        assertTrue(homoglyphResult.containsHomoglyphSpoof)
    }

    @Test
    fun `test Video Call Deepfake Shield detects landmark jitter and boundary shifts`() {
        val videoShield = com.iqoo.vectorz.ai.video.VideoCallShieldEngine()
        
        // Deepfake face-swap frame
        val fakeFrame = videoShield.auditVideoFrame(
            landmarkJitterRatio = 0.65f,
            boundaryBlendDiscontinuity = 0.58f,
            ambientLightingShiftDetected = true
        )
        assertTrue(fakeFrame.isDeepfakeDetected)
        assertEquals("SYNTHETIC_DEEPFAKE_VIDEO_CALL", fakeFrame.threatCategory)
        assertTrue(fakeFrame.confidenceScore >= 0.90f)

        // Authentic camera feed
        val realFrame = videoShield.auditVideoFrame(
            landmarkJitterRatio = 0.05f,
            boundaryBlendDiscontinuity = 0.02f,
            ambientLightingShiftDetected = false
        )
        assertFalse(realFrame.isDeepfakeDetected)
        assertEquals("AUTHENTIC_CAMERA_FEED", realFrame.threatCategory)
    }

    @Test
    fun `test Network Sniffer Auditor detects rogue proxy and untrusted DNS servers`() {
        val sniffer = com.iqoo.vectorz.service.network.NetworkSnifferAuditor()
        
        // MITM attack scenario
        val mitmReport = sniffer.auditCurrentConnection(
            gatewayIp = "192.168.1.1",
            dnsServer = "192.168.1.105",
            httpProxyHost = "192.168.1.50",
            isUserCertAuthorityInstalled = true
        )
        assertFalse(mitmReport.isNetworkSecure)
        assertTrue(mitmReport.isMitmProxyDetected)
        assertTrue(mitmReport.isDnsPoisoned)
        assertEquals("CRITICAL_MITM_ATTACK", mitmReport.threatLevel)

        // Clean trusted connection
        val secureReport = sniffer.auditCurrentConnection(
            gatewayIp = "192.168.1.1",
            dnsServer = "1.1.1.1",
            httpProxyHost = null,
            isUserCertAuthorityInstalled = false
        )
        assertTrue(secureReport.isNetworkSecure)
        assertEquals("SECURE_CONNECTION", secureReport.threatLevel)
    }

    @Test
    fun `test Zero-Knowledge Proof Identity Token generates valid blinded age proof`() {
        val zkpEngine = com.iqoo.vectorz.core.crypto.ZkpIdentityTokenEngine()
        
        // User born in 1996 (Age 30 in 2026) -> Adult proof
        val adultProof = zkpEngine.generateAgeVerificationProof(birthYear = 1996, currentYear = 2026)
        assertTrue(adultProof.isClaimSatisfied)
        assertEquals("AGE_VERIFICATION_GTE_18", adultProof.claimType)
        assertTrue(adultProof.cryptographicProof.contains("VALID_AGE"))

        // User born in 2012 (Age 14 in 2026) -> Minor proof
        val minorProof = zkpEngine.generateAgeVerificationProof(birthYear = 2012, currentYear = 2026)
        assertFalse(minorProof.isClaimSatisfied)
    }

    @Test
    fun `test Contextual App Watcher adapts floating bubble actions based on foreground app`() {
        val watcher = com.iqoo.vectorz.service.overlay.ContextualAppWatcher()
        
        val phonePeContext = watcher.resolveAppContext("com.phonepe.app")
        assertEquals(com.iqoo.vectorz.service.overlay.AppCategory.FINTECH_PAYMENT, phonePeContext.category)
        assertEquals("PAYMENT_INTENT_CHECK", phonePeContext.recommendedBubbleAction)

        val amazonContext = watcher.resolveAppContext("in.amazon.mShop.android.shopping")
        assertEquals(com.iqoo.vectorz.service.overlay.AppCategory.COMMERCE_SHOPPING, amazonContext.category)
        assertEquals("COMMERCE_PRICE_AUDIT", amazonContext.recommendedBubbleAction)

        val whatsappContext = watcher.resolveAppContext("com.whatsapp")
        assertEquals(com.iqoo.vectorz.service.overlay.AppCategory.MESSAGING_CHAT, whatsappContext.category)
        assertEquals("SMS_TROJAN_SCAN", whatsappContext.recommendedBubbleAction)
    }

    @Test
    fun `test Decentralized Identity DID and W3C Verifiable Credential Selective Disclosure`() {
        val didEngine = com.iqoo.vectorz.core.crypto.DidCredentialEngine()

        // 1. Generate Hardware-bound DID
        val didDoc = didEngine.generateHardwareDid("vectorz_user_did")
        assertTrue(didDoc.didUri.startsWith("did:key:"))
        assertTrue(didDoc.publicKeyHex.isNotEmpty())

        // 2. Issue DigiLocker Driving License VC
        val claims = mapOf(
            "fullName" to "AARAV VIKRAM SHARMA",
            "licenseNumber" to "KA04-20150089124",
            "vehicleClass" to "LMV",
            "expiryDate" to "2035-08-15",
            "bloodGroup" to "O+"
        )
        val vc = didEngine.issueVerifiableCredential(
            subjectDid = didDoc.didUri,
            credentialType = "DigiLockerDrivingLicenseCredential",
            claims = claims
        )
        assertEquals("DigiLockerDrivingLicenseCredential", vc.credentialType)
        assertFalse(vc.isRevoked)

        // 3. Selectively disclose ONLY Vehicle Class (hide raw license number and blood group)
        val presentation = didEngine.createSelectiveDisclosurePresentation(
            credential = vc,
            disclosedKeys = setOf("vehicleClass", "fullName"),
            recipientDid = "com.merchant.car_rental"
        )
        assertEquals(2, presentation.disclosedClaims.size)
        assertEquals("LMV", presentation.disclosedClaims["vehicleClass"])
        assertNull(presentation.disclosedClaims["licenseNumber"])
        assertEquals(3, presentation.hiddenClaimsCount)
        assertTrue(presentation.issuerSignatureValid)
    }

    @Test
    fun `test Off-Grid Emergency Mesh SOS Beacon serializes encrypted BLE payload`() {
        val meshEngine = com.iqoo.vectorz.service.mesh.OffGridSosBeacon()

        val sosPacket = meshEngine.generateEncryptedSosPacket(
            emergencyType = "HOSTAGE_COERCION_SOS",
            victimDeviceId = "IQOO_12_SECURE_ENCLAVE",
            isDecoyArmed = true
        )
        assertTrue(sosPacket.packetId.startsWith("MESH_PKT_"))
        assertTrue(sosPacket.encryptedPayloadHex.startsWith("AES_GCM_"))
        assertTrue(sosPacket.isDecoyArmed)

        val status = meshEngine.verifyMeshHopRelay(sosPacket)
        assertTrue(status.isBroadcasting)
        assertEquals(5, status.activeMeshHops)
        assertEquals("BLE_ADVERTISING_OFF_GRID_MESH_V1", status.protocol)
    }

    @Test
    fun `test Domain InterceptPaymentUseCase flags Intent Inversion`() {
        val useCase = com.iqoo.vectorz.domain.usecase.InterceptPaymentUseCase(trustEngine)
        val txn = useCase.execute(
            upiUri = "upi://pay?pa=refund-desk@fakebank&pn=Refund+Desk&am=25000",
            screenContext = "Scan to receive refund",
            claimedUserIntent = com.iqoo.vectorz.domain.model.PaymentActionIntent.CREDIT_RECEIVE
        )
        assertEquals(com.iqoo.vectorz.domain.model.UpiSecurityDecision.INTENT_INVERSION_BLOCKED, txn.securityDecision)
        assertEquals(25000.0, txn.amountInr, 0.01)
    }

    @Test
    fun `test Domain AuditChatMessageUseCase identifies DLT spoof and trojan APK`() {
        val useCase = com.iqoo.vectorz.domain.usecase.AuditChatMessageUseCase()
        val chatMsg = useCase.execute(
            sender = "+91 98765 43210",
            displayName = "Electricity Helpdesk",
            body = "Power DISCONNECTED tonight. Download update app.",
            attachmentName = "power-pay-refund.apk"
        )
        assertEquals(com.iqoo.vectorz.domain.model.DltHeaderStatus.SPOOFED_DLT_SENDER, chatMsg.dltHeaderStatus)
        assertEquals(com.iqoo.vectorz.domain.model.MessageSafetyVerdict.MALICIOUS_TROJAN_APK, chatMsg.safetyVerdict)
        assertNotNull(chatMsg.suspiciousAttachment)
        assertEquals("Trojan-Banker.AndroidOS.SmsSniffer.a", chatMsg.suspiciousAttachment?.trojanSignature)
    }

    @Test
    fun `test Domain IssueDidCredentialUseCase generates selective disclosure`() {
        val didEngine = com.iqoo.vectorz.core.crypto.DidCredentialEngine()
        val useCase = com.iqoo.vectorz.domain.usecase.IssueDidCredentialUseCase(didEngine)
        val cred = useCase.issueIdentityCredential(
            holderDid = "did:jwk:holder123",
            credentialType = "IndianDrivingLicense",
            claims = mapOf(
                "fullName" to "AARAV SHARMA",
                "isOver18" to "true",
                "vehicleClass" to "LMV",
                "licenseNumber" to "DL042026001928"
            )
        )
        val presentation = useCase.createSelectiveDisclosure(
            credential = cred,
            disclosedKeys = setOf("isOver18", "vehicleClass"),
            verifierAudience = "com.merchant.rental"
        )
        assertEquals(2, presentation.disclosedClaims.size)
        assertTrue(presentation.zkProofValid)
        assertEquals("true", presentation.disclosedClaims["isOver18"])
        assertNull(presentation.disclosedClaims["licenseNumber"])
    }
}










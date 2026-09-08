// VECTOR-Z — Interactive iQOO Companion Simulator Engine

// Canonical In-Memory Hardware Vault (Simulating AES-256-GCM Keystore)
const initialVault = [
    {
        id: "doc-aadhaar",
        type: "AADHAAR",
        title: "Aadhaar Card",
        category: "Government ID",
        fields: [
            { label: "Full Legal Name", key: "FULL_NAME", value: "AARAV VIKRAM SHARMA", masked: "AARAV VIKRAM SHARMA", sensitivity: "MEDIUM" },
            { label: "12-Digit Aadhaar", key: "AADHAAR_NUMBER", value: "9081 4452 7819", masked: "XXXX-XXXX-7819", sensitivity: "HIGH" },
            { label: "Date of Birth", key: "DATE_OF_BIRTH", value: "15/08/1996", masked: "15/08/1996", sensitivity: "MEDIUM" },
            { label: "Full Address", key: "ADDRESS", value: "Flat 402, Coral Heights, Indiranagar, Bengaluru", masked: "Flat 402, Coral Heights...", sensitivity: "MEDIUM" },
            { label: "PIN Code", key: "POSTAL_CODE", value: "560038", masked: "560038", sensitivity: "MEDIUM" }
        ]
    },
    {
        id: "doc-pan",
        type: "PAN",
        title: "PAN Card",
        category: "Tax & Financial ID",
        fields: [
            { label: "Full Legal Name", key: "FULL_NAME", value: "AARAV VIKRAM SHARMA", masked: "AARAV VIKRAM SHARMA", sensitivity: "MEDIUM" },
            { label: "PAN Number", key: "PAN_NUMBER", value: "ABCDE1234F", masked: "XXXXX1234X", sensitivity: "HIGH" },
            { label: "Date of Birth", key: "DATE_OF_BIRTH", value: "15/08/1996", masked: "15/08/1996", sensitivity: "MEDIUM" }
        ]
    },
    {
        id: "doc-passport",
        type: "PASSPORT",
        title: "Indian Passport",
        category: "Travel & Identity",
        fields: [
            { label: "Full Legal Name", key: "FULL_NAME", value: "AARAV VIKRAM SHARMA", masked: "AARAV VIKRAM SHARMA", sensitivity: "MEDIUM" },
            { label: "Passport Number", key: "PASSPORT_NUMBER", value: "Z8912401", masked: "ZXXXXXX1", sensitivity: "HIGH" },
            { label: "Date of Birth", key: "DATE_OF_BIRTH", value: "15/08/1996", masked: "15/08/1996", sensitivity: "MEDIUM" }
        ]
    }
];

let vaultData = JSON.parse(JSON.stringify(initialVault));
let pendingAutofill = null;
let auditLedger = [
    { category: "PAYMENT_GUARD", app: "com.phonepe.app", action: "Inspected QR: ABC MART REFUND", risk: "HIGH_RISK", status: "CONFLICT_ALERT", time: "09:38" },
    { category: "VAULT_ACCESS", app: "com.iqoo.vectorz", action: "Saved PAN Card to Keystore", risk: "SAFE", status: "STORED", time: "09:30" }
];

// Initialize on Load
document.addEventListener("DOMContentLoaded", () => {
    renderVaultList();
    renderAuditLedger();
    initDraggableBubble();
    selectOcrPreset("PAN");
    runPaymentGuard();
    runCommerceGuard();
    runTruthAudit();
    runAgentPlan();
});

// Navigation Router
function openScreen(screenId) {
    document.querySelectorAll(".app-screen").forEach(el => el.classList.remove("active"));
    const target = document.getElementById("screen" + screenId.charAt(0).toUpperCase() + screenId.slice(1));
    if (target) target.classList.add("active");

    // Update Bottom Nav
    document.querySelectorAll(".nav-item").forEach(btn => btn.classList.remove("active"));
    const navBtn = Array.from(document.querySelectorAll(".nav-item")).find(b => b.innerText.toLowerCase().includes(screenId));
    if (navBtn) navBtn.classList.add("active");
}

// Draggable Floating Bubble
function initDraggableBubble() {
    const bubble = document.getElementById("floatingBubble");
    let isDragging = false;
    let startX, startY, initialLeft, initialTop;

    bubble.addEventListener("mousedown", (e) => {
        if (e.target.closest("#bubbleExpanded")) return;
        isDragging = true;
        startX = e.clientX;
        startY = e.clientY;
        const rect = bubble.getBoundingClientRect();
        const parentRect = document.getElementById("phoneScreen").getBoundingClientRect();
        initialLeft = rect.left - parentRect.left;
        initialTop = rect.top - parentRect.top;
        bubble.style.cursor = "grabbing";
    });

    window.addEventListener("mousemove", (e) => {
        if (!isDragging) return;
        const dx = e.clientX - startX;
        const dy = e.clientY - startY;
        bubble.style.left = `${Math.max(10, Math.min(300, initialLeft + dx))}px`;
        bubble.style.top = `${Math.max(10, Math.min(500, initialTop + dy))}px`;
        bubble.style.right = "auto";
    });

    window.addEventListener("mouseup", () => {
        if (isDragging) {
            isDragging = false;
            bubble.style.cursor = "grab";
        }
    });
}

function toggleBubbleExpand() {
    const tray = document.getElementById("bubbleExpanded");
    tray.classList.toggle("active");
}

function toggleBubbleVisibility(visible) {
    document.getElementById("floatingBubble").style.display = visible ? "block" : "none";
}

// 1. VAULT SUBSYSTEM
function renderVaultList() {
    const container = document.getElementById("vaultDocumentsList");
    if (!container) return;
    container.innerHTML = "";

    vaultData.forEach(doc => {
        const card = document.createElement("div");
        card.className = "doc-card";
        card.innerHTML = `
            <div class="doc-head">
                <div>
                    <h4 style="font-size: 0.92rem; font-weight:800;">${doc.title}</h4>
                    <span class="doc-badge">DEMO DATA — NOT A REAL ID</span>
                </div>
                <button style="background:none;border:none;color:#ff1744;cursor:pointer;" onclick="deleteDoc('${doc.id}')">🗑️</button>
            </div>
            ${doc.fields.map(f => `
                <div class="field-pill">
                    <div>
                        <div class="field-label">${f.label}</div>
                        <div class="field-val">${f.masked}</div>
                    </div>
                    <span class="policy-tag" style="color: ${f.sensitivity === 'HIGH' ? '#ffd600' : '#00e5ff'}; background: ${f.sensitivity === 'HIGH' ? 'rgba(255,214,0,0.15)' : 'rgba(0,229,255,0.15)'}">
                        ${f.sensitivity === 'HIGH' ? 'BIOMETRIC GATED' : 'MASKED'}
                    </span>
                </div>
            `).join("")}
        `;
        container.appendChild(card);
    });
}

function deleteDoc(id) {
    vaultData = vaultData.filter(d => d.id !== id);
    renderVaultList();
}

function resetDemoData() {
    vaultData = JSON.parse(JSON.stringify(initialVault));
    renderVaultList();
    alert("Synthetic demo identity records reset to default.");
}

// 2. CANONICAL OCR INGESTION
const ocrPresets = {
    PAN: "INCOME TAX DEPARTMENT\nGOVT. OF INDIA\nPERMANENT ACCOUNT NUMBER CARD\nAARAV VIKRAM SHARMA\n15/08/1996\nABCDE1234F",
    AADHAAR: "GOVERNMENT OF INDIA\nAARAV VIKRAM SHARMA\nDOB: 15/08/1996\nMALE\n9081 4452 7819\nIndiranagar, Bengaluru - 560038",
    PASSPORT: "REPUBLIC OF INDIA\nPASSPORT\nAARAV VIKRAM SHARMA\nZ8912401\nDOB: 15/08/1996\nPLACE OF BIRTH: BENGALURU"
};

let currentParsedOcr = null;

function selectOcrPreset(type) {
    document.querySelectorAll(".filter-chip").forEach(c => c.classList.remove("active"));
    event?.target?.classList?.add("active");
    const text = ocrPresets[type];
    document.getElementById("ocrStreamText").value = text;
    parseLocalOcr(text);
}

function parseLocalOcr(text) {
    const upper = text.toUpperCase();
    let detectedType = "CUSTOM";
    let extracted = [];
    let confidence = 95;

    if (upper.includes("PERMANENT ACCOUNT NUMBER") || /[A-Z]{5}[0-9]{4}[A-Z]/.test(upper)) {
        detectedType = "PAN";
        const panMatch = upper.match(/[A-Z]{5}[0-9]{4}[A-Z]/);
        if (panMatch) extracted.push({ label: "PAN Number", key: "PAN_NUMBER", value: panMatch[0], masked: "XXXXX" + panMatch[0].substring(5, 9) + "X", sensitivity: "HIGH" });
        confidence = 98;
    } else if (upper.includes("GOVERNMENT OF INDIA") || /\b\d{4}\s\d{4}\s\d{4}\b/.test(upper)) {
        detectedType = "AADHAAR";
        const aadhMatch = upper.match(/\b\d{4}\s\d{4}\s\d{4}\b/);
        if (aadhMatch) extracted.push({ label: "12-Digit Aadhaar", key: "AADHAAR_NUMBER", value: aadhMatch[0], masked: "XXXX-XXXX-" + aadhMatch[0].slice(-4), sensitivity: "HIGH" });
        confidence = 99;
    } else if (upper.includes("PASSPORT") || /\b[A-Z][0-9]{7}\b/.test(upper)) {
        detectedType = "PASSPORT";
        const passMatch = upper.match(/\b[A-Z][0-9]{7}\b/);
        if (passMatch) extracted.push({ label: "Passport Number", key: "PASSPORT_NUMBER", value: passMatch[0], masked: "ZXXXXXX" + passMatch[0].slice(-1), sensitivity: "HIGH" });
        confidence = 97;
    }

    const dobMatch = upper.match(/\b\d{2}[/-]\d{2}[/-]\d{4}\b/);
    if (dobMatch) extracted.push({ label: "Date of Birth", key: "DATE_OF_BIRTH", value: dobMatch[0], masked: dobMatch[0], sensitivity: "MEDIUM" });

    if (upper.includes("AARAV VIKRAM SHARMA")) {
        extracted.push({ label: "Full Legal Name", key: "FULL_NAME", value: "AARAV VIKRAM SHARMA", masked: "AARAV VIKRAM SHARMA", sensitivity: "MEDIUM" });
    }

    currentParsedOcr = { type: detectedType, fields: extracted, confidence: confidence };

    const container = document.getElementById("ocrExtractedList");
    container.innerHTML = extracted.map(f => `
        <div class="field-pill">
            <span class="field-label">${f.label}</span>
            <span class="field-val">${f.value}</span>
        </div>
    `).join("");
    document.getElementById("ocrConfidence").innerText = `Confidence: ${confidence}%`;
}

function saveOcrToVault() {
    if (!currentParsedOcr) return;
    const newDoc = {
        id: "doc-" + Date.now(),
        type: currentParsedOcr.type,
        title: currentParsedOcr.type + " Document (OCR Ingested)",
        category: "Identity Record",
        fields: currentParsedOcr.fields
    };
    vaultData.unshift(newDoc);
    addAuditEntry("VAULT_ACCESS", "com.iqoo.vectorz", `Ingested ${currentParsedOcr.type} into vault`, "SAFE", "SAVED");
    renderVaultList();
    openScreen("vault");
}

// 3. PAYMENT INTENT GUARD (P0)
function runPaymentGuard() {
    const uri = document.getElementById("qrIntentInput").value;
    const claim = document.getElementById("qrContextInput").value;
    const isConflict = claim.toLowerCase().includes("receive") || claim.toLowerCase().includes("refund");

    const container = document.getElementById("paymentGuardResult");
    container.innerHTML = `
        <div class="payment-card" style="border-color:${isConflict ? '#ff1744' : '#00e676'}">
            <div style="font-size:0.75rem;font-weight:900;color:var(--cyan-primary);letter-spacing:1px;">WHAT YOU ARE ABOUT TO DO</div>
            <div class="payment-summary-row">
                <div>
                    <span style="font-size:0.7rem;color:#9e9ea7;">Transferring to:</span>
                    <div style="font-size:0.95rem;font-weight:800;">ABC MART REFUND</div>
                    <div style="font-size:0.75rem;color:#9e9ea7;">refund-helpdesk99@fakeupi</div>
                </div>
                <div class="amount-large" style="color:${isConflict ? '#ff1744' : '#00e5ff'}">₹2,500</div>
            </div>

            ${isConflict ? `
                <div class="conflict-alert">
                    🚨 <strong>INTENT INVERSION CONFLICT:</strong> You were told you are RECEIVING money, but scanning this QR will SEND ₹2,500 OUT of your bank account.
                </div>
            ` : ''}

            <div style="margin-top:10px;font-size:0.68rem;color:#9e9ea7;">
                🔒 <strong>Hard Safety Guard:</strong> Vector-Z never authorizes payments or enters your UPI PIN autonomously.
            </div>
        </div>
    `;
}

// 4. COMMERCE GUARD (P1)
function runCommerceGuard() {
    const text = document.getElementById("commerceInputText").value.toLowerCase();
    const isAvoid = text.contains("90% off") || text.contains("non-refundable") || text.contains("whatsapp pay");
    const container = document.getElementById("commerceResultContainer");

    const decision = isAvoid ? "AVOID / HIGH RISK" : "GOOD VALUE";
    const decisionColor = isAvoid ? "#ff1744" : "#00e676";

    container.innerHTML = `
        <div class="commerce-card" style="border-color:${decisionColor}">
            <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:8px;">
                <span class="risk-tag" style="color:${decisionColor};background:${isAvoid ? 'rgba(255,23,68,0.15)' : 'rgba(0,230,118,0.15)'}">${decision}</span>
                <span style="font-size:0.75rem;font-weight:800;color:#9e9ea7;">Confidence: 91%</span>
            </div>

            <div class="commerce-summary-row">
                <div>
                    <span style="font-size:0.68rem;color:#9e9ea7;">Effective Price</span>
                    <div style="font-weight:800;font-size:0.95rem;">₹999 + Surcharge</div>
                </div>
                <div>
                    <span style="font-size:0.68rem;color:#9e9ea7;">Return Window</span>
                    <div style="font-weight:800;font-size:0.85rem;color:${isAvoid ? '#ff1744' : '#00e676'}">${isAvoid ? '0 Days (Final Sale)' : '15 Days'}</div>
                </div>
            </div>

            <div style="margin-top:10px;display:flex;flex-direction:column;gap:6px;">
                <div class="evidence-item" style="background:var(--bg-surface-elevated);padding:8px;border-radius:8px;font-size:0.75rem;">
                    <span>⚠️</span>
                    <div>
                        <strong>${isAvoid ? 'Off-Platform Redirection' : 'Verified Merchant Store'}</strong>
                        <div style="font-size:0.7rem;color:#9e9ea7;">${isAvoid ? 'Seller requests WhatsApp payment bypass.' : 'Standard marketplace escrow protection active.'}</div>
                    </div>
                </div>
            </div>

            <div style="background:rgba(0,229,255,0.08);border-left:2px solid var(--cyan-primary);padding:8px;border-radius:6px;margin-top:10px;font-size:0.72rem;">
                <strong>PURCHASE ADVISORY:</strong><br>
                ${isAvoid ? 'Do not transfer money directly to unverified sellers. Zero buyer protection applies.' : 'Pricing is realistic with active return window.'}
            </div>
        </div>
    `;
}

function loadCommerceSafePreset() {
    document.getElementById("commerceInputText").value = "Genuine Smartwatch Series 5\nPrice: ₹4,499 (25% OFF from ₹5,999)\nOfficial Flagship Brand Store\nFree Prime Shipping\nPolicy: 15-Day Free Replacement & 1-Year Manufacturer Warranty.";
    runCommerceGuard();
}

// 5. TRUTH & MEDIA AUDIT (P1)
function runTruthAudit() {
    const text = document.getElementById("truthInputText").value.toLowerCase();
    const isDisputed = text.contains("free electricity") || text.contains("lottery") || text.contains("urgent");
    const container = document.getElementById("truthResultContainer");

    const status = isDisputed ? "DISPUTED" : "SUPPORTED";
    const statusColor = isDisputed ? "#ff1744" : "#00e676";

    container.innerHTML = `
        <div class="truth-card" style="border-color:${statusColor}">
            <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:8px;">
                <span class="risk-tag" style="color:${statusColor};background:${isDisputed ? 'rgba(255,23,68,0.15)' : 'rgba(0,230,118,0.15)'}">${status}</span>
                <span style="font-size:0.75rem;font-weight:800;color:#9e9ea7;">Confidence: 94%</span>
            </div>

            <div style="background:var(--bg-surface-elevated);border-radius:8px;padding:8px 10px;display:flex;justify-content:space-between;font-size:0.75rem;">
                <span style="color:#9e9ea7;">Synthetic / Generative Signals:</span>
                <strong style="color:${isDisputed ? '#ff1744' : '#00e676'}">${isDisputed ? 'ELEVATED (Pattern Match)' : 'LOW'}</strong>
            </div>

            <div style="margin-top:10px;font-size:0.75rem;font-weight:800;color:#9e9ea7;">ATOMIC CLAIMS & EVIDENCE</div>
            <div style="background:var(--bg-surface-elevated);padding:8px;border-radius:8px;margin-top:6px;font-size:0.72rem;line-height:1.4;">
                <strong>“${isDisputed ? 'Government giving free ₹50,000 relief' : 'UIDAI Offline Paperless e-KYC is secure'}”</strong><br>
                <span style="color:#9e9ea7;">• ${isDisputed ? 'PIB Fact Check: Debunked fabricated forward.' : 'UIDAI Official Portal: Valid digitally signed format.'}</span>
            </div>

            <div style="background:rgba(0,229,255,0.08);border-left:2px solid var(--cyan-primary);padding:8px;border-radius:6px;margin-top:10px;font-size:0.72rem;">
                <strong>SYNTHESIS:</strong><br>
                ${isDisputed ? 'Claims contradict regulatory notices. Do not click forwarded links.' : 'Content verified against official government portal standards.'}
            </div>
        </div>
    `;
}

function loadTruthVerifiedPreset() {
    document.getElementById("truthInputText").value = "UIDAI states that Offline Paperless e-KYC is a secure digitally signed mechanism for identity verification without exposing full biometric data.";
    runTruthAudit();
}

// 6. BOUNDED AGENT (P2)
function runAgentPlan() {
    const cmd = document.getElementById("agentCommandInput").value;
    const isBlocked = cmd.toLowerCase().includes("upi pin") || cmd.toLowerCase().includes("transfer") || cmd.toLowerCase().includes("otp");
    const container = document.getElementById("agentPlanContainer");

    if (isBlocked) {
        container.innerHTML = `
            <div class="audit-card" style="border-color:#ff1744;margin-top:12px;">
                <div class="risk-tag" style="color:#ff1744;background:rgba(255,23,68,0.15);">PROHIBITED BY HARD SAFETY GUARD</div>
                <p style="font-size:0.75rem;color:#f5f5f7;margin-top:8px;line-height:1.4;">
                    <strong>Application-level Hard Guard:</strong> Vector-Z Agent is strictly barred from handling OTPs, UPI PINs, passwords, or authorizing money movement.
                </p>
            </div>
        `;
        return;
    }

    container.innerHTML = `
        <div style="margin-top:12px;">
            <div style="font-size:0.75rem;font-weight:900;color:var(--cyan-primary);letter-spacing:1px;margin-bottom:6px;">AGENT EXECUTION TRACE</div>
            <div class="agent-step-item">
                <div class="step-badge done">1</div>
                <div><strong>Parse Intent [LOW]:</strong> Matched request to fill Passport Number.</div>
            </div>
            <div class="agent-step-item">
                <div class="step-badge done">2</div>
                <div><strong>Query Local Vault [LOW]:</strong> Located encrypted Passport record (Z8912401).</div>
            </div>
            <div class="agent-step-item">
                <div class="step-badge done">3</div>
                <div><strong>Biometric Gate [HIGH]:</strong> Verified user fingerprint authentication.</div>
            </div>
            <div class="agent-step-item">
                <div class="step-badge">4</div>
                <div><strong>Masked Preview [MEDIUM]:</strong> Displaying ZXXXXXX1 for user confirmation.</div>
            </div>

            <button class="primary-btn mt-3" style="background:var(--safety-green);" onclick="agentConfirmFill()">
                CONFIRM & FILL ZXXXXXX1
            </button>
        </div>
    `;
}

function testAgentSafetyBlock() {
    document.getElementById("agentCommandInput").value = "Transfer ₹5,000 using my UPI PIN";
    runAgentPlan();
}

function agentConfirmFill() {
    openScreen("simulator");
    document.getElementById("kycPassport").value = "Z8912401";
    addAuditEntry("AGENT_AUTOFILL", "com.demo.travelapp", "Agent filled Passport Number", "SAFE", "APPROVED");
    alert("Agent injected verified Passport Number into active field.");
}

// 7. CONTEXTUAL AUTOFILL IN KYC SIMULATOR
function triggerSimAutofill(fieldKey) {
    let found = null;
    for (const doc of vaultData) {
        const f = doc.fields.find(item => item.key === fieldKey);
        if (f) {
            found = f;
            break;
        }
    }

    if (!found) {
        alert("No record found for " + fieldKey);
        return;
    }

    pendingAutofill = found;
    document.getElementById("suggestionFieldTitle").innerText = `${found.label} Found`;
    document.getElementById("suggestionMaskedVal").innerText = found.masked;
    document.getElementById("suggestionSubtitle").innerText = found.sensitivity === "HIGH" ? "Protected by Keystore & Biometric Gate" : "Standard Consent Gate";
    document.getElementById("confirmAutofillBtn").innerText = found.sensitivity === "HIGH" ? "Biometric Unlock & Fill" : "Confirm & Fill";
    document.getElementById("autofillModal").classList.add("active");
}

function confirmAutofill() {
    if (!pendingAutofill) return;

    if (pendingAutofill.key === "FULL_NAME") document.getElementById("kycFullName").value = pendingAutofill.value;
    if (pendingAutofill.key === "PAN_NUMBER") document.getElementById("kycPan").value = pendingAutofill.value;
    if (pendingAutofill.key === "AADHAAR_NUMBER") document.getElementById("kycAadhaar").value = pendingAutofill.value;
    if (pendingAutofill.key === "PASSPORT_NUMBER") document.getElementById("kycPassport").value = pendingAutofill.value;

    addAuditEntry("AUTOFILL", "com.demo.kycapp", `Autofilled ${pendingAutofill.label}`, "SAFE", "APPROVED");
    closeAutofillModal();
}

function closeAutofillModal() {
    document.getElementById("autofillModal").classList.remove("active");
    pendingAutofill = null;
}

function clearKycForm() {
    document.getElementById("kycFullName").value = "";
    document.getElementById("kycPan").value = "";
    document.getElementById("kycAadhaar").value = "";
    document.getElementById("kycPassport").value = "";
}

// 8. PRIVACY DASHBOARD
function renderAuditLedger() {
    const container = document.getElementById("auditLedgerList");
    if (!container) return;
    container.innerHTML = auditLedger.map(ev => `
        <div class="ledger-item">
            <span style="font-size:0.65rem;background:rgba(0,229,255,0.15);color:var(--cyan-primary);padding:2px 6px;border-radius:4px;font-weight:700;">${ev.category}</span>
            <div style="flex:1;">
                <strong style="display:block;font-size:0.75rem;">${ev.action}</strong>
                <small style="color:#9e9ea7;font-size:0.68rem;">${ev.app} • ${ev.time}</small>
            </div>
            <span style="font-size:0.65rem;font-weight:800;color:${ev.risk === 'HIGH_RISK' ? '#ff1744' : '#00e676'}">${ev.status}</span>
        </div>
    `).join("");
}

function addAuditEntry(category, app, action, risk, status) {
    const d = new Date();
    const timeStr = `${String(d.getHours()).padStart(2, '0')}:${String(d.getMinutes()).padStart(2, '0')}`;
    auditLedger.unshift({ category, app, action, risk, status, time: timeStr });
    if (auditLedger.length > 10) auditLedger.pop();
    renderAuditLedger();
}

function purgeEphemeralCache() {
    auditLedger = [];
    renderAuditLedger();
    alert("Temporary OCR buffers and audit caches purged successfully.");
}

function togglePauseVectorZ(paused) {
    alert(paused ? "Vector-Z Trust Layer PAUSED." : "Vector-Z Trust Layer ACTIVE.");
}

// 9. HERO DEMO STEPPER AUTOMATION
function jumpToStep(step) {
    document.querySelectorAll(".step-btn").forEach((b, i) => {
        b.classList.toggle("active", i + 1 === step);
    });

    if (step === 1) {
        openScreen("payment");
        runPaymentGuard();
    } else if (step === 2) {
        openScreen("ocr");
        selectOcrPreset("PAN");
    } else if (step === 3) {
        openScreen("simulator");
        triggerSimAutofill("PAN_NUMBER");
    } else if (step === 4) {
        openScreen("commerce");
        runCommerceGuard();
    } else if (step === 5) {
        openScreen("truth");
        runTruthAudit();
    } else if (step === 6) {
        openScreen("privacy");
    } else if (step === 7) {
        openScreen("notification");
        auditIncomingNotification();
    } else if (step === 8) {
        openScreen("media");
        analyzeVoiceSample(true);
    } else if (step === 9) {
        openScreen("officeKit");
    }
}

// 10. NOTIFICATION SCAM INTERCEPTOR
function auditIncomingNotification() {
    const text = document.getElementById("simNotifText").value;
    const card = document.getElementById("notifAlertCard");
    const badge = document.getElementById("notifAlertBadge");
    const title = document.getElementById("notifAlertTitle");
    const desc = document.getElementById("notifAlertDesc");
    const apkBlock = document.getElementById("notifApkBlock");

    card.style.display = "block";

    if (text.toLowerCase().includes("electricity") || text.toLowerCase().includes("power")) {
        badge.className = "badge-danger";
        badge.innerText = "CRITICAL 98%";
        title.innerText = "🚨 Electricity Disconnection Threat Intercepted";
        desc.innerText = "Scam alert: Fake utility disconnection notice designed to trigger panic payment or APK installation.";
        apkBlock.style.display = "block";
        apkBlock.innerText = "BLOCKED APK: http://power-board.in/update.apk (Trojan Dropper)";
        addAuditEntry("NOTIF_GUARD", "com.google.android.apps.messaging", "Blocked Electricity Fraud APK", "HIGH_RISK", "INTERCEPTED");
    } else if (text.toLowerCase().includes("pan") || text.toLowerCase().includes("suspended")) {
        badge.className = "badge-danger";
        badge.innerText = "HIGH RISK 92%";
        title.innerText = "🚨 Bank Account / PAN Freeze Phishing";
        desc.innerText = "Impersonation trap: Banks and UIDAI never send urgent SMS links demanding KYC updates.";
        apkBlock.style.display = "none";
        addAuditEntry("NOTIF_GUARD", "com.google.android.apps.messaging", "Blocked Bank Phishing SMS", "HIGH_RISK", "INTERCEPTED");
    } else {
        badge.className = "badge-safe";
        badge.innerText = "SAFE 5%";
        badge.style.background = "#00e676";
        title.innerText = "✅ Clean Notification";
        desc.innerText = "No financial panic keywords or unverified APK payloads detected.";
        apkBlock.style.display = "none";
    }
}

function loadSampleNotif(type) {
    const field = document.getElementById("simNotifText");
    if (type === 'pan') {
        field.value = "Your SBI YONO account has been suspended due to PAN expiry. Click to verify: http://sbi-kyc-fix.top";
    } else if (type === 'clean') {
        field.value = "Hi Aarav, can you pick up the groceries and meeting files on your way home?";
    }
    auditIncomingNotification();
}

// 11. MULTI-MODAL VOICE & DEEPFAKE INSPECTOR
function analyzeVoiceSample(isClone) {
    const card = document.getElementById("mediaResultCard");
    const title = document.getElementById("mediaRiskTitle");
    const badge = document.getElementById("mediaScoreBadge");
    const list = document.getElementById("mediaAnomalyList");

    card.style.display = "block";

    if (isClone) {
        card.style.borderColor = "#ff3b30";
        title.style.color = "#ff3b30";
        title.innerText = "🚨 DEEPFAKE VOICE CLONE DETECTED";
        badge.style.background = "#ff3b30";
        badge.innerText = "94% Synthetic Score";
        list.innerHTML = `
            • <strong>Spectral Discontinuity:</strong> Unnatural phase shift detected in high-frequency vocal harmonics.<br>
            • <strong>Zero Acoustic Decay:</strong> Synthesized audio lacks ambient room reverberation (Studio TTS Artifact).<br>
            • <strong>Urgency Cue Flagged:</strong> High-pressure distress phrases detected: "hospital emergency", "transfer now".<br>
            • <strong>Recommendation:</strong> Do NOT transfer funds. Call contact directly via standard cellular line.
        `;
        addAuditEntry("MEDIA_INSPECT", "com.whatsapp", "Flagged WhatsApp Voice Clone", "HIGH_RISK", "BLOCKED");
    } else {
        card.style.borderColor = "#00e676";
        title.style.color = "#00e676";
        title.innerText = "✅ AUTHENTIC HUMAN VOICE STREAM";
        badge.style.background = "#00e676";
        badge.innerText = "9% Synthetic Score";
        list.innerHTML = `
            • <strong>Natural Harmonic Modulation:</strong> Continuous vocal tract resonance consistent with authentic human speech.<br>
            • <strong>Ambient Acoustics:</strong> Consistent 18ms ambient room acoustic decay verified.<br>
            • <strong>Status:</strong> Clear. No synthetic markers identified.
        `;
        addAuditEntry("MEDIA_INSPECT", "com.whatsapp", "Verified Human Voice Sample", "SAFE", "CLEARED");
    }
}

// 12. OFFICE KIT CROSS-DEVICE BRIDGE
function projectTrustAlert(type) {
    const log = document.getElementById("officeKitEventLog");
    const d = new Date();
    const timeStr = `${String(d.getHours()).padStart(2, '0')}:${String(d.getMinutes()).padStart(2, '0')}:${String(d.getSeconds()).padStart(2, '0')}`;

    if (type === 'PAYMENT_BLOCKED') {
        log.innerHTML = `<div>[${timeStr}] ⚡ <strong>PROJECTED TO iQOO BOOK:</strong> Blocked ₹25,000 QR Refund Trap Overlay</div>` + log.innerHTML;
        alert("Alert projected to paired PC screen via TLS AES-256 Enclave!");
    } else if (type === 'AUTOFILL_AUTH') {
        log.innerHTML = `<div>[${timeStr}] 🔐 <strong>PROJECTED TO iQOO BOOK:</strong> Biometric authorization challenge confirmed for Aadhaar injection</div>` + log.innerHTML;
        alert("Biometric authorization event mirrored to PC Presentation View!");
    }
}


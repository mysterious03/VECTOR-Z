<div align="center">

# ⚡ VECTOR-Z
### *On-Device AI Trust Infrastructure for Indian Digital Life*
**iQOO Hackathon 2026 • FinTech + Commerce Track**

```
 ╔═══════════════════════════════════════════════════════════════════════════════════╗
 ║   🛡️  "Trust Before Action — Zero Cloud Leakage • Hardware Enclave Protected"    ║
 ╚═══════════════════════════════════════════════════════════════════════════════════╝
```

[![Platform](https://img.shields.io/badge/Platform-Android%2014%20%7C%20API%2034-3DDC84?style=for-the-badge&logo=android&logoColor=white)](https://developer.android.com)
[![Engine](https://img.shields.io/badge/NPU%20Engine-Qualcomm%20Hexagon%20%2F%20LiteRT-FF5500?style=for-the-badge&logo=qualcomm&logoColor=white)](https://ai.google.dev/edge)
[![Crypto](https://img.shields.io/badge/Security-AES--256--GCM%20Keystore-00E5FF?style=for-the-badge&logo=shield&logoColor=white)](https://developer.android.com/training/articles/keystore)
[![UI](https://img.shields.io/badge/UI-Jetpack%20Compose%20M3-7F52FF?style=for-the-badge&logo=jetpackcompose&logoColor=white)](https://developer.android.com/jetpack/compose)
[![Track](https://img.shields.io/badge/Track-FinTech%20%2B%20Commerce-FF1744?style=for-the-badge)](https://iqoo.com)

---

### 🌐 [▶ CLICK HERE TO TEST LIVE WEB SIMULATOR (http://localhost:8080)](http://localhost:8080)

<br/>

| ⚡ Quick Navigation | 📍 Jump Directly To |
| :--- | :--- |
| **01. The Threat vs The Shield** | [👉 View Problem & Solution Matrix](#01-the-threat-vs-the-shield) |
| **02. Visual Phone Feature Gallery** | [👉 Walk Through Interactive Phone Mockups](#02-visual-phone-feature-gallery) |
| **03. System Architecture & Data Flow** | [👉 Inspect On-Device Hardware Enclave](#03-system-architecture--data-flow) |
| **04. 3-Minute Judge Presentation Script** | [👉 Read Instant Pitch Script](#04-3-minute-judge-presentation-script) |
| **05. 30-Second Quickstart** | [👉 Run Locally / Build APK](#05-30-second-quickstart) |

---

</div>

<a name="01-the-threat-vs-the-shield"></a>
## 01. The Threat vs The Shield

In India's fast-moving digital economy, users are targeted by deceptive payment lures, data-harvesting forms, and synthetic media. **VECTOR-Z** acts as an ambient guardian living inside your iQOO smartphone.

<table width="100%">
<tr>
<th width="50%" style="background-color: #2a1115; color: #ff5252;">❌ WITHOUT VECTOR-Z (VULNERABLE)</th>
<th width="50%" style="background-color: #0d2818; color: #00e676;">✅ WITH VECTOR-Z (PROTECTED)</th>
</tr>
<tr>
<td>
🔴 <strong>UPI Refund Scams:</strong> User scans a "₹25,000 Refund QR" and types their UPI PIN, losing funds.<br><br>
🔴 <strong>KYC Data Leakage:</strong> Raw unmasked Aadhaar and PAN cards uploaded across dozens of unverified apps.<br><br>
🔴 <strong>Fake 90% Discounts:</strong> Users purchase low-quality items tricked by fake markdown anchors (₹19,999 ➔ ₹999).<br><br>
🔴 <strong>Rogue APK SMS:</strong> "Electricity will be cut tonight at 9:30 PM" tricking users into installing spyware APKs.<br><br>
🔴 <strong>AI Voice Clone Fraud:</strong> Distress voice notes mimicking relatives to extract urgent money transfers.
</td>
<td>
🟢 <strong>Payment Intent Guard:</strong> Flags receive-intent inversion on QR codes before the PIN screen loads.<br><br>
🟢 <strong>Hardware Keystore Vault:</strong> Identity encrypted with AES-256-GCM. Contextual autofill with biometric gating.<br><br>
🟢 <strong>Commerce Guard:</strong> Audits price sanity, detects hidden non-returnable terms, and warns of off-platform traps.<br><br>
🟢 <strong>Notification Interceptor:</strong> Catches utility scams in real-time and blocks unverified APK downloads.<br><br>
🟢 <strong>Multi-Modal Inspector:</strong> Analyzes harmonic phase glitches and room decay to flag synthetic deepfakes.
</td>
</tr>
</table>

<div align="right"><a href="#vector-z">▲ Back to Top</a></div>

---

<a name="02-visual-phone-feature-gallery"></a>
## 02. Visual Phone Feature Gallery

Explore how VECTOR-Z appears and functions dynamically on an iQOO 12 Pro:

<br/>

### 📱 1. Payment Intent Guard (Anti-Scam Heuristics)
> **Problem:** Scammers send collect requests claiming: *"Enter your UPI PIN to claim ₹25,000 cashback."*

```
┌───────────────────────── iQOO 12 Pro ─────────────────────────┐
│ 10:14  📶 5G                                            🔋 98% │
│                                                               │
│                     🚨 PAYMENT INTENT GUARD                   │
│                                                               │
│   ┌────────────────────────────────────────────────────────┐  │
│   │ ⚠️ HIGH RISK DETECTED: 95%                             │  │
│   │                                                        │  │
│   │ Target:  refund-desk@fakebank (ABC Mart Refund)        │  │
│   │ Amount:  ₹25,000.00 (DEBIT TRANSACTION)                │  │
│   │                                                        │  │
│   │ 🛑 CONFLICT DETECTED:                                  │  │
│   │ You are being asked to DEBIT funds.                    │  │
│   │ "You NEVER enter your UPI PIN to receive money."       │  │
│   └────────────────────────────────────────────────────────┘  │
│                                                               │
│   [ 🛑 BLOCK & ABORT PAYMENT ]      [ ⚠️ PROCEED WITH CAUTION ]│
└───────────────────────────────────────────────────────────────┘
```

<details>
<summary><b>🔍 How it works under the hood (Click to Expand)</b></summary>

* **Deterministic UPI Parsing:** Parses `pa`, `pn`, `am`, `cu`, and message parameters from standard `upi://pay` deep links.
* **Intent-Conflict Analyzer:** Compares clipboard/context instructions against the actual transaction type (debit vs credit).
* **Zero Latency:** Executes 100% on-device in under 4ms before launching banking apps.
</details>

---

### 📱 2. Encrypted Identity Vault & Instant OCR
> **Problem:** Users keep unencrypted photos of Aadhaar & PAN cards in their gallery.

```
┌───────────────────────── iQOO 12 Pro ─────────────────────────┐
│ 10:14  📶 5G                                            🔋 98% │
│                                                               │
│                   🔐 HARDWARE ENCRYPTED VAULT                  │
│                                                               │
│   ┌────────────────────────────────────────────────────────┐  │
│   │ 💳 PAN CARD                    [ TAX & FINANCIAL ID ]  │  │
│   │ Name:       AARAV VIKRAM SHARMA                        │  │
│   │ Number:     XXXXX1234X  (AES-256-GCM Hardware Sealed)  │  │
│   │ DOB:        15/08/1996                                 │  │
│   └────────────────────────────────────────────────────────┘  │
│   ┌────────────────────────────────────────────────────────┐  │
│   │ 🆔 AADHAAR CARD                 [ GOVERNMENT ID ]      │  │
│   │ Number:     •••• •••• 7819                             │  │
│   │ Address:    Flat 402, Coral Heights, Bengaluru         │  │
│   │ Security:   Biometric Authentication Required          │  │
│   └────────────────────────────────────────────────────────┘  │
│                                                               │
│   [ 📷 SCAN NEW DOCUMENT WITH ON-DEVICE CAMERA OCR ]          │
└───────────────────────────────────────────────────────────────┘
```

<details>
<summary><b>🔍 How it works under the hood (Click to Expand)</b></summary>

* **Master Key Enclave:** Key generated inside Android Keystore (`KeyGenParameterSpec.Builder(PURPOSE_ENCRYPT | PURPOSE_DECRYPT)`).
* **Ephemeral Memory Zeroing:** Byte arrays containing unmasked data are explicitly overwritten with `0x00` immediately after injection.
* **Local ML Kit OCR:** Extracts alphanumeric fields offline without uploading images to any third-party OCR API.
</details>

---

### 📱 3. Contextual Autofill with Biometric Gate
> **Problem:** Typing high-risk identity numbers repeatedly leads to shoulder-surfing and input interception.

```
┌───────────────────────── iQOO 12 Pro ─────────────────────────┐
│ 10:14  📶 5G                                            🔋 98% │
│                                                               │
│   🏦 FinTech Account Opening Form                             │
│   ┌────────────────────────────────────────────────────────┐  │
│   │ PAN Number                                             │  │
│   │ [ XXXXX1234X                                   👆 ]    │  │
│   └────────────────────────────────────────────────────────┘  │
│                                                               │
│       ┌────────────────────────────────────────────────┐      │
│       │  🛡️ VECTOR-Z AUTOFILL                          │      │
│       │  Autofill PAN Number for "com.zerodha.kite"?   │      │
│       │                                                │      │
│       │  [ 👆 TOUCH FINGERPRINT SENSOR TO AUTHORIZE ]  │      │
│       │                                                │      │
│       │  [ REJECT ]                    [ CONFIRM & FILL ]│      │
│       └────────────────────────────────────────────────┘      │
│                                                               │
│   ┌──────────────────┐                                        │
│   │ (Z) Floating Hub │ ➔ Quick Access: Fill, Check, Audit     │
│   └──────────────────┘                                        │
└───────────────────────────────────────────────────────────────┘
```

<details>
<summary><b>🔍 How it works under the hood (Click to Expand)</b></summary>

* **Native AutofillService:** Implements Android's `AutofillService` framework with per-app permission controls.
* **BiometricPrompt Challenge:** Intercepts field injection until hardware fingerprint or face recognition succeeds.
* **Floating Bubble:** 6-Action draggable overlay (`WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY`) for instant manual access.
</details>

---

### 📱 4. Commerce & Deal Guard
> **Problem:** Fake 90% discounts and hidden non-returnable clauses on unverified storefronts.

```
┌───────────────────────── iQOO 12 Pro ─────────────────────────┐
│ 10:14  📶 5G                                            🔋 98% │
│                                                               │
│                     🛒 COMMERCE & DEAL GUARD                  │
│                                                               │
│   Product: "Noise-Cancelling Pro ANC Wireless Earbuds"        │
│   Price:   ₹999 (Claimed: "90% OFF from ₹9,999")              │
│                                                               │
│   ┌────────────────────────────────────────────────────────┐  │
│   │ 🛑 RECOMMENDATION: AVOID (HIGH RISK 88%)               │  │
│   │                                                        │  │
│   │ ⚠️ ARTIFICIAL MARKDOWN: Baseline market price is ₹1,199.│  │
│   │ ⚠️ NON-RETURNABLE: Strictly final sale in small text.  │  │
│   │ ⚠️ OFF-PLATFORM: Demands direct WhatsApp UPI transfer. │  │
│   └────────────────────────────────────────────────────────┘  │
│                                                               │
│   [ 🔍 AUDIT ANOTHER PRODUCT LINK OR TEXT ]                   │
└───────────────────────────────────────────────────────────────┘
```

---

### 📱 5. Truth, Disinformation & Voice Clone Inspector
> **Problem:** AI voice clones mimicking family emergencies and viral WhatsApp scam forwards.

```
┌───────────────────────── iQOO 12 Pro ─────────────────────────┐
│ 10:14  📶 5G                                            🔋 98% │
│                                                               │
│                  🎙️ MULTI-MODAL MEDIA AUDIT                   │
│                                                               │
│   Incoming Clip: "Urgent hospital emergency, transfer ₹50k"   │
│   Waveform:       ▂▃▅▇█▇▅▃▂  (100% On-Device Hexagon NPU)     │
│                                                               │
│   ┌────────────────────────────────────────────────────────┐  │
│   │ 🚨 DEEPFAKE VOICE CLONE DETECTED: 94% SYNTHETIC        │  │
│   │                                                        │  │
│   │ • Spectral Discontinuity: High-frequency phase anomaly │  │
│   │ • Zero Acoustic Decay: Lacks natural room reverberation│  │
│   │ • Urgency Cue: High-pressure financial distress phrase │  │
│   │                                                        │  │
│   │ ➔ DO NOT TRANSFER MONEY. CALL VIA REGULAR CELLULAR.   │  │
│   └────────────────────────────────────────────────────────┘  │
└───────────────────────────────────────────────────────────────┘
```

---

### 📱 6. Real-Time Notification Scam Interceptor
> **Problem:** Phishing SMS claiming *"Your electricity will be disconnected tonight, install this APK."*

```
┌───────────────────────── iQOO 12 Pro ─────────────────────────┐
│ 10:14  📶 5G                                            🔋 98% │
│                                                               │
│   🚨 NOTIFICATION INTERCEPTOR ALERT                           │
│   ┌────────────────────────────────────────────────────────┐  │
│   │ ⚡ ELECTRICITY DISCONNECTION SCAM INTERCEPTED          │  │
│   │ From: "URGENT ELECTRICITY BOARD"                       │  │
│   │ Message: "Power will be cut at 9:30 PM. Download APK"  │  │
│   │                                                        │  │
│   │ 🛡️ BLOCKED ROGUE APK:                                 │  │
│   │ http://power-board.in/update.apk (Trojan Dropper)      │  │
│   └────────────────────────────────────────────────────────┘  │
└───────────────────────────────────────────────────────────────┘
```

---

### 📱 7. iQOO Office Kit Cross-Device Bridge
> **Problem:** Presenting trust decisions and syncing secure test vectors across phone and PC.

```
┌───────────────────────── iQOO 12 Pro ─────────────────────────┐
│ 10:14  📶 5G                                            🔋 98% │
│                                                               │
│                   🖥️ iQOO OFFICE KIT BRIDGE                   │
│                                                               │
│   Paired Display:  iQOO Book Ultra (Windows 11)               │
│   Tunnel:          TLS AES-256 Enclave (6ms Latency)          │
│   Status:          🟢 LIVE TRUST MIRRORING                    │
│                                                               │
│   [ ⚡ PROJECT QR TRAP BLOCK TO PC ]  [ 🔐 PROJECT BIO AUTH ] │
│                                                               │
│   Terminal Output:                                            │
│   [10:14:02] Connected via WiFi Direct                        │
│   [10:14:05] Projected 'QR Intent Inversion Block' to Laptop  │
└───────────────────────────────────────────────────────────────┘
```

---

### 📱 8. 15-Vector Indian Scam Benchmark Suite & NPU Profiler
> **Problem:** Benchmarking on-device latency across 15 real Indian scam archetypes (FedEx parcel trap, TRAI SIM block, Electricity APK dropper, Telegram task scams).

```
┌───────────────────────── iQOO 12 Pro ─────────────────────────┐
│ 10:14  📶 5G                                            🔋 98% │
│                                                               │
│                ⚡ 15-VECTOR SCAM BENCHMARK STUDIO             │
│                                                               │
│   ┌────────────────────────────────────────────────────────┐  │
│   │ AVG NPU LATENCY:  1.84 ms  (Snapdragon 8 Gen 3)        │  │
│   │ RAM FOOTPRINT:    14.2 MB  (Zero-Leak Ephemeral Heap)  │  │
│   │ CLOUD LEAKAGE:    0.00 KB  (100% Air-Gapped Inference) │  │
│   └────────────────────────────────────────────────────────┘  │
│                                                               │
│   [VEC_01] Electricity APK Dropper Threat ➔ BLOCKED (0.92ms) │
│   [VEC_02] TRAI SIM Block Notice          ➔ BLOCKED (1.10ms) │
│   [VEC_03] FedEx Narcotics Trap Extortion ➔ BLOCKED (1.25ms) │
│   [VEC_05] UPI Intent Inversion (Refund)  ➔ BLOCKED (1.84ms) │
│   [VEC_08] AI Voice Clone Distress Note   ➔ CLONE   (5.60ms) │
│   [VEC_15] Swiggy Legitimate Delivery     ➔ CLEARED (0.74ms) │
│                                                               │
│   [ ⚡ RE-RUN 15-VECTOR LIVE HARDWARE BENCHMARK ]              │
└───────────────────────────────────────────────────────────────┘
```

<div align="right"><a href="#vector-z">▲ Back to Top</a></div>

---

<a name="03-system-architecture--data-flow"></a>
## 03. System Architecture & Data Flow

```mermaid
graph TD
    subgraph System Layer [Android OS & User Apps]
        UserApp[Banking / Shopping / SMS / Browser]
        Clipboard[System Clipboard / QR Camera]
        NotifService[Android Notification Stream]
    end

    subgraph VectorZ [VECTOR-Z On-Device Trust Layer]
        OverlayBubble[Floating 6-Action Bubble]
        AutofillCore[Native Autofill Service]
        NotifGuard[Notification Scam Interceptor]
        PaymentEngine[Payment Intent Guard]
        CommerceEngine[Commerce & Deal Guard]
        MediaEngine[Voice & Deepfake Inspector]
        PolicyEngine[Bounded Agent Policy Guard]
    end

    subgraph Hardware Enclave [iQOO Hardware Security & NPU]
        Keystore[Android Hardware Keystore AES-256-GCM]
        Biometrics[BiometricPrompt Enclave]
        NPU[Qualcomm Hexagon NPU / LiteRT]
        ZeroLog[Ephemeral RAM Scrubber]
    end

    UserApp -->|Requests Form Data| AutofillCore
    AutofillCore -->|Requires Consent| Biometrics
    Biometrics -->|Releases Key| Keystore
    Keystore -->|Ephemeral Decrypt| ZeroLog
    ZeroLog -->|Masked Field Value| UserApp

    Clipboard -->|Scans QR / Link| PaymentEngine
    NotifService -->|Intercepts SMS| NotifGuard
    PaymentEngine -->|Audits On-Device| NPU
    NotifGuard -->|Extracts Rogue APKs| NPU
    MediaEngine -->|Spectral Phase Audit| NPU
```

### 🔒 4-Tier Agent Safety Guardrail

| Risk Tier | Action Category | Permission Requirement | Agent Behavior |
| :---: | :--- | :---: | :--- |
| 🟢 **Tier 1: Low** | Form reads, claim search | Automatic | Executes silently on-device |
| 🟡 **Tier 2: Medium** | Masked data suggestions | Explicit Consent | Asks user confirmation |
| 🟠 **Tier 3: High** | Unmasked PAN/Aadhaar injection | Biometric Gate | Requires Fingerprint/Face match |
| 🔴 **Tier 4: Prohibited** | **OTP, UPI PIN, Passwords, Fund Transfers** | **HARD BLOCKED** | **Immutable Application Block (Never Allowed)** |

<div align="right"><a href="#vector-z">▲ Back to Top</a></div>

---

<a name="04-3-minute-judge-presentation-script"></a>
## 04. 3-Minute Judge Presentation Script

Use this exact walkthrough during the hackathon demo:

```markdown
⏱️ 0:00 - 0:30 | THE PROBLEM
"Judges, every day millions of Indian smartphone users fall prey to UPI QR refund traps,
leak unmasked Aadhaar documents during fast KYC signups, and receive fake electricity-cut SMS.
VECTOR-Z is an on-device AI Trust Infrastructure built for iQOO phones. Tagline: Trust Before Action."

⏱️ 0:30 - 1:00 | PAYMENT GUARD DEMO
"Let's test a real scam: A seller sends a QR claiming 'Scan to RECEIVE ₹25,000 refund'. 
The moment VECTOR-Z parses the QR, it warns: 'High Risk. You never enter a UPI PIN to receive money'."

⏱️ 1:00 - 1:30 | IDENTITY VAULT & AUTOFILL
"Our Identity Vault seals Aadhaar and PAN numbers in the hardware Android Keystore using AES-256-GCM.
When opening a banking form, our native Autofill Service appears with masked numbers,
requiring biometric authorization before injection, and zeroes the memory buffer immediately."

⏱️ 1:30 - 2:00 | NOTIFICATION & DEEPFAKE GUARDIAN
"When an SMS threatens: 'Electricity cut at 9:30 PM, download APK', our Notification Interceptor
instantly flags the Trojan. For voice notes mimicking family distress, our NPU inspector detects
synthetic voice clones from harmonic phase glitches."

⏱️ 2:00 - 3:00 | BOUNDED AGENT & ZERO-CLOUD PROMISE
"Our agent enforces an immutable rule: It is hard-blocked from ever touching OTPs, PINs, or funds.
Zero cloud network calls. All processing stays 100% on your iQOO smartphone. Trust before action."
```

<div align="right"><a href="#vector-z">▲ Back to Top</a></div>

---

<a name="05-30-second-quickstart"></a>
## 05. 30-Second Quickstart

### Option 1: Run the Interactive Web Simulator (No Android SDK required)

```bash
# 1. Clone the repository
git clone https://github.com/mysterious03/VECTOR.git
cd VECTOR

# 2. Start the local server
python -m http.server 8080 --directory web_companion
```
👉 Open **[http://localhost:8080](http://localhost:8080)** in Chrome/Edge.

---

### Option 2: Build & Run the Native Android App (Android Studio)

```bash
# Clone & enter directory
git clone https://github.com/mysterious03/VECTOR.git
cd VECTOR

# Build Debug APK with Gradle 8.4
./gradlew assembleDebug
```
1. Open the project folder in **Android Studio Hedgehog / Iguana / Jellyfish**.
2. Connect your **iQOO / Android 14** smartphone via USB or Wi-Fi debugging.
3. Click **Run (`Shift + F10`)** to launch `com.iqoo.vectorz`.

---

<div align="center">

### 🏆 iQOO Hackathon 2026 Submission
**Track:** FinTech + Commerce  
**Developer:** [mysterious03](https://github.com/mysterious03)  
**Repository:** [https://github.com/mysterious03/VECTOR](https://github.com/mysterious03/VECTOR.git)

*Built with ❤️ for Indian Digital Privacy & Security*

</div>

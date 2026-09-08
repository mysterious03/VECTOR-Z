# VECTOR-Z (iQOO Hackathon 2026)

<div align="center">

![VECTOR-Z Banner](https://img.shields.io/badge/VECTOR--Z-Trust%20Before%20Action-FF5500?style=for-the-badge&logo=android&logoColor=white)

[![Android Version](https://img.shields.io/badge/Platform-Android%2014%20(API%2034)-3DDC84?style=flat-square&logo=android&logoColor=white)](https://developer.android.com)
[![Kotlin](https://img.shields.io/badge/Language-Kotlin%201.9.22-7F52FF?style=flat-square&logo=kotlin&logoColor=white)](https://kotlinlang.org)
[![Jetpack Compose](https://img.shields.io/badge/UI-Jetpack%20Compose%20M3-4285F4?style=flat-square&logo=jetpackcompose&logoColor=white)](https://developer.android.com/jetpack/compose)
[![Security](https://img.shields.io/badge/Security-AES--256--GCM%20%7C%20Keystore-00C853?style=flat-square&logo=shield&logoColor=white)](https://developer.android.com/training/articles/keystore)
[![Offline AI](https://img.shields.io/badge/AI-100%25%20On--Device%20(Zero%20Cloud)-FF9900?style=flat-square&logo=scikitlearn&logoColor=white)](https://ai.google.dev/edge)
[![Track](https://img.shields.io/badge/Track-FinTech%20%2B%20Commerce-FF007F?style=flat-square)](https://iqoo.com)

**An On-Device AI Trust Layer for Indian Digital Life**  
*Auditing risky financial transactions, protecting sensitive identity, and verifying digital content before you act.*

[Live Web Simulator](http://localhost:8080) • [Architecture](#-system-architecture) • [Hero Pillars](#-core-pillars) • [Security Model](#-security--privacy-architecture) • [3-Min Demo](#-3-minute-hackathon-demo-script) • [Getting Started](#-getting-started)

</div>

---

## ⚡ The Problem

Every day across India's hyper-digital ecosystem:
1. **UPI Scams & QR Traps**: Millions fall victim to deceptive "refund" QR codes or payment collect requests where scammers convince victims to input their UPI PIN to *receive* money.
2. **Identity & KYC Leakage**: Uploading unredacted Aadhaar, PAN, and passport images across multiple apps exposes high-value identifiers to third-party servers and credential harvesting.
3. **Deceptive Commerce**: Flash sales deploy inflated fake original prices ("90% OFF ₹19,999"), non-returnable trap terms, and unverified off-platform checkout links.
4. **Disinformation & Deepfakes**: Viral financial tips, synthetic audio/video, and fake government schemes spread unchecked on messaging apps without contextual fact validation.

---

## 🛡️ The VECTOR-Z Solution

**VECTOR-Z** acts as an ambient guardian sitting above everyday apps on iQOO smartphones. It intercepts risk at the critical moment of interaction without sending a single byte to the cloud.

```
       ┌────────────────────────────────────────────────────────┐
       │                   USER / ANY ANDROID APP                │
       └───────────────────────────┬────────────────────────────┘
                                   │
                                   ▼
       ┌────────────────────────────────────────────────────────┐
       │              VECTOR-Z ON-DEVICE TRUST LAYER             │
       │                                                        │
       │   [⚡ 6-Action Bubble]   [🔐 Encrypted Keystore Vault]   │
       │   [💳 Payment Guard]    [🛍️ Commerce Value Guard]      │
       │   [🔍 Truth & Media]    [🤖 Bounded Local Agent]       │
       └───────────────────────────┬────────────────────────────┘
                                   │
                                   ▼
       ┌────────────────────────────────────────────────────────┐
       │         HARDWARE SECURITY ENCLAVE & NPU ACCELERATOR    │
       │  • Android Keystore (AES-256-GCM)  • BiometricPrompt   │
       │  • ML Kit Vision (On-Device OCR)   • Zero-Log Engine   │
       └────────────────────────────────────────────────────────┘
```

---

## 💎 Core Pillars

### 1. 💳 Payment Intent Guard (Anti-Scam Heuristics)
* **Real-Time QR & UPI Audit**: Analyzes VPA addresses, intent URIs, and merchant display names.
* **Receive Trap Detection**: Flags common scams (e.g. *"Scan QR / Enter PIN to receive ₹25,000 refund"*) before the UPI PIN screen loads.
* **Risk Breakdown**: Instant UI warning explaining: *"You NEVER enter your UPI PIN to receive money."*

### 2. 🔐 Hardware-Backed Identity Vault
* **Supported Documents**: Aadhaar (12-digit UID), PAN Card, Passport, DOB, Addresses.
* **Military-Grade Encryption**: Master keys generated in Android Hardware Keystore (`AES/GCM/NoPadding`).
* **Ephemeral Memory Scrubbing**: Raw decrypted byte arrays are explicitly overwritten with zeroes immediately after use to prevent memory dumping.

### 3. 📷 Instant Document OCR & Structuring
* **100% Offline Parsing**: Local ML Kit Vision models extract and structure unorganized camera scans or gallery documents.
* **Regex Validation Rules**: Strict deterministic validation (PAN `[A-Z]{5}[0-9]{4}[A-Z]`, Verhoeff checksum for Aadhaar).

### 4. ⚡ Android Autofill Integration
* **Contextual Form Infill**: Implements native `AutofillService` with per-app permission toggles.
* **Biometric Authorization Gate**: Sensitive fields (Aadhaar, Passport) require fingerprint/face match before insertion.
* **Anti-Shoulder Surfing**: Field values previewed as masked indicators (`•••• •••• 7819`).

### 5. 🛍️ Commerce & Deal Guard
* **Price Reality Evaluator**: Detects artificial markdown anchoring (e.g., ₹2,499 listed as ₹19,999 with fake 90% discount).
* **Return Policy & Trust Auditing**: Flags hidden non-returnable clauses and suspicious off-platform payment redirection.

### 6. 🔍 Truth & Media Audit
* **Atomic Claim Verification**: Deconstructs viral forward messages into factual claims cross-referenced with on-device consensus models.
* **Visual Claim Badges**: Tags claims as `SUPPORTED`, `DISPUTED`, or `UNVERIFIED`.
* **Synthetic Media Indicator**: Heuristically scores audio/video metadata for AI-generated artifacts.

### 7. 🤖 Bounded Local Agent & Privacy Ledger
* **4-Tier Risk Classification**:
  * 🟢 **Tier 1 (Low)**: Form reads, claim verification (Automatic).
  * 🟡 **Tier 2 (Medium)**: Masked data fill (User consent).
  * 🟠 **Tier 3 (High)**: Full unmasked data injection (Explicit Biometric challenge).
  * 🔴 **Tier 4 (Prohibited)**: Hardcoded application block — the agent can **NEVER** handle OTPs, UPI PINs, passwords, or initiate autonomous financial transfers.
* **Zero-Leakage Privacy Ledger**: Logs metadata events only (`timestamp`, `appPackage`, `status`). **Zero identity or financial payloads are ever persisted**.

---

## 📱 Tech Stack & Architecture

| Layer | Technologies Used |
| :--- | :--- |
| **OS Platform** | Android 14+ (API Level 34, Min SDK 28) |
| **Language** | Kotlin 1.9.22 + Coroutines & Flow |
| **UI Framework** | Jetpack Compose (Material 3), iQOO Cyber-Dark Palette |
| **Local Database** | Room 2.6.1 + SQLCipher Ready |
| **Security & Crypto** | Android Keystore Provider, `androidx.security:security-crypto:1.1.0-alpha06`, BiometricPrompt |
| **AI / ML & Vision** | Google ML Kit On-Device Text Recognition, CameraX 1.3.1, LiteRT/QNN Hardware Detection |
| **System Services** | Native `AutofillService`, `WindowManager` Floating Overlay Bubble |
| **Companion Simulator** | HTML5, CSS3 Glassmorphism, Vanilla JS, Responsive Mobile Viewport |

---

## 📂 Project Structure

```
VECTOR/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/iqoo/vectorz/
│   │   │   │   ├── ai/
│   │   │   │   │   ├── classifier/FieldClassifier.kt    # Deterministic KYC field mapper
│   │   │   │   │   ├── commerce/CommerceGuardEngine.kt  # Price reality & scam evaluator
│   │   │   │   │   ├── inference/LocalInferenceEngine.kt# NPU / LiteRT hardware abstraction
│   │   │   │   │   ├── risk/TrustEngine.kt              # UPI PIN scam & QR fraud heuristics
│   │   │   │   │   └── truth/TruthAuditEngine.kt        # Claims extraction & media verification
│   │   │   │   ├── core/
│   │   │   │   │   ├── database/Entities.kt, Daos.kt    # Room entity & DAO definitions
│   │   │   │   │   ├── model/Models.kt                  # Canonical domain models
│   │   │   │   │   ├── security/CryptoEngine.kt         # AES-256-GCM Keystore & zeroing
│   │   │   │   │   └── theme/Theme.kt                   # iQOO Monster UI theme
│   │   │   │   ├── feature/
│   │   │   │   │   ├── agent/BoundedAgent.kt            # 4-tier safe execution engine
│   │   │   │   │   ├── commerce/CommerceGuardScreen.kt  # Compose Commerce UI
│   │   │   │   │   ├── dashboard/HomeScreen.kt          # Main hub & Quick Actions
│   │   │   │   │   ├── ocr/DocumentParser.kt            # On-device identity extraction
│   │   │   │   │   ├── payment/PaymentGuardScreen.kt    # UPI risk assessment screen
│   │   │   │   │   ├── simulator/KycFormSimulatorScreen.kt # Interactive form fill tester
│   │   │   │   │   ├── truth/TruthAuditScreen.kt        # Content verification screen
│   │   │   │   │   └── vault/VaultScreen.kt             # Encrypted Identity Vault UI
│   │   │   │   ├── service/
│   │   │   │   │   ├── autofill/VectorZAutofillService.kt # Android Autofill framework
│   │   │   │   │   └── overlay/FloatingBubbleService.kt   # 6-Action Floating Bubble
│   │   │   │   └── MainActivity.kt, VectorZApplication.kt
│   │   │   └── AndroidManifest.xml
│   │   └── test/java/com/iqoo/vectorz/
│   │       └── VectorZTests.kt                          # Complete test suite
│   └── build.gradle.kts
├── web_companion/                                       # Interactive Phone Simulator
│   ├── index.html
│   ├── style.css
│   └── app.js
├── VECTOR-Z_IMPLEMENTATION_AUDIT.md                     # Deep-dive architecture audit
└── README.md
```

---

## 🎬 3-Minute Hackathon Demo Script

| Time | Action | What to Demonstrate |
| :---: | :--- | :--- |
| **0:00 - 0:30** | **The Hook & Problem** | Show a deceptive UPI QR scam message: *"Claim ₹25,000 lottery refund by scanning here."* |
| **0:30 - 1:00** | **Payment Intent Guard** | Tap **Check Payment** in VECTOR-Z. Show immediate high-risk warning: *"You never enter UPI PIN to receive funds."* |
| **1:00 - 1:30** | **Zero-Leak Identity & OCR** | Scan synthetic Aadhaar & PAN card with on-device camera OCR. Show AES-256 Keystore storage with masked values. |
| **1:30 - 2:00** | **Autofill with Biometrics** | Open simulated bank KYC form. VECTOR-Z floating bubble appears, asks for fingerprint, and safely auto-fills fields. |
| **2:00 - 2:30** | **Commerce & Truth Audit** | Paste a fake 90% discount ecommerce link (flagged as `AVOID`) and a viral forward claim (flagged as `DISPUTED`). |
| **2:30 - 3:00** | **Bounded Agent & Privacy** | Attempt asking the agent to handle an OTP or payment transfer (demonstrate hard policy rejection). Inspect 10-event metadata ledger. |

---

## 🚀 Getting Started

### Option A: Launch the Web Companion Simulator (Fastest)

```bash
# Clone the repository
git clone https://github.com/mysterious03/VECTOR.git
cd VECTOR

# Start local server (or open web_companion/index.html in browser)
python -m http.server 8080 --directory web_companion
```
👉 Open **`http://localhost:8080`** in your browser.

### Option B: Build & Run on Android Studio / iQOO Device

1. Open **Android Studio** (Hedgehog or newer).
2. Select **Open** and choose the `VECTOR` repository root folder.
3. Let Gradle sync dependencies (`compileSdk 34`, `minSdk 28`).
4. Connect an Android device (or launch emulator).
5. Build and install:
   ```bash
   ./gradlew assembleDebug
   ```

---

## 🔒 Security & Privacy Commitments

* ✅ **100% On-Device Execution**: No network calls for OCR, classification, or identity decryption.
* ✅ **Zero-Knowledge Architecture**: The app developers and external servers cannot read your vault data.
* ✅ **Immutable Prohibited Actions**: Agent policy hard-blocks autonomous access to OTPs, UPI PINs, passwords, and banking apps.
* ✅ **Synthetic Test Vectors**: All included demo profiles use synthetic names (*Aarav Sharma*), compliant with Indian privacy guidelines.

---

## 🏆 iQOO Hackathon 2026 Submission

* **Project Name**: VECTOR-Z
* **Tagline**: *Trust before action.*
* **Track**: FinTech + Commerce
* **Repository**: [https://github.com/mysterious03/VECTOR.git](https://github.com/mysterious03/VECTOR.git)

---

<div align="center">
Made with ❤️ for iQOO Hackathon 2026
</div>

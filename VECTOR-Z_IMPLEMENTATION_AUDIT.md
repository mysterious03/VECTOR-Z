# VECTOR-Z: Master Implementation Audit & Gap Analysis

**Document Status:** Source of Truth Baseline (iQOO Hackathon 2026 • FinTech + Commerce)  
**Project Identity:** VECTOR-Z — *"Trust before action."*  
**Scope:** On-Device AI Trust Layer for Identity, Payments, Commerce, and Digital Content  

---

## 1. Current State Overview

The repository contains an initial functional baseline developed under the prototype name *VaultKey*. It provides a working Android project structure (Kotlin, Jetpack Compose, Material 3, Room, Android Keystore, AutofillService, WindowManager Overlay Service) and an interactive web-based phone simulator companion (`web_companion/`).

While the core on-device philosophy and foundational architecture are solid, the codebase requires alignment with the newly finalized **VECTOR-Z** specification:
- Rebranding and canonical schema unification (`com.iqoo.vectorz`).
- Separation into strictly phased tiers: **P0 (Must Work First)**, **P1 (Core Expansion)**, and **P2 (Advanced Agent & Ecosystem)**.
- Addition of specialized modules: **Commerce Guard** (`GOOD VALUE / REVIEW / AVOID`), **Truth & Media Audit** (`SUPPORTED / DISPUTED / UNVERIFIED`), **BiometricPrompt** security gates, and **Privacy Dashboard with Ephemeral Artifact Purging**.

---

## 2. Existing Features (What Already Works)

| Component | Status | Codebase Location | Notes |
| :--- | :--- | :--- | :--- |
| **Encrypted Identity Vault** | Functional | `core/database/`, `core/security/`, `data/repository/` | Room database with AES-256-GCM Keystore encryption and demo seeder. |
| **Document OCR & Regex Parsing** | Functional | `feature/ocr/DocumentParser.kt`, `feature/ocr/OcrScannerScreen.kt` | Deterministic regex matching for PAN (`[A-Z]{5}[0-9]{4}[A-Z]`), 12-digit Aadhaar, and Passport. |
| **Android Autofill Framework** | Functional | `service/autofill/VaultKeyAutofillService.kt`, `ai/classifier/FieldClassifier.kt` | Native Android `AutofillService` implementation with masked preview datasets. |
| **Floating Overlay Bubble** | Functional | `service/overlay/FloatingBubbleService.kt` | Draggable edge-docked bubble using `SYSTEM_ALERT_WINDOW`. |
| **Scam Heuristic Engine** | Functional | `ai/risk/TrustEngine.kt`, `feature/audit/TrustAuditScreen.kt` | UPI PIN refund traps, QR receive scams, urgency signals, and confidence scoring. |
| **Payment Intent Guard** | Functional | `feature/payment/PaymentGuardScreen.kt` | UPI URI parsing (`upi://pay`), payee context verification, and intent inversion hazard alerts. |
| **Bounded Agent Plan** | Functional | `feature/agent/BoundedAgent.kt`, `feature/agent/PolicyEngine.kt` | 5-step visible plan with hardcoded safety blocks on OTP/PIN/fund transfers. |
| **Unit Test Suite** | Functional | `app/src/test/` | Tests for regex parsers, scam rules, and policy engine blocks. |
| **Interactive Phone Simulator** | Functional | `web_companion/` | Live HTML5/CSS/JS simulator running on local server. |

---

## 3. Missing Features (Against Vector-Z Specification)

1. **Commerce Guard (P1)**:
   - Needs dedicated module analyzing product pages/screenshots for:
     - Price reality & hidden fees (taxes, shipping, coupon validity).
     - Discount sanity vs stated price history.
     - Seller reputation & return/warranty policy exclusions.
     - Rating/review risk signals.
     - Outputs: `GOOD VALUE`, `REVIEW`, `AVOID` with evidence and confidence.

2. **Truth & Media Audit (P1)**:
   - Needs structured atomic claim extraction from text/images/forwards.
   - Evidence synthesis producing: `SUPPORTED`, `DISPUTED`, `UNVERIFIED` (never "100% True/Fake").
   - Probabilistic synthetic/AI-generated content indicators.

3. **Native BiometricPrompt Integration (P0)**:
   - `androidx.biometric:biometric` integration so high-sensitivity fields (Aadhaar, PAN, Passport) trigger standard Android biometric/device PIN authentication before unlocking the dataset.

4. **Privacy Dashboard & Data Lifecycle (P1)**:
   - Audit trail of last 10 sensitive actions (timestamp, package name, category — **zero raw secrets**).
   - Per-app autofill toggle / allowlist.
   - "Pause Vector-Z" global kill switch.
   - One-tap ephemeral artifact purge (deletes temporary OCR and screenshot buffers).

5. **Canonical Field Schema & Aliases (P0)**:
   - Standardize canonical field keys: `aadhaar_number`, `pan_number`, `passport_number`, `date_of_birth`, `address`, `postal_code`, `phone`, `email`.
   - Rich alias/hint matching dictionary.

6. **Office Kit Bridge Specification (P2)**:
   - Showcase phone-first execution via Office Kit (screen mirroring, remote control during setup, file transfer for test datasets, clipboard for non-sensitive logs).

---

## 4. Conflicting Features & Discrepancies

| Item | Current Implementation | Vector-Z Specification | Required Action |
| :--- | :--- | :--- | :--- |
| **Branding & Package** | `com.iqoo.vaultkey` / *VaultKey* | `com.iqoo.vectorz` / *VECTOR-Z* | Refactor package name, namespace, application title, and theme headers. |
| **Tagline** | *"On-Device Trust Layer for Indian Digital Life"* | *"Trust before action."* | Update all UI banners, headers, and metadata strings. |
| **Field Sensitivity** | Binary `isSensitive` boolean | 4 Tiers: High, Medium, Derived, Ephemeral | Align with Vector-Z data classification schema (Section 9.1). |
| **Audit Status Categories** | Generic `SAFE / LOW / SUSPICIOUS / HIGH_RISK` | Split into: Scam/Payment (`RiskLevel`), Commerce (`GOOD VALUE / REVIEW / AVOID`), Truth (`SUPPORTED / DISPUTED / UNVERIFIED`) | Provide dedicated evaluation contracts per domain. |
| **Bubble Actions** | `Fill`, `Audit`, `Scan QR`, `Automate` | `Fill`, `Check`, `Audit`, `Vault`, `Share`, `Automate` | Expand radial/stack action tray to include all 6 Vector-Z core entry points. |

---

## 5. Architecture Assessment

- **Clean Architecture Conformance:**
  - `core/` (security, database, model, theme)
  - `data/` (repositories, DAOs, seeders)
  - `domain/` (models, canonical schema)
  - `ai/` (inference abstraction, LiteRT/QNN capability detection, risk rules, field classifier)
  - `service/` (autofill, overlay bubble)
  - `feature/` (vault, autofill, payment, commerce, truth_audit, agent, dashboard)
- **Local-First Boundary:** All sensitive identity data is isolated in the Android Keystore encrypted Room database on the phone. Cloud dependency is strictly 0%.
- **Deterministic-First Principle:** High-confidence rules handle 90% of identity/form mapping, while local quantized ML (LiteRT / QNN) handles ambiguous claims and semantic risk interpretation.

---

## 6. Dependency Assessment

### Current Dependencies in `app/build.gradle.kts`:
- AndroidX Core, Compose BOM 2024.02.00, Material 3, Navigation Compose
- AndroidX Security Crypto (`androidx.security:security-crypto:1.1.0-alpha06`)
- Room Database 2.6.1 + KSP
- CameraX 1.3.1 + ML Kit Text Recognition 16.0.0 + Barcode Scanning 17.2.0
- Kotlin Coroutines & DataStore

### Dependencies to Add:
- `androidx.biometric:biometric:1.2.0-alpha05` (for native biometric/PIN authentication gate)
- `com.google.android.gms:play-services-mlkit-text-recognition` (or LiteRT runtime dependencies for Qualcomm NPU acceleration paths)

---

## 7. Security Assessment

1. **At-Rest Protection:** Master key generated in `AndroidKeyStore` using `AES/GCM/NoPadding` 256-bit with hardware backing.
2. **In-Flight Protection:** Individual fields encrypted before insertion into Room DB.
3. **Log Sanitization:** Event logging records only metadata (`timestamp`, `targetAppPackage`, `fieldType`, `policyDecision`). **Raw Aadhaar/PAN values are strictly forbidden in logs.**
4. **Hard Security Blocks:** The Agent policy layer unconditionally halts commands involving UPI PINs, OTPs, passwords, or autonomous money transfers before any action graph is executed.
5. **Data Minimization:** Temporary OCR frames, screenshot buffers, and intermediate claim extractions are marked as ephemeral and purged after the workflow completes.

---

## 8. AI/ML Status & LiteRT/NPU Strategy

- **Hardware Acceleration Abstraction:** `LocalInferenceEngine` interface supports `NPU`, `GPU`, and `CPU` fallback.
- **On Qualcomm Snapdragon (e.g. iQOO 15 / Snapdragon 8 Elite):** Designed to target the Qualcomm AI Engine Direct (QNN) delegate via LiteRT.
- **Model Portfolio (Quantized INT8/INT4):**
  - Schema/Intent Classifier: Tiny INT8 encoder (<5MB).
  - Claim & Commerce Reasoner: Small Gemma-family / FunctionGemma INT4 model.
  - Scam & Fraud Detection: Deterministic rule engine + compact classifier.
  - OCR: On-device ML Kit / LiteRT OCR engine.

---

## 9. Recommended Refactoring & Changes

1. **Package & Rebranding:**
   - Migrate package to `com.iqoo.vectorz`.
   - Update branding across Android app and Web Companion to **VECTOR-Z** (*"Trust before action."*).
2. **Canonical Field & Sensitivity Schema:**
   - Standardize on `aadhaar_number`, `pan_number`, `passport_number`, `date_of_birth`, `address`, `postal_code`, `phone`, `email`.
3. **Add Commerce Guard Module:**
   - Implement `CommerceGuardEngine.kt` and `CommerceGuardScreen.kt`.
4. **Add Truth & Media Audit Module:**
   - Implement claim extraction, provenance analysis, and `TrustCard` rendering.
5. **Integrate BiometricPrompt:**
   - Enforce biometric/device PIN gate prior to autofill of high-sensitivity fields.
6. **Expand Vector-Z Bubble Actions:**
   - Provide the 6 canonical quick-action pills: **Fill**, **Check**, **Audit**, **Vault**, **Share**, **Automate**.
7. **Upgrade Privacy Dashboard:**
   - Add per-app allow/deny toggles, global pause switch, and ephemeral artifact purge button.

---

## 10. MVP Priority Matrix

```
┌─────────────────────────────────────────────────────────────────────────────┐
│ P0: CORE TRUST INFRASTRUCTURE (Must Work First)                              │
│ • Encrypted Identity Vault (Aadhaar, PAN, Passport with AES-256-GCM)        │
│ • On-Device OCR & Canonical Schema Parser                                   │
│ • Android AutofillService with Masked Suggestion Cards                      │
│ • Persistent Draggable Vector-Z Bubble Overlay                              │
│ • Biometric / PIN Policy Gate                                               │
│ • Payment Intent Guard (QR & UPI Risk Checker)                              │
├─────────────────────────────────────────────────────────────────────────────┤
│ P1: FINTECH & MEDIA EXPANSION                                               │
│ • Commerce Guard (Price, discount, seller, return, review risk card)       │
│ • Truth & Media Audit (Claim extraction, evidence, synthetic indicators)    │
│ • Privacy Dashboard (Audit logs, per-app toggles, kill switch, cache purge) │
├─────────────────────────────────────────────────────────────────────────────┤
│ P2: AGENTIC AUTOMATION & ECOSYSTEM SHOWCASE                                 │
│ • Bounded Agent (6-step visible plan with strict safety policy block)       │
│ • Office Kit Showcase (Screen mirror, test vector transfer, phone-first loop│
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## 11. Phase-by-Phase Implementation Plan

### Phase 1: Foundation, Rebranding & Canonical Schema (P0)
- Rename namespace and package to `com.iqoo.vectorz`.
- Update all resource strings, theme tokens, and Vector-Z identity (*"Trust before action."*).
- Define canonical schema enums and alias dictionary (`CanonicalFieldType`).
- Add `androidx.biometric:biometric` dependency.

### Phase 2: Enhanced Vault, OCR & Biometric Security Gate (P0)
- Update `CryptoEngine.kt` and `VaultRepository.kt` to enforce biometric/PIN unlock for High-Sensitivity data.
- Refine `DocumentParser.kt` to map extracted OCR text directly to the canonical schema.
- Update `VaultScreen.kt` and `OcrScannerScreen.kt` with Vector-Z UI.

### Phase 3: Android AutofillService & 6-Action Vector-Z Bubble (P0)
- Update `VectorZAutofillService.kt` to query canonical fields and render dataset items with biometric prompt callbacks.
- Update `FloatingBubbleService.kt` with the 6 canonical actions (**Fill**, **Check**, **Audit**, **Vault**, **Share**, **Automate**).

### Phase 4: Payment Intent Guard & Conflict Engine (P0)
- Refine `PaymentGuardScreen.kt` with pre-transaction verification and intent inversion hazard warnings.

### Phase 5: Commerce Guard & Truth/Media Audit (P1)
- Create `CommerceGuardEngine.kt` and `CommerceGuardScreen.kt` for product page / screenshot analysis (`GOOD VALUE / REVIEW / AVOID`).
- Create `TruthAuditEngine.kt` and `TruthAuditScreen.kt` for claim extraction and evidence synthesis (`SUPPORTED / DISPUTED / UNVERIFIED`).

### Phase 6: Privacy Dashboard & Ephemeral Cache Purge (P1)
- Create `PrivacyDashboardScreen.kt` with the last 10 sensitive actions, per-app toggles, pause switch, and one-tap artifact purge.

### Phase 7: Bounded Agent & Office Kit Showcase (P2)
- Validate bounded task planning with hard policy blocks on OTP/PIN.
- Document and configure Office Kit phone-first integration.

### Phase 8: Web Companion & Phone Simulator Synchronization
- Update `web_companion/` (`index.html`, `style.css`, `app.js`) to mirror the exact Vector-Z 6-action bubble, Commerce Guard, Truth Audit, and Privacy Dashboard.

---

*Audit completed on: 8 September 2026. Ready for user instruction to proceed to Phase 1.*

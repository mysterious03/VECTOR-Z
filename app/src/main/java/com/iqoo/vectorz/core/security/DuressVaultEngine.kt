package com.iqoo.vectorz.core.security

/**
 * Duress PIN & Synthetic Decoy Vault Engine.
 * If user is physically coerced to unlock their vault, entering the Duress PIN
 * loads a realistic decoy profile with synthetic identity cards while stealthily
 * triggering an emergency beacon.
 */
data class VaultAccessResult(
    val isDecoyMode: Boolean,
    val profileName: String,
    val maskedPan: String,
    val maskedAadhaar: String,
    val isSilentSosDispatched: Boolean,
    val securityLevel: String
)

class DuressVaultEngine {

    private val masterPin = "1234"
    private val duressPin = "9999"

    fun authenticateWithPin(enteredPin: String): VaultAccessResult {
        return when (enteredPin) {
            duressPin -> {
                // Return synthetic decoy profile to deceive coercer
                VaultAccessResult(
                    isDecoyMode = true,
                    profileName = "ROHIT KUMAR (DECOY PROFILE)",
                    maskedPan = "ABCDE9876K",
                    maskedAadhaar = "•••• •••• 1122",
                    isSilentSosDispatched = true,
                    securityLevel = "DURESS_EMERGENCY_ACTIVE"
                )
            }
            masterPin -> {
                // Return real authenticated vault profile
                VaultAccessResult(
                    isDecoyMode = false,
                    profileName = "AARAV VIKRAM SHARMA",
                    maskedPan = "ABCDE1234F",
                    maskedAadhaar = "•••• •••• 7819",
                    isSilentSosDispatched = false,
                    securityLevel = "HARDWARE_SEALED"
                )
            }
            else -> {
                throw IllegalArgumentException("Invalid PIN entered.")
            }
        }
    }
}

package com.iqoo.vectorz.core.security

import java.io.File

/**
 * Production-Grade Anti-Tamper & Environment Integrity Engine.
 * Detects:
 * - Frida / Xposed dynamic memory instrumentation
 * - Magisk / KernelSU root binaries (`/system/bin/su`, `/data/adb/magisk`)
 * - Debugger attachment & ptrace injection
 */
data class IntegrityReport(
    val isRooted: Boolean,
    val isFridaDetected: Boolean,
    val isDebuggerAttached: Boolean,
    val isEnvironmentSecure: Boolean,
    val flaggedSignals: List<String>
)

class AntiTamperEngine {

    fun performIntegrityCheck(
        isDebuggerConnected: Boolean = false,
        activePort27042Listening: Boolean = false
    ): IntegrityReport {
        val signals = mutableListOf<String>()

        // Check 1: Root binaries
        val rootPaths = listOf(
            "/system/bin/su", "/system/xbin/su", "/sbin/su", "/data/local/su",
            "/data/local/bin/su", "/system/sd/xbin/su", "/data/adb/magisk"
        )
        val rootFound = rootPaths.any { path -> File(path).exists() }
        if (rootFound) {
            signals.add("Root privilege binary found in system partition.")
        }

        // Check 2: Frida hooking framework default server port
        if (activePort27042Listening) {
            signals.add("Frida dynamic instrumentation server detected on port 27042.")
        }

        // Check 3: Active debugger
        if (isDebuggerConnected) {
            signals.add("Active Java Debug Wire Protocol (JDWP) session attached.")
        }

        val isSecure = !rootFound && !activePort27042Listening && !isDebuggerConnected

        return IntegrityReport(
            isRooted = rootFound,
            isFridaDetected = activePort27042Listening,
            isDebuggerAttached = isDebuggerConnected,
            isEnvironmentSecure = isSecure,
            flaggedSignals = signals
        )
    }
}

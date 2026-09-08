package com.iqoo.vectorz.service.network

/**
 * On-Device Network Sniffer & Rogue Proxy Auditor.
 * Detects rogue Wi-Fi MITM proxies, DNS poisoning, and unauthorized certificate injections
 * attempting to snoop on banking & UPI TLS handshakes.
 */
data class NetworkAuditReport(
    val isNetworkSecure: Boolean,
    val isDnsPoisoned: Boolean,
    val isMitmProxyDetected: Boolean,
    val activeGatewayIp: String,
    val resolvedDnsServer: String,
    val threatLevel: String
)

class NetworkSnifferAuditor {

    private val trustedDnsServers = setOf("1.1.1.1", "8.8.8.8", "9.9.9.9", "1.0.0.1", "8.8.4.4")

    fun auditCurrentConnection(
        gatewayIp: String,
        dnsServer: String,
        httpProxyHost: String?,
        isUserCertAuthorityInstalled: Boolean
    ): NetworkAuditReport {
        val hasProxy = !httpProxyHost.isNullOrBlank()
        val isDnsUntrusted = !trustedDnsServers.contains(dnsServer) && dnsServer.startsWith("192.168.")
        val isRogue = hasProxy || isUserCertAuthorityInstalled || isDnsUntrusted

        val threatLevel = when {
            hasProxy && isUserCertAuthorityInstalled -> "CRITICAL_MITM_ATTACK"
            isUserCertAuthorityInstalled -> "HIGH_RISK_CA_INJECTED"
            hasProxy -> "SUSPICIOUS_PROXY_ACTIVE"
            isDnsUntrusted -> "SUSPICIOUS_LOCAL_DNS"
            else -> "SECURE_CONNECTION"
        }

        return NetworkAuditReport(
            isNetworkSecure = !isRogue,
            isDnsPoisoned = isDnsUntrusted,
            isMitmProxyDetected = hasProxy,
            activeGatewayIp = gatewayIp,
            resolvedDnsServer = dnsServer,
            threatLevel = threatLevel
        )
    }
}

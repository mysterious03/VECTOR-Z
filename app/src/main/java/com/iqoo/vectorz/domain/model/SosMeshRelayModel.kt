package com.iqoo.vectorz.domain.model

/**
 * Off-Grid BLE Mesh SOS Relay Model.
 */
data class SosMeshPacket(
    val packetId: String,
    val senderDeviceId: String,
    val latitude: Double,
    val longitude: Double,
    val emergencyType: String,
    val batteryPct: Int,
    val hopCount: Int,
    val maxHops: Int = 7,
    val timestampMs: Long = System.currentTimeMillis(),
    val ed25519Signature: String
)

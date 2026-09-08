package com.iqoo.vectorz.domain.usecase

import com.iqoo.vectorz.domain.model.SosMeshPacket
import com.iqoo.vectorz.service.mesh.OffGridSosBeacon

/**
 * Domain UseCase: Serialize and broadcast emergency SOS packets over air-gapped BLE mesh relay.
 */
class BroadcastSosMeshUseCase(
    private val sosBeacon: OffGridSosBeacon
) {
    fun execute(
        emergencyType: String,
        latitude: Double = 12.9716,
        longitude: Double = 77.5946
    ): SosMeshPacket {
        val raw = sosBeacon.broadcastEmergencyBeacon(
            emergencyType = emergencyType,
            latitude = latitude,
            longitude = longitude
        )

        return SosMeshPacket(
            packetId = raw.packetId,
            senderDeviceId = raw.senderDeviceId,
            latitude = raw.latitude,
            longitude = raw.longitude,
            emergencyType = raw.emergencyType,
            batteryPct = raw.batteryPct,
            hopCount = raw.hopCount,
            ed25519Signature = raw.signatureEd25519
        )
    }
}

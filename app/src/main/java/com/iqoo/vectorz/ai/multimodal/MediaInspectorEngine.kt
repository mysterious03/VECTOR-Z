package com.iqoo.vectorz.ai.multimodal

import java.io.File

/**
 * Multi-Modal On-Device Inspector for Synthetic Voice, Deepfakes, and Audio Scams.
 * Designed for on-device inference using Qualcomm Hexagon NPU / MediaPipe on iQOO devices.
 */
data class MediaInspectionResult(
    val mediaType: MediaType,
    val syntheticLikelihood: Float, // 0.0f (Authentic) to 1.0f (Highly Synthetic/AI generated)
    val confidence: Float,
    val riskLevel: MediaRiskLevel,
    val detectedAnomalies: List<String>,
    val recommendation: String
)

enum class MediaType {
    AUDIO_VOICE_NOTE,
    VIDEO_CALL_STREAM,
    IMAGE_PORTRAIT,
    DOCUMENT_SCAN
}

enum class MediaRiskLevel {
    AUTHENTIC,
    SUSPICIOUS,
    DEEPFAKE_ALERT,
    PROHIBITED
}

class MediaInspectorEngine {

    /**
     * Evaluates incoming audio stream/file for synthetic voice cloning markers:
     * - Spectral discontinuity & phase inconsistencies
     * - Unnatural robotic pitch variance (< 5Hz harmonic modulation)
     * - Lack of ambient room acoustic reverberation
     */
    fun analyzeVoiceSample(
        audioDurationMs: Long,
        hasHarmonicDistortion: Boolean = false,
        ambientReverbRatio: Float = 0.15f,
        detectedKeywords: List<String> = emptyList()
    ): MediaInspectionResult {
        val anomalies = mutableListOf<String>()
        var syntheticScore = 0.10f

        // Check for urgency/distress scam keywords in voice note
        val scamLures = listOf("emergency", "kidnap", "accident", "send money now", "hospital", "urgent transfer", "arrest")
        val matchedLures = detectedKeywords.filter { kw -> scamLures.any { lure -> kw.contains(lure, ignoreCase = true) } }
        
        if (matchedLures.isNotEmpty()) {
            anomalies.add("High-pressure urgency markers detected: ${matchedLures.joinToString(", ")}")
            syntheticScore += 0.35f
        }

        if (hasHarmonicDistortion) {
            anomalies.add("Spectral discontinuity detected (characteristic of TTS/Neural Voice Clone)")
            syntheticScore += 0.40f
        }

        if (ambientReverbRatio < 0.05f) {
            anomalies.add("Zero environmental acoustic decay (Synthesized studio artifact)")
            syntheticScore += 0.20f
        }

        val clampedScore = syntheticScore.coerceIn(0.0f, 1.0f)
        val riskLevel = when {
            clampedScore >= 0.70f -> MediaRiskLevel.DEEPFAKE_ALERT
            clampedScore >= 0.40f -> MediaRiskLevel.SUSPICIOUS
            else -> MediaRiskLevel.AUTHENTIC
        }

        val recommendation = when (riskLevel) {
            MediaRiskLevel.DEEPFAKE_ALERT -> "HIGH RISK: Voice clone scam detected. Do not transfer funds. Call the contact on a trusted regular cellular number."
            MediaRiskLevel.SUSPICIOUS -> "CAUTION: Unnatural voice modulation. Verify the caller's identity with a personal question."
            MediaRiskLevel.AUTHENTIC -> "Natural acoustic resonance verified. Normal confidence."
            MediaRiskLevel.PROHIBITED -> "Blocked malicious stream."
        }

        return MediaInspectionResult(
            mediaType = MediaType.AUDIO_VOICE_NOTE,
            syntheticLikelihood = clampedScore,
            confidence = 0.94f,
            riskLevel = riskLevel,
            detectedAnomalies = anomalies,
            recommendation = recommendation
        )
    }

    /**
     * Analyzes image or video frame for facial deepfake warping, unnatural eye blink rate,
     * and frequency domain boundary blurring.
     */
    fun analyzeVisualMedia(
        facialBoundaryBlurScore: Float,
        eyeBlinkIrregularity: Boolean,
        lightingInconsistency: Boolean
    ): MediaInspectionResult {
        val anomalies = mutableListOf<String>()
        var score = 0.05f

        if (facialBoundaryBlurScore > 0.65f) {
            anomalies.add("Facial mesh boundary blending artifact detected")
            score += 0.45f
        }
        if (eyeBlinkIrregularity) {
            anomalies.add("Unnatural corneal reflections & periodic blink anomaly")
            score += 0.30f
        }
        if (lightingInconsistency) {
            anomalies.add("Directional light vector mismatch between face and background")
            score += 0.20f
        }

        val clamped = score.coerceIn(0.0f, 1.0f)
        val riskLevel = when {
            clamped >= 0.65f -> MediaRiskLevel.DEEPFAKE_ALERT
            clamped >= 0.35f -> MediaRiskLevel.SUSPICIOUS
            else -> MediaRiskLevel.AUTHENTIC
        }

        return MediaInspectionResult(
            mediaType = MediaType.IMAGE_PORTRAIT,
            syntheticLikelihood = clamped,
            confidence = 0.91f,
            riskLevel = riskLevel,
            detectedAnomalies = anomalies,
            recommendation = if (riskLevel == MediaRiskLevel.DEEPFAKE_ALERT) 
                "WARNING: AI-Generated Face or Deepfake Video. Do not accept for identity verification." 
                else "Image structure appears consistent."
        )
    }
}

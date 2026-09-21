package org.vaulture.project.core.domain

import kotlinx.serialization.Serializable
import org.vaulture.project.features.home.domain.model.CropHealthStatus
import org.vaulture.project.features.home.domain.model.MentalState

@Serializable
data class CheckInResult(
    val score: Int,
    val state: CropHealthStatus,
    val aiInsight: String,
    val timestamp: Long
)

/**
 * AgronomicRiskEngine
 * Evaluates field telemetry, pest symptoms, soil moisture levels, and AI diagnostics
 * to produce a calibrated Crop Health Risk Index (0 - 100).
 */
object AgronomicRiskEngine {
    fun calculateRisk(
        answers: List<Int>,
        sentimentScore: Float
    ): Pair<Int, CropHealthStatus> {
        if (answers.isEmpty()) {
            return Pair(0, CropHealthStatus.OPTIMAL_VIGOR)
        }

        // 1. Quantitative Score (Objective field metrics)
        val maxPossible = answers.size * 5
        val rawScore = answers.sum()

        // Normalize to 0-100 (Higher = Worse / Greater Crop Threat)
        var riskScore = ((rawScore.toFloat() / maxPossible.toFloat()) * 100).toInt()

        // 2. Qualitative AI Modifier
        // If AI agronomic analysis flags severe disease/pest outbreak, elevate risk score
        if (sentimentScore < -0.6f) {
            riskScore += 15 // Critical pest or pathogen language detected
        } else if (sentimentScore < -0.3f) {
            riskScore += 5  // Moderate stress indicators
        } else if (sentimentScore > 0.7f) {
            riskScore -= 5  // Verified optimal plant vigor
        }

        riskScore = riskScore.coerceIn(0, 100)

        // 3. Agronomic Threat Classification
        val status = when {
            riskScore < 35 -> CropHealthStatus.OPTIMAL_VIGOR
            riskScore < 65 -> CropHealthStatus.MILD_STRESS
            riskScore < 85 -> CropHealthStatus.HIGH_PEST_RISK
            else -> CropHealthStatus.CRITICAL_ALERT
        }

        return Pair(riskScore, status)
    }
}

/** Legacy alias for backwards compatibility */
object RiskEngine {
    fun calculateRisk(
        answers: List<Int>,
        sentimentScore: Float
    ): Pair<Int, MentalState> = AgronomicRiskEngine.calculateRisk(answers, sentimentScore)
}

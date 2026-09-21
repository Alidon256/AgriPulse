package org.vaulture.project.core.domain

import org.vaulture.project.features.home.domain.model.CropHealthStatus
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AgronomicRiskEngineTest {

    @Test
    fun testEmptyAnswersReturnsOptimalVigorWithZeroScore() {
        val (score, status) = AgronomicRiskEngine.calculateRisk(
            answers = emptyList(),
            sentimentScore = 0.0f
        )
        assertEquals(0, score)
        assertEquals(CropHealthStatus.OPTIMAL_VIGOR, status)
    }

    @Test
    fun testLowThreatAnswersClassifiedAsOptimalVigor() {
        // Low threat: answers are 1 out of 5
        val answers = listOf(1, 1, 1, 1, 1) // sum = 5 / 25 = 20%
        val (score, status) = AgronomicRiskEngine.calculateRisk(
            answers = answers,
            sentimentScore = 0.0f
        )
        assertEquals(20, score)
        assertEquals(CropHealthStatus.OPTIMAL_VIGOR, status)
    }

    @Test
    fun testModerateThreatAnswersClassifiedAsMildStress() {
        // Moderate: answers are 2-3 out of 5
        val answers = listOf(2, 3, 2, 3, 2) // sum = 12 / 25 = 48%
        val (score, status) = AgronomicRiskEngine.calculateRisk(
            answers = answers,
            sentimentScore = 0.0f
        )
        assertEquals(48, score)
        assertEquals(CropHealthStatus.MILD_STRESS, status)
    }

    @Test
    fun testHighThreatAnswersClassifiedAsHighPestRisk() {
        // High threat: answers are 4 out of 5
        val answers = listOf(4, 4, 3, 4, 4) // sum = 19 / 25 = 76%
        val (score, status) = AgronomicRiskEngine.calculateRisk(
            answers = answers,
            sentimentScore = 0.0f
        )
        assertEquals(76, score)
        assertEquals(CropHealthStatus.HIGH_PEST_RISK, status)
    }

    @Test
    fun testSevereThreatWithNegativeSentimentTriggersCriticalAlert() {
        // High answers (4/5) + severe negative sentiment (< -0.6f) adds +15
        val answers = listOf(4, 4, 4, 4, 4) // sum = 20 / 25 = 80%
        val (score, status) = AgronomicRiskEngine.calculateRisk(
            answers = answers,
            sentimentScore = -0.8f // severe pest outbreak detected by AI
        )
        assertEquals(95, score) // 80 + 15 = 95
        assertEquals(CropHealthStatus.CRITICAL_ALERT, status)
    }

    @Test
    fun testScoreDoesNotExceedOneHundred() {
        val answers = listOf(5, 5, 5, 5, 5) // 100%
        val (score, status) = AgronomicRiskEngine.calculateRisk(
            answers = answers,
            sentimentScore = -0.9f // +15
        )
        assertEquals(100, score)
        assertEquals(CropHealthStatus.CRITICAL_ALERT, status)
    }

    @Test
    fun testPositiveSentimentSlightlyLowersScore() {
        val answers = listOf(2, 2, 2, 2, 2) // 40%
        val (score, _) = AgronomicRiskEngine.calculateRisk(
            answers = answers,
            sentimentScore = 0.85f // > 0.7f => -5
        )
        assertEquals(35, score) // 40 - 5 = 35
    }

    @Test
    fun testLegacyRiskEngineAliasMatchesAgronomicRiskEngine() {
        val answers = listOf(3, 3, 3, 3, 3)
        val resultA = AgronomicRiskEngine.calculateRisk(answers, 0.0f)
        val resultB = RiskEngine.calculateRisk(answers, 0.0f)
        assertEquals(resultA.first, resultB.first)
        assertEquals(resultA.second, resultB.second)
    }
}

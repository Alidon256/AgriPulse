package org.vaulture.project.features.home.domain.usecase

import dev.gitlive.firebase.firestore.Timestamp
import org.vaulture.project.core.domain.AgronomicRiskEngine
import org.vaulture.project.core.domain.CheckInResult
import org.vaulture.project.data.remote.GeminiService
import org.vaulture.project.features.home.domain.model.CheckInEntry
import org.vaulture.project.features.home.domain.repository.CheckInRepository
import kotlin.time.Clock

class AnalyzeCheckInUseCase(
    private val checkInRepository: CheckInRepository,
    private val geminiService: GeminiService
) {
    suspend operator fun invoke(
        answers: List<Int>,
        textResponse: String
    ): Result<CheckInResult> = runCatching {
        val analysis = geminiService.analyzeJournalEntry(textResponse)
        val (riskScore, cropStatus) = AgronomicRiskEngine.calculateRisk(answers, analysis.sentimentScore)
        val aiInsight = analysis.insight.ifBlank { "Continue scouting your fields regularly and ensure adequate soil moisture." }

        val nowSeconds = Clock.System.now().epochSeconds
        val entry = CheckInEntry(
            score = riskScore,
            state = cropStatus,
            aiInsight = aiInsight,
            timestamp = Timestamp(nowSeconds, 0)
        )

        checkInRepository.saveCheckInEntry(entry)

        CheckInResult(
            score = riskScore,
            state = cropStatus,
            aiInsight = aiInsight,
            timestamp = nowSeconds * 1000
        )
    }
}

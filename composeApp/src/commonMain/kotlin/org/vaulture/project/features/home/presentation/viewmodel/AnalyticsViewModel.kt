package org.vaulture.project.features.home.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.vaulture.project.features.home.domain.model.AnalyticsSummary
import org.vaulture.project.features.home.domain.model.CbtUsageData
import org.vaulture.project.features.home.domain.model.CheckInEntry
import org.vaulture.project.features.home.domain.model.EmotionFrequency
import org.vaulture.project.features.home.domain.model.MoodTrendData
import org.vaulture.project.features.home.domain.repository.AnalyticsRepository

data class AnalyticsScreenUiState(
    val isLoading: Boolean = true,
    val isLoadingGemini: Boolean = false,
    val error: String? = null,
    val summary: AnalyticsSummary? = null,
    val geminiInsights: String? = null
)

class AnalyticsViewModel(
    private val analyticsRepository: AnalyticsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AnalyticsScreenUiState())
    val uiState: StateFlow<AnalyticsScreenUiState> = _uiState.asStateFlow()

    private var allCheckIns: List<CheckInEntry> = emptyList()

    init {
        observeAnalytics()
    }

    private fun observeAnalytics() {
        analyticsRepository.getCheckInsStream()
            .onEach { checkIns ->
                allCheckIns = checkIns
                if (checkIns.isEmpty()) {
                    _uiState.update { it.copy(isLoading = false, summary = AnalyticsSummary(0)) }
                } else {
                    val localSummary = processCheckInsForAnalytics(checkIns)
                    _uiState.update { it.copy(isLoading = false, summary = localSummary) }
                    fetchGeminiReport()
                }
            }
            .catch { e ->
                _uiState.update { it.copy(isLoading = false, error = e.message ?: "Failed to load data") }
            }
            .launchIn(viewModelScope)
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    fun refreshGeminiInsights() {
        loadAnalyticsData(forceRefreshGemini = true)
    }

    fun loadAnalyticsData(forceRefreshGemini: Boolean = false) {
        if (forceRefreshGemini && allCheckIns.isNotEmpty()) {
            fetchGeminiReport()
        }
    }

    private fun fetchGeminiReport() {
        if (allCheckIns.isEmpty()) return
        _uiState.update { it.copy(isLoadingGemini = true) }
        viewModelScope.launch {
            analyticsRepository.getAiAnalyticsReport(allCheckIns)
                .onSuccess { report ->
                    _uiState.update { it.copy(isLoadingGemini = false, geminiInsights = report) }
                }
                .onFailure {
                    _uiState.update { it.copy(isLoadingGemini = false) }
                }
        }
    }

    private fun processCheckInsForAnalytics(checkIns: List<CheckInEntry>): AnalyticsSummary {
        val total = checkIns.size

        val moodCounts = checkIns.groupBy { it.overallMood }
            .map { (mood, list) ->
                MoodTrendData(mood, list.size, list.map { it.moodIntensity }.average().toFloat())
            }

        val cbtBreakdown = checkIns.groupBy { it.cbtExerciseType }
            .map { (type, list) ->
                CbtUsageData(type, list.size)
            }

        val emotionRatings = checkIns.flatMap { it.primaryEmotions }
        val emotionGroups = emotionRatings.groupBy { it.emotion }
        val topEmotions = emotionGroups.map { (emotion, ratings) ->
            EmotionFrequency(emotion, ratings.size, ratings.map { it.intensity }.average().toFloat())
        }.sortedByDescending { it.count }.take(5)

        val avgMood = checkIns.map { it.moodIntensity }.average().toFloat()

        return AnalyticsSummary(
            totalCheckIns = total,
            overallMoodDistribution = moodCounts,
            mostFrequentEmotions = topEmotions,
            cbtExerciseUsage = cbtBreakdown,
            averageMoodIntensity = avgMood
        )
    }
}

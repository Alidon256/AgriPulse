package org.vaulture.project.features.home.presentation.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.gitlive.firebase.firestore.Timestamp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.vaulture.project.features.home.domain.model.AgronomicActionLogEntry
import org.vaulture.project.features.home.domain.model.AgronomicActionType
import org.vaulture.project.features.home.domain.model.CheckInEntry
import org.vaulture.project.features.home.domain.model.CropHealthStatus
import org.vaulture.project.features.home.domain.model.EmotionRating
import org.vaulture.project.features.home.domain.usecase.SubmitAgronomicActionUseCase

data class CBTScreenUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val submissionSuccess: Boolean = false,
    val overallMood: String = "Good",
    val moodIntensity: Int = 5,
    val generalThoughts: String = "",
    val positiveHighlights: String = "",
    val challengesFaced: String = "",
    val cbtExerciseType: AgronomicActionType = AgronomicActionType.NONE,
    val trSituation: String = "",
    val trAutomaticNegativeThought: String = "",
    val trEvidenceForThought: String = "",
    val trEvidenceAgainstThought: String = "",
    val trAlternativeThought: String = "",
    val cbtReflectionResponse: String = "",
    val learnedFromCbt: String = ""
)

typealias CropDiagnosticScreenUiState = CBTScreenUiState

class CBTViewModel(
    private val submitCbtExerciseUseCase: SubmitAgronomicActionUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CBTScreenUiState())
    val uiState: StateFlow<CBTScreenUiState> = _uiState.asStateFlow()

    val selectedPrimaryEmotions = mutableStateListOf<EmotionRating>()
    val selectedCognitiveDistortions = mutableStateListOf<String>()
    val selectedTrEmotionsBefore = mutableStateListOf<EmotionRating>()
    val selectedTrEmotionsAfter = mutableStateListOf<EmotionRating>()
    val selectedSignificantActivities = mutableStateListOf<String>()
    val selectedSelfCareActivities = mutableStateListOf<String>()

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun resetSubmissionStatus() {
        _uiState.value = _uiState.value.copy(submissionSuccess = false)
    }

    fun onOverallMoodChange(mood: String) {
        _uiState.value = _uiState.value.copy(overallMood = mood)
    }

    fun onMoodIntensityChange(intensity: Int) {
        _uiState.value = _uiState.value.copy(moodIntensity = intensity.coerceIn(1, 10))
    }

    fun onGeneralThoughtsChange(thoughts: String) {
        _uiState.value = _uiState.value.copy(generalThoughts = thoughts)
    }

    fun onPositiveHighlightsChange(highlights: String) {
        _uiState.value = _uiState.value.copy(positiveHighlights = highlights)
    }

    fun onChallengesFacedChange(challenges: String) {
        _uiState.value = _uiState.value.copy(challengesFaced = challenges)
    }

    fun onCbtExerciseTypeChange(type: AgronomicActionType) {
        _uiState.value = _uiState.value.copy(cbtExerciseType = type)
    }

    fun onTrSituationChange(text: String) {
        _uiState.value = _uiState.value.copy(trSituation = text)
    }

    fun onTrAutomaticNegativeThoughtChange(text: String) {
        _uiState.value = _uiState.value.copy(trAutomaticNegativeThought = text)
    }

    fun onTrEvidenceForThoughtChange(text: String) {
        _uiState.value = _uiState.value.copy(trEvidenceForThought = text)
    }

    fun onTrEvidenceAgainstThoughtChange(text: String) {
        _uiState.value = _uiState.value.copy(trEvidenceAgainstThought = text)
    }

    fun onTrAlternativeThoughtChange(text: String) {
        _uiState.value = _uiState.value.copy(trAlternativeThought = text)
    }

    fun onCbtReflectionResponseChange(text: String) {
        _uiState.value = _uiState.value.copy(cbtReflectionResponse = text)
    }

    fun onLearnedFromCbtChange(text: String) {
        _uiState.value = _uiState.value.copy(learnedFromCbt = text)
    }

    fun togglePrimaryEmotion(emotion: String, intensity: Int = 5) {
        val existing = selectedPrimaryEmotions.find { it.emotion == emotion }
        if (existing != null) {
            selectedPrimaryEmotions.remove(existing)
        } else {
            selectedPrimaryEmotions.add(EmotionRating(emotion, intensity))
        }
    }

    fun updatePrimaryEmotionIntensity(emotion: String, intensity: Int) {
        val index = selectedPrimaryEmotions.indexOfFirst { it.emotion == emotion }
        if (index != -1) {
            selectedPrimaryEmotions[index] = selectedPrimaryEmotions[index].copy(intensity = intensity.coerceIn(1, 10))
        }
    }

    fun toggleCognitiveDistortion(distortion: String) {
        if (selectedCognitiveDistortions.contains(distortion)) {
            selectedCognitiveDistortions.remove(distortion)
        } else {
            selectedCognitiveDistortions.add(distortion)
        }
    }

    fun addSignificantActivity(activity: String) {
        if (activity.isNotBlank() && !selectedSignificantActivities.contains(activity)) {
            selectedSignificantActivities.add(activity)
        }
    }

    fun removeSignificantActivity(activity: String) {
        selectedSignificantActivities.remove(activity)
    }

    fun addSelfCareActivity(activity: String) {
        if (activity.isNotBlank() && !selectedSelfCareActivities.contains(activity)) {
            selectedSelfCareActivities.add(activity)
        }
    }

    fun removeSelfCareActivity(activity: String) {
        selectedSelfCareActivities.remove(activity)
    }

    fun submitCheckIn() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null, submissionSuccess = false)
            val currentState = _uiState.value

            val thoughtRecordEntry = if (currentState.cbtExerciseType == AgronomicActionType.CROP_DIAGNOSIS) {
                AgronomicActionLogEntry(
                    situation = currentState.trSituation,
                    automaticNegativeThought = currentState.trAutomaticNegativeThought,
                    emotionsBefore = selectedTrEmotionsBefore.toList(),
                    cognitiveDistortions = selectedCognitiveDistortions.toList(),
                    evidenceForThought = currentState.trEvidenceForThought,
                    evidenceAgainstThought = currentState.trEvidenceAgainstThought,
                    alternativeThought = currentState.trAlternativeThought,
                    emotionsAfter = selectedTrEmotionsAfter.toList()
                )
            } else {
                null
            }

            val calculatedScore = ((10 - currentState.moodIntensity) * 10).coerceIn(0, 100)
            val calculatedState = when {
                calculatedScore < 30 -> CropHealthStatus.OPTIMAL_VIGOR
                calculatedScore < 60 -> CropHealthStatus.MILD_STRESS
                calculatedScore < 80 -> CropHealthStatus.HIGH_PEST_RISK
                else -> CropHealthStatus.CRITICAL_ALERT
            }

            val newEntry = CheckInEntry(
                timestamp = Timestamp.now(),
                score = calculatedScore,
                state = calculatedState,
                aiInsight = "Field action log: ${currentState.cbtExerciseType.displayName}.",
                overallMood = currentState.overallMood,
                moodIntensity = currentState.moodIntensity,
                primaryEmotions = selectedPrimaryEmotions.toList(),
                generalThoughts = currentState.generalThoughts,
                positiveHighlights = currentState.positiveHighlights,
                challengesFaced = currentState.challengesFaced,
                significantActivities = selectedSignificantActivities.toList(),
                selfCareActivities = selectedSelfCareActivities.toList(),
                cbtExerciseType = currentState.cbtExerciseType,
                thoughtRecord = thoughtRecordEntry,
                cbtReflectionPrompt = getCbtPromptForType(currentState.cbtExerciseType),
                cbtReflectionResponse = if (currentState.cbtExerciseType != AgronomicActionType.CROP_DIAGNOSIS) currentState.cbtReflectionResponse else null,
                learnedFromCbt = currentState.learnedFromCbt
            )

            submitCbtExerciseUseCase(newEntry)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(isLoading = false, submissionSuccess = true)
                    resetAllFields()
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(isLoading = false, error = e.message ?: "Failed to save field action.")
                }
        }
    }

    private fun getCbtPromptForType(type: AgronomicActionType): String? {
        return when (type) {
            AgronomicActionType.PEST_SOIL_ACTION -> "Describe pest management actions taken, organic sprays applied, or soil enrichment completed."
            AgronomicActionType.HARVEST_JOURNAL -> "Record harvest progress, yields, and successful crop practices on the farm today."
            AgronomicActionType.DROUGHT_MITIGATION -> "Outline drought, heatwave, or irrigation challenges and the proactive steps taken to preserve soil moisture."
            AgronomicActionType.SEASONAL_REFLECTION -> "Reflect on weather patterns, seasonal planting timing, and long-term farm resilience."
            else -> null
        }
    }

    fun resetAllFields() {
        _uiState.value = CBTScreenUiState()
        selectedPrimaryEmotions.clear()
        selectedCognitiveDistortions.clear()
        selectedTrEmotionsBefore.clear()
        selectedTrEmotionsAfter.clear()
        selectedSignificantActivities.clear()
        selectedSelfCareActivities.clear()
    }
}

typealias CropDiagnosticViewModel = CBTViewModel

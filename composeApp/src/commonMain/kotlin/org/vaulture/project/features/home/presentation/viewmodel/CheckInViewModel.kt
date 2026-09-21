package org.vaulture.project.features.home.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.vaulture.project.core.domain.CheckInResult
import org.vaulture.project.features.home.domain.model.MentalState
import org.vaulture.project.features.home.domain.repository.CheckInRepository
import org.vaulture.project.features.home.domain.usecase.AnalyzeCheckInUseCase
import kotlin.time.Clock
import kotlin.time.Instant

data class CheckInUiState(
    val isLoadingQuestions: Boolean = true,
    val step: Int = 0,
    val questions: List<String> = emptyList(),
    val answers: MutableList<Int> = mutableListOf(),
    val textResponse: String = "",
    val isAnalyzing: Boolean = false,
    val result: CheckInResult? = null
)

class CheckInViewModel(
    private val checkInRepository: CheckInRepository,
    private val analyzeCheckInUseCase: AnalyzeCheckInUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(CheckInUiState())
    val uiState = _uiState.asStateFlow()

    private val _latestResult = MutableStateFlow<CheckInResult?>(null)
    val latestResult = _latestResult.asStateFlow()

    private var checkInListenerJob: Job? = null

    init {
        observeTodayResult()
        loadDailyQuestions()
    }

    fun syncResult(result: CheckInResult) {
        _uiState.update { it.copy(result = result) }
    }

    private fun observeTodayResult() {
        checkInListenerJob?.cancel()
        checkInListenerJob = checkInRepository.getLatestCheckInStream()
            .onEach { entry ->
                if (entry != null) {
                    val entryInstant = Instant.fromEpochSeconds(entry.timestamp.seconds)
                    val entryDate = entryInstant.toLocalDateTime(TimeZone.currentSystemDefault()).date
                    val todayDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date

                    if (entryDate == todayDate) {
                        val domainState = when (entry.state.name) {
                            "STABLE" -> MentalState.STABLE
                            "MILD_STRESS" -> MentalState.MILD_STRESS
                            "HIGH_STRESS" -> MentalState.HIGH_STRESS
                            "BURNOUT_RISK" -> MentalState.BURNOUT_RISK
                            else -> MentalState.STABLE
                        }

                        val domainResult = CheckInResult(
                            score = entry.score,
                            state = domainState,
                            aiInsight = entry.aiInsight,
                            timestamp = entry.timestamp.seconds * 1000
                        )

                        _latestResult.value = domainResult
                        _uiState.update { it.copy(result = domainResult) }
                    } else {
                        _latestResult.value = null
                        _uiState.update { it.copy(result = null) }
                    }
                } else {
                    _latestResult.value = null
                }
            }
            .catch {
                _latestResult.value = null
            }
            .launchIn(viewModelScope)
    }

    private fun loadDailyQuestions() {
        val questions = checkInRepository.getDailyQuestions()
        _uiState.update {
            it.copy(
                isLoadingQuestions = false,
                questions = questions
            )
        }
    }

    fun selectAnswer(score: Int) {
        val currentAnswers = _uiState.value.answers.toMutableList()
        currentAnswers.add(score)
        _uiState.update { it.copy(answers = currentAnswers, step = it.step + 1) }
    }

    fun onTextChange(text: String) {
        _uiState.update { it.copy(textResponse = text) }
    }

    fun submitCheckIn() {
        _uiState.update { it.copy(isAnalyzing = true) }

        viewModelScope.launch {
            analyzeCheckInUseCase(
                answers = _uiState.value.answers,
                textResponse = _uiState.value.textResponse
            ).onSuccess { domainResult ->
                _uiState.update {
                    it.copy(
                        isAnalyzing = false,
                        result = domainResult
                    )
                }
                _latestResult.value = domainResult
            }.onFailure {
                _uiState.update { it.copy(isAnalyzing = false) }
            }
        }
    }

    fun resetCheckIn() {
        _uiState.update {
            CheckInUiState(
                isLoadingQuestions = false,
                questions = _uiState.value.questions,
                step = 0,
                answers = mutableListOf(),
                textResponse = "",
                isAnalyzing = false,
                result = null
            )
        }
    }
}

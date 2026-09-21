package org.vaulture.project.features.wellness.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.gitlive.firebase.firestore.Timestamp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime
import org.vaulture.project.features.wellness.domain.model.WellnessRecord
import org.vaulture.project.features.wellness.domain.model.WellnessStats
import org.vaulture.project.features.wellness.domain.model.AGRONOMIC_PROTOCOLS
import org.vaulture.project.features.wellness.domain.model.AgronomicStep
import org.vaulture.project.features.wellness.domain.model.WellnessType
import org.vaulture.project.features.wellness.domain.repository.WellnessRepository
import kotlin.time.Clock

data class WellnessUiState(
    val phase: WellnessPhase = WellnessPhase.SETUP,
    val isTimerRunning: Boolean = false,
    val timeLeftSeconds: Int = 0,
    val totalDurationSeconds: Int = 0,
    val currentActivity: WellnessType? = null,
    val stats: WellnessStats = WellnessStats(),
    val isCompleting: Boolean = false,
    val breathText: String = "Prepare",
    val sessionSaved: Boolean = false,
    val currentStepIndex: Int = 0,
    val checkedItems: Set<String> = emptySet()
)

enum class WellnessPhase { SETUP, ACTIVE, SUMMARY }

class WellnessViewModel(
    private val wellnessRepository: WellnessRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(WellnessUiState())
    val uiState = _uiState.asStateFlow()

    private var timerJob: Job? = null
    private var statsListenerJob: Job? = null

    init {
        observeStats()
    }

    private fun observeStats() {
        statsListenerJob?.cancel()
        statsListenerJob = wellnessRepository.getWellnessStatsStream()
            .onEach { stats ->
                _uiState.update { it.copy(stats = stats) }
            }
            .catch { }
            .launchIn(viewModelScope)
    }

    fun selectActivity(activity: WellnessType) {
        _uiState.update { it.copy(currentActivity = activity, currentStepIndex = 0, checkedItems = emptySet()) }
    }

    fun selectStep(index: Int) {
        val type = _uiState.value.currentActivity ?: WellnessType.BREATHING
        val steps = AGRONOMIC_PROTOCOLS[type] ?: emptyList()
        if (index in steps.indices) {
            _uiState.update { it.copy(currentStepIndex = index) }
        }
    }

    fun nextStep() {
        val type = _uiState.value.currentActivity ?: WellnessType.BREATHING
        val steps = AGRONOMIC_PROTOCOLS[type] ?: emptyList()
        val next = (_uiState.value.currentStepIndex + 1).coerceAtMost(steps.size - 1)
        _uiState.update { it.copy(currentStepIndex = next) }
    }

    fun previousStep() {
        val prev = (_uiState.value.currentStepIndex - 1).coerceAtLeast(0)
        _uiState.update { it.copy(currentStepIndex = prev) }
    }

    fun toggleChecklistItem(itemKey: String) {
        _uiState.update { current ->
            val updated = if (current.checkedItems.contains(itemKey)) {
                current.checkedItems - itemKey
            } else {
                current.checkedItems + itemKey
            }
            current.copy(checkedItems = updated)
        }
    }

    fun finishActionNow() {
        timerJob?.cancel()
        completeActivity()
    }

    fun startTimer(durationMinutes: Int) {
        timerJob?.cancel()
        val totalSeconds = durationMinutes * 60
        val type = _uiState.value.currentActivity ?: WellnessType.BREATHING

        _uiState.update {
            it.copy(
                phase = WellnessPhase.ACTIVE,
                timeLeftSeconds = totalSeconds,
                totalDurationSeconds = totalSeconds,
                isTimerRunning = true,
                isCompleting = false,
                sessionSaved = false,
                breathText = "Scouting Active",
                currentStepIndex = 0,
                checkedItems = emptySet()
            )
        }

        timerJob = viewModelScope.launch(Dispatchers.Main) {
            try {
                var elapsed = 0
                while (isActive && _uiState.value.timeLeftSeconds > 0) {
                    delay(1000)
                    elapsed++

                    val steps = AGRONOMIC_PROTOCOLS[type] ?: emptyList()
                    val secondsPerStep = if (steps.isNotEmpty()) totalSeconds / steps.size else 60
                    val currentCalculatedStep = if (secondsPerStep > 0 && steps.isNotEmpty()) {
                        (elapsed / secondsPerStep).coerceAtMost(steps.size - 1)
                    } else 0

                    _uiState.update {
                        it.copy(
                            timeLeftSeconds = it.timeLeftSeconds - 1,
                            currentStepIndex = maxOf(it.currentStepIndex, currentCalculatedStep)
                        )
                    }
                }

                if (isActive && _uiState.value.timeLeftSeconds == 0) {
                    completeActivity()
                }
            } catch (e: Exception) {
            }
        }
    }

    private fun completeActivity() {
        val type = _uiState.value.currentActivity ?: return
        val duration = _uiState.value.totalDurationSeconds

        _uiState.update { it.copy(isTimerRunning = false, isCompleting = true) }

        viewModelScope.launch {
            withContext(NonCancellable) {
                val record = WellnessRecord(
                    type = type,
                    durationSeconds = duration,
                    timestamp = Timestamp.now()
                )

                val updatedStats = calculateUpdatedStats(_uiState.value.stats, duration / 60)

                wellnessRepository.saveWellnessSession(record, updatedStats)
                    .onSuccess {
                        withContext(Dispatchers.Main) {
                            _uiState.update {
                                it.copy(
                                    stats = updatedStats,
                                    phase = WellnessPhase.SUMMARY,
                                    isCompleting = false,
                                    sessionSaved = true
                                )
                            }
                        }
                    }
                    .onFailure {
                        _uiState.update { it.copy(isCompleting = false) }
                    }
            }
        }
    }

    private fun calculateUpdatedStats(current: WellnessStats, durationMins: Int): WellnessStats {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        val todayStr = now.toString()
        val yesterdayStr = now.minus(1, DateTimeUnit.DAY).toString()

        val isNewDay = current.lastActivityDate != todayStr

        val newStreak = when (current.lastActivityDate) {
            todayStr -> current.currentStreak
            yesterdayStr -> current.currentStreak + 1
            "" -> 1
            else -> 1
        }

        val sessionsToday = if (isNewDay) 1 else (current.sessionsToday + 1).coerceAtMost(5)
        val pointsEarned = 50 + (durationMins * 10) + (sessionsToday * 20)

        return current.copy(
            currentStreak = newStreak,
            longestStreak = maxOf(newStreak, current.longestStreak),
            lastActivityDate = todayStr,
            totalMinutes = current.totalMinutes + durationMins,
            sessionsToday = sessionsToday,
            resiliencePoints = current.resiliencePoints + pointsEarned
        )
    }

    fun resetTimer() {
        timerJob?.cancel()
        _uiState.update {
            it.copy(
                phase = WellnessPhase.SETUP,
                isTimerRunning = false,
                timeLeftSeconds = 0,
                breathText = "Prepare"
            )
        }
    }

    fun resetToSetup() = resetTimer()

    fun togglePauseResume() {
        if (_uiState.value.isTimerRunning) {
            timerJob?.cancel()
            _uiState.update { it.copy(isTimerRunning = false) }
        } else {
            startTimer(_uiState.value.timeLeftSeconds / 60)
        }
    }
}

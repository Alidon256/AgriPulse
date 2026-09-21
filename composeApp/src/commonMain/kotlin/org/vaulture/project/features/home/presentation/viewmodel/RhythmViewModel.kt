package org.vaulture.project.features.home.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.vaulture.project.core.util.KmpAudioPlayer
import org.vaulture.project.data.repos.SearchContext
import org.vaulture.project.features.home.domain.model.RhythmTrack
import org.vaulture.project.features.home.domain.repository.RhythmRepository

data class RhythmUiState(
    val tracks: List<RhythmTrack> = emptyList(),
    val searchQuery: String = "",
    val isTracksLoading: Boolean = false,
    val currentTrack: RhythmTrack? = null,
    val isPlaying: Boolean = false,
    val currentPositionMs: Long = 0,
    val durationMs: Long = 0,
    val progress: Long = 0L,
    val duration: Long = 0L,
    val error: String? = null
)

class RhythmViewModel(
    private val rhythmRepository: RhythmRepository,
    val audioPlayer: KmpAudioPlayer
) : ViewModel() {

    private val _uiState = MutableStateFlow(RhythmUiState())
    val uiState: StateFlow<RhythmUiState> = _uiState.asStateFlow()
    private val _searchSuggestions = MutableStateFlow<List<RhythmTrack>>(emptyList())
    val searchSuggestions: StateFlow<List<RhythmTrack>> = _searchSuggestions.asStateFlow()

    init {
        observeAudioPlayer()
        loadTracks()
    }

    private fun observeAudioPlayer() {
        viewModelScope.launch {
            combine(audioPlayer.isPlaying, audioPlayer.currentPosition, audioPlayer.duration) { playing, pos, dur ->
                _uiState.update { currentState ->
                    val effectiveDuration = if (dur > 0L) dur else currentState.duration
                    currentState.copy(
                        isPlaying = playing,
                        currentPositionMs = pos,
                        durationMs = effectiveDuration,
                        progress = pos,
                        duration = effectiveDuration
                    )
                }
            }.collect()
        }
    }

    private fun loadTracks() {
        _uiState.update { it.copy(isTracksLoading = true) }
        rhythmRepository.getTracksStream()
            .onEach { trackList ->
                _uiState.update { it.copy(tracks = trackList, isTracksLoading = false) }
            }
            .catch { e ->
                _uiState.update { it.copy(isTracksLoading = false, error = e.message) }
            }
            .launchIn(viewModelScope)
    }

    fun updateSearchQuery(query: String, context: SearchContext) {
        _uiState.update { it.copy(searchQuery = query) }

        if (query.isBlank()) {
            _searchSuggestions.value = emptyList()
        } else {
            val filtered = _uiState.value.tracks.filter {
                it.title.contains(query, ignoreCase = true) ||
                        it.artist.contains(query, ignoreCase = true)
            }
            _searchSuggestions.value = filtered
        }
    }

    fun playNext() {
        val current = _uiState.value.currentTrack ?: return
        val list = _uiState.value.tracks
        val index = list.indexOf(current)
        if (index != -1 && index < list.lastIndex) {
            playTrack(list[index + 1])
        }
    }

    fun playPrevious() {
        val current = _uiState.value.currentTrack ?: return
        val list = _uiState.value.tracks
        val index = list.indexOf(current)
        if (index > 0) {
            playTrack(list[index - 1])
        }
    }

    fun togglePlayPause() {
        if (_uiState.value.isPlaying) {
            audioPlayer.pause()
        } else {
            _uiState.value.currentTrack?.let { playTrack(it) }
        }
    }

    fun togglePlayback() = togglePlayPause()

    fun closePlayer() {
        audioPlayer.stop()
        _uiState.update { it.copy(currentTrack = null, isPlaying = false, progress = 0) }
    }

    fun seekTo(positionMs: Long) {
        audioPlayer.seekTo(positionMs)
    }

    fun loadTrackById(trackId: String) {
        val existingTrack = _uiState.value.tracks.find { it.id == trackId }
        if (existingTrack != null) {
            _uiState.update { it.copy(currentTrack = existingTrack) }
            if (audioPlayer.isPlaying.value == false || _uiState.value.currentTrack?.id != trackId) {
                playTrack(existingTrack)
            }
        }
    }

    fun playTrack(track: RhythmTrack) {
        _uiState.update { it.copy(currentTrack = track) }

        val parsedDurationMs = parseDurationStringToMillis(track.duration)
        if (parsedDurationMs > 0L) {
            _uiState.update { it.copy(durationMs = parsedDurationMs, duration = parsedDurationMs) }
        }

        val url = track.previewUrl.ifBlank { track.previewUrl }
        if (url.isNotBlank()) {
            audioPlayer.play(url, track.title, track.artist)
        }
    }

    private fun parseDurationStringToMillis(durationStr: String): Long {
        if (durationStr.isBlank()) return 0L
        return try {
            val parts = durationStr.split(":")
            if (parts.size == 2) {
                val mins = parts[0].trim().toLong()
                val secs = parts[1].trim().toLong()
                (mins * 60 + secs) * 1000L
            } else if (parts.size == 3) {
                val hours = parts[0].trim().toLong()
                val mins = parts[1].trim().toLong()
                val secs = parts[2].trim().toLong()
                (hours * 3600 + mins * 60 + secs) * 1000L
            } else {
                0L
            }
        } catch (e: Exception) {
            0L
        }
    }
}

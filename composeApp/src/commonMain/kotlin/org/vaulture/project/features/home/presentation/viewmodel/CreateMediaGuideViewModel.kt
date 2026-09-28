package org.vaulture.project.features.home.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.vaulture.project.features.home.domain.model.FarmerVideoGuide
import org.vaulture.project.features.home.domain.model.RhythmTrack
import org.vaulture.project.features.home.domain.repository.RhythmRepository
import org.vaulture.project.features.home.domain.repository.VideoGuideRepository
import kotlin.random.Random

enum class GuideMediaType {
    VIDEO,
    AUDIO
}

data class CreateMediaGuideUiState(
    val mediaType: GuideMediaType = GuideMediaType.VIDEO,
    val title: String = "",
    val description: String = "",
    val instructor: String = "",
    val category: String = "Pest & Disease Control",
    val language: String = "English",
    val durationText: String = "05:00",
    val mediaUrl: String = "",
    val thumbnailUrl: String = "https://images.unsplash.com/photo-1592982537447-7440770cbfc9?w=800",
    val keyTakeaways: List<String> = listOf("Scout fields early morning", "Apply organic biological repellent"),
    val tags: List<String> = listOf("pestcontrol", "organic", "maize"),
    val isPublishing: Boolean = false,
    val publishSuccess: Boolean = false,
    val errorMessage: String? = null
)

class CreateMediaGuideViewModel(
    private val videoGuideRepository: VideoGuideRepository,
    private val rhythmRepository: RhythmRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateMediaGuideUiState())
    val uiState = _uiState.asStateFlow()

    fun setMediaType(type: GuideMediaType) {
        _uiState.update {
            it.copy(
                mediaType = type,
                thumbnailUrl = if (type == GuideMediaType.VIDEO) {
                    "https://images.unsplash.com/photo-1592982537447-7440770cbfc9?w=800"
                } else {
                    "https://images.unsplash.com/photo-1500937386664-56d1dfef3854?w=800"
                }
            )
        }
    }

    fun updateTitle(title: String) = _uiState.update { it.copy(title = title, errorMessage = null) }
    fun updateDescription(desc: String) = _uiState.update { it.copy(description = desc) }
    fun updateInstructor(instructor: String) = _uiState.update { it.copy(instructor = instructor) }
    fun updateCategory(category: String) = _uiState.update { it.copy(category = category) }
    fun updateLanguage(language: String) = _uiState.update { it.copy(language = language) }
    fun updateDuration(duration: String) = _uiState.update { it.copy(durationText = duration) }
    fun updateMediaUrl(url: String) = _uiState.update { it.copy(mediaUrl = url, errorMessage = null) }
    fun updateThumbnailUrl(url: String) = _uiState.update { it.copy(thumbnailUrl = url) }

    fun addKeyTakeaway(takeaway: String) {
        if (takeaway.isNotBlank() && !_uiState.value.keyTakeaways.contains(takeaway.trim())) {
            _uiState.update { it.copy(keyTakeaways = it.keyTakeaways + takeaway.trim()) }
        }
    }

    fun removeKeyTakeaway(takeaway: String) {
        _uiState.update { it.copy(keyTakeaways = it.keyTakeaways.filterNot { item -> item == takeaway }) }
    }

    fun toggleTag(tag: String) {
        val cleanTag = tag.trim().removePrefix("#").lowercase()
        _uiState.update { state ->
            val updatedTags = if (state.tags.contains(cleanTag)) {
                state.tags.filterNot { it == cleanTag }
            } else {
                state.tags + cleanTag
            }
            state.copy(tags = updatedTags)
        }
    }

    fun publishGuide(onSuccess: () -> Unit) {
        viewModelScope.launch {
            publishGuideInternal(onSuccess)
        }
    }

    suspend fun publishGuideInternal(onSuccess: () -> Unit) {
        val state = _uiState.value

        if (state.title.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Please enter a title for the agronomic guide.") }
            return
        }

        val effectiveMediaUrl = if (state.mediaUrl.isNotBlank()) {
            state.mediaUrl.trim()
        } else if (state.mediaType == GuideMediaType.VIDEO) {
            "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4"
        } else {
            "https://cdn.pixabay.com/download/audio/2022/05/27/audio_1808fbf07a.mp3"
        }

        val effectiveInstructor = if (state.instructor.isNotBlank()) {
            state.instructor.trim()
        } else {
            "Community Farmer Extension"
        }

        _uiState.update { it.copy(isPublishing = true, errorMessage = null) }

        try {
            val generatedId = "guide_${Random.nextLong(100000, 999999)}"

            if (state.mediaType == GuideMediaType.VIDEO) {
                val videoGuide = FarmerVideoGuide(
                    id = generatedId,
                    title = state.title.trim(),
                    description = state.description.trim(),
                    instructor = effectiveInstructor,
                    durationText = state.durationText.ifBlank { "05:00" },
                    category = state.category,
                    thumbnailUrl = state.thumbnailUrl,
                    videoUrl = effectiveMediaUrl,
                    youtubeVideoId = if (effectiveMediaUrl.contains("youtube.com") || effectiveMediaUrl.contains("youtu.be")) {
                        effectiveMediaUrl.substringAfterLast("/").substringAfterLast("v=")
                    } else {
                        ""
                    },
                    keyTakeaways = state.keyTakeaways,
                    language = state.language,
                    tags = state.tags
                )
                videoGuideRepository.addVideoGuide(videoGuide)
            } else {
                val audioTrack = RhythmTrack(
                    id = generatedId,
                    title = state.title.trim(),
                    artist = effectiveInstructor,
                    album = "${state.category} • ${state.language}",
                    thumbnailUrl = state.thumbnailUrl,
                    duration = state.durationText.ifBlank { "05:00" },
                    tags = state.tags,
                    listenerCount = 1,
                    isExplicit = false,
                    isLiked = false,
                    previewUrl = effectiveMediaUrl
                )
                rhythmRepository.addTrack(audioTrack)
            }

            _uiState.update { it.copy(isPublishing = false, publishSuccess = true) }
            onSuccess()
        } catch (e: Exception) {
            _uiState.update {
                it.copy(
                    isPublishing = false,
                    errorMessage = "Failed to publish guide: ${e.message ?: "Unknown error"}"
                )
            }
        }
    }
}

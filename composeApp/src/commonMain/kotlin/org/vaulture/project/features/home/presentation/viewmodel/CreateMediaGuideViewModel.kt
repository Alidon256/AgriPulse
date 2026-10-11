package org.vaulture.project.features.home.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.FirebaseAuth
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.storage.FirebaseStorage
import dev.gitlive.firebase.storage.storage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.vaulture.project.core.util.VideoMetadata
import org.vaulture.project.core.util.upload
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
    val isUploadingMedia: Boolean = false,
    val uploadProgressMessage: String? = null,
    val publishSuccess: Boolean = false,
    val errorMessage: String? = null
)

class CreateMediaGuideViewModel(
    private val videoGuideRepository: VideoGuideRepository,
    private val rhythmRepository: RhythmRepository,
    private val auth: FirebaseAuth? = null,
    private val storage: FirebaseStorage? = null
) : ViewModel() {

    private val firebaseAuth: FirebaseAuth? get() = auth ?: try { Firebase.auth } catch (_: Throwable) { null }
    private val firebaseStorage: FirebaseStorage? get() = storage ?: try { Firebase.storage } catch (_: Throwable) { null }

    private val _uiState = MutableStateFlow(CreateMediaGuideUiState())
    val uiState = _uiState.asStateFlow()

    init {
        try {
            firebaseAuth?.currentUser?.displayName?.let { name ->
                if (name.isNotBlank()) {
                    _uiState.update { it.copy(instructor = name) }
                }
            }
        } catch (_: Throwable) {}
    }

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
    fun updateMediaUrl(url: String) {
        val trimmed = url.trim()
        val ytId = extractYouTubeVideoId(trimmed)
        _uiState.update { state ->
            val updatedThumb = if (ytId.isNotBlank()) {
                "https://img.youtube.com/vi/$ytId/hqdefault.jpg"
            } else {
                state.thumbnailUrl
            }
            state.copy(
                mediaUrl = url,
                thumbnailUrl = updatedThumb,
                errorMessage = null
            )
        }
    }
    fun updateThumbnailUrl(url: String) = _uiState.update { it.copy(thumbnailUrl = url) }

    private suspend fun uploadFile(bytes: ByteArray, path: String): String {
        val currentStorage = firebaseStorage ?: throw IllegalStateException("Firebase Storage is unavailable")
        val ref = currentStorage.reference(path)
        ref.upload(bytes)
        return ref.getDownloadUrl()
    }

    fun handleVideoPicked(videoBytes: ByteArray, metadata: VideoMetadata?) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isUploadingMedia = true,
                    uploadProgressMessage = "Extracting video metadata & uploading...",
                    errorMessage = null
                )
            }

            try {
                val currentUid = firebaseAuth?.currentUser?.uid ?: "user"
                val timestamp = kotlin.time.Clock.System.now().toEpochMilliseconds()
                val randomKey = Random.nextLong(1000, 9999)

                // 1. Upload extracted thumbnail if available
                var thumbUrl = _uiState.value.thumbnailUrl
                if (metadata?.thumbnailBytes != null) {
                    _uiState.update { it.copy(uploadProgressMessage = "Uploading extracted video thumbnail...") }
                    thumbUrl = uploadFile(
                        metadata.thumbnailBytes,
                        "video_guides/thumbnails/${currentUid}_${timestamp}_${randomKey}.jpg"
                    )
                }

                // 2. Upload video file
                _uiState.update { it.copy(uploadProgressMessage = "Uploading video file...") }
                val ext = metadata?.fileName?.substringAfterLast('.', "mp4") ?: "mp4"
                val videoUrl = uploadFile(
                    videoBytes,
                    "video_guides/videos/${currentUid}_${timestamp}_${randomKey}.$ext"
                )

                _uiState.update {
                    it.copy(
                        mediaUrl = videoUrl,
                        thumbnailUrl = thumbUrl,
                        durationText = metadata?.durationText?.ifBlank { it.durationText } ?: it.durationText,
                        isUploadingMedia = false,
                        uploadProgressMessage = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isUploadingMedia = false,
                        uploadProgressMessage = null,
                        errorMessage = "Failed to upload video: ${e.message ?: "Unknown error"}"
                    )
                }
            }
        }
    }

    fun handleThumbnailPicked(imageBytes: ByteArray) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isUploadingMedia = true,
                    uploadProgressMessage = "Uploading custom thumbnail...",
                    errorMessage = null
                )
            }

            try {
                val currentUid = firebaseAuth?.currentUser?.uid ?: "user"
                val timestamp = kotlin.time.Clock.System.now().toEpochMilliseconds()
                val randomKey = Random.nextLong(1000, 9999)

                val thumbUrl = uploadFile(
                    imageBytes,
                    "video_guides/thumbnails/${currentUid}_${timestamp}_${randomKey}_custom.jpg"
                )

                _uiState.update {
                    it.copy(
                        thumbnailUrl = thumbUrl,
                        isUploadingMedia = false,
                        uploadProgressMessage = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isUploadingMedia = false,
                        uploadProgressMessage = null,
                        errorMessage = "Failed to upload thumbnail: ${e.message ?: "Unknown error"}"
                    )
                }
            }
        }
    }

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

        if (state.mediaUrl.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Please provide a valid video stream or YouTube URL for your guide.") }
            return
        }

        val effectiveMediaUrl = state.mediaUrl.trim()
        val currentUid = firebaseAuth?.currentUser?.uid ?: ""
        val effectiveInstructor = if (state.instructor.isNotBlank()) {
            state.instructor.trim()
        } else {
            firebaseAuth?.currentUser?.displayName ?: "Community Creator"
        }

        _uiState.update { it.copy(isPublishing = true, errorMessage = null) }

        try {
            val generatedId = "guide_${Random.nextLong(100000, 999999)}"

            if (state.mediaType == GuideMediaType.VIDEO) {
                val videoGuide = FarmerVideoGuide(
                    id = generatedId,
                    userId = currentUid,
                    title = state.title.trim(),
                    description = state.description.trim(),
                    instructor = effectiveInstructor,
                    durationText = state.durationText.ifBlank { "05:00" },
                    category = state.category,
                    thumbnailUrl = state.thumbnailUrl.trim(),
                    videoUrl = effectiveMediaUrl,
                    youtubeVideoId = extractYouTubeVideoId(effectiveMediaUrl),
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
                    thumbnailUrl = state.thumbnailUrl.trim(),
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

    private fun extractYouTubeVideoId(url: String): String {
        val trimmed = url.trim()
        val pattern = Regex("(?:youtube\\.com/(?:[^/]+/.+/|(?:v|e(?:mbed)?)/|.*[?&]v=)|youtu\\.be/)([^\"&?/\\s]{11})", RegexOption.IGNORE_CASE)
        val match = pattern.find(trimmed)
        if (match != null) return match.groupValues[1]
        if (trimmed.length == 11 && trimmed.matches(Regex("^[a-zA-Z0-9_-]{11}$"))) {
            return trimmed
        }
        return ""
    }
}

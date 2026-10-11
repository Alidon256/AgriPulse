package org.vaulture.project.features.home.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class FarmerVideoGuide(
    val id: String,
    val userId: String = "",
    val title: String,
    val description: String,
    val instructor: String,
    val durationText: String,
    val category: String,
    val thumbnailUrl: String,
    val videoUrl: String,
    val youtubeVideoId: String = "",
    val keyTakeaways: List<String> = emptyList(),
    val language: String = "English",
    val tags: List<String> = emptyList()
)

// Zero mock data in the app: all video guides are fetched from user profiles & community Firestore
val DEFAULT_VIDEO_GUIDES = emptyList<FarmerVideoGuide>()

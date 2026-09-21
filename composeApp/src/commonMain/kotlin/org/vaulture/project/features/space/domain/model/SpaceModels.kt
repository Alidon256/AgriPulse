package org.vaulture.project.features.space.domain.model

import dev.gitlive.firebase.firestore.Timestamp
import dev.gitlive.firebase.firestore.TimestampSerializer
import kotlinx.serialization.Serializable

@Serializable
data class Space(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val coverImageUrl: String = "",
    val ownerId: String = "",
    val memberIds: List<String> = emptyList(),
    @Serializable(with = TimestampSerializer::class)
    val createdAt: Timestamp? = null,
    val memberPhotoUrls: List<String> = emptyList(),
    val unreadCount: Int = 0,
)

@Serializable
data class SpaceMessage(
    val id: String = "",
    val spaceId: String = "",
    val authorId: String = "",
    val authorName: String = "",
    val authorAvatarUrl: String = "",
    val text: String = "",
    @Serializable(with = TimestampSerializer::class)
    val timestamp: Timestamp? = null
)

val DEFAULT_SPACES = listOf(
    Space(
        id = "space_maize_cereals",
        name = "Maize & Cereals",
        description = "Agronomy techniques, drought-hardy seed selection, and harvest yield optimization for maize, sorghum, and cereal growers.",
        coverImageUrl = "https://images.unsplash.com/photo-1551754655-cd27e38d2076?w=800&auto=format&fit=crop&q=80",
        ownerId = "system",
        memberIds = listOf("system"),
        unreadCount = 0
    ),
    Space(
        id = "space_organic_pest",
        name = "Organic Pest Control",
        description = "Scouting, early identification, and biological control of Fall Armyworm, locusts, and stem borers using neem spray and companion crops.",
        coverImageUrl = "https://images.unsplash.com/photo-1597916829826-02e5bb4a54e0?w=800&auto=format&fit=crop&q=80",
        ownerId = "system",
        memberIds = listOf("system"),
        unreadCount = 0
    ),
    Space(
        id = "space_climate_irrigation",
        name = "Climate & Irrigation",
        description = "Rainwater harvesting, soil mulching, micro-drip irrigation, and weather-resilient practices during prolonged dry spells.",
        coverImageUrl = "https://images.unsplash.com/photo-1563514227147-6d2ff665a6a0?w=800&auto=format&fit=crop&q=80",
        ownerId = "system",
        memberIds = listOf("system"),
        unreadCount = 0
    ),
    Space(
        id = "space_market_prices",
        name = "Market Prices",
        description = "Regional wholesale and farm-gate market commodity prices, transport corridor updates, and collective bargaining hubs.",
        coverImageUrl = "https://images.unsplash.com/photo-1500937386664-56d1dfef3854?w=800&auto=format&fit=crop&q=80",
        ownerId = "system",
        memberIds = listOf("system"),
        unreadCount = 0
    )
)
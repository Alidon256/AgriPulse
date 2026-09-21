package org.vaulture.project.features.space.domain.repository

import kotlinx.coroutines.flow.Flow
import org.vaulture.project.core.domain.User
import org.vaulture.project.features.space.domain.model.Space
import org.vaulture.project.features.space.domain.model.SpaceMessage
import org.vaulture.project.features.space.domain.model.Story

interface SpaceRepository {
    fun getStoriesStream(): Flow<List<Story>>
    suspend fun createStory(title: String, mediaBytes: ByteArray?, mediaFileName: String?): Result<Unit>

    fun getPostsStream(filter: String = "ALL"): Flow<List<Story>>
    fun getSpacePostsStream(spaceId: String): Flow<List<Story>>
    suspend fun createPost(
        text: String,
        spaceId: String? = null,
        spaceName: String? = null,
        imageBytes: ByteArray? = null,
        imageFileName: String? = null
    ): Result<Unit>
    suspend fun deletePost(postId: String): Result<Unit>

    suspend fun toggleLikePost(postId: String, currentLikes: List<String>): Result<Unit>
    suspend fun toggleBookmarkPost(postId: String, currentBookmarks: List<String>): Result<Unit>

    fun getCommentsStream(postId: String): Flow<List<Story.Comment>>
    suspend fun addComment(postId: String, text: String): Result<Unit>

    fun getSpacesStream(): Flow<List<Space>>
    fun getSpaceDetailsStream(spaceId: String): Flow<Space?>
    suspend fun createSpace(name: String, description: String, icon: String, isPrivate: Boolean, imageBytes: ByteArray? = null): Result<Space>
    suspend fun updateSpace(spaceId: String, name: String, description: String, icon: String, imageBytes: ByteArray? = null): Result<Unit>
    suspend fun joinSpace(spaceId: String): Result<Unit>
    suspend fun leaveSpace(spaceId: String): Result<Unit>

    fun getSpaceMessagesStream(spaceId: String): Flow<List<SpaceMessage>>
    suspend fun sendSpaceMessage(spaceId: String, text: String): Result<Unit>

    fun getUserProfileStream(userId: String): Flow<User?>
    suspend fun updateUserProfile(user: User, imageBytes: ByteArray? = null, imageFileName: String? = null): Result<Unit>
    fun getCommunityFarmersStream(): Flow<List<User>>
}


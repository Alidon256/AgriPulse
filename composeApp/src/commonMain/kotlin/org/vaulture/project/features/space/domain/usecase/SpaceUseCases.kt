package org.vaulture.project.features.space.domain.usecase

import kotlinx.coroutines.flow.Flow
import org.vaulture.project.core.domain.User
import org.vaulture.project.features.space.domain.model.Space
import org.vaulture.project.features.space.domain.model.SpaceMessage
import org.vaulture.project.features.space.domain.model.Story
import org.vaulture.project.features.space.domain.repository.SpaceRepository

class SpaceUseCases(
    private val repository: SpaceRepository
) {
    fun getStoriesStream(): Flow<List<Story>> = repository.getStoriesStream()
    
    suspend fun createStory(
        title: String,
        mediaBytes: ByteArray?,
        mediaFileName: String?
    ): Result<Unit> = repository.createStory(title, mediaBytes, mediaFileName)

    fun getPostsStream(filter: String = "ALL"): Flow<List<Story>> = repository.getPostsStream(filter)

    fun getSpacePostsStream(spaceId: String): Flow<List<Story>> = repository.getSpacePostsStream(spaceId)

    suspend fun createPost(
        text: String,
        spaceId: String? = null,
        spaceName: String? = null,
        imageBytes: ByteArray? = null,
        imageFileName: String? = null
    ): Result<Unit> = repository.createPost(text, spaceId, spaceName, imageBytes, imageFileName)

    suspend fun deletePost(postId: String): Result<Unit> = repository.deletePost(postId)

    suspend fun toggleLikePost(postId: String, currentLikes: List<String>): Result<Unit> =
        repository.toggleLikePost(postId, currentLikes)

    suspend fun toggleBookmarkPost(postId: String, currentBookmarks: List<String>): Result<Unit> =
        repository.toggleBookmarkPost(postId, currentBookmarks)

    fun getCommentsStream(postId: String): Flow<List<Story.Comment>> =
        repository.getCommentsStream(postId)

    suspend fun addComment(postId: String, text: String): Result<Unit> =
        repository.addComment(postId, text)

    fun getSpacesStream(): Flow<List<Space>> = repository.getSpacesStream()

    fun getSpaceDetailsStream(spaceId: String): Flow<Space?> = repository.getSpaceDetailsStream(spaceId)

    suspend fun createSpace(
        name: String,
        description: String,
        icon: String,
        isPrivate: Boolean,
        imageBytes: ByteArray? = null
    ): Result<Space> = repository.createSpace(name, description, icon, isPrivate, imageBytes)

    suspend fun updateSpace(
        spaceId: String,
        name: String,
        description: String,
        icon: String,
        imageBytes: ByteArray? = null
    ): Result<Unit> = repository.updateSpace(spaceId, name, description, icon, imageBytes)

    suspend fun joinSpace(spaceId: String): Result<Unit> = repository.joinSpace(spaceId)

    suspend fun leaveSpace(spaceId: String): Result<Unit> = repository.leaveSpace(spaceId)

    fun getSpaceMessagesStream(spaceId: String): Flow<List<SpaceMessage>> =
        repository.getSpaceMessagesStream(spaceId)

    suspend fun sendSpaceMessage(spaceId: String, text: String): Result<Unit> =
        repository.sendSpaceMessage(spaceId, text)

    fun getUserProfileStream(userId: String): Flow<User?> =
        repository.getUserProfileStream(userId)

    suspend fun updateUserProfile(
        user: User,
        imageBytes: ByteArray? = null,
        imageFileName: String? = null
    ): Result<Unit> = repository.updateUserProfile(user, imageBytes, imageFileName)

    fun getCommunityFarmersStream(): Flow<List<User>> = repository.getCommunityFarmersStream()
}


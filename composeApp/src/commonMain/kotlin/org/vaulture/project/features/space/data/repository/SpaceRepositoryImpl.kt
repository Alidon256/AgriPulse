package org.vaulture.project.features.space.data.repository

import dev.gitlive.firebase.auth.FirebaseAuth
import dev.gitlive.firebase.firestore.FieldValue
import dev.gitlive.firebase.firestore.FirebaseFirestore
import dev.gitlive.firebase.firestore.Timestamp
import dev.gitlive.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import org.vaulture.project.core.domain.User
import org.vaulture.project.core.util.upload
import org.vaulture.project.features.space.domain.model.DEFAULT_SPACES
import org.vaulture.project.features.space.domain.model.Space
import org.vaulture.project.features.space.domain.model.SpaceMessage
import org.vaulture.project.features.space.domain.model.Story
import org.vaulture.project.features.space.domain.repository.SpaceRepository

class SpaceRepositoryImpl(
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage,
    private val auth: FirebaseAuth
) : SpaceRepository {

    private suspend fun uploadFile(bytes: ByteArray, path: String): String {
        val ref = storage.reference(path)
        ref.upload(bytes)
        return ref.getDownloadUrl()
    }

    private fun getUsersMapFlow(): Flow<Map<String, User>> = firestore
        .collection("users")
        .snapshots
        .map { snapshot ->
            snapshot.documents.mapNotNull { doc ->
                try {
                    val user: User = doc.data()
                    val uid = if (user.uid.isBlank()) doc.id else user.uid
                    uid to user
                } catch (e: Exception) {
                    null
                }
            }.toMap()
        }

    private suspend fun fetchAuthorDetails(userId: String): Pair<String, String?> {
        val user = auth.currentUser
        var name = user?.displayName ?: user?.email?.substringBefore("@") ?: "Anonymous"
        var photoUrl: String? = user?.photoURL

        try {
            val userDoc = firestore.collection("users").document(userId).get()
            if (userDoc.exists) {
                val dbUser = userDoc.data<User>()
                dbUser.photoUrl?.ifBlank { null }?.let { photoUrl = it }
                dbUser.effectiveName.ifBlank { null }?.let { name = it }
            }
        } catch (_: Exception) {
            // fallback to Auth profile
        }
        return Pair(name, photoUrl)
    }

    override fun getStoriesStream(): Flow<List<Story>> = combine(
        firestore.collection("stories").snapshots,
        getUsersMapFlow()
    ) { storiesSnapshot, userMap ->
        storiesSnapshot.documents.mapNotNull { doc ->
            try {
                val story: Story = doc.data()
                val author = userMap[story.userId]
                val realProfileUrl = author?.photoUrl?.ifBlank { null } ?: story.userProfileUrl?.ifBlank { null }
                val realUserName = author?.effectiveName?.ifBlank { null } ?: story.userName.ifBlank { "Anonymous" }

                story.copy(
                    storyId = doc.id,
                    userName = realUserName,
                    userProfileUrl = realProfileUrl
                )
            } catch (_: Exception) {
                null
            }
        }.filter { !it.isFeed }
    }

    override suspend fun createStory(
        title: String,
        mediaBytes: ByteArray?,
        mediaFileName: String?
    ): Result<Unit> = runCatching {
        val user = auth.currentUser ?: throw IllegalStateException("User not authenticated")
        val mediaUrl = if (mediaBytes != null && mediaFileName != null) {
            uploadFile(mediaBytes, "story_media/${user.uid}_${Timestamp.now().seconds}_$mediaFileName")
        } else null

        val (authorName, authorPhotoUrl) = fetchAuthorDetails(user.uid)

        val story = Story(
            userId = user.uid,
            userName = authorName,
            userProfileUrl = authorPhotoUrl,
            textContent = title,
            contentUrl = mediaUrl,
            timestamp = Timestamp.now(),
            isFeed = false
        )
        firestore.collection("stories").add(story)
    }

    override fun getPostsStream(filter: String): Flow<List<Story>> = combine(
        firestore.collection("stories").snapshots,
        getUsersMapFlow()
    ) { storiesSnapshot, userMap ->
        val currentUserId = auth.currentUser?.uid
        storiesSnapshot.documents.mapNotNull { doc ->
            try {
                val story: Story = doc.data()
                val isLiked = currentUserId != null && story.likedBy.contains(currentUserId)
                val author = userMap[story.userId]
                val realProfileUrl = author?.photoUrl?.ifBlank { null } ?: story.userProfileUrl?.ifBlank { null }
                val realUserName = author?.effectiveName?.ifBlank { null } ?: story.userName.ifBlank { "Anonymous" }

                story.copy(
                    storyId = doc.id,
                    userName = realUserName,
                    userProfileUrl = realProfileUrl,
                    likeCount = story.likedBy.size,
                    isLiked = isLiked
                )
            } catch (_: Exception) {
                null
            }
        }.filter { it.isFeed }
    }

    override fun getSpacePostsStream(spaceId: String): Flow<List<Story>> = combine(
        firestore.collection("spaces").document(spaceId).collection("posts").snapshots,
        getUsersMapFlow()
    ) { postsSnapshot, userMap ->
        val currentUserId = auth.currentUser?.uid
        postsSnapshot.documents.mapNotNull { doc ->
            try {
                val story: Story = doc.data()
                val isLiked = currentUserId != null && story.likedBy.contains(currentUserId)
                val author = userMap[story.userId]
                val realProfileUrl = author?.photoUrl?.ifBlank { null } ?: story.userProfileUrl?.ifBlank { null }
                val realUserName = author?.effectiveName?.ifBlank { null } ?: story.userName.ifBlank { "Anonymous" }

                story.copy(
                    storyId = doc.id,
                    userName = realUserName,
                    userProfileUrl = realProfileUrl,
                    likeCount = story.likedBy.size,
                    isLiked = isLiked
                )
            } catch (_: Exception) {
                null
            }
        }
    }

    override suspend fun createPost(
        text: String,
        spaceId: String?,
        spaceName: String?,
        imageBytes: ByteArray?,
        imageFileName: String?
    ): Result<Unit> = runCatching {
        val user = auth.currentUser ?: throw IllegalStateException("User not authenticated")
        val imageUrl = if (imageBytes != null && imageFileName != null) {
            uploadFile(imageBytes, "post_images/${user.uid}_${Timestamp.now().seconds}_$imageFileName")
        } else null

        val (authorName, authorPhotoUrl) = fetchAuthorDetails(user.uid)

        val post = Story(
            userId = user.uid,
            userName = authorName,
            userProfileUrl = authorPhotoUrl,
            textContent = text,
            contentUrl = imageUrl,
            timestamp = Timestamp.now(),
            isFeed = spaceId == null
        )

        if (spaceId != null) {
            firestore.collection("spaces").document(spaceId).collection("posts").add(post)
        } else {
            firestore.collection("stories").add(post)
        }
    }

    override suspend fun deletePost(postId: String): Result<Unit> = runCatching {
        firestore.collection("stories").document(postId).delete()
    }

    override suspend fun toggleLikePost(postId: String, currentLikes: List<String>): Result<Unit> = runCatching {
        val userId = auth.currentUser?.uid ?: return@runCatching
        val docRef = firestore.collection("stories").document(postId)
        if (currentLikes.contains(userId)) {
            docRef.update(
                "likeCount" to (currentLikes.size - 1).coerceAtLeast(0),
                "likedBy" to currentLikes.filter { it != userId }
            )
        } else {
            docRef.update(
                "likeCount" to currentLikes.size + 1,
                "likedBy" to currentLikes + userId
            )
        }
    }

    override suspend fun toggleBookmarkPost(
        postId: String,
        currentBookmarks: List<String>
    ): Result<Unit> = runCatching {
        val userId = auth.currentUser?.uid ?: return@runCatching
        val docRef = firestore.collection("stories").document(postId)
        if (currentBookmarks.contains(userId)) {
            docRef.update(
                "bookmarkedBy" to currentBookmarks.filter { it != userId }
            )
        } else {
            docRef.update(
                "bookmarkedBy" to currentBookmarks + userId
            )
        }
    }

    override fun getCommentsStream(postId: String): Flow<List<Story.Comment>> = combine(
        firestore.collection("stories").document(postId).collection("comments").snapshots,
        getUsersMapFlow()
    ) { commentsSnapshot, userMap ->
        commentsSnapshot.documents.mapNotNull { doc ->
            try {
                val comment: Story.Comment = doc.data()
                val author = userMap[comment.userId]
                val realProfileUrl = author?.photoUrl?.ifBlank { null } ?: comment.userProfileUrl?.ifBlank { null }
                val realUserName = author?.effectiveName?.ifBlank { null } ?: comment.userName.ifBlank { "Anonymous" }

                comment.copy(
                    commentId = doc.id,
                    userName = realUserName,
                    userProfileUrl = realProfileUrl
                )
            } catch (_: Exception) {
                null
            }
        }
    }

    override suspend fun addComment(postId: String, text: String): Result<Unit> = runCatching {
        val user = auth.currentUser ?: throw IllegalStateException("User not authenticated")
        val (authorName, authorPhotoUrl) = fetchAuthorDetails(user.uid)

        val comment = Story.Comment(
            userId = user.uid,
            storyId = postId,
            userName = authorName,
            userProfileUrl = authorPhotoUrl,
            text = text,
            timestamp = Timestamp.now()
        )
        firestore.collection("stories").document(postId).collection("comments").add(comment)
    }

    override fun getSpacesStream(): Flow<List<Space>> = combine(
        firestore.collection("spaces").snapshots,
        getUsersMapFlow()
    ) { spacesSnapshot, userMap ->
        val spaces = spacesSnapshot.documents.mapNotNull { doc ->
            try {
                val space: Space = doc.data()
                val photoUrls = space.memberIds.mapNotNull { memberUid ->
                    userMap[memberUid]?.photoUrl?.ifBlank { null }
                        ?: "https://ui-avatars.com/api/?name=${userMap[memberUid]?.effectiveName ?: "User"}&background=6366f1&color=fff"
                }
                space.copy(
                    id = doc.id,
                    memberPhotoUrls = photoUrls
                )
            } catch (_: Exception) {
                null
            }
        }
        if (spaces.isEmpty()) {
            DEFAULT_SPACES
        } else {
            spaces
        }
    }

    override fun getSpaceDetailsStream(spaceId: String): Flow<Space?> = combine(
        firestore.collection("spaces").document(spaceId).snapshots,
        getUsersMapFlow()
    ) { doc, userMap ->
        if (doc.exists) {
            try {
                val space: Space = doc.data()
                val photoUrls = space.memberIds.mapNotNull { memberUid ->
                    userMap[memberUid]?.photoUrl?.ifBlank { null }
                        ?: "https://ui-avatars.com/api/?name=${userMap[memberUid]?.effectiveName ?: "User"}&background=6366f1&color=fff"
                }
                space.copy(
                    id = doc.id,
                    memberPhotoUrls = photoUrls
                )
            } catch (_: Exception) {
                DEFAULT_SPACES.find { it.id == spaceId }
            }
        } else {
            DEFAULT_SPACES.find { it.id == spaceId }
        }
    }

    override suspend fun createSpace(
        name: String,
        description: String,
        icon: String,
        isPrivate: Boolean,
        imageBytes: ByteArray?
    ): Result<Space> = runCatching {
        val user = auth.currentUser ?: throw IllegalStateException("User not authenticated")
        val coverUrl = if (imageBytes != null) {
            uploadFile(imageBytes, "space_covers/${user.uid}_${Timestamp.now().seconds}.jpg")
        } else icon

        val space = Space(
            name = name,
            description = description,
            coverImageUrl = coverUrl,
            ownerId = user.uid,
            memberIds = listOf(user.uid),
            createdAt = Timestamp.now()
        )
        val ref = firestore.collection("spaces").add(space)
        space.copy(id = ref.id)
    }

    override suspend fun updateSpace(
        spaceId: String,
        name: String,
        description: String,
        icon: String,
        imageBytes: ByteArray?
    ): Result<Unit> = runCatching {
        val user = auth.currentUser ?: throw IllegalStateException("User not authenticated")
        val coverUrl = if (imageBytes != null) {
            uploadFile(imageBytes, "space_covers/$spaceId/${user.uid}_${Timestamp.now().seconds}.jpg")
        } else icon

        val docRef = firestore.collection("spaces").document(spaceId)
        val doc = docRef.get()
        if (doc.exists) {
            docRef.update(
                "name" to name,
                "description" to description,
                "coverImageUrl" to coverUrl
            )
        } else {
            val defaultSpace = DEFAULT_SPACES.find { it.id == spaceId }
            val newSpace = (defaultSpace ?: Space(id = spaceId, name = name, description = description, coverImageUrl = coverUrl)).copy(
                name = name,
                description = description,
                coverImageUrl = coverUrl,
                ownerId = defaultSpace?.ownerId?.ifBlank { user.uid } ?: user.uid,
                memberIds = (defaultSpace?.memberIds ?: emptyList()) + user.uid
            )
            docRef.set(newSpace)
        }
    }

    override suspend fun joinSpace(spaceId: String): Result<Unit> = runCatching {
        val userId = auth.currentUser?.uid ?: return@runCatching
        val docRef = firestore.collection("spaces").document(spaceId)
        val doc = docRef.get()
        if (doc.exists) {
            docRef.update("memberIds" to FieldValue.arrayUnion(userId))
        } else {
            val defaultSpace = DEFAULT_SPACES.find { it.id == spaceId }
            if (defaultSpace != null) {
                val updatedMembers = if (defaultSpace.memberIds.contains(userId)) defaultSpace.memberIds else defaultSpace.memberIds + userId
                docRef.set(defaultSpace.copy(memberIds = updatedMembers))
            }
        }
    }

    override suspend fun leaveSpace(spaceId: String): Result<Unit> = runCatching {
        val userId = auth.currentUser?.uid ?: return@runCatching
        val docRef = firestore.collection("spaces").document(spaceId)
        val doc = docRef.get()
        if (doc.exists) {
            docRef.update("memberIds" to FieldValue.arrayRemove(userId))
        }
    }

    override fun getSpaceMessagesStream(spaceId: String): Flow<List<SpaceMessage>> = combine(
        firestore.collection("spaces").document(spaceId).collection("messages").snapshots,
        getUsersMapFlow()
    ) { messagesSnapshot, userMap ->
        messagesSnapshot.documents.mapNotNull { doc ->
            try {
                val msg: SpaceMessage = doc.data()
                val author = userMap[msg.authorId]
                val realAvatarUrl = author?.photoUrl?.ifBlank { null } ?: msg.authorAvatarUrl.ifBlank { null } ?: ""
                val realAuthorName = author?.effectiveName?.ifBlank { null } ?: msg.authorName.ifBlank { "Anonymous" }

                msg.copy(
                    id = doc.id,
                    authorName = realAuthorName,
                    authorAvatarUrl = realAvatarUrl
                )
            } catch (_: Exception) {
                null
            }
        }
    }

    override suspend fun sendSpaceMessage(spaceId: String, text: String): Result<Unit> = runCatching {
        val user = auth.currentUser ?: throw IllegalStateException("User not authenticated")
        val (authorName, authorPhotoUrl) = fetchAuthorDetails(user.uid)

        val message = SpaceMessage(
            spaceId = spaceId,
            authorId = user.uid,
            authorName = authorName,
            authorAvatarUrl = authorPhotoUrl ?: "",
            text = text,
            timestamp = Timestamp.now()
        )
        firestore.collection("spaces").document(spaceId).collection("messages").add(message)
    }

    override fun getUserProfileStream(userId: String): Flow<User?> = firestore
        .collection("users")
        .document(userId)
        .snapshots
        .map { doc ->
            val firebaseUser = auth.currentUser
            val fbUser = if (firebaseUser != null && firebaseUser.uid == userId) {
                User(
                    uid = firebaseUser.uid,
                    displayName = firebaseUser.displayName,
                    username = firebaseUser.displayName ?: firebaseUser.email?.substringBefore("@"),
                    email = firebaseUser.email,
                    photoUrl = firebaseUser.photoURL
                )
            } else null

            if (doc.exists) {
                try {
                    val parsed = doc.data<User>()
                    parsed.copy(
                        uid = if (parsed.uid.isBlank()) userId else parsed.uid,
                        displayName = parsed.displayName?.ifBlank { null } ?: fbUser?.displayName,
                        username = parsed.username?.ifBlank { null } ?: fbUser?.username,
                        email = parsed.email?.ifBlank { null } ?: fbUser?.email,
                        photoUrl = parsed.photoUrl?.ifBlank { null } ?: fbUser?.photoUrl
                    )
                } catch (_: Exception) {
                    fbUser ?: User(
                        uid = userId,
                        displayName = "Guest User"
                    )
                }
            } else {
                fbUser ?: User(
                    uid = userId,
                    displayName = "Guest User"
                )
            }
        }

    override suspend fun updateUserProfile(
        user: User,
        imageBytes: ByteArray?,
        imageFileName: String?
    ): Result<Unit> = runCatching {
        val finalAvatarUrl = if (imageBytes != null && imageFileName != null) {
            uploadFile(imageBytes, "user_avatars/${user.uid}_$imageFileName")
        } else user.photoUrl

        val updatedUser = user.copy(photoUrl = finalAvatarUrl)
        firestore.collection("users").document(user.uid).set(updatedUser)

        try {
            auth.currentUser?.updateProfile(
                displayName = updatedUser.displayName,
                photoUrl = finalAvatarUrl
            )
        } catch (_: Exception) {
            // ignore if Auth profile update fails
        }
    }

    override fun getCommunityFarmersStream(): Flow<List<User>> = firestore
        .collection("users")
        .snapshots
        .map { snapshot ->
            snapshot.documents.mapNotNull { doc ->
                try {
                    val user: User = doc.data()
                    user.copy(uid = if (user.uid.isBlank()) doc.id else user.uid)
                } catch (_: Exception) {
                    null
                }
            }
        }
}


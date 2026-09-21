package org.vaulture.project.features.space.presentation.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.gitlive.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.vaulture.project.core.domain.User
import org.vaulture.project.features.space.domain.model.Space
import org.vaulture.project.features.space.domain.model.SpaceMessage
import org.vaulture.project.features.space.domain.model.Story
import org.vaulture.project.features.space.domain.usecase.SpaceUseCases

data class SpaceUiState(
    val userName: String = "",
    val userAvatarUrl: String? = null,
    val stories: List<Story> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

enum class ProfileFilter {
    MY_POSTS,
    LIKED,
    BOOKMARKED
}

open class SpaceViewModel(
    private val spaceUseCases: SpaceUseCases,
    val auth: FirebaseAuth
) : ViewModel() {

    private var feedsListenerJob: Job? = null
    private var spacesListenerJob: Job? = null
    private var postsListenerJob: Job? = null
    private var userProfileListenerJob: Job? = null
    private var chatListenerJob: Job? = null

    private val _uiState = MutableStateFlow(SpaceUiState(isLoading = true))
    val uiState: StateFlow<SpaceUiState> = _uiState.asStateFlow()

    private val _searchResults = MutableStateFlow<List<User>>(emptyList())
    val searchResults: StateFlow<List<User>> = _searchResults.asStateFlow()

    private val _feeds = MutableStateFlow<List<Story>>(emptyList())
    val feeds: StateFlow<List<Story>> = _feeds.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _spaces = MutableStateFlow<List<Space>>(emptyList())
    val spaces: StateFlow<List<Space>> = _spaces.asStateFlow()

    private val _isLoadingSpaces = MutableStateFlow(true)
    val isLoadingSpaces: StateFlow<Boolean> = _isLoadingSpaces.asStateFlow()

    val spaceName = mutableStateOf("")
    val spaceDescription = mutableStateOf("")

    private val _isCreatingSpace = MutableStateFlow(false)
    val isCreatingSpace: StateFlow<Boolean> = _isCreatingSpace.asStateFlow()

    private val _currentSpace = MutableStateFlow<Space?>(null)
    val currentSpace: StateFlow<Space?> = _currentSpace.asStateFlow()

    private val _spacePosts = MutableStateFlow<List<Story>>(emptyList())
    val spacePosts: StateFlow<List<Story>> = _spacePosts.asStateFlow()

    private val _spaceMessages = MutableStateFlow<List<SpaceMessage>>(emptyList())
    val spaceMessages: StateFlow<List<SpaceMessage>> = _spaceMessages.asStateFlow()

    val newMessageText = mutableStateOf("")
    val isLoadingPosts = MutableStateFlow(false)

    private val _filteredSpaces = MutableStateFlow<List<Space>>(emptyList())
    val filteredSpaces: StateFlow<List<Space>> = _filteredSpaces.asStateFlow()

    private val _filteredFeeds = MutableStateFlow<List<Story>>(emptyList())
    val filteredFeeds: StateFlow<List<Story>> = _filteredFeeds.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching.asStateFlow()

    private val _activeSpaceId = MutableStateFlow<String?>(null)
    val activeSpaceId: StateFlow<String?> = _activeSpaceId.asStateFlow()

    private val _userStories = MutableStateFlow<List<Story>>(emptyList())
    val userStories: StateFlow<List<Story>> = _userStories.asStateFlow()

    private val _userLikedStories = MutableStateFlow<List<Story>>(emptyList())
    val userLikedStories: StateFlow<List<Story>> = _userLikedStories.asStateFlow()

    private val _userBookmarkedStories = MutableStateFlow<List<Story>>(emptyList())
    val userBookmarkedStories: StateFlow<List<Story>> = _userBookmarkedStories.asStateFlow()

    private val _isLoadingProfileData = MutableStateFlow(false)
    val isLoadingProfileData: StateFlow<Boolean> = _isLoadingProfileData.asStateFlow()

    private val _isUpdatingProfile = MutableStateFlow(false)
    val isUpdatingProfile: StateFlow<Boolean> = _isUpdatingProfile.asStateFlow()

    private val _targetUserProfile = MutableStateFlow<User?>(null)
    val targetUserProfile: StateFlow<User?> = _targetUserProfile.asStateFlow()

    private val _isFollowing = MutableStateFlow<Map<String, Boolean>>(emptyMap())
    val isFollowing: StateFlow<Map<String, Boolean>> = _isFollowing.asStateFlow()

    private val _userProfile = MutableStateFlow<User?>(null)
    val userProfile: StateFlow<User?> = _userProfile.asStateFlow()

    private val _commentsState = MutableStateFlow<List<Story.Comment>>(emptyList())
    val commentsState: StateFlow<List<Story.Comment>> = _commentsState.asStateFlow()

    private var communityFarmersJob: Job? = null
    private val _communityFarmers = MutableStateFlow<List<User>>(emptyList())
    val communityFarmers: StateFlow<List<User>> = _communityFarmers.asStateFlow()

    init {
        listenToStoriesAndPosts()
        listenToSpaces()
        listenToCommunityFarmers()
        viewModelScope.launch {
            auth.authStateChanged.collect { firebaseUser ->
                if (firebaseUser != null) {
                    loadUserProfile(firebaseUser.uid)
                } else {
                    _userProfile.value = null
                }
            }
        }
    }

    private fun listenToCommunityFarmers() {
        communityFarmersJob?.cancel()
        communityFarmersJob = spaceUseCases.getCommunityFarmersStream()
            .onEach { farmers ->
                _communityFarmers.value = farmers
            }
            .catch {
                // Ignore or handle
            }
            .launchIn(viewModelScope)
    }

    private fun listenToStoriesAndPosts() {
        feedsListenerJob?.cancel()
        feedsListenerJob = spaceUseCases.getPostsStream()
            .onEach { postsList ->
                _feeds.value = postsList
                _filteredFeeds.value = postsList
                _isLoading.value = false
            }
            .catch { e ->
                _error.value = e.message
                _isLoading.value = false
            }
            .launchIn(viewModelScope)

        spaceUseCases.getStoriesStream()
            .onEach { storiesList ->
                _uiState.update { it.copy(stories = storiesList, isLoading = false) }
            }
            .catch { e ->
                _uiState.update { it.copy(error = e.message, isLoading = false) }
            }
            .launchIn(viewModelScope)
    }

    private fun listenToSpaces() {
        spacesListenerJob?.cancel()
        spacesListenerJob = spaceUseCases.getSpacesStream()
            .onEach { spaceList ->
                _spaces.value = spaceList
                _filteredSpaces.value = spaceList
                _isLoadingSpaces.value = false
            }
            .catch {
                _isLoadingSpaces.value = false
            }
            .launchIn(viewModelScope)
    }

    fun loadUserProfile(userId: String? = null) {
        val targetUid = userId ?: auth.currentUser?.uid
        if (targetUid == null) {
            val fbUser = auth.currentUser
            if (fbUser != null) {
                val fallbackUser = User(
                    uid = fbUser.uid,
                    displayName = fbUser.displayName,
                    username = fbUser.displayName ?: fbUser.email?.substringBefore("@"),
                    email = fbUser.email,
                    photoUrl = fbUser.photoURL
                )
                _userProfile.value = fallbackUser
                _uiState.update {
                    it.copy(
                        userName = fallbackUser.effectiveName,
                        userAvatarUrl = fallbackUser.photoUrl
                    )
                }
            }
            return
        }

        userProfileListenerJob?.cancel()
        userProfileListenerJob = spaceUseCases.getUserProfileStream(targetUid)
            .onEach { user ->
                _userProfile.value = user
                _uiState.update {
                    it.copy(
                        userName = user?.effectiveName ?: "Guest User",
                        userAvatarUrl = user?.photoUrl
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    fun loadPublicProfile(userId: String) {
        spaceUseCases.getUserProfileStream(userId)
            .onEach { user ->
                _targetUserProfile.value = user
            }
            .launchIn(viewModelScope)
    }

    fun toggleFollow(userId: String) {
        _isFollowing.update { map ->
            val current = map[userId] ?: false
            map + (userId to !current)
        }
    }

    fun toggleLike(story: Story) {
        viewModelScope.launch {
            spaceUseCases.toggleLikePost(story.storyId, story.likedBy)
        }
    }

    fun toggleLikeSpace(story: Story) = toggleLike(story)
    fun toggleLikeSpace(story: Story, spaceId: String) = toggleLike(story)

    fun toggleBookmark(story: Story) {
        viewModelScope.launch {
            spaceUseCases.toggleBookmarkPost(story.storyId, story.bookmarkedBy)
        }
    }

    fun toggleBookmarkSpace(story: Story) = toggleBookmark(story)
    fun toggleBookmarkSpace(story: Story, spaceId: String) = toggleBookmark(story)

    fun getCommentsForStory(storyId: String): StateFlow<List<Story.Comment>> {
        loadComments(storyId)
        return commentsState
    }

    fun getCommentsForPost(postId: String): StateFlow<List<Story.Comment>> {
        loadComments(postId)
        return commentsState
    }

    fun getCommentsForPost(spaceId: String, postId: String): StateFlow<List<Story.Comment>> =
        getCommentsForPost(postId)

    fun loadComments(postId: String) {
        spaceUseCases.getCommentsStream(postId)
            .onEach { commentsList ->
                _commentsState.value = commentsList
            }
            .launchIn(viewModelScope)
    }

    fun addComment(postId: String, text: String) {
        if (text.isBlank()) return
        viewModelScope.launch {
            spaceUseCases.addComment(postId, text)
        }
    }

    fun addCommentSpace(postId: String, text: String) = addComment(postId, text)
    fun addCommentSpace(spaceId: String, postId: String, text: String) = addComment(postId, text)

    fun addStory(
        title: String = "",
        mediaBytes: ByteArray? = null,
        mediaFileName: String? = null,
        mediaContent: ByteArray? = null,
        thumbnailContent: ByteArray? = null,
        textContent: String? = null,
        contentType: Story.ContentType = Story.ContentType.PHOTO,
        isFeed: Boolean = false,
        aspectRatio: Float = 1.0f,
        visibility: Story.Visibility = Story.Visibility.PUBLIC,
        onProgress: (String) -> Unit = {},
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        val finalTitle = textContent ?: title
        val finalBytes = mediaContent ?: mediaBytes
        val finalFileName = mediaFileName ?: "story_media_${contentType.name.lowercase()}"

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            onProgress("Uploading story...")
            spaceUseCases.createStory(finalTitle, finalBytes, finalFileName)
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false) }
                    onSuccess()
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.message) }
                    onError(e.message ?: "Failed to upload story")
                }
        }
    }

    fun createPost(
        text: String,
        spaceId: String? = null,
        spaceName: String? = null,
        imageBytes: ByteArray? = null,
        imageFileName: String? = null,
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        if (text.isBlank()) return
        viewModelScope.launch {
            spaceUseCases.createPost(text, spaceId, spaceName, imageBytes, imageFileName)
                .onSuccess { onSuccess() }
                .onFailure { e -> onError(e.message ?: "Failed to create post") }
        }
    }

    fun deletePost(postId: String) {
        viewModelScope.launch {
            spaceUseCases.deletePost(postId)
        }
    }

    fun createSpace(
        name: String = spaceName.value,
        description: String = spaceDescription.value,
        icon: String = "✨",
        isPrivate: Boolean = false,
        coverImageBytes: ByteArray? = null,
        onSuccess: (String) -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        viewModelScope.launch {
            _isCreatingSpace.value = true
            spaceUseCases.createSpace(name, description, icon, isPrivate, coverImageBytes)
                .onSuccess { space ->
                    _isCreatingSpace.value = false
                    onSuccess(space.id)
                }
                .onFailure { e ->
                    _isCreatingSpace.value = false
                    onError(e.message ?: "Failed to create space")
                }
        }
    }

    fun updateSpace(
        spaceId: String,
        name: String,
        description: String,
        icon: String,
        imageBytes: ByteArray? = null,
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        viewModelScope.launch {
            _isCreatingSpace.value = true
            spaceUseCases.updateSpace(spaceId, name, description, icon, imageBytes)
                .onSuccess {
                    _isCreatingSpace.value = false
                    onSuccess()
                }
                .onFailure { e ->
                    _isCreatingSpace.value = false
                    onError(e.message ?: "Failed to update space")
                }
        }
    }

    fun joinSpace(spaceId: String) {
        viewModelScope.launch {
            spaceUseCases.joinSpace(spaceId)
        }
    }

    fun leaveSpace(spaceId: String) {
        viewModelScope.launch {
            spaceUseCases.leaveSpace(spaceId)
        }
    }

    private var currentSpaceJob: Job? = null

    fun selectSpace(spaceId: String) {
        _activeSpaceId.value = spaceId

        currentSpaceJob?.cancel()
        currentSpaceJob = spaceUseCases.getSpaceDetailsStream(spaceId)
            .onEach { spaceDoc ->
                _currentSpace.value = spaceDoc
            }
            .launchIn(viewModelScope)

        postsListenerJob?.cancel()
        postsListenerJob = spaceUseCases.getSpacePostsStream(spaceId)
            .onEach { posts ->
                _spacePosts.value = posts
            }
            .launchIn(viewModelScope)

        chatListenerJob?.cancel()
        chatListenerJob = spaceUseCases.getSpaceMessagesStream(spaceId)
            .onEach { messages ->
                _spaceMessages.value = messages
            }
            .launchIn(viewModelScope)
    }

    fun loadSpaceDetails(spaceId: String) = selectSpace(spaceId)
    fun listenForChatMessages(spaceId: String) = selectSpace(spaceId)

    fun clearSpaceDetails() {
        currentSpaceJob?.cancel()
        _activeSpaceId.value = null
        _currentSpace.value = null
        _spacePosts.value = emptyList()
        _spaceMessages.value = emptyList()
    }

    fun sendSpaceMessage(spaceId: String, text: String) {
        if (text.isBlank()) return
        viewModelScope.launch {
            spaceUseCases.sendSpaceMessage(spaceId, text)
            newMessageText.value = ""
        }
    }

    fun sendChatMessage(spaceId: String, text: String) = sendSpaceMessage(spaceId, text)
    fun sendChatMessage(spaceId: String) = sendSpaceMessage(spaceId, newMessageText.value)

    fun loadProfileData(filter: ProfileFilter = ProfileFilter.MY_POSTS) {
        val uid = auth.currentUser?.uid ?: return
        _isLoadingProfileData.value = true
        when (filter) {
            ProfileFilter.MY_POSTS -> {
                spaceUseCases.getPostsStream()
                    .onEach { posts ->
                        _userStories.value = posts.filter { it.userId == uid }
                        _isLoadingProfileData.value = false
                    }
                    .launchIn(viewModelScope)
            }
            ProfileFilter.LIKED -> {
                spaceUseCases.getPostsStream()
                    .onEach { posts ->
                        _userLikedStories.value = posts.filter { it.likedBy.contains(uid) }
                        _isLoadingProfileData.value = false
                    }
                    .launchIn(viewModelScope)
            }
            ProfileFilter.BOOKMARKED -> {
                spaceUseCases.getPostsStream()
                    .onEach { posts ->
                        _userBookmarkedStories.value = posts.filter { it.bookmarkedBy.contains(uid) }
                        _isLoadingProfileData.value = false
                    }
                    .launchIn(viewModelScope)
            }
        }
    }

    fun updatePortfolio(
        displayName: String,
        newPhotoBytes: ByteArray? = null,
        bio: String? = null,
        farmName: String? = null,
        country: String? = null,
        region: String? = null,
        farmSize: String? = null,
        primaryCrops: List<String> = emptyList(),
        farmingType: String? = null,
        irrigationType: String? = null,
        soilType: String? = null,
        experienceLevel: String? = null,
        certifications: List<String> = emptyList(),
        mainChallenges: List<String> = emptyList(),
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        val current = userProfile.value ?: return
        val updated = current.copy(
            displayName = displayName,
            username = displayName,
            bio = bio ?: current.bio,
            farmName = farmName ?: current.farmName,
            country = country ?: current.country,
            region = region ?: current.region,
            farmSize = farmSize ?: current.farmSize,
            primaryCrops = if (primaryCrops.isNotEmpty()) primaryCrops else current.primaryCrops,
            farmingType = farmingType ?: current.farmingType,
            irrigationType = irrigationType ?: current.irrigationType,
            soilType = soilType ?: current.soilType,
            experienceLevel = experienceLevel ?: current.experienceLevel,
            certifications = if (certifications.isNotEmpty()) certifications else current.certifications,
            mainChallenges = if (mainChallenges.isNotEmpty()) mainChallenges else current.mainChallenges
        )
        _isUpdatingProfile.value = true
        viewModelScope.launch {
            spaceUseCases.updateUserProfile(updated, newPhotoBytes, "profile.jpg")
                .onSuccess {
                    _isUpdatingProfile.value = false
                    _userProfile.value = updated
                    onSuccess()
                }
                .onFailure { e ->
                    _isUpdatingProfile.value = false
                    onError(e.message ?: "Update failed")
                }
        }
    }


    fun updateUserProfile(
        username: String?,
        displayName: String?,
        bio: String?,
        imageBytes: ByteArray? = null,
        imageFileName: String? = null,
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        val current = userProfile.value ?: return
        val updated = current.copy(
            username = username ?: current.username,
            displayName = displayName ?: current.displayName
        )
        viewModelScope.launch {
            spaceUseCases.updateUserProfile(updated, imageBytes, imageFileName)
                .onSuccess { onSuccess() }
                .onFailure { e -> onError(e.message ?: "Failed to update profile") }
        }
    }

    fun searchSpaces(query: String) {
        if (query.isBlank()) {
            _filteredSpaces.value = _spaces.value
            _filteredFeeds.value = _feeds.value
            _isSearching.value = false
            return
        }
        _isSearching.value = true
        val lowercaseQuery = query.lowercase()
        _filteredSpaces.value = _spaces.value.filter {
            it.name.lowercase().contains(lowercaseQuery) ||
                    it.description.lowercase().contains(lowercaseQuery)
        }
        _filteredFeeds.value = _feeds.value.filter {
            (it.textContent?.lowercase()?.contains(lowercaseQuery) == true) ||
                    (it.userName.lowercase().contains(lowercaseQuery))
        }
    }

    fun onSearchQueryChanged(query: String) = searchSpaces(query)
}

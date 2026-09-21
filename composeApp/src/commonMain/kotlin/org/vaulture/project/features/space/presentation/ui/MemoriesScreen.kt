package org.vaulture.project.features.space.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.SearchOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import org.vaulture.project.core.theme.PoppinsTypography
import org.vaulture.project.features.space.domain.model.Story
import org.vaulture.project.navigation.Routes
import org.vaulture.project.presentation.ui.components.PostItem
import org.vaulture.project.features.space.presentation.viewmodel.SpaceViewModel

@Composable
fun MemoriesScreen(
    stories: List<Story>,
    viewModel: SpaceViewModel,
    modifier: Modifier = Modifier,
    onCommentClick: (String) -> Unit,
    onBookmarkClick: (String) -> Unit,
    navController: NavController
) {
    val followingMap by viewModel.isFollowing.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val isSearching by viewModel.isSearching.collectAsState()
    val currentUser by viewModel.auth.authStateChanged.collectAsState(initial = null)
    val currentUserId = currentUser?.uid ?: ""

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        when {
            isLoading && stories.isEmpty() -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(strokeWidth = 3.dp)
                }
            }

            stories.isNotEmpty() -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(
                        items = stories,
                        key = { it.storyId }
                    ) { post ->
                       PostItem(
                            post = post,
                            currentUserId = currentUserId,
                            isFollowing = followingMap[post.userId] ?: false,
                            onFollowClick = { targetId -> viewModel.toggleFollow(targetId) },
                            onLikeClick = { viewModel.toggleLike(post) },
                            onCommentClick = { onCommentClick(post.storyId) },
                           onProfileClick = { userId ->
                               navController.navigate(Routes.PORTFOLIO(userId))
                           },
                            onBookmarkClick = { viewModel.toggleBookmark(post) },
                            onOptionClick = {}
                        )
                        ListItemDivider()
                    }
                }
            }

            error != null -> {
                ErrorPlaceholder(
                    errorMessage = error!!
                )
            }

            else -> {
                EmptySearchPlaceholder()
            }
        }

        if (isSearching) {
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth().align(Alignment.TopCenter).height(2.dp),
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun EmptySearchPlaceholder() {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Outlined.SearchOff,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
        )
        Spacer(Modifier.height(16.dp))
        Text(
            "No Field Updates Found",
            style = PoppinsTypography().headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Text(
            "Try a different keyword or share a new crop observation.",
            textAlign = TextAlign.Center,
            style = PoppinsTypography().bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}


@Composable
private fun ErrorPlaceholder(errorMessage: String) {
    val displayMessage = if (errorMessage.contains("FAILED_PRECONDITION") || errorMessage.contains("http") || errorMessage.contains("index")) {
        "Unable to load feed at this time. Please try again in a moment."
    } else {
        errorMessage
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Outlined.ErrorOutline,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.error
        )
        Text(
            text = "Connection Interrupted",
            style = PoppinsTypography().headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.error
        )
        Text(
            text = displayMessage,
            style = PoppinsTypography().bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

@Composable
fun ListItemDivider() {
    HorizontalDivider(
        modifier = Modifier.fillMaxWidth(),
        thickness = 0.5.dp,
        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
    )
}

package org.vaulture.project.presentation.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Comment
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import org.vaulture.project.core.theme.PoppinsTypography
import org.vaulture.project.core.util.NativeVideoPlayer
import org.vaulture.project.core.util.formatTimestamp
import org.vaulture.project.features.space.domain.model.Story


@Composable
fun PostItem(
    post: Story,
    currentUserId: String,
    isFollowing: Boolean,
    onFollowClick: (String) -> Unit,
    onLikeClick: (String) -> Unit,
    onCommentClick: (String) -> Unit,
    onBookmarkClick: (String) -> Unit,
    onProfileClick: (String) -> Unit,
    onOptionClick: (String) -> Unit = {},
    onEditClick: ((Story) -> Unit)? = null,
    onDeleteClick: ((Story) -> Unit)? = null
) {
    val isLiked = post.likedBy.contains(currentUserId)
    val isBookmarked = post.bookmarkedBy.contains(currentUserId)


    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .padding(vertical = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val avatarModel = post.userProfileUrl?.ifBlank { null }
                ?: "https://ui-avatars.com/api/?name=${post.userName.ifBlank { "User" }}&background=6366f1&color=fff"
            AsyncImage(
                model = avatarModel,
                contentDescription = null,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .clickable { onProfileClick(post.userId) },
                contentScale = ContentScale.Crop,
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = post.userName,
                    style = PoppinsTypography().titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.clickable { onProfileClick(post.userId) }
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = formatTimestamp(post.timestamp),
                        style = PoppinsTypography().bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (post.isEdited) {
                        Text(
                            text = " • Edited",
                            style = PoppinsTypography().labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            if (post.userId != currentUserId) {
                TextButton(
                    onClick = { onFollowClick(post.userId) },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = if (isFollowing) MaterialTheme.colorScheme.outline
                        else MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(
                        imageVector = if (isFollowing) Icons.Default.Check else Icons.Default.Add,
                        modifier = Modifier.size(16.dp),
                        contentDescription = null
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(if (isFollowing) "Connected" else "Connect")
                }
            }

            var showOptionsMenu by remember { mutableStateOf(false) }

            Box {
                IconButton(onClick = { showOptionsMenu = true }) {
                    Icon(
                        Icons.Default.MoreVert,
                        contentDescription = "Options",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                DropdownMenu(
                    expanded = showOptionsMenu,
                    onDismissRequest = { showOptionsMenu = false }
                ) {
                    if (post.userId == currentUserId) {
                        DropdownMenuItem(
                            text = { Text("Edit Memory") },
                            leadingIcon = {
                                Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                            },
                            onClick = {
                                showOptionsMenu = false
                                onEditClick?.invoke(post)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Delete Memory", color = MaterialTheme.colorScheme.error) },
                            leadingIcon = {
                                Icon(Icons.Default.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                            },
                            onClick = {
                                showOptionsMenu = false
                                onDeleteClick?.invoke(post)
                            }
                        )
                    }
                    DropdownMenuItem(
                        text = { Text("Details") },
                        leadingIcon = {
                            Icon(Icons.Default.Info, contentDescription = null, modifier = Modifier.size(18.dp))
                        },
                        onClick = {
                            showOptionsMenu = false
                            onOptionClick(post.storyId)
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (!post.textContent.isNullOrBlank()) {
            var isExpanded by remember { mutableStateOf(false) }
            val textLimit = 150

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .animateContentSize()
            ) {
                val annotatedText = buildAnnotatedString {
                    if (post.textContent.length > textLimit && !isExpanded) {
                        append(post.textContent.take(textLimit))
                        withStyle(style = SpanStyle(
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )) {
                            append(" ... See more")
                        }
                    } else {
                        append(post.textContent)
                        if (isExpanded && post.textContent.length > textLimit) {
                            withStyle(style = SpanStyle(
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )) {
                                append(" Show less")
                            }
                        }
                    }
                }

                Text(
                    text = annotatedText,
                    style = PoppinsTypography().bodyMedium,
                    lineHeight = 20.sp,
                    modifier = Modifier.clickable {
                        if (post.textContent.length > textLimit) isExpanded = !isExpanded
                    }
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
        }


        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.07f))
        ) {
            when (post.contentType) {
                Story.ContentType.PHOTO -> if (!post.contentUrl.isNullOrEmpty()) {
                    AsyncImage(
                        model = post.contentUrl,
                        contentDescription = "Post image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 330.dp)
                            .wrapContentHeight(),
                        contentScale = ContentScale.FillWidth,
                    )
                }
                Story.ContentType.VIDEO -> if (!post.contentUrl.isNullOrEmpty()) {
                    VideoPlayerPlaceholder(
                        thumbnailUrl = post.thumbnailUrl,
                        aspectRatio = post.aspectRatio ?: (16f / 9f),
                        videoUrl = post.contentUrl,
                        duration = post.duration
                    )
                }
                else -> {}
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Favorite,
                contentDescription = null,
                modifier = Modifier.size(14.dp),
                tint = if (post.isLiked) Color.Red else MaterialTheme.colorScheme.outline
            )
            Spacer(Modifier.width(4.dp))
            Text(
                "${post.likeCount}",
                style = PoppinsTypography().labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.weight(1f))
            Text(
                "${post.commentCount} comments",
                style = PoppinsTypography().labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 16.dp),
            thickness = 0.5.dp,
            color = MaterialTheme.colorScheme.outlineVariant
        )

        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            PostActionButton(
                icon = if (isLiked) Icons.Filled.Favorite else Icons.Default.FavoriteBorder,
                text = "Support",
                onClick = { onLikeClick(post.storyId) },
                isActivated = isLiked
            )
            PostActionButton(
                icon = Icons.AutoMirrored.Filled.Comment,
                text = "Reply",
                onClick = { onCommentClick(post.storyId) }
            )
            PostActionButton(
                icon = if (isBookmarked) Icons.Filled.Bookmark else Icons.Default.BookmarkBorder,
                text = "Save",
                onClick = { onBookmarkClick(post.storyId) },
                isActivated = isBookmarked
            )
        }
    }

}

@Composable
fun PostActionButton(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit,
    isActivated: Boolean = false
) {
    val color = if (isActivated) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant

    TextButton(
        onClick = onClick,
        modifier = Modifier.height(40.dp),
        colors = ButtonDefaults.textButtonColors(contentColor = color)
    ) {
        Icon(
            icon,
            contentDescription = null,
            modifier = Modifier
                .size(20.dp)
        )
        Spacer(Modifier.width(6.dp))
        Text(
            text,
            style = PoppinsTypography().labelMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun VideoPlayerPlaceholder(
    thumbnailUrl: String?,
    modifier: Modifier = Modifier,
    aspectRatio: Float = 16f / 9f,
    videoUrl: String? = null,
    duration: String? = null
) {
    var isPlaying by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(aspectRatio)
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        // Keep the preview thumbnail always rendered underneath so it previews as it plays
        if (thumbnailUrl != null) {
            AsyncImage(
                model = thumbnailUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize().alpha(0.9f)
            )
        }

        if (isPlaying && !videoUrl.isNullOrBlank()) {
            NativeVideoPlayer(
                url = videoUrl,
                modifier = Modifier.fillMaxSize(),
                autoPlay = true
            )
            IconButton(
                onClick = { isPlaying = false },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                    .size(28.dp)
            ) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "Stop Video",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
        } else {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.9f),
                modifier = Modifier
                    .size(56.dp)
                    .clickable {
                        if (!videoUrl.isNullOrBlank()) {
                            isPlaying = true
                        }
                    }
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Play Video",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.padding(12.dp).fillMaxSize()
                )
            }

            if (!duration.isNullOrBlank()) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color.Black.copy(alpha = 0.75f),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp)
                ) {
                    Text(
                        text = duration,
                        color = Color.White,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                    )
                }
            }
        }
    }
}

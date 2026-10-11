package org.vaulture.project.presentation.ui.screens.space

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import org.vaulture.project.core.domain.MediaFile
import org.vaulture.project.core.theme.PoppinsTypography
import org.vaulture.project.core.util.ImagePicker
import org.vaulture.project.core.util.NativeVideoPlayer
import org.vaulture.project.core.util.VideoPicker
import org.vaulture.project.features.space.domain.model.Story
import org.vaulture.project.features.space.presentation.viewmodel.SpaceViewModel
import kotlin.text.forEach
import kotlin.text.lowercase

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddStoryScreen(
    viewModel: SpaceViewModel,
    onStoryAdded: () -> Unit,
    onCancel: () -> Unit
) {
    var textContent by remember { mutableStateOf("") }
    var selectedMediaFile by remember { mutableStateOf<MediaFile?>(null) }
    var isFeed by remember { mutableStateOf(true) }

    var isLoading by remember { mutableStateOf(false) }
    var progressMessage by remember { mutableStateOf<String?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showImagePicker by remember { mutableStateOf(false) }
    var showVideoPicker by remember { mutableStateOf(false) }
    var selectedVisibility by remember { mutableStateOf(Story.Visibility.PUBLIC) }

    ImagePicker(
        show = showImagePicker,
        onImageSelected = { imageData ->
            showImagePicker = false
            if (imageData != null) {
                selectedMediaFile = MediaFile(
                    content = imageData,
                    thumbnail = null,
                    type = Story.ContentType.PHOTO,
                    aspectRatio = 1f
                )
            }
        }
    )

    VideoPicker(
        show = showVideoPicker,
        onVideoSelected = { videoBytes, metadata ->
            showVideoPicker = false
            if (videoBytes != null) {
                selectedMediaFile = MediaFile(
                    content = videoBytes,
                    thumbnail = metadata?.thumbnailBytes,
                    type = Story.ContentType.VIDEO,
                    aspectRatio = metadata?.aspectRatio ?: 1.777f,
                    duration = metadata?.durationText,
                    localUri = metadata?.localUri
                )
            }
        }
    )

    val handleAddStoryClick: () -> Unit = {
        isLoading = true
        errorMessage = null
        progressMessage = "Publishing field update..."

        val finalContentType = selectedMediaFile?.type ?: Story.ContentType.TEXT

        if (finalContentType == Story.ContentType.TEXT && textContent.isBlank()) {
            errorMessage = "Share field observations or a crop photo to proceed."
            isLoading = false
        } else {
            viewModel.addStory(
                mediaContent = selectedMediaFile?.content,
                thumbnailContent = selectedMediaFile?.thumbnail,
                textContent = textContent,
                contentType = finalContentType,
                isFeed = isFeed,
                aspectRatio = selectedMediaFile?.aspectRatio ?: 1f,
                visibility = selectedVisibility,
                duration = selectedMediaFile?.duration,
                onProgress = { progressMessage = it },
                onSuccess = {
                    isLoading = false
                    onStoryAdded()
                },
                onError = {
                    isLoading = false
                    errorMessage = it
                }
            )
        }
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val isWide = maxWidth > 920.dp

        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            "Share Field Update",
                            style = PoppinsTypography().titleLarge.copy(fontWeight = FontWeight.Bold)
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onCancel) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                null
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        titleContentColor = MaterialTheme.colorScheme.onBackground,
                        navigationIconContentColor = MaterialTheme.colorScheme.onBackground
                    )
                )
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = if (isWide) 0.dp else 24.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Column(
                        modifier = Modifier
                            .widthIn(max = 600.dp)
                            .padding(vertical = 32.dp),
                        verticalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        OutlinedTextField(
                            value = textContent,
                            onValueChange = { textContent = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 160.dp),
                            placeholder = {
                                Text(
                                "What field updates or crop observations would you like to share?",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            },
                            shape = RoundedCornerShape(20.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                            )
                        )

                        AnimatedVisibility(visible = selectedMediaFile != null) {
                            selectedMediaFile?.let { file ->
                                var isPlayingPreview by remember(file) { mutableStateOf(false) }

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .aspectRatio(if (file.aspectRatio in 0.5f..2.5f) file.aspectRatio else 1.77f)
                                        .clip(RoundedCornerShape(24.dp))
                                        .background(MaterialTheme.colorScheme.surfaceVariant),
                                    contentAlignment = Alignment.Center
                                ) {
                                    // Persistent thumbnail preview underneath
                                    AsyncImage(
                                        model = file.thumbnail ?: file.content,
                                        contentDescription = "Selected media preview",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )

                                    if (file.type == Story.ContentType.VIDEO) {
                                        if (isPlayingPreview && !file.localUri.isNullOrBlank()) {
                                            NativeVideoPlayer(
                                                url = file.localUri,
                                                modifier = Modifier.fillMaxSize(),
                                                autoPlay = true
                                            )
                                            IconButton(
                                                onClick = { isPlayingPreview = false },
                                                modifier = Modifier
                                                    .align(Alignment.TopStart)
                                                    .padding(12.dp)
                                                    .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                                                    .size(32.dp)
                                            ) {
                                                Icon(
                                                    Icons.Default.Close,
                                                    contentDescription = "Stop Preview",
                                                    tint = Color.White,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            }
                                        } else {
                                            Surface(
                                                shape = CircleShape,
                                                color = Color.Black.copy(alpha = 0.65f),
                                                modifier = Modifier
                                                    .size(52.dp)
                                                    .clickable {
                                                        if (!file.localUri.isNullOrBlank()) {
                                                            isPlayingPreview = true
                                                        }
                                                    }
                                            ) {
                                                Icon(
                                                    Icons.Default.PlayArrow,
                                                    contentDescription = "Preview Video",
                                                    tint = Color.White,
                                                    modifier = Modifier.padding(12.dp).fillMaxSize()
                                                )
                                            }
                                        }

                                        if (!file.duration.isNullOrBlank()) {
                                            Surface(
                                                shape = RoundedCornerShape(8.dp),
                                                color = Color.Black.copy(alpha = 0.75f),
                                                modifier = Modifier
                                                    .align(Alignment.BottomEnd)
                                                    .padding(12.dp)
                                            ) {
                                                Row(
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Icon(
                                                        Icons.Default.Schedule,
                                                        contentDescription = null,
                                                        tint = Color.White,
                                                        modifier = Modifier.size(14.dp)
                                                    )
                                                    Spacer(Modifier.width(4.dp))
                                                    Text(
                                                        text = file.duration,
                                                        color = Color.White,
                                                        style = MaterialTheme.typography.labelSmall,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    IconButton(
                                        onClick = { selectedMediaFile = null },
                                        modifier = Modifier
                                            .align(Alignment.TopEnd)
                                            .padding(12.dp)
                                            .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                                    ) {
                                        Icon(
                                            Icons.Default.Close,
                                            contentDescription = "Remove media",
                                            tint = Color.White,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Surface(
                                onClick = { showImagePicker = true },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(16.dp),
                                color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f),
                                border = if (selectedMediaFile?.type == Story.ContentType.PHOTO)
                                    BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
                                else
                                    null
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        Icons.Default.AddAPhoto,
                                        null,
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(Modifier.width(10.dp))
                                    Text(
                                        "Add Photo",
                                        style = PoppinsTypography().labelLarge
                                    )
                                }
                            }

                            Surface(
                                onClick = { showVideoPicker = true },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(16.dp),
                                color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f),
                                border = if (selectedMediaFile?.type == Story.ContentType.VIDEO)
                                    BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
                                else
                                    null
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        Icons.Default.Videocam,
                                        null,
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(Modifier.width(10.dp))
                                    Text(
                                        "Add Video",
                                        style = PoppinsTypography().labelLarge
                                    )
                                }
                            }
                        }

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        ) {
                            Text(
                                "Who can see this field update?",
                                style = PoppinsTypography().labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Story.Visibility.entries.forEach { visibility ->
                                    FilterChip(
                                        selected = selectedVisibility == visibility,
                                        onClick = { selectedVisibility = visibility },
                                        label = {
                                            Text(
                                                text = visibility.name.lowercase()
                                                    .replace("_", " ")
                                                    .replaceFirstChar { it.uppercase() }
                                            )
                                        },
                                        leadingIcon = {
                                            Icon(
                                                imageVector = when (visibility) {
                                                    Story.Visibility.PUBLIC -> Icons.Default.Public
                                                    Story.Visibility.CONNECTS_ONLY -> Icons.Default.People
                                                    Story.Visibility.PRIVATE -> Icons.Default.Lock
                                                },
                                                contentDescription = null,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                            selectedLabelColor = MaterialTheme.colorScheme.primary,
                                            selectedLeadingIconColor = MaterialTheme.colorScheme.primary
                                        ),
                                        border = FilterChipDefaults.filterChipBorder(
                                            enabled = true,
                                            selected = selectedVisibility == visibility,
                                            borderColor = MaterialTheme.colorScheme.outlineVariant,
                                            selectedBorderColor = MaterialTheme.colorScheme.primary,
                                            borderWidth = 1.dp
                                        )
                                    )
                                }
                            }

                            AnimatedContent(
                                targetState = selectedVisibility,
                                transitionSpec = {
                                    fadeIn() togetherWith fadeOut()
                                },
                                label = "VisibilityDescription"
                            ) { targetVis ->
                                val description = when (targetVis) {
                                    Story.Visibility.PUBLIC -> "Shared with the entire AgriPulse farmer community."
                                    Story.Visibility.CONNECTS_ONLY -> "Only visible to you and farmers you've connected with."
                                    Story.Visibility.PRIVATE -> "Visible only to you. Private field observation notes."
                                }
                                Text(
                                    text = description,
                                    style = PoppinsTypography().bodySmall,
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                                    modifier = Modifier.padding(top = 8.dp, start = 4.dp)
                                )
                            }
                        }


                        if (errorMessage != null) {
                            Text(
                                text = errorMessage!!,
                                color = MaterialTheme.colorScheme.error,
                                style = PoppinsTypography().bodySmall,
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )
                        }

                        Spacer(Modifier.height(100.dp))
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Button(
                        onClick = handleAddStoryClick,
                        enabled = !isLoading && (textContent.isNotBlank() || selectedMediaFile != null),
                        modifier = Modifier
                            .widthIn(min = 200.dp, max = 400.dp)
                            .height(56.dp),
                        shape = RoundedCornerShape(20.dp),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                    ) {
                        if (isLoading) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                CircularProgressIndicator(
                                    modifier = Modifier
                                        .size(24.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                                Spacer(Modifier.width(12.dp))
                                Text(
                                    progressMessage ?: "Posting...",
                                    style = PoppinsTypography().labelLarge,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        } else {
                            Text(
                                "Breathe into Community",
                                fontWeight = FontWeight.Bold,
                                style = PoppinsTypography().labelMedium,
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            }
        }
    }
}


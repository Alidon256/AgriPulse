package org.vaulture.project.presentation.ui.components

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil3.compose.AsyncImage
import org.vaulture.project.core.theme.PoppinsTypography
import org.vaulture.project.core.util.ImagePicker
import org.vaulture.project.core.util.NativeVideoPlayer
import org.vaulture.project.core.util.VideoPicker
import org.vaulture.project.features.space.domain.model.Story

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditMemoryDialog(
    story: Story,
    onDismiss: () -> Unit,
    onSave: (newText: String, newMediaBytes: ByteArray?, fileName: String?, newThumbnailBytes: ByteArray?, newVisibility: Story.Visibility, newDuration: String?) -> Unit
) {
    var textContent by remember { mutableStateOf(story.textContent ?: "") }
    var selectedVisibility by remember { mutableStateOf(story.visibility) }
    var newMediaBytes by remember { mutableStateOf<ByteArray?>(null) }
    var newThumbnailBytes by remember { mutableStateOf<ByteArray?>(null) }
    var newFileName by remember { mutableStateOf<String?>(null) }
    var durationText by remember { mutableStateOf(story.duration) }
    var localUri by remember { mutableStateOf<String?>(null) }
    var isVideoSelected by remember { mutableStateOf(story.contentType == Story.ContentType.VIDEO) }
    var showImagePicker by remember { mutableStateOf(false) }
    var showVideoPicker by remember { mutableStateOf(false) }
    var isSaving by remember { mutableStateOf(false) }

    ImagePicker(
        show = showImagePicker,
        onImageSelected = { bytes ->
            showImagePicker = false
            if (bytes != null) {
                newMediaBytes = bytes
                newThumbnailBytes = null
                durationText = null
                localUri = null
                isVideoSelected = false
                newFileName = "updated_photo_${kotlin.time.Clock.System.now().toEpochMilliseconds()}.jpg"
            }
        }
    )

    VideoPicker(
        show = showVideoPicker,
        onVideoSelected = { videoBytes, metadata ->
            showVideoPicker = false
            if (videoBytes != null) {
                newMediaBytes = videoBytes
                newThumbnailBytes = metadata?.thumbnailBytes
                durationText = metadata?.durationText
                localUri = metadata?.localUri
                isVideoSelected = true
                val ext = metadata?.fileName?.substringAfterLast('.', "mp4") ?: "mp4"
                newFileName = "updated_video_${kotlin.time.Clock.System.now().toEpochMilliseconds()}.$ext"
            }
        }
    )

    Dialog(onDismissRequest = { if (!isSaving) onDismiss() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Edit Memory",
                            style = PoppinsTypography().titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Update your crop observation or memory details",
                            style = PoppinsTypography().bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        enabled = !isSaving
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                // Media Preview
                val previewModel: Any? = newThumbnailBytes ?: newMediaBytes ?: story.thumbnailUrl ?: story.contentUrl
                if (previewModel != null) {
                    var isPlayingPreview by remember(previewModel) { mutableStateOf(false) }
                    val playUrl = localUri ?: story.contentUrl

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        // Keep thumbnail preview always underneath as it plays
                        AsyncImage(
                            model = previewModel,
                            contentDescription = "Memory Media Preview",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )

                        if (isVideoSelected) {
                            if (isPlayingPreview && !playUrl.isNullOrBlank()) {
                                NativeVideoPlayer(
                                    url = playUrl,
                                    modifier = Modifier.fillMaxSize(),
                                    autoPlay = true
                                )
                                IconButton(
                                    onClick = { isPlayingPreview = false },
                                    modifier = Modifier
                                        .align(Alignment.TopStart)
                                        .padding(8.dp)
                                        .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                                        .size(28.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Close,
                                        contentDescription = "Stop Preview",
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            } else {
                                Surface(
                                    shape = CircleShape,
                                    color = Color.Black.copy(alpha = 0.65f),
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clickable {
                                            if (!playUrl.isNullOrBlank()) {
                                                isPlayingPreview = true
                                            }
                                        }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PlayArrow,
                                        contentDescription = "Video",
                                        tint = Color.White,
                                        modifier = Modifier.padding(10.dp)
                                    )
                                }
                            }

                            if (!durationText.isNullOrBlank()) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color.Black.copy(alpha = 0.75f),
                                    modifier = Modifier
                                        .align(Alignment.BottomStart)
                                        .padding(8.dp)
                                ) {
                                    Text(
                                        text = durationText!!,
                                        color = Color.White,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                    )
                                }
                            }
                        }

                        // Change media buttons overlay
                        Row(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            FilledTonalButton(
                                onClick = { showImagePicker = true },
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Icon(Icons.Default.PhotoCamera, null, modifier = Modifier.size(14.dp))
                                Spacer(Modifier.width(4.dp))
                                Text("Photo", style = PoppinsTypography().labelSmall)
                            }

                            FilledTonalButton(
                                onClick = { showVideoPicker = true },
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Icon(Icons.Default.Videocam, null, modifier = Modifier.size(14.dp))
                                Spacer(Modifier.width(4.dp))
                                Text("Video", style = PoppinsTypography().labelSmall)
                            }
                        }
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = { showImagePicker = true },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.AddAPhoto, null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Add Photo", style = PoppinsTypography().labelSmall)
                        }

                        OutlinedButton(
                            onClick = { showVideoPicker = true },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Videocam, null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Add Video", style = PoppinsTypography().labelSmall)
                        }
                    }
                }

                // Text Edit Field
                OutlinedTextField(
                    value = textContent,
                    onValueChange = { textContent = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 130.dp),
                    label = { Text("Observation / Note") },
                    placeholder = { Text("Describe your field update, crop stage, or note...") },
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                    )
                )

                // Visibility Selector
                Column {
                    Text(
                        text = "Visibility",
                        style = PoppinsTypography().labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(8.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
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
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            )
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))

                // Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        enabled = !isSaving,
                        modifier = Modifier.weight(1f).height(46.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Cancel")
                    }

                    Button(
                        onClick = {
                            if (textContent.isNotBlank()) {
                                isSaving = true
                                onSave(textContent.trim(), newMediaBytes, newFileName, newThumbnailBytes, selectedVisibility, durationText)
                            }
                        },
                        enabled = !isSaving && textContent.isNotBlank(),
                        modifier = Modifier.weight(1f).height(46.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        if (isSaving) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("Save Changes")
                        }
                    }
                }
            }
        }
    }
}

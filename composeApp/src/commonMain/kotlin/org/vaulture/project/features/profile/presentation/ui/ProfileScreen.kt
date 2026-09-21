package org.vaulture.project.features.profile.presentation.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Article
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.vaulture.project.features.space.domain.model.Story
import org.vaulture.project.core.domain.User
import org.vaulture.project.core.theme.PoppinsTypography
import org.vaulture.project.features.wellness.presentation.viewmodel.WellnessViewModel
import org.vaulture.project.features.wellness.domain.model.WellnessStats
import org.vaulture.project.features.wellness.domain.model.WellnessType
import org.vaulture.project.navigation.Routes
import org.vaulture.project.presentation.ui.components.AIPrivacyCard
import org.vaulture.project.presentation.ui.components.ProfileAvatar
import org.vaulture.project.presentation.ui.components.PulseBadgeCard
import org.vaulture.project.presentation.ui.components.SectionHeader
import org.vaulture.project.presentation.ui.components.StaggeredStoryItem
import org.vaulture.project.presentation.ui.components.StatBox
import org.vaulture.project.presentation.ui.components.StreakBanner
import org.vaulture.project.presentation.ui.components.WellnessActionItem
import org.vaulture.project.presentation.ui.components.WellnessStatsRow
import org.vaulture.project.features.space.presentation.viewmodel.ProfileFilter
import org.vaulture.project.features.space.presentation.viewmodel.SpaceViewModel
import kotlin.time.Clock

@Composable
fun ProfileScreen(
    wellnessViewModel: WellnessViewModel,
    onSignOut: () -> Unit,
    navController: NavController,
    spaceViewModel: SpaceViewModel,
    selectedFilter: ProfileFilter,
    onFilterSelected: (ProfileFilter) -> Unit,
    onCommentClick: (String) -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToEditProfile: () -> Unit
) {
    val statsState by wellnessViewModel.uiState.collectAsState()
    val user by spaceViewModel.userProfile.collectAsState()
    val currentUserId = user?.uid ?: ""

    val myPosts by spaceViewModel.userStories.collectAsState()
    val likedPosts by spaceViewModel.userLikedStories.collectAsState()
    val bookmarkedPosts by spaceViewModel.userBookmarkedStories.collectAsState()
    val isLoading by spaceViewModel.isLoadingProfileData.collectAsState()

    val displayedStories = when (selectedFilter) {
        ProfileFilter.MY_POSTS -> myPosts
        ProfileFilter.LIKED -> likedPosts
        ProfileFilter.BOOKMARKED -> bookmarkedPosts
    }

    // Trigger data loading when the filter changes
    LaunchedEffect(selectedFilter) {
        spaceViewModel.loadProfileData(selectedFilter)
    }
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val isExpanded = maxWidth > 920.dp

        AnimatedContent(
            targetState = isExpanded,
            transitionSpec = { fadeIn(animationSpec = spring()) togetherWith fadeOut(animationSpec = spring()) },
            label = "ResponsiveProfileLayout"
        ) { expanded ->
            if (expanded) {
                ProfileScreenExpanded(
                    user = user,
                    stats = statsState.stats,
                    onSignOut = onSignOut,
                    wellnessViewModel = wellnessViewModel,
                    navController = navController,
                    selectedFilter = selectedFilter,
                    onFilterSelected = onFilterSelected,
                    isLoading = isLoading,
                    displayedStories = displayedStories,
                    currentUserId = currentUserId,
                    onCommentClick = onCommentClick,
                    spaceViewModel = spaceViewModel,
                    onNavigateToEditProfile = onNavigateToEditProfile
                )
            } else {
                ProfileScreenCompact(
                    user = user,
                    stats = statsState.stats,
                    spaceViewModel = spaceViewModel,
                    selectedFilter = selectedFilter,
                    onFilterSelected = onFilterSelected,
                    isLoading = isLoading,
                    displayedStories = displayedStories,
                    currentUserId = currentUserId,
                    onCommentClick = onCommentClick,
                    onNavigateToSettings = onNavigateToSettings,
                    onNavigateToEditProfile = onNavigateToEditProfile
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProfileScreenCompact(
    user: User?,
    stats: WellnessStats,
    onNavigateToSettings: () -> Unit,
    spaceViewModel: SpaceViewModel,
    onFilterSelected: (ProfileFilter) -> Unit,
    onCommentClick: (String) -> Unit,
    selectedFilter: ProfileFilter,
    isLoading: Boolean,
    displayedStories: List<Story>,
    currentUserId: String,
    onNavigateToEditProfile: () -> Unit
) {

    val bannerHeight = 200.dp
    val avatarInitialSize = 110.dp

    val gridState = rememberLazyStaggeredGridState()

    Scaffold(containerColor = MaterialTheme.colorScheme.background) { paddingValues ->
        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = paddingValues.calculateBottomPadding()),
            state = gridState,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalItemSpacing = 8.dp,
            contentPadding = PaddingValues(horizontal = 8.dp)
        ) {
            item(span = StaggeredGridItemSpan.FullLine) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(bannerHeight + avatarInitialSize / 2)
                ) {
                    AsyncImage(
                        model = "https://images.unsplash.com/photo-1625246333195-78d9c38ad449?w=1200",
                        contentDescription = "Farm landscape banner",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxWidth().height(bannerHeight)
                    )
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .background(Brush.verticalGradient(listOf(Color.Black.copy(0.3f), Color.Transparent)))
                    )

                    val avatarY = (bannerHeight - (avatarInitialSize / 2))

                    Box(
                        modifier = Modifier
                            .padding(top = avatarY)
                            .size(110.dp)
                            .align(Alignment.TopCenter)
                    ) {
                        Box(
                            contentAlignment = Alignment.BottomEnd,
                            modifier = Modifier.background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                CircleShape
                            )
                        ) {
                            ProfileAvatar(
                                user?.photoUrl,
                                "User",
                                Modifier.size(120.dp)
                            )
                            FilledIconButton(
                                onClick = onNavigateToEditProfile,
                                modifier = Modifier.size(32.dp).offset(x = 4.dp, y = 4.dp),
                                colors = IconButtonDefaults.filledIconButtonColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = MaterialTheme.colorScheme.onPrimary
                                )
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit Profile", modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }

            item(span = StaggeredGridItemSpan.FullLine) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .padding(top = 8.dp)
                ) {
                    Text(
                        user?.displayName ?: user?.username?: "Farmer",
                        style = PoppinsTypography().headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        user?.email ?: "",
                        style = PoppinsTypography().bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(24.dp))
                }
            }

            item(span = StaggeredGridItemSpan.FullLine) {
                FarmerCredentialsCard(user = user)
            }

            item(span = StaggeredGridItemSpan.FullLine) {
                RealWellnessStatsRow(stats)
            }


            item(span = StaggeredGridItemSpan.FullLine) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ProfileFilter.entries.forEach { filter ->
                            FilterChip(
                                selected = selectedFilter == filter,
                                onClick = { onFilterSelected(filter) },
                                label = {
                                    Text(when(filter) {
                                        ProfileFilter.MY_POSTS -> "My Posts"
                                        ProfileFilter.LIKED -> "Liked"
                                        ProfileFilter.BOOKMARKED -> "Saved"
                                    })
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = when(filter) {
                                            ProfileFilter.MY_POSTS -> Icons.Default.Article
                                            ProfileFilter.LIKED -> Icons.Default.Favorite
                                            ProfileFilter.BOOKMARKED -> Icons.Default.Bookmark
                                        },
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            )
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                }
            }

            if (isLoading) {
                item(span = StaggeredGridItemSpan.FullLine) {
                    Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
            } else if (displayedStories.isEmpty()) {
                item(span = StaggeredGridItemSpan.FullLine) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.Info,
                            null,
                            tint = MaterialTheme.colorScheme.outline
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "No posts found here yet.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                items(displayedStories, key = { it.storyId }) { story ->
                    StaggeredStoryItem(
                        story = story,
                        currentUserId = currentUserId,
                        onLikeClick = { spaceViewModel.toggleLike(story) },
                        onClick = { onCommentClick(story.storyId) }
                    )
                }
            }

            item(span = StaggeredGridItemSpan.FullLine) {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 8.dp, vertical = 32.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    PulseBadgeCard(totalPoints = stats.resiliencePoints)
                    AIPrivacyCard()
                    SettingsItem(onClick = onNavigateToSettings )
                    Spacer(Modifier.height(32.dp))
                }
            }
        }
    }
}

@Composable
private fun ProfileScreenExpanded(
    user: User?,
    stats: WellnessStats,
    onSignOut: () -> Unit,
    wellnessViewModel: WellnessViewModel,
    navController: NavController,
    selectedFilter: ProfileFilter,
    onFilterSelected: (ProfileFilter) -> Unit,
    isLoading: Boolean,
    displayedStories: List<Story>,
    currentUserId: String,
    onCommentClick: (String) -> Unit,
    spaceViewModel: SpaceViewModel,
    onNavigateToEditProfile: () -> Unit
) {
    val wellnessState by wellnessViewModel.uiState.collectAsState()
    var selectedRailItem by remember { mutableStateOf("Profile") }
    val currentHour = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).hour
    val greeting = when (currentHour) {
        in 0..11 -> "Good Morning 🌄,"
        in 12..17 -> "Good Afternoon 😎,"
        else -> "Good Evening 🌙,"
    }

    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxHeight()
                .weight(0.3f)
                .padding(vertical =8.dp, horizontal = 16.dp)
                .background(MaterialTheme.colorScheme.background),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                Row(
                    modifier = Modifier,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = greeting,
                        style = PoppinsTypography().bodyLarge.copy(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                    Text(
                        text ="${" "}${user?.displayName?.substringBefore(' ') ?: user?.username?.substringBefore(' ') ?: "..."}",
                        style = PoppinsTypography().bodyLarge.copy(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            item {
                Spacer(modifier = Modifier.height(8.dp))
                StreakBanner(
                    wellnessState.stats.currentStreak
                )
            }
            item {
                SectionHeader(
                    title = "Agronomic Actions",
                    icon = Icons.Default.Agriculture
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.horizontalScroll(rememberScrollState())
                ) {
                    WellnessActionItem(
                        "Soil Health",
                        "Soil Moisture",
                        Icons.Default.Air,
                        bg = Color(0xFF2E7D32)
                    ) {
                        wellnessViewModel.selectActivity(WellnessType.BREATHING)
                        navController.navigate(Routes.WELLNESS_TIMER)
                    }

                    WellnessActionItem(
                        "Pest Check",
                        "Pests & Crop Scouting",
                        Icons.Default.BugReport,
                        bg = Color(0xFFF57C00)
                    ) {
                        wellnessViewModel.selectActivity(WellnessType.YOGA)
                        navController.navigate(Routes.WELLNESS_TIMER)
                    }

                    WellnessActionItem(
                        "Irrigation",
                        "Drought Care",
                        Icons.Default.WaterDrop,
                        bg = Color(0xFF0288D1)
                    ) {
                        wellnessViewModel.selectActivity(WellnessType.MEDITATION)
                        navController.navigate(Routes.WELLNESS_TIMER)
                    }
                }
            }
            item {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Lock,
                            contentDescription = "Privacy",
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(Modifier.width(12.dp))
                        Text(
                            "Responsible AI: Your privacy is respected. Personal journal responses are analyzed strictly in-context and never shared.",
                            style = PoppinsTypography().bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
        VerticalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.outlineVariant)

        val gridState = rememberLazyStaggeredGridState()

        LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Fixed(2),
                modifier = Modifier
                    .weight(0.4f).padding(start = 16.dp,end = 16.dp) ,
                state = gridState,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalItemSpacing = 8.dp,
                contentPadding = PaddingValues(horizontal = 8.dp)
            ) {
                item(span = StaggeredGridItemSpan.FullLine) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(contentAlignment = Alignment.BottomEnd) {
                            ProfileAvatar(
                                user?.photoUrl,
                                "User",
                                Modifier.size(120.dp)
                            )
                            FilledIconButton(
                                onClick = onNavigateToEditProfile,
                                modifier = Modifier.size(32.dp).offset(x = 4.dp, y = 4.dp),
                                colors = IconButtonDefaults.filledIconButtonColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = MaterialTheme.colorScheme.onPrimary
                                )
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit Profile", modifier = Modifier.size(16.dp))
                            }
                        }
                        Spacer(Modifier.width(32.dp))
                        Column {
                            Text(
                                user?.displayName ?: user?.username?: "Farmer",
                                style = PoppinsTypography().displaySmall,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                user?.email ?: "",
                                style = PoppinsTypography().titleMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                item(span = StaggeredGridItemSpan.FullLine) {
                    FarmerCredentialsCard(user = user)
                }

                item(span = StaggeredGridItemSpan.FullLine) {
                    RealWellnessStatsRow(stats)
                }


                item(span = StaggeredGridItemSpan.FullLine) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(start = 16.dp,end = 16.dp,top = 24.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            ProfileFilter.entries.forEach { filter ->
                                FilterChip(
                                    selected = selectedFilter == filter,
                                    onClick = { onFilterSelected(filter) },
                                    label = {
                                        Text(when(filter) {
                                            ProfileFilter.MY_POSTS -> "My Posts"
                                            ProfileFilter.LIKED -> "Liked"
                                            ProfileFilter.BOOKMARKED -> "Saved"
                                        })
                                    },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = when(filter) {
                                                ProfileFilter.MY_POSTS -> Icons.AutoMirrored.Filled.Article
                                                ProfileFilter.LIKED -> Icons.Default.Favorite
                                                ProfileFilter.BOOKMARKED -> Icons.Default.Bookmark
                                            },
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                )
                            }
                        }
                        Spacer(Modifier.height(16.dp))
                    }
                }

                if (isLoading) {
                    item(span = StaggeredGridItemSpan.FullLine) {
                        Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }
                } else if (displayedStories.isEmpty()) {
                    item(span = StaggeredGridItemSpan.FullLine) {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Default.Info,
                                null,
                                tint = MaterialTheme.colorScheme.outline
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                "No posts found here yet.",
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    items(displayedStories, key = { it.storyId }) { story ->
                        StaggeredStoryItem(
                            story = story,
                            currentUserId = currentUserId,
                            onLikeClick = { spaceViewModel.toggleLike(story) },
                            onClick = { onCommentClick(story.storyId) }
                        )
                    }
                }

        }

        VerticalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.outlineVariant)
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .weight(0.3f)
                .verticalScroll(rememberScrollState())
                .background(MaterialTheme.colorScheme.background)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text(
                "Your Wellness Vault",
                style = PoppinsTypography().titleLarge,
                fontWeight = FontWeight.Bold
            )

            PulseBadgeCard(totalPoints = stats.resiliencePoints)

            WellnessStatsRow(stats = stats)

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.ShieldMoon,
                        null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.width(12.dp))
                    Text(
                        "This is a farmer community hub. Please share sustainable practices and be supportive of all growers in the AgriPulse community.",
                        style = PoppinsTypography().bodySmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
            AIPrivacyCard()
            Spacer(Modifier.padding(vertical = 60.dp))
        }
    }

}

@Composable
private fun SettingsItem(onClick: ()->Unit){
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Settings,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.width(12.dp))
            Text(
                "Settings",
                style = PoppinsTypography().bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun RealWellnessStatsRow(stats: WellnessStats) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatBox(
            label = "Crop Checks",
            value = "${stats.sessionsToday}/5",
            modifier = Modifier.weight(1f),
            icon = Icons.Default.BatteryChargingFull,
            bg = Color(0xFF03A9F4)
        )
        StatBox(
            label = "Field Streak",
            value = "${stats.currentStreak}d",
            modifier = Modifier.weight(1f),
            icon = Icons.Default.LocalFireDepartment,
            bg = Color(0xFF4CAF50)
        )
        StatBox(
            label = "Field XP",
            value = "${stats.resiliencePoints}",
            modifier = Modifier.weight(1f),
            icon = Icons.Default.EmojiEvents,
            bg = Color(0xFF9C27B0)
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FarmerCredentialsCard(
    user: User?,
    modifier: Modifier = Modifier
) {
    val farmName = user?.farmName?.ifBlank { null }
    val location = when {
        !user?.region.isNullOrBlank() && !user?.country.isNullOrBlank() -> "${user?.region}, ${user?.country}"
        !user?.region.isNullOrBlank() -> user?.region!!
        !user?.country.isNullOrBlank() -> user?.country!!
        else -> null
    }
    val farmSize = user?.farmSize?.ifBlank { null }
    val primaryCrops = user?.primaryCrops ?: emptyList()
    val farmingType = user?.farmingType?.ifBlank { null }
    val irrigationType = user?.irrigationType?.ifBlank { null }
    val soilType = user?.soilType?.ifBlank { null }
    val experienceLevel = user?.experienceLevel?.ifBlank { null }
    val certifications = user?.certifications ?: emptyList()
    val bio = user?.bio?.ifBlank { null }

    val hasAnyData = farmName != null || location != null || farmSize != null ||
            primaryCrops.isNotEmpty() || farmingType != null || irrigationType != null ||
            soilType != null || certifications.isNotEmpty() || bio != null

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 6.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Agriculture,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = farmName ?: "Farm Profile & Credentials",
                        style = PoppinsTypography().titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                if (experienceLevel != null) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.8f)
                    ) {
                        Text(
                            text = experienceLevel,
                            style = PoppinsTypography().labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            if (location != null || farmSize != null) {
                Spacer(Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = listOfNotNull(location, farmSize).joinToString(" • Scale: "),
                        style = PoppinsTypography().bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (bio != null) {
                Spacer(Modifier.height(10.dp))
                Text(
                    text = bio,
                    style = PoppinsTypography().bodySmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = 18.sp
                )
            }

            if (primaryCrops.isNotEmpty()) {
                Spacer(Modifier.height(12.dp))
                Text(
                    text = "Primary Crops:",
                    style = PoppinsTypography().labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.height(4.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    primaryCrops.forEach { crop ->
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.surface,
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                        ) {
                            Text(
                                text = crop,
                                style = PoppinsTypography().labelSmall,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }

            val agronomicTags = listOfNotNull(
                farmingType?.let { "Model: $it" },
                irrigationType?.let { "Water: $it" },
                soilType?.let { "Soil: $it" }
            )

            if (agronomicTags.isNotEmpty() || certifications.isNotEmpty()) {
                Spacer(Modifier.height(12.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    agronomicTags.forEach { tag ->
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f)
                        ) {
                            Text(
                                text = tag,
                                style = PoppinsTypography().labelSmall,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                fontSize = 10.sp,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                    certifications.forEach { cert ->
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFF2E7D32).copy(alpha = 0.15f),
                            border = BorderStroke(1.dp, Color(0xFF2E7D32).copy(alpha = 0.4f))
                        ) {
                            Text(
                                text = "🏅 $cert",
                                style = PoppinsTypography().labelSmall,
                                color = Color(0xFF2E7D32),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }

            if (!hasAnyData) {
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "No farm credentials configured yet. Tap the edit icon above to set your crops, acreage, and practices.",
                    style = PoppinsTypography().bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}




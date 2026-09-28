package org.vaulture.project.features.home.presentation.ui

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import org.koin.compose.koinInject
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.vaulture.project.features.home.domain.model.RhythmTrack
import org.vaulture.project.features.wellness.domain.model.WellnessType
import org.vaulture.project.core.domain.CheckInResult
import org.vaulture.project.core.domain.User
import org.vaulture.project.core.theme.PoppinsTypography
import org.vaulture.project.core.util.rememberKmpAudioPlayer
import org.vaulture.project.presentation.ui.components.AIPrivacyCard
import org.vaulture.project.presentation.ui.components.CropMarketPriceTicker
import org.vaulture.project.presentation.ui.components.WeatherForecastWidget
import org.vaulture.project.presentation.ui.components.CardCarousel
import org.vaulture.project.presentation.ui.components.EmptyStateHero
import org.vaulture.project.presentation.ui.components.InsightHeroCard
import org.vaulture.project.presentation.ui.components.ProfileAvatar
import org.vaulture.project.presentation.ui.components.PulseBadgeCard
import org.vaulture.project.presentation.ui.components.PulseFab
import org.vaulture.project.presentation.ui.components.RhythmItem
import org.vaulture.project.presentation.ui.components.SectionHeader
import org.vaulture.project.presentation.ui.components.ShimmerRhythmItem
import org.vaulture.project.presentation.ui.components.StreakBanner
import org.vaulture.project.presentation.ui.components.WellnessActionItem
import org.vaulture.project.presentation.ui.components.WellnessStatsRow
import org.vaulture.project.presentation.ui.components.FarmerCommunityHubsSection
import org.vaulture.project.presentation.ui.components.FarmerConnectSection
import org.vaulture.project.presentation.ui.components.VideoGuideCard
import org.vaulture.project.presentation.ui.components.VideoGuidePlayerDialog
import org.vaulture.project.features.home.domain.model.DEFAULT_VIDEO_GUIDES
import org.vaulture.project.features.home.domain.model.FarmerVideoGuide
import org.vaulture.project.features.home.domain.repository.VideoGuideRepository
import org.vaulture.project.features.space.domain.model.Space
import org.vaulture.project.features.home.presentation.viewmodel.CheckInViewModel
import org.vaulture.project.features.home.presentation.viewmodel.RhythmViewModel
import org.vaulture.project.features.space.presentation.viewmodel.SpaceViewModel
import org.vaulture.project.features.wellness.presentation.viewmodel.WellnessViewModel
import org.vaulture.project.navigation.Routes
import kotlin.time.Clock

val ColorOptimal = Color(0xFF4CAF50)      // Green (Optimal Vigor)
val ColorStable = ColorOptimal
val ColorMild = Color(0xFFFFC107)         // Amber (Mild Stress)
val ColorHigh = Color(0xFFFF5722)         // Deep Orange (High Pest Risk)
val ColorCritical = Color(0xFFD32F2F)     // Red (Critical Alert)
val ColorBurnout = ColorCritical

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToCheckIn: () -> Unit,
    navController: NavController,
    wellnessViewModel: WellnessViewModel,
    spaceViewModel: SpaceViewModel,
    onSignOut: () -> Unit
) {
    val checkInViewModel: CheckInViewModel = koinInject()
    val rhythmViewModel: RhythmViewModel = koinInject()
    var showCheckInModal by remember { mutableStateOf(false) }
    val checkInState by checkInViewModel.uiState.collectAsState()
    val latestPersistentResult by checkInViewModel.latestResult.collectAsState()
    val user by spaceViewModel.userProfile.collectAsState()
    val spaces by spaceViewModel.spaces.collectAsState()
    val communityFarmers by spaceViewModel.communityFarmers.collectAsState()

    val displayResult = latestPersistentResult ?: checkInState.result
    val hasResult = displayResult != null
    LaunchedEffect(latestPersistentResult) {
        if (latestPersistentResult != null && checkInState.result == null) {
            checkInViewModel.syncResult(latestPersistentResult!!)
        }
    }

    LaunchedEffect(latestPersistentResult, checkInState.result) {
        println("DEBUG: DB Result = ${latestPersistentResult?.aiInsight?.take(20)}... (Score: ${latestPersistentResult?.score})")
        println("DEBUG: Local Result = ${checkInState.result?.aiInsight?.take(20)}... (Score: ${checkInState.result?.score})")
        println("DEBUG: Consolidated displayResult = ${displayResult?.state?.name ?: "NULL"}")
    }

    Scaffold(
        containerColor = Color.Transparent,
        floatingActionButton = {
            AnimatedVisibility(
                visible = !showCheckInModal && !hasResult,
                enter = scaleIn() + fadeIn(),
                exit = scaleOut() + fadeOut()
            ) {
                PulseFab(onClick = {
                    showCheckInModal = true
                })
            }
        }
    ) { padding ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding)
        ) {
            val isWideScreen = maxWidth > 920.dp

            AnimatedContent(
                targetState = showCheckInModal,
                transitionSpec = {
                    if (targetState) {
                        slideInVertically { it } + fadeIn() togetherWith fadeOut() + slideOutVertically { -it / 2 }
                    } else {
                        slideInVertically { -it } + fadeIn() togetherWith fadeOut() + slideOutVertically { it / 2 }
                    }
                },
                label = "ScreenTransition"
            ) { isCheckingIn ->
                if (isCheckingIn) {
                    CheckInScreen(
                        viewModel = checkInViewModel,
                        onClose = { showCheckInModal = false }
                    )
                } else {
                    if (isWideScreen) {
                        DashboardWebLayout(
                            latestResult = displayResult,
                            onStartCheckIn = { showCheckInModal = true },
                            navController = navController,
                            viewModel = rhythmViewModel,
                            wellnessViewModel = wellnessViewModel,
                            onSignOut = onSignOut,
                            user = user,
                            spaces = spaces,
                            communityFarmers = communityFarmers
                        )
                    } else {
                        DashboardMobileLayout(
                            latestResult = displayResult,
                            onStartCheckIn = { showCheckInModal = true },
                            navController = navController,
                            viewModel = rhythmViewModel,
                            wellnessViewModel = wellnessViewModel,
                            user = user,
                            spaces = spaces,
                            communityFarmers = communityFarmers
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PopularSection(
    tracks: List<RhythmTrack>,
    isLoading: Boolean,
    viewModel: RhythmViewModel,
    navController: NavController
) {
    val firstRowTracks = tracks.take((tracks.size + 1) / 2)
    val secondRowTracks = tracks.drop((tracks.size + 1) / 2)

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 16.dp,
                    top = 8.dp,
                    end = 8.dp
                ),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Extension Audio Guides 📻",
                style = PoppinsTypography().headlineMedium.copy(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            )
            Text(
                text = "See All",
                style = PoppinsTypography().bodySmall.copy(
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier
                    .clickable { navController.navigate(Routes.MELODIES) }
            )
        }
        if (isLoading || tracks.isEmpty()) {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, start = 16.dp),
                contentPadding = PaddingValues(horizontal = 0.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(4, key = { "shimmer_popular_$it" }) {
                    ShimmerRhythmItem()
                }
            }
        } else {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                contentPadding = PaddingValues(horizontal = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(firstRowTracks, key = { it.id }) { track ->
                    RhythmItem(
                        track = track,
                        onClick = {
                            viewModel.playTrack(track)
                            navController.navigate(Routes.RHYTHM_PLAYER(track.id))
                        }
                    )
                }
            }
            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 12.dp)
                    .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)),
                thickness = 1.dp
            )
        }
        if (isLoading || tracks.isEmpty() && tracks.size <= ((tracks.size + 1) / 2) ) {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                contentPadding = PaddingValues(horizontal = 0.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(4, key = { "shimmer_popular_row2_$it" }) {
                    ShimmerRhythmItem()
                }
            }
        } else if (secondRowTracks.isNotEmpty()){
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                contentPadding = PaddingValues(horizontal = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(secondRowTracks, key = { it.id }) { track ->
                    RhythmItem(
                        track = track,
                        onClick = {
                            viewModel.playTrack(track)
                            navController.navigate(Routes.RHYTHM_PLAYER(track.id))
                        }
                    )
                }
            }
        }
        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp)
                .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)),
            thickness = 1.dp
        )
    }
}

@Composable
private fun RecommendedSection(
    tracks: List<RhythmTrack>,
    isLoading: Boolean,
    viewModel: RhythmViewModel,
    navController: NavController
) {
    val currentHour = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).hour

    val (greetingTitle, targetTags) = remember(currentHour) {
        when (currentHour) {
            in 5..11 -> "Start Your Day ☀️" to listOf("energy", "upbeat", "motivation", "focus")
            in 12..17 -> "Afternoon Focus 🧠" to listOf("focus", "productivity", "concentration", "chill")
            else -> "Wind Down for Sleep 🌙" to listOf("sleep", "relaxation", "calm", "peaceful")
        }
    }

    val recommendedTracks = remember(tracks, targetTags) {
        if (tracks.isEmpty()) return@remember emptyList()

        val filtered = tracks.filter { track ->
            track.tags.any { tag -> targetTags.contains(tag.lowercase()) }
        }

        if (filtered.isNotEmpty()) filtered.take(10) else tracks.shuffled().take(10)
    }

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 16.dp,
                    top = 8.dp,
                    end = 8.dp
                ),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = greetingTitle,
                style = PoppinsTypography().headlineMedium.copy(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            )
            Text(
                text = "See All",
                style = PoppinsTypography().bodySmall.copy(
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier
                    .clickable { navController.navigate(Routes.MELODIES) }
            )
        }

        if (isLoading || tracks.isEmpty()) {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, start = 16.dp),
                contentPadding = PaddingValues(horizontal = 0.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(4, key = { "shimmer_recommended_$it" }) {
                    ShimmerRhythmItem()
                }
            }
        } else {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                contentPadding = PaddingValues(horizontal = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(recommendedTracks, key = { it.id }) { track ->
                    RhythmItem(
                        track = track,
                        onClick = {
                            viewModel.playTrack(track)
                            navController.navigate(Routes.RHYTHM_PLAYER(track.id))
                        }
                    )
                }
            }
            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 12.dp)
                    .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)),
                thickness = 1.dp
            )
        }
    }
}


@Composable
fun DashboardMobileLayout(
    user: User?,
    latestResult: CheckInResult?,
    onStartCheckIn: () -> Unit,
    navController: NavController,
    viewModel: RhythmViewModel,
    wellnessViewModel: WellnessViewModel,
    spaces: List<Space> = emptyList(),
    communityFarmers: List<User> = emptyList()
) {

    val wellnessState by wellnessViewModel.uiState.collectAsState()
    val rhythmState by viewModel.uiState.collectAsState()
    val currentHour = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).hour
    val greeting = when (currentHour) {
        in 0..11 -> "Good Morning 🌾,"
        in 12..17 -> "Good Afternoon ☀️,"
        else -> "Good Evening 🌙,"
    }

    val videoGuideRepository: VideoGuideRepository = koinInject()
    val videoGuides by remember(videoGuideRepository) {
        videoGuideRepository.getVideoGuidesStream()
    }.collectAsState(initial = DEFAULT_VIDEO_GUIDES)

    var activeVideo by remember { mutableStateOf<FarmerVideoGuide?>(null) }

    if (activeVideo != null) {
        VideoGuidePlayerDialog(
            video = activeVideo!!,
            onDismiss = { activeVideo = null }
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp)
    ) {
        item {
            Row(
                modifier = Modifier.padding(horizontal = 8.dp),
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
                    text = "${" "}Farmer ${user?.effectiveName ?: "..."}",
                    style = PoppinsTypography().bodyLarge.copy(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.weight(1f))
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .clickable { /* Profile */ },
                    contentAlignment = Alignment.Center
                ) {
                    ProfileAvatar(
                        user?.photoUrl?.ifBlank { null }
                            ?: "https://ui-avatars.com/api/?name=${user?.effectiveName ?: "User"}&background=2e7d32&color=fff",
                        "Profile Picture",
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }

        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            ) {
                StreakBanner(wellnessState.stats.currentStreak)
            }
        }

        item {
            WeatherForecastWidget(
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }

        item {
            CropMarketPriceTicker()
        }

        // 1. Farmer Community Hubs / Spaces on Home Screen
        item {
            FarmerCommunityHubsSection(
                spaces = spaces,
                onSpaceClick = { space -> navController.navigate(Routes.SPACE_DETAIL(space.id)) },
                onSeeAllClick = { navController.navigate(Routes.SPACES) }
            )
        }

        // 2. Demonstration Video Guides Carousel Row on Home Screen
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Field Demonstration Videos 🎥",
                            style = PoppinsTypography().headlineMedium.copy(
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )
                        Text(
                            text = "Visual guides on pest scouting, zai pits, and drip lines",
                            style = PoppinsTypography().bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 12.sp
                            )
                        )
                    }
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.clickable { navController.navigate(Routes.CREATE_MEDIA_GUIDE) }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Add,
                                    contentDescription = "Add Guide",
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(Modifier.width(4.dp))
                                Text(
                                    text = "Add Guide",
                                    style = PoppinsTypography().bodySmall.copy(
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }

                        Text(
                            text = "See All",
                            style = PoppinsTypography().bodySmall.copy(
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.SemiBold
                            ),
                            modifier = Modifier.clickable { navController.navigate(Routes.MELODIES) }
                        )
                    }
                }

                Spacer(Modifier.height(8.dp))

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(videoGuides, key = { it.id }) { video ->
                        VideoGuideCard(
                            video = video,
                            onClick = { activeVideo = video }
                        )
                    }
                }
            }
        }

        // 3. Meet & Connect with Fellow Farmers
        item {
            FarmerConnectSection(
                farmers = communityFarmers,
                onOpenSpaces = { navController.navigate(Routes.SPACES) }
            )
        }


        item {
            RecommendedSection(
                tracks = rhythmState.tracks,
                isLoading = rhythmState.isTracksLoading,
                viewModel = viewModel,
                navController = navController
            )
        }

        item {
            CardCarousel(
                modifier = Modifier.fillMaxWidth(),
                navController = navController
            )
        }

        item {
            HeroSection(
                latestResult,
                onStartCheckIn
            )
        }

        item {
            PopularSection(
                tracks = rhythmState.tracks,
                isLoading = rhythmState.isTracksLoading,
                viewModel = viewModel,
                navController = navController
            )
        }

        item {
            SectionHeader(
                title = "Agronomic Actions",
                icon = Icons.Default.SelfImprovement
            )
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(start = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                WellnessActionItem(
                    "Soil Health",
                    "Moisture & Ribbon",
                    Icons.Default.Air,
                    bg = Color(0xFF2E7D32)
                ) {
                    wellnessViewModel.selectActivity(WellnessType.BREATHING)
                    navController.navigate(Routes.WELLNESS_TIMER)
                }

                WellnessActionItem(
                    "Pest Scouting",
                    "W-Pattern & FAW",
                    Icons.Default.BugReport,
                    bg = Color(0xFFF57C00)
                ) {
                    wellnessViewModel.selectActivity(WellnessType.YOGA)
                    navController.navigate(Routes.WELLNESS_TIMER)
                }

                WellnessActionItem(
                    "Drought Care",
                    "Emitter Uniformity",
                    Icons.Default.WaterDrop,
                    bg = Color(0xFF0288D1)
                ) {
                    wellnessViewModel.selectActivity(WellnessType.MEDITATION)
                    navController.navigate(Routes.WELLNESS_TIMER)
                }
            }
        }
    }
}

@Composable
fun DashboardWebLayout(
    latestResult: CheckInResult?,
    onStartCheckIn: () -> Unit,
    navController: NavController,
    viewModel: RhythmViewModel,
    wellnessViewModel: WellnessViewModel,
    onSignOut: () -> Unit,
    user: User?,
    spaces: List<Space> = emptyList(),
    communityFarmers: List<User> = emptyList()
) {
    val state by viewModel.uiState.collectAsState()
    val wellnessState by wellnessViewModel.uiState.collectAsState()
    val currentHour = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).hour
    val greeting = when (currentHour) {
        in 0..11 -> "Good Morning 🌾,"
        in 12..17 -> "Good Afternoon ☀️,"
        else -> "Good Evening 🌙,"
    }

    val videoGuideRepository: VideoGuideRepository = koinInject()
    val videoGuides by remember(videoGuideRepository) {
        videoGuideRepository.getVideoGuidesStream()
    }.collectAsState(initial = DEFAULT_VIDEO_GUIDES)

    var activeVideo by remember { mutableStateOf<FarmerVideoGuide?>(null) }
    var isRightPaneCollapsed by remember { mutableStateOf(false) }

    if (activeVideo != null) {
        VideoGuidePlayerDialog(
            video = activeVideo!!,
            onDismiss = { activeVideo = null }
        )
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
                .padding(vertical = 8.dp, horizontal = 16.dp)
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
                        text = "${" "}Farmer ${user?.effectiveName ?: "..."}",
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
                StreakBanner(wellnessState.stats.currentStreak)
            }
            item {
                WeatherForecastWidget()
            }
            item {
                CropMarketPriceTicker()
            }
            item {
                SectionHeader(
                    title = "Agronomic Actions",
                    icon = Icons.Default.SelfImprovement
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.horizontalScroll(rememberScrollState())
                ) {
                    WellnessActionItem(
                        "Soil Health",
                        "Moisture & Ribbon",
                        Icons.Default.Air,
                        bg = Color(0xFF2E7D32)
                    ) {
                        wellnessViewModel.selectActivity(WellnessType.BREATHING)
                        navController.navigate(Routes.WELLNESS_TIMER)
                    }

                    WellnessActionItem(
                        "Pest Scouting",
                        "W-Pattern & FAW",
                        Icons.Default.BugReport,
                        bg = Color(0xFFF57C00)
                    ) {
                        wellnessViewModel.selectActivity(WellnessType.YOGA)
                        navController.navigate(Routes.WELLNESS_TIMER)
                    }

                    WellnessActionItem(
                        "Drought Care",
                        "Emitter Uniformity",
                        Icons.Default.WaterDrop,
                        bg = Color(0xFF0288D1)
                    ) {
                        wellnessViewModel.selectActivity(WellnessType.MEDITATION)
                        navController.navigate(Routes.WELLNESS_TIMER)
                    }
                }
            }
        }

        VerticalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.outlineVariant)

        LazyColumn(
            modifier = Modifier.weight(if (isRightPaneCollapsed) 0.67f else 0.4f),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                FarmerCommunityHubsSection(
                    spaces = spaces,
                    onSpaceClick = { space -> navController.navigate(Routes.SPACE_DETAIL(space.id)) },
                    onSeeAllClick = { navController.navigate(Routes.SPACES) }
                )
            }
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Field Demonstration Videos 🎥",
                            style = PoppinsTypography().headlineMedium.copy(
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = MaterialTheme.colorScheme.primaryContainer,
                                modifier = Modifier.clickable { navController.navigate(Routes.CREATE_MEDIA_GUIDE) }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.Add,
                                        contentDescription = "Add Guide",
                                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(Modifier.width(4.dp))
                                    Text(
                                        text = "Add Guide",
                                        style = PoppinsTypography().bodySmall.copy(
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }
                            }

                            Text(
                                text = "See All",
                                style = PoppinsTypography().bodySmall.copy(
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.SemiBold
                                ),
                                modifier = Modifier.clickable { navController.navigate(Routes.MELODIES) }
                            )
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        items(videoGuides, key = { it.id }) { video ->
                            VideoGuideCard(video = video, onClick = { activeVideo = video })
                        }
                    }
                }
            }
            item {
                FarmerConnectSection(
                    farmers = communityFarmers,
                    onOpenSpaces = { navController.navigate(Routes.SPACES) }
                )
            }
            item {
                RecommendedSection(
                    tracks = state.tracks,
                    isLoading = state.isTracksLoading,
                    viewModel = viewModel,
                    navController = navController
                )
            }
            item {
                CardCarousel(
                    modifier = Modifier.fillMaxWidth(),
                    navController = navController
                )
            }
            item {
                PopularSection(
                    tracks = state.tracks,
                    isLoading = state.isTracksLoading,
                    viewModel = viewModel,
                    navController = navController
                )
            }
            item {
                HeroSection(
                    latestResult,
                    onStartCheckIn
                )
            }

        }
        VerticalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.outlineVariant)

        if (isRightPaneCollapsed) {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(52.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
                    .padding(vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                IconButton(
                    onClick = { isRightPaneCollapsed = false }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Expand Farm Vault",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(Modifier.height(16.dp))
                Icon(
                    imageVector = Icons.Default.ShieldMoon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(22.dp)
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(0.3f)
                    .verticalScroll(rememberScrollState())
                    .background(MaterialTheme.colorScheme.background)
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Your Farm Resilience Vault",
                        style = PoppinsTypography().titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(
                        onClick = { isRightPaneCollapsed = true },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Collapse Vault Pane",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                PulseBadgeCard(totalPoints = wellnessState.stats.resiliencePoints)

                WellnessStatsRow(stats = wellnessState.stats)

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
                            "AgriPulse is a safe community for smallholder farmers. Share crop observations, pest warnings, and agricultural advice with mutual respect.",
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
}


@Composable
fun HeroSection(latestResult: CheckInResult?, onStartCheckIn: () -> Unit) {
    if (latestResult != null) {
        InsightHeroCard(
            latestResult
        )
    } else {
        EmptyStateHero(
            onStartCheckIn
        )
    }
}


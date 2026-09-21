package org.vaulture.project.features.space.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Agriculture
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShieldMoon
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.vaulture.project.core.domain.User
import org.vaulture.project.core.theme.PoppinsTypography
import org.vaulture.project.features.space.presentation.viewmodel.SpaceViewModel
import org.vaulture.project.features.wellness.domain.model.WellnessType
import org.vaulture.project.features.wellness.presentation.viewmodel.WellnessViewModel
import org.vaulture.project.navigation.Routes

import org.vaulture.project.presentation.ui.components.AIPrivacyCard
import org.vaulture.project.presentation.ui.components.FilterTabBar
import org.vaulture.project.presentation.ui.components.PulseBadgeCard
import org.vaulture.project.presentation.ui.components.SearchBar
import org.vaulture.project.presentation.ui.components.SectionHeader
import org.vaulture.project.presentation.ui.components.StreakBanner
import org.vaulture.project.presentation.ui.components.WellnessActionItem
import org.vaulture.project.presentation.ui.components.WellnessStatsRow

import kotlin.time.Clock


@Composable
fun SpacesHomeScreen(
    viewModel: SpaceViewModel,
    selectedFilter: SpaceFilter,
    onFilterSelected: (SpaceFilter) -> Unit,
    onSpaceClick: (spaceId: String) -> Unit,
    onCreateSpaceClick: () -> Unit,
    onAddStoryClick: () -> Unit,
    onCommentClick: (storyId: String) -> Unit,
    wellnessViewModel: WellnessViewModel,
    navController: NavController,
    onSignOut: () -> Unit,
){
    val user by viewModel.userProfile.collectAsState()
    BoxWithConstraints(modifier = Modifier.fillMaxSize()){
        val isExpanded = maxWidth > 920.dp

        AnimatedContent(
            targetState = isExpanded,
            transitionSpec = {fadeIn(animationSpec = spring()) togetherWith fadeOut(animationSpec = spring())},
            label = "ResponsiveSpacesLayout"
        ){ expanded ->
            if (expanded){
                SpaceHomeScreenExpandable(
                    viewModel = viewModel,
                    selectedFilter = selectedFilter,
                    onFilterSelected = onFilterSelected,
                    onSpaceClick = onSpaceClick,
                    onCreateSpaceClick = onCreateSpaceClick,
                    onAddStoryClick = onAddStoryClick,
                    onCommentClick = onCommentClick,
                    wellnessViewModel = wellnessViewModel,
                    navController = navController,
                    onSignOut = onSignOut,
                    user = user,
                )
            }else {
                SpacesHomeScreenCompat(
                    viewModel = viewModel,
                    selectedFilter = selectedFilter,
                    onFilterSelected = onFilterSelected,
                    onSpaceClick = onSpaceClick,
                    onCreateSpaceClick = onCreateSpaceClick,
                    onAddStoryClick = onAddStoryClick,
                    onCommentClick = onCommentClick,
                    navController = navController
                )
            }
        }
    }
}


@Composable
fun SpaceHomeScreenExpandable(
    viewModel: SpaceViewModel,
    selectedFilter: SpaceFilter,
    onFilterSelected: (SpaceFilter) -> Unit,
    onSpaceClick: (spaceId: String) -> Unit,
    onCreateSpaceClick: () -> Unit,
    onAddStoryClick: () -> Unit,
    onCommentClick: (String) -> Unit,
    wellnessViewModel: WellnessViewModel,
    navController: NavController,
    onSignOut: () -> Unit,
    user: User?,
){
    val activeId by viewModel.activeSpaceId.collectAsState()
    val statsState by wellnessViewModel.uiState.collectAsState()
    var selectedRailItem by remember { mutableStateOf("Spaces") }
    var searchQuery by remember { mutableStateOf("") }
    var isSearchExpanded by remember { mutableStateOf(false) }
    val isSearching by viewModel.isSearching.collectAsState()
    val wellnessState by wellnessViewModel.uiState.collectAsState()
    val currentHour = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).hour
    val greeting = when (currentHour) {
        in 0..11 -> "Good Morning 🌄,"
        in 12..17 -> "Good Afternoon 😎,"
        else -> "Good Evening 🌙,"
    }

    Row(modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.background)
    ){
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
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                    Text(
                        text = greeting,
                        style = PoppinsTypography().bodyLarge.copy(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                    Text(
                        text = "${" "}${user?.displayName?.substringBefore(' ') ?: user?.username?.substringBefore(' ') ?: "..."}",
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
                    onClick = { selectedRailItem = "Spaces" },
                    colors = CardDefaults.cardColors(
                        containerColor = if (selectedRailItem == "Spaces") MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            if (selectedRailItem == "Spaces")
                                Icons.Filled.Groups
                            else
                                Icons.Outlined.Groups,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(Modifier.width(12.dp))
                        Text(
                            "Spaces",
                            style = PoppinsTypography().bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
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
                            Icons.Default.Groups,
                            contentDescription = "Farmer Hub",
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(Modifier.width(12.dp))
                        Text(
                            "This is a farmer community hub. Please share sustainable practices and be supportive of all growers in AgriPulse.",
                            style = PoppinsTypography().bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
        VerticalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.outlineVariant)


        Scaffold(
            topBar = {
                if (isSearching) {
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth().height(2.dp),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = Color.Transparent
                    )
                }
                Column(modifier = Modifier.background(MaterialTheme.colorScheme.background).padding(horizontal = 16.dp)) {
                    SearchBar(
                        query = searchQuery,
                        onQueryChange = {
                            searchQuery = it
                            viewModel.onSearchQueryChanged(it)
                        },
                        onSearch = {},
                        isExpanded = isSearchExpanded,
                        onToggleExpanded = { isSearchExpanded = !isSearchExpanded },
                        placeholderText = "Search farmer hubs (e.g. Maize, Pest Control, Irrigation)...",
                        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                    )
                    if (!isSearchExpanded) {
                        FilterTabBar(
                            selectedFilter = selectedFilter,
                            onFilterSelected = onFilterSelected
                        )
                    }
                }
            },
            floatingActionButton = {
                if (!isSearchExpanded) {
                    val icon = if (selectedFilter == SpaceFilter.Spaces) Icons.Default.Add else Icons.Default.Edit
                    val label = if (selectedFilter == SpaceFilter.Spaces) "Create Farmer Hub" else "Share Field Update"

                    ExtendedFloatingActionButton(
                        text = { Text(label) },
                        icon = { Icon(icon, null) },
                        onClick = if (selectedFilter == SpaceFilter.Spaces) onCreateSpaceClick else onAddStoryClick,
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        shape = RoundedCornerShape(16.dp)
                    )
                }
            },
            modifier = Modifier.weight(0.4f)
        ) { paddingValues ->
            Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
                AnimatedContent(
                    targetState = selectedFilter,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    label = "ContentTransition"
                ) { filter ->
                    when (filter) {
                        SpaceFilter.Spaces -> {
                            val spaces by viewModel.filteredSpaces.collectAsState()
                            SpacesListContent(
                                spaces = spaces,
                                onSpaceClick = { spaceId ->
                                    println("[UI] User clicked space: $spaceId. Triggering Join...")
                                    viewModel.joinSpace(spaceId)
                                    onSpaceClick(spaceId)
                                },
                                activeSpaceId = activeId
                            )
                        }
                        SpaceFilter.Memories -> {
                            val memories by viewModel.filteredFeeds.collectAsState()
                            MemoriesScreen(
                                modifier = Modifier.fillMaxSize(),
                                stories = memories,
                                viewModel = viewModel,
                                onCommentClick = onCommentClick,
                                onBookmarkClick = { storyId -> viewModel.toggleBookmark(memories.find { it.storyId == storyId }!!) },
                                navController = navController
                            )
                        }
                    }
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
                "Your Farm Resilience Vault",
                style = PoppinsTypography().titleLarge,
                fontWeight = FontWeight.Bold
            )

            PulseBadgeCard(totalPoints = statsState.stats.resiliencePoints)

            WellnessStatsRow(stats = statsState.stats)

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
                        "Your farmer hubs are encrypted and AI-moderated to ensure verified agricultural knowledge.",
                        style = PoppinsTypography().bodySmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
            AIPrivacyCard()
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpacesHomeScreenCompat(
    viewModel: SpaceViewModel,
    selectedFilter: SpaceFilter,
    onFilterSelected: (SpaceFilter) -> Unit,
    onSpaceClick: (spaceId: String) -> Unit,
    onCreateSpaceClick: () -> Unit,
    onAddStoryClick: () -> Unit,
    onCommentClick: (String) -> Unit,
    navController: NavController
) {
    val activeId by viewModel.activeSpaceId.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var isSearchExpanded by remember { mutableStateOf(false) }
    val isSearching by viewModel.isSearching.collectAsState()

    Scaffold(
        topBar = {
            if (isSearching) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth().height(2.dp),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = Color.Transparent
                )
            }

            Column(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.background)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { navController.navigateUp() },
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                    SearchBar(
                        query = searchQuery,
                        onQueryChange = {
                            searchQuery = it
                            viewModel.onSearchQueryChanged(it)
                        },
                        onSearch = {},
                        isExpanded = isSearchExpanded,
                        onToggleExpanded = { isSearchExpanded = !isSearchExpanded },
                        placeholderText = "Search farmer hubs (e.g. Maize, Pest Control, Irrigation)...",
                        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                    )
                }
                if (!isSearchExpanded) {
                    FilterTabBar(
                        selectedFilter = selectedFilter,
                        onFilterSelected = onFilterSelected
                    )
                }
            }
        },
        floatingActionButton = {
            when (selectedFilter) {
                SpaceFilter.Spaces -> FloatingActionButton(
                    onClick = onCreateSpaceClick,
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.clip(CircleShape)
                ) { Icon(
                    Icons.Default.Add,
                    null,
                    tint = MaterialTheme.colorScheme.onPrimary
                ) }
                SpaceFilter.Memories -> FloatingActionButton(
                    onClick = onAddStoryClick,
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.clip(CircleShape)
                ) { Icon(
                    Icons.Default.Edit,
                    null,
                    tint = MaterialTheme.colorScheme.onPrimary
                ) }
            }
        }
    ) { paddingValues ->
        AnimatedContent(
            targetState = selectedFilter,
            modifier = Modifier.padding(paddingValues),
            label = "ScreenContentCompat"
        ) { filter ->
            when (filter) {
                SpaceFilter.Spaces -> {
                    val spaces by viewModel.filteredSpaces.collectAsState()
                    SpacesListContent(
                        spaces = spaces,
                        onSpaceClick = { spaceId ->
                            println("[UI] User clicked space: $spaceId. Triggering Join...")
                            viewModel.joinSpace(spaceId)
                            onSpaceClick(spaceId)
                        },
                        activeSpaceId = activeId
                    )
                }
                SpaceFilter.Memories -> {
                    val memories by viewModel.filteredFeeds.collectAsState()
                    MemoriesScreen(
                        modifier = Modifier.fillMaxSize(),
                        stories = memories,
                        viewModel = viewModel,
                        onCommentClick = onCommentClick,
                        onBookmarkClick = {},
                        navController = navController
                    )
                }
            }
        }
    }
}



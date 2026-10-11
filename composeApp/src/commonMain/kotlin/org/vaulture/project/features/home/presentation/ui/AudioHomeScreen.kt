package org.vaulture.project.features.home.presentation.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import org.vaulture.project.core.theme.PoppinsTypography
import org.vaulture.project.data.repos.SearchContext
import org.vaulture.project.data.repos.SearchableItem
import org.vaulture.project.presentation.ui.components.FilterChips
import org.vaulture.project.presentation.ui.components.RhythmItem
import org.vaulture.project.presentation.ui.components.SearchBar
import org.vaulture.project.features.home.presentation.viewmodel.RhythmViewModel
import org.vaulture.project.navigation.Routes
import kotlin.math.roundToInt
import kotlin.text.contains
import kotlin.text.lowercase

data class RhythmCategory(val name: String, val tags: List<String>)

@Composable
fun RhythmHomeScreen(
    navController: NavHostController,
    viewModel: RhythmViewModel,
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsState()
    val rawSuggestions by viewModel.searchSuggestions.collectAsState()

    var isSearchExpanded by remember { mutableStateOf(false) }
    val recentSearches = remember { mutableStateListOf<String>() }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val screenWidth = maxWidth
        val isLargeScreen = screenWidth >= 920.dp
        val padding = if (isLargeScreen) 32.dp else 16.dp
        val searchBarWidthFraction = if (isLargeScreen) 0.7f else 1f

        Column(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = padding, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (!isSearchExpanded) {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Default.Cancel,
                            contentDescription = "Cancel",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                SearchBar(
                    query = uiState.searchQuery,
                    onQueryChange = { newQuery ->
                        viewModel.updateSearchQuery(newQuery, SearchContext.RHYTHM)
                    },
                    onSearch = { query ->
                        if (query.isNotBlank() && !recentSearches.contains(query)) {
                            recentSearches.add(0, query)
                        }
                        isSearchExpanded = false
                    },
                    isExpanded = isSearchExpanded,
                    onToggleExpanded = { isSearchExpanded = !isSearchExpanded },
                    suggestions = rawSuggestions.map {
                        SearchableItem.RhythmItem(it)
                    },
                    onSuggestionClick = { suggestion ->
                        viewModel.updateSearchQuery(suggestion.title, SearchContext.RHYTHM)
                        isSearchExpanded = false
                    },
                    recentSearches = recentSearches.toList(),
                    placeholderText = "Search audio guides...",
                    modifier = Modifier.weight(1f).fillMaxWidth(searchBarWidthFraction)
                )

                if (!isSearchExpanded) {
                    Spacer(Modifier.width(8.dp))
                    IconButton(onClick = { navController.navigate(Routes.CREATE_MEDIA_GUIDE) }) {
                        Icon(
                            imageVector = Icons.Default.AddCircle,
                            contentDescription = "Upload Audio Guide",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }

            RhythmHomeContent(
                searchQuery = uiState.searchQuery,
                navController = navController,
                viewModel = viewModel,
                padding = padding,
                screenWidth = screenWidth
            )
        }
    }
}

@Composable
fun RhythmHomeContent(
    searchQuery: String,
    navController: NavHostController,
    viewModel: RhythmViewModel,
    padding: Dp,
    screenWidth: Dp
) {
    val uiState by viewModel.uiState.collectAsState()
    val videoGuideRepository: org.vaulture.project.features.home.domain.repository.VideoGuideRepository = org.koin.compose.koinInject()
    val videoGuides by remember(videoGuideRepository) {
        videoGuideRepository.getVideoGuidesStream()
    }.collectAsState(initial = emptyList())
    var selectedVideo by remember { mutableStateOf<org.vaulture.project.features.home.domain.model.FarmerVideoGuide?>(null) }

    if (selectedVideo != null) {
        org.vaulture.project.presentation.ui.components.VideoGuidePlayerDialog(
            video = selectedVideo!!,
            onDismiss = { selectedVideo = null }
        )
    }

    val filteredBySearch = remember(uiState.tracks, searchQuery) {
        if (searchQuery.isBlank()) uiState.tracks
        else uiState.tracks.filter { track ->
            track.title.contains(searchQuery, ignoreCase = true) ||
                    track.artist.contains(searchQuery, ignoreCase = true) ||
                    track.tags.any { it.contains(searchQuery, ignoreCase = true) }
        }
    }

    val categories = remember {
        listOf(
            RhythmCategory(
                "Pest Control",
                listOf("pest-control", "organic-pesticide", "neem")
            ),
            RhythmCategory(
                "Soil Health",
                listOf("soil-health", "nitrogen-fixation", "mulching")
            ),
            RhythmCategory(
                "Irrigation",
                listOf("irrigation", "water-harvesting", "dry-season")
            ),
            RhythmCategory(
                "Crop Yield",
                listOf("yield", "intercropping", "maize", "cassava")
            )
        )
    }

    var selectedCategoryName by remember { mutableStateOf<String?>(null) }

    val finalTracks = remember(filteredBySearch, selectedCategoryName) {
        if (selectedCategoryName == null) filteredBySearch
        else filteredBySearch.filter { track ->
            track.tags.any { tag ->
                categories.find { it.name == selectedCategoryName }?.tags?.any { catTag ->
                    tag.equals(catTag, ignoreCase = true)
                } == true
            }
        }
    }

    val columnCount = (screenWidth.value / 180).roundToInt().coerceAtLeast(2)

    val hasCategorizedTracks = remember(uiState.tracks) {
        uiState.tracks.any { track ->
            categories.any { category ->
                track.tags.any { tag -> category.tags.contains(tag.lowercase()) }
            }
        }
    }

    if (selectedCategoryName == null && searchQuery.isBlank()) {

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            // First Row: Demonstration Video Guides
            item {
                Column(modifier = Modifier.fillMaxWidth().padding(top = 4.dp)) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = padding, vertical = 6.dp)
                    ) {
                        Text(
                            text = "Video Guides",
                            style = PoppinsTypography().headlineMedium.copy(
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(
                            text = "Practical video guides & tutorials",
                            style = PoppinsTypography().bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                    if (videoGuides.isEmpty()) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = padding, vertical = 6.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "No video guides yet",
                                        style = PoppinsTypography().titleSmall.copy(fontWeight = FontWeight.SemiBold)
                                    )
                                    Text(
                                        text = "Share a video guide from your profile!",
                                        style = PoppinsTypography().bodySmall.copy(
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    )
                                }
                                Spacer(Modifier.width(12.dp))
                                Button(
                                    onClick = { navController.navigate(Routes.CREATE_MEDIA_GUIDE) },
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text("Add Guide")
                                }
                            }
                        }
                    } else {
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = padding),
                            horizontalArrangement = Arrangement.spacedBy(14.dp),
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            items(
                                videoGuides,
                                key = { it.id }
                            ) { video ->
                                org.vaulture.project.presentation.ui.components.VideoGuideCard(
                                    video = video,
                                    onClick = { selectedVideo = video }
                                )
                            }
                        }
                    }
                }
            }

            // Second Section: Audio Guides with Filters
            item {
                Column(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = padding, vertical = 6.dp)
                    ) {
                        Text(
                            text = "Audio Guides",
                            style = PoppinsTypography().headlineMedium.copy(
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(
                            text = "Listen to practical voice advice & audio guides",
                            style = PoppinsTypography().bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                    Spacer(Modifier.height(10.dp))
                    FilterChips(
                        categories = categories.map { it.name },
                        selectedCategory = selectedCategoryName,
                        onFilterSelected = { selected ->
                            selectedCategoryName =
                                if (selectedCategoryName == selected) null else selected
                        },
                        modifier = Modifier.padding(horizontal = padding)
                    )
                }
            }

            if (!hasCategorizedTracks) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = padding, vertical = 12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Headphones,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                            Spacer(Modifier.height(12.dp))
                            Text(
                                text = "No Audio Guides Yet",
                                style = PoppinsTypography().titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = "Upload an audio guide or explore video guides above!",
                                style = PoppinsTypography().bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                            Spacer(Modifier.height(16.dp))
                            Button(
                                onClick = { navController.navigate(Routes.CREATE_MEDIA_GUIDE) },
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.AddCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(6.dp))
                                Text("Upload Guide")
                            }
                        }
                    }
                }
            } else {
                items(categories) { category ->
                    val categoryTracks = uiState.tracks.filter { track ->
                        track.tags.any { tag -> category.tags.contains(tag.lowercase()) }
                    }.take(8)

                    if (categoryTracks.isNotEmpty()) {
                        Column {
                            Text(
                                text = category.name,
                                style = PoppinsTypography().headlineMedium.copy(
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                modifier = Modifier.padding(horizontal = padding, vertical = 8.dp)
                            )
                            LazyRow(
                                contentPadding = PaddingValues(horizontal = padding),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(categoryTracks) { track ->
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
                    }
                }
            }
        }
    } else {

        Column(modifier = Modifier.fillMaxSize()) {
            FilterChips(
                categories = categories.map { it.name },
                selectedCategory = selectedCategoryName,
                onFilterSelected = { selected ->
                    selectedCategoryName = if (selectedCategoryName == selected) null else selected
                },
                modifier = Modifier.padding(horizontal = padding, vertical = 8.dp)
            )

            AnimatedContent(targetState = finalTracks, label = "GridTransition") { tracksToShow ->
                if (tracksToShow.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = padding, vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                            ),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp).fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Headphones,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(40.dp)
                                )
                                Spacer(Modifier.height(12.dp))
                                Text(
                                    text = if (searchQuery.isNotBlank()) "No guides matching '$searchQuery'" else "No guides in this category",
                                    style = PoppinsTypography().titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    text = "Try searching with different keywords or clear active filters.",
                                    style = PoppinsTypography().bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(Modifier.height(16.dp))
                                OutlinedButton(
                                    onClick = {
                                        viewModel.updateSearchQuery("", SearchContext.RHYTHM)
                                        selectedCategoryName = null
                                    },
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text("Clear Filters")
                                }
                            }
                        }
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(columnCount),
                        modifier = Modifier.fillMaxSize().padding(horizontal = padding),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(bottom = 32.dp)
                    ) {
                        items(tracksToShow) { track ->
                            RhythmItem(
                                track = track,
                                isSelected = uiState.currentTrack?.id == track.id,
                                onClick = {
                                    viewModel.playTrack(track)
                                    navController.navigate(Routes.RHYTHM_PLAYER(track.id))
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

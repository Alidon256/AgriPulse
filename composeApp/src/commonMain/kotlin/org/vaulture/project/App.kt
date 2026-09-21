package org.vaulture.project

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.firestore
import dev.gitlive.firebase.storage.storage
import kotlinx.coroutines.launch
import org.vaulture.project.core.theme.AppTheme
import org.vaulture.project.core.util.WindowSize
import org.vaulture.project.core.util.getWindowSizeClass
import org.vaulture.project.core.util.rememberKmpAudioPlayer
import org.vaulture.project.data.local.OnboardingManager
import org.vaulture.project.di.getSettingsViewModel
import org.vaulture.project.features.auth.data.AuthServiceImpl
import org.vaulture.project.features.auth.presentation.LoginViewModel
import org.vaulture.project.features.home.presentation.viewmodel.CBTViewModel
import org.vaulture.project.features.home.presentation.viewmodel.RhythmViewModel
import org.vaulture.project.navigation.BottomNavItem
import org.vaulture.project.navigation.NavGraph
import org.vaulture.project.navigation.NavDestination
import org.vaulture.project.navigation.Routes
import org.vaulture.project.presentation.ui.components.AppNavigationRail
import org.vaulture.project.presentation.ui.components.MiniPlayer
import org.vaulture.project.features.space.presentation.ui.CommentSheetContent
import org.vaulture.project.features.space.presentation.viewmodel.SpaceViewModel
import org.vaulture.project.features.wellness.presentation.viewmodel.WellnessViewModel


import org.koin.compose.KoinContext
import org.koin.compose.koinInject
import org.vaulture.project.di.initKoin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App(
    onGoogleSignInRequest: (() -> Unit)? = null
) {
    remember {
        runCatching { initKoin() }
    }

    KoinContext {
        val navController: NavHostController = rememberNavController()
        val loginViewModel: LoginViewModel = koinInject()
        val spaceViewModel: SpaceViewModel = koinInject()
        val rhythmViewModel: RhythmViewModel = koinInject()
        val wellnessViewModel: WellnessViewModel = koinInject()
        val cbtViewModel: CBTViewModel = koinInject()
        val settingsViewModel = getSettingsViewModel()
        remember(settingsViewModel) {
            OnboardingManager.init(settingsViewModel.settings)
        }
    val authState by loginViewModel.isAuthenticated.collectAsState(initial = null)
    val isFirstRun = remember(settingsViewModel) { !OnboardingManager.hasCompletedOnboarding }
    val playerState by rhythmViewModel.uiState.collectAsState()
    val settingsState by settingsViewModel.uiState.collectAsState()

    LaunchedEffect(authState) {
        if (authState == true) {
            OnboardingManager.completeOnboarding()
        }
    }

    val startDestination: NavDestination? = when (authState) {
        true -> Routes.HOME
        false -> if (isFirstRun) Routes.SPLASH else Routes.LOGIN
        null -> null
    }

    val bottomNavItems = listOf(
        BottomNavItem("Home", Icons.Outlined.Home, Icons.Filled.Home, Routes.HOME),
        BottomNavItem("Space", Icons.Outlined.People, Icons.Filled.People, Routes.SPACES),
        BottomNavItem("Profile", Icons.Outlined.Person, Icons.Filled.Person, Routes.PROFILE)
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val mainNavRoutePatterns = remember {
        setOf(
            Routes.HOME.routePattern,
            Routes.SPACES.routePattern,
            Routes.PROFILE.routePattern,
            Routes.SETTINGS.routePattern
        )
    }
    val isNavVisible = currentDestination?.hierarchy?.any { destination ->
        val routeName = destination.route ?: ""
        mainNavRoutePatterns.any { pattern -> routeName.contains(pattern, ignoreCase = true) }
    } == true

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var selectedStoryIdForComments by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    AppTheme(
        content = {
            BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                val windowSize = getWindowSizeClass(maxWidth)
                val isExpandedLayout = windowSize != WindowSize.Compact

                if (isExpandedLayout) {
                    // Large/Expanded/Tablet Screen Layout with Hau-styled Side Navigation Rail
                    Row(modifier = Modifier.fillMaxSize()) {
                        if (isNavVisible) {
                            AppNavigationRail(
                                currentDestination = currentDestination,
                                onNavigate = { destination ->
                                    navController.navigate(destination) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                onSignOut = {
                                    scope.launch {
                                        loginViewModel.authService.signOut()
                                        navController.navigate(Routes.LOGIN) {
                                            popUpTo(0)
                                        }
                                    }
                                }
                            )
                        }

                        Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
                            Scaffold(
                                modifier = Modifier.fillMaxSize(),
                                bottomBar = {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(Color.Transparent)
                                    ) {
                                        val currentRoute = currentDestination?.route
                                        val isPlayerScreen = currentRoute?.contains("RHYTHM_PLAYER") == true

                                        AnimatedVisibility(
                                            visible = (playerState.currentTrack != null) && !isPlayerScreen,
                                            enter = slideInVertically { it } + fadeIn(),
                                            exit = slideOutVertically { it } + fadeOut()
                                        ) {
                                            playerState.currentTrack?.let { track ->
                                                Box(
                                                    modifier = Modifier.padding(
                                                        bottom = 8.dp,
                                                        start = 8.dp,
                                                        end = 8.dp
                                                    )
                                                ) {
                                                    MiniPlayer(
                                                        track = track,
                                                        isPlaying = playerState.isPlaying,
                                                        onTogglePlay = { rhythmViewModel.togglePlayback() },
                                                        onNext = { rhythmViewModel.playNext() },
                                                        onClose = { rhythmViewModel.closePlayer() },
                                                        onClick = {
                                                            navController.navigate(Routes.RHYTHM_PLAYER(track.id))
                                                        }
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            ) { paddingValues ->
                                AppContent(
                                    navController = navController,
                                    startDestination = startDestination,
                                    loginViewModel = loginViewModel,
                                    spaceViewModel = spaceViewModel,
                                    wellnessViewModel = wellnessViewModel,
                                    rhythmViewModel = rhythmViewModel,
                                    cbtViewModel = cbtViewModel,
                                    settingsViewModel = settingsViewModel,
                                    authState = authState,
                                    onGoogleSignInRequest = onGoogleSignInRequest,
                                    selectedStoryIdForComments = selectedStoryIdForComments,
                                    onCommentClick = { selectedStoryIdForComments = it },
                                    sheetState = sheetState,
                                    onDismissComments = { selectedStoryIdForComments = null },
                                    paddingValues = paddingValues
                                )
                            }
                        }
                    }
                } else {
                    // Compact Mobile Screen Layout with Bottom Navigation Bar
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        bottomBar = {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color.Transparent)
                            ) {
                                val currentRoute = currentDestination?.route
                                val isPlayerScreen = currentRoute?.contains("RHYTHM_PLAYER") == true

                                AnimatedVisibility(
                                    visible = playerState.currentTrack != null && !isPlayerScreen,
                                    enter = slideInVertically { it } + fadeIn(),
                                    exit = slideOutVertically { it } + fadeOut()
                                ) {
                                    playerState.currentTrack?.let { track ->
                                        Box(
                                            modifier = Modifier.padding(
                                                bottom = 8.dp,
                                                start = 8.dp,
                                                end = 8.dp
                                            )
                                        ) {
                                            MiniPlayer(
                                                track = track,
                                                isPlaying = playerState.isPlaying,
                                                onTogglePlay = { rhythmViewModel.togglePlayback() },
                                                onNext = { rhythmViewModel.playNext() },
                                                onClose = { rhythmViewModel.closePlayer() },
                                                onClick = {
                                                    navController.navigate(Routes.RHYTHM_PLAYER(track.id))
                                                }
                                            )
                                        }
                                    }
                                }

                                AnimatedVisibility(
                                    visible = isNavVisible,
                                    enter = slideInVertically { it },
                                    exit = slideOutVertically { it }
                                ) {
                                    NavigationBar(
                                        modifier = Modifier.fillMaxWidth().height(64.dp),
                                        containerColor = MaterialTheme.colorScheme.background,
                                        tonalElevation = 8.dp
                                    ) {
                                        bottomNavItems.forEach { item ->
                                            val isSelected = currentDestination?.hierarchy?.any {
                                                it.route?.contains(
                                                    item.destination.routePattern,
                                                    ignoreCase = true
                                                ) == true
                                            } == true

                                            NavigationBarItem(
                                                selected = isSelected,
                                                onClick = {
                                                    navController.navigate(item.destination) {
                                                        popUpTo(navController.graph.findStartDestination().id) {
                                                            saveState = true
                                                        }
                                                        launchSingleTop = true
                                                        restoreState = true
                                                    }
                                                },
                                                icon = {
                                                    Icon(
                                                        if (isSelected) item.selectedIcon else item.unselectedIcon,
                                                        item.label
                                                    )
                                                },
                                                label = {
                                                    Text(
                                                        item.label,
                                                        fontSize = 11.sp,
                                                        style = MaterialTheme.typography.labelSmall,
                                                        modifier = Modifier.padding(top = 0.dp)
                                                    )
                                                },
                                                colors = NavigationBarItemDefaults.colors(
                                                    selectedIconColor = MaterialTheme.colorScheme.primary,
                                                    selectedTextColor = MaterialTheme.colorScheme.primary,
                                                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                                    indicatorColor = Color.Transparent
                                                ),
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    ) { paddingValues ->
                        AppContent(
                            navController = navController,
                            startDestination = startDestination,
                            loginViewModel = loginViewModel,
                            spaceViewModel = spaceViewModel,
                            wellnessViewModel = wellnessViewModel,
                            rhythmViewModel = rhythmViewModel,
                            cbtViewModel = cbtViewModel,
                            settingsViewModel = settingsViewModel,
                            authState = authState,
                            onGoogleSignInRequest = onGoogleSignInRequest,
                            selectedStoryIdForComments = selectedStoryIdForComments,
                            onCommentClick = { selectedStoryIdForComments = it },
                            sheetState = sheetState,
                            onDismissComments = { selectedStoryIdForComments = null },
                            paddingValues = paddingValues
                        )
                    }
                }
            }
        },
        themeMode = settingsState.themeMode,
        themePalette = settingsState.themePalette
    )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppContent(
    navController: NavHostController,
    startDestination: NavDestination?,
    loginViewModel: LoginViewModel,
    spaceViewModel: SpaceViewModel,
    wellnessViewModel: WellnessViewModel,
    rhythmViewModel: RhythmViewModel,
    cbtViewModel: CBTViewModel,
    settingsViewModel: org.vaulture.project.features.profile.presentation.viewmodel.SettingsViewModel,
    authState: Boolean?,
    onGoogleSignInRequest: (() -> Unit)?,
    selectedStoryIdForComments: String?,
    onCommentClick: (String) -> Unit,
    sheetState: SheetState,
    onDismissComments: () -> Unit,
    paddingValues: PaddingValues
) {
    val scope = rememberCoroutineScope()

    if (selectedStoryIdForComments != null) {
        ModalBottomSheet(
            onDismissRequest = onDismissComments,
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface,
            dragHandle = { BottomSheetDefaults.DragHandle() },
        ) {
            CommentSheetContent(
                storyId = selectedStoryIdForComments,
                viewModel = spaceViewModel,
                onClose = {
                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                        if (!sheetState.isVisible) onDismissComments()
                    }
                }
            )
        }
    }

    NavGraph(
        navController = navController,
        startDestination = startDestination,
        loginViewModel = loginViewModel,
        spaceViewModel = spaceViewModel,
        wellnessViewModel = wellnessViewModel,
        rhythmViewModel = rhythmViewModel,
        cbtViewModel = cbtViewModel,
        settingsViewModel = settingsViewModel,
        authState = authState,
        onGoogleSignInRequest = onGoogleSignInRequest,
        onCommentClick = onCommentClick,
        modifier = Modifier.fillMaxSize().padding(paddingValues)
            .consumeWindowInsets(paddingValues)
    )
}

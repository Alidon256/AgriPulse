package org.vaulture.project.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.firestore
import dev.gitlive.firebase.storage.storage
import org.koin.compose.koinInject
import kotlinx.coroutines.launch
import org.vaulture.project.data.local.OnboardingManager
import org.vaulture.project.features.onboarding.presentation.ui.ConnectScreen
import org.vaulture.project.features.onboarding.presentation.ui.InsightScreen
import org.vaulture.project.features.auth.presentation.LoginScreen
import org.vaulture.project.features.onboarding.presentation.ui.OnboardingScreenOne
import org.vaulture.project.features.onboarding.presentation.ui.OnboardingScreenTwo
import org.vaulture.project.features.onboarding.presentation.ui.SplashScreen
import org.vaulture.project.features.onboarding.presentation.ui.WelcomeScreen
import org.vaulture.project.features.profile.presentation.ui.MindsetPortfolioScreen
import org.vaulture.project.presentation.ui.screens.space.AddStoryScreen
import org.vaulture.project.features.space.presentation.ui.SpacesHomeScreen
import org.vaulture.project.features.auth.presentation.LoginViewModel
import org.vaulture.project.features.home.presentation.ui.AnalyticsScreen
import org.vaulture.project.features.home.presentation.ui.CBTScreen
import org.vaulture.project.features.home.presentation.ui.HomeScreen
import org.vaulture.project.features.home.presentation.ui.RhythmHomeScreen
import org.vaulture.project.features.home.presentation.ui.RhythmPlayerScreen
import org.vaulture.project.features.home.presentation.ui.CreateMediaGuideScreen
import org.vaulture.project.features.home.presentation.viewmodel.AnalyticsViewModel
import org.vaulture.project.features.home.presentation.viewmodel.CBTViewModel
import org.vaulture.project.features.home.presentation.viewmodel.CreateMediaGuideViewModel
import org.vaulture.project.features.home.presentation.viewmodel.RhythmViewModel
import org.vaulture.project.features.profile.presentation.ui.EditPortfolioScreen
import org.vaulture.project.features.profile.presentation.ui.ProfileScreen
import org.vaulture.project.features.profile.presentation.ui.SettingsScreen
import org.vaulture.project.features.profile.presentation.viewmodel.SettingsViewModel
import org.vaulture.project.features.space.presentation.ui.CreateSpaceScreen
import org.vaulture.project.features.space.presentation.ui.SpaceFilter
import org.vaulture.project.features.space.presentation.ui.SpacesScreen
import org.vaulture.project.features.space.presentation.viewmodel.ProfileFilter
import org.vaulture.project.features.space.presentation.viewmodel.SpaceViewModel
import org.vaulture.project.features.wellness.presentation.ui.WellnessTimerScreen
import org.vaulture.project.features.wellness.presentation.viewmodel.WellnessViewModel
import org.vaulture.project.ui.screens.SpaceDetailScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: NavDestination?,
    loginViewModel: LoginViewModel,
    spaceViewModel: SpaceViewModel,
    wellnessViewModel: WellnessViewModel,
    rhythmViewModel: RhythmViewModel,
    cbtViewModel: CBTViewModel,
    settingsViewModel: SettingsViewModel,
    authState: Boolean?,
    onGoogleSignInRequest: (() -> Unit)?,
    onCommentClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    var selectedProfileFilter by rememberSaveable { mutableStateOf(ProfileFilter.MY_POSTS) }

    if (startDestination == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = modifier
        ) {
            composable<Routes.SPLASH> {
                SplashScreen(
                    onTimeout = {
                        navController.navigate(Routes.WELCOME) {
                            popUpTo<Routes.SPLASH> {
                                inclusive = true
                            }
                        }
                    })
            }
            composable<Routes.WELCOME> {
                WelcomeScreen(
                    onGetStarted = { navController.navigate(Routes.ONBOARDING_ONE) },
                    onLoginClicked = {
                        OnboardingManager.completeOnboarding()
                        navController.navigate(Routes.LOGIN) {
                            popUpTo<Routes.WELCOME> {
                                inclusive = true
                            }
                        }
                    }
                )
            }
            composable<Routes.ONBOARDING_ONE> {
                OnboardingScreenOne(
                    onNext = { navController.navigate(Routes.ONBOARDING_TWO) },
                    onSkip = {
                        OnboardingManager.completeOnboarding()
                        navController.navigate(Routes.LOGIN) {
                            popUpTo<Routes.WELCOME> {
                                inclusive = true
                            }
                        }
                    }
                )
            }
            composable<Routes.ONBOARDING_TWO> {
                OnboardingScreenTwo(
                    onNext = { navController.navigate(Routes.BEST_DEALS) },
                    onSkip = {
                        OnboardingManager.completeOnboarding()
                        navController.navigate(Routes.LOGIN) {
                            popUpTo<Routes.WELCOME> {
                                inclusive = true
                            }
                        }
                    }
                )
            }
            composable<Routes.BEST_DEALS> {
                InsightScreen(
                    onGetStarted = {
                        OnboardingManager.completeOnboarding()
                        navController.navigate(Routes.CONNECT)
                    }
                )
            }
            composable<Routes.CONNECT> {
                ConnectScreen(
                    onNavigateToLogin = {
                        OnboardingManager.completeOnboarding()
                        navController.navigate(Routes.LOGIN) {
                            popUpTo(navController.graph.id) {
                                inclusive = true
                            }
                        }
                    },
                    onNavigateToSignUp = {
                        OnboardingManager.completeOnboarding()
                        navController.navigate(Routes.SIGN_UP) {
                            popUpTo(navController.graph.id) {
                                inclusive = true
                            }
                        }
                    })
            }

            composable<Routes.PORTFOLIO> { backStackEntry ->
                val args = backStackEntry.toRoute<Routes.PORTFOLIO>()
                MindsetPortfolioScreen(
                    userId = args.userId,
                    viewModel = spaceViewModel,
                    onBack = { navController.popBackStack() },
                    onEditClick = { navController.navigate(Routes.EDIT_USER_INFO) }
                )
            }
            composable<Routes.EDIT_USER_INFO> {
                EditPortfolioScreen(
                    viewModel = spaceViewModel,
                    onBack = { navController.popBackStack() }
                )
            }

            composable<Routes.LOGIN> {
                LaunchedEffect(authState, navController) {
                    if (authState == true) {
                        navController.navigate(Routes.HOME) {
                            popUpTo(navController.graph.id) {
                                inclusive = true
                            }
                        }
                    }
                }
                LoginScreen(
                    viewModel = loginViewModel,
                    onGoogleSignInRequest = {
                        onGoogleSignInRequest?.invoke()
                    },
                    initialSignUpMode = false
                )
            }
            composable<Routes.SIGN_UP> {
                LaunchedEffect(authState, navController) {
                    if (authState == true) {
                        navController.navigate(Routes.HOME) {
                            popUpTo(navController.graph.id) {
                                inclusive = true
                            }
                        }
                    }
                }
                LoginScreen(
                    viewModel = loginViewModel,
                    onGoogleSignInRequest = { onGoogleSignInRequest?.invoke() },
                    initialSignUpMode = true
                )
            }

            composable<Routes.HOME> {
                HomeScreen(
                    onNavigateToCheckIn = { navController.navigate(Routes.CHECK_IN) },
                    navController = navController,
                    wellnessViewModel = wellnessViewModel,
                    onSignOut = {
                        scope.launch {
                            loginViewModel.authService.signOut()
                            navController.navigate(Routes.LOGIN) {
                                popUpTo(0)
                            }
                        }
                    },
                    spaceViewModel = spaceViewModel
                )
            }
            composable<Routes.CHECK_IN> {
                CBTScreen(
                    navController = navController,
                    viewModel = cbtViewModel
                )
            }
            composable<Routes.SETTINGS> {
                SettingsScreen(
                    viewModel = settingsViewModel,
                    onBack = { navController.popBackStack() },
                    onSignOut = { revisitOnboarding ->
                        scope.launch {
                            loginViewModel.authService.signOut()
                            if (revisitOnboarding) {
                                OnboardingManager.resetOnboarding()
                                navController.navigate(Routes.WELCOME) {
                                    popUpTo(0) { inclusive = true }
                                }
                            } else {
                                navController.navigate(Routes.LOGIN) {
                                    popUpTo(0) { inclusive = true }
                                }
                            }
                        }
                    },
                    onRevisitOnboarding = {
                        OnboardingManager.resetOnboarding()
                        navController.navigate(Routes.WELCOME)
                    }
                )
            }

            composable<Routes.MELODIES> {
                RhythmHomeScreen(
                    navController = navController,
                    viewModel = rhythmViewModel
                )
            }

            composable<Routes.CREATE_MEDIA_GUIDE> {
                val createMediaGuideViewModel: CreateMediaGuideViewModel = koinInject()
                CreateMediaGuideScreen(
                    viewModel = createMediaGuideViewModel,
                    onBack = { navController.popBackStack() },
                    onPublished = { navController.popBackStack() }
                )
            }

            composable<Routes.RHYTHM_PLAYER> { backStackEntry ->
                val route: Routes.RHYTHM_PLAYER = backStackEntry.toRoute()
                RhythmPlayerScreen(
                    trackId = route.trackId,
                    viewModel = rhythmViewModel,
                    onBack = { navController.popBackStack() }
                )
            }

            composable<Routes.WELLNESS_TIMER> {
                WellnessTimerScreen(
                    viewModel = wellnessViewModel,
                    onBack = navController::popBackStack,
                )
            }

            composable<Routes.ANALYTICS> {
                val analyticsViewModel: AnalyticsViewModel = koinInject()
                AnalyticsScreen(
                    navController = navController,
                    viewModel = analyticsViewModel
                )
            }
            composable<Routes.SPACES> {
                var selectedFilter by rememberSaveable { mutableStateOf(SpaceFilter.Spaces) }
                SpacesScreen(
                    selectedFilter = selectedFilter,
                    onFilterSelected = { filter -> selectedFilter = filter },
                    onSpaceClick = { spaceId ->
                        navController.navigate(Routes.SPACE_DETAIL(spaceId = spaceId))
                    },
                    onCreateSpaceClick = {
                        navController.navigate(Routes.CREATE_SPACE)
                    },
                    onAddStoryClick = {
                        navController.navigate(Routes.ADD_STORY)
                    },
                    viewModel = spaceViewModel,
                    onCommentClick = onCommentClick,
                    wellnessViewModel = wellnessViewModel,
                    navController = navController,
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
            composable<Routes.COMMUNITY> {
                var selectedFilter by rememberSaveable { mutableStateOf(SpaceFilter.Spaces) }
                SpacesHomeScreen(
                    selectedFilter = selectedFilter,
                    onFilterSelected = { filter -> selectedFilter = filter },
                    onSpaceClick = { spaceId ->
                        navController.navigate(Routes.SPACE_DETAIL(spaceId = spaceId))
                    },
                    onCreateSpaceClick = {
                        navController.navigate(Routes.CREATE_SPACE)
                    },
                    onAddStoryClick = {
                        navController.navigate(Routes.ADD_STORY)
                    },
                    viewModel = spaceViewModel,
                    onCommentClick = onCommentClick,
                    wellnessViewModel = wellnessViewModel,
                    navController = navController,
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
            composable<Routes.CREATE_SPACE> {
                CreateSpaceScreen(
                    onNavigateBack = { navController.popBackStack() },
                    viewModel = spaceViewModel,
                    onSpaceCreated = { navController.popBackStack() },
                )
            }

            composable<Routes.SPACE_DETAIL> { backStackEntry ->
                val spaceId = backStackEntry.toRoute<Routes.SPACE_DETAIL>().spaceId
                SpaceDetailScreen(
                    spaceId = spaceId,
                    onNavigateBack = { navController.popBackStack() },
                    viewModel = spaceViewModel,
                    onSpaceSelected = { newSpaceId ->
                        navController.navigate(Routes.SPACE_DETAIL(spaceId = newSpaceId)) {
                            popUpTo(navController.graph.id) { inclusive = true }
                        }
                    }
                )
            }

            composable<Routes.ADD_STORY> {
                AddStoryScreen(
                    viewModel = spaceViewModel,
                    onStoryAdded = { navController.popBackStack() },
                    onCancel = { navController.popBackStack() }
                )
            }
            composable<Routes.PROFILE> {
                ProfileScreen(
                    wellnessViewModel = wellnessViewModel,
                    onSignOut = {
                        scope.launch {
                            loginViewModel.authService.signOut()
                            navController.navigate(Routes.LOGIN) {
                                popUpTo(0)
                            }
                        }
                    },
                    navController = navController,
                    spaceViewModel = spaceViewModel,
                    onFilterSelected = { selectedProfileFilter = it },
                    onCommentClick = onCommentClick,
                    selectedFilter = selectedProfileFilter,
                    onNavigateToSettings = {navController.navigate(Routes.SETTINGS)},
                    onNavigateToEditProfile = {navController.navigate(Routes.EDIT_USER_INFO)}
                )
            }
        }
    }
}

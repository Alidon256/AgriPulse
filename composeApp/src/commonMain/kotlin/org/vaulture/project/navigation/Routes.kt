package org.vaulture.project.navigation

import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.serialization.Serializable

@Serializable
sealed interface NavDestination {
    val routePattern: String
}

@Serializable
object Routes {
    @Serializable data object SPLASH : NavDestination { override val routePattern: String = "SPLASH" }
    @Serializable data object WELCOME : NavDestination { override val routePattern: String = "WELCOME" }
    @Serializable data object ONBOARDING_ONE : NavDestination { override val routePattern: String = "ONBOARDING_ONE" }
    @Serializable data object ONBOARDING_TWO : NavDestination { override val routePattern: String = "ONBOARDING_TWO" }
    @Serializable data object BEST_DEALS : NavDestination { override val routePattern: String = "BEST_DEALS" }
    @Serializable data object CONNECT : NavDestination { override val routePattern: String = "CONNECT" }
    @Serializable data object LOGIN : NavDestination { override val routePattern: String = "LOGIN" }
    @Serializable data object SIGN_UP : NavDestination { override val routePattern: String = "SIGN_UP" }

    // Main App Routes
    @Serializable data object HOME : NavDestination { override val routePattern: String = "HOME" }
    @Serializable data object SPACES : NavDestination { override val routePattern: String = "SPACES" }
    @Serializable data object PROFILE : NavDestination { override val routePattern: String = "PROFILE" }

    @Serializable data class SPACE_DETAIL(val spaceId: String) : NavDestination { override val routePattern: String = "SPACE_DETAIL" }
    @Serializable data object ADD_STORY : NavDestination { override val routePattern: String = "ADD_STORY" }
    @Serializable data object CHECK_IN: NavDestination { override val routePattern: String = "CHECK_IN" }
    @Serializable data object ANALYTICS: NavDestination { override val routePattern: String = "ANALYTICS" }
    @Serializable data object MELODIES: NavDestination { override val routePattern: String = "AUDIO" }
    @Serializable data class RHYTHM_PLAYER(val trackId: String) : NavDestination { override val routePattern: String = "RHYTHM_PLAYER" }
    @Serializable data object WELLNESS_TIMER: NavDestination { override val routePattern: String = "WELLNESS_TIMER" }
    @Serializable data object COMMUNITY: NavDestination { override val routePattern: String = "COMMUNITY" }
    @Serializable data object SETTINGS : NavDestination { override val routePattern: String = "SETTINGS" }
    @Serializable data class PORTFOLIO(val userId: String) : NavDestination { override val routePattern: String = "PORTFOLIO" }
    @Serializable data object CREATE_SPACE : NavDestination { override val routePattern: String = "CREATE_SPACE" }
    @Serializable data object EDIT_USER_INFO : NavDestination { override val routePattern: String = "EDIT_USER_INFO" }
    @Serializable data object CREATE_MEDIA_GUIDE : NavDestination { override val routePattern: String = "CREATE_MEDIA_GUIDE" }

    // Agronomic Resilience Aliases
    val CROP_DIAGNOSTIC get() = CHECK_IN
    val AUDIO_GUIDES get() = MELODIES
    val UPLOAD_MEDIA_GUIDE get() = CREATE_MEDIA_GUIDE
}

data class BottomNavItem(
    val label: String,
    val unselectedIcon: ImageVector,
    val selectedIcon: ImageVector,
    val destination: NavDestination
)

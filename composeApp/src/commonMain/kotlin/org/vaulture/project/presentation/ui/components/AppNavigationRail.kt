package org.vaulture.project.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Analytics
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import org.vaulture.project.navigation.BottomNavItem
import org.vaulture.project.navigation.Routes

@Composable
fun AppNavigationRail(
    currentDestination: NavDestination?,
    onNavigate: (org.vaulture.project.navigation.NavDestination) -> Unit,
    onSignOut: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val topItems = listOf(
        BottomNavItem("Home", Icons.Outlined.Home, Icons.Filled.Home, Routes.HOME),
        BottomNavItem("Spaces", Icons.Outlined.People, Icons.Filled.People, Routes.SPACES),
        BottomNavItem("Insights", Icons.Outlined.Analytics, Icons.Filled.Analytics, Routes.ANALYTICS)
    )

    val bottomItems = listOf(
        BottomNavItem("Profile", Icons.Outlined.Person, Icons.Filled.Person, Routes.PROFILE),
        BottomNavItem("Settings", Icons.Outlined.Settings, Icons.Filled.Settings, Routes.SETTINGS)
    )

    Surface(
        modifier = modifier.width(72.dp).fillMaxHeight(),
        color = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp),
        tonalElevation = 1.dp
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    topItems.forEach { item ->
                        val isSelected = currentDestination?.hierarchy?.any {
                            it.route?.contains(item.destination.routePattern, ignoreCase = true) == true
                        } == true

                        RailItem(
                            item = item,
                            isSelected = isSelected,
                            onClick = { onNavigate(item.destination) }
                        )
                    }
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    bottomItems.forEach { item ->
                        val isSelected = currentDestination?.hierarchy?.any {
                            it.route?.contains(item.destination.routePattern, ignoreCase = true) == true
                        } == true

                        RailItem(
                            item = item,
                            isSelected = isSelected,
                            onClick = { onNavigate(item.destination) }
                        )
                    }

                    if (onSignOut != null) {
                        RailActionItem(
                            label = "Sign Out",
                            icon = Icons.AutoMirrored.Filled.Logout,
                            iconTint = MaterialTheme.colorScheme.error,
                            onClick = onSignOut
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(1.dp)
                    .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
            )
        }
    }
}

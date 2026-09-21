package org.vaulture.project.core.util

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Represents window size classes for responsive navigation and layout adaptation.
 */
enum class WindowSize {
    Compact,   // Phones / narrow screens (< 600.dp)
    Medium,    // Foldables / medium screens (600.dp - 840.dp)
    Expanded,  // Tablets / desktop viewports (840.dp - 1200.dp)
    Large      // Extra wide desktop viewports (>= 1200.dp)
}

/**
 * Returns the corresponding [WindowSize] for a given layout width.
 */
fun getWindowSizeClass(width: Dp): WindowSize {
    return when {
        width < 600.dp -> WindowSize.Compact
        width < 840.dp -> WindowSize.Medium
        width < 1200.dp -> WindowSize.Expanded
        else -> WindowSize.Large
    }
}

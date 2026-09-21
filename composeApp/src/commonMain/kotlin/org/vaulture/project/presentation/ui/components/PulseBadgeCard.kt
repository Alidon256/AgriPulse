package org.vaulture.project.presentation.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.vaulture.project.core.domain.Level
import org.vaulture.project.core.theme.PoppinsTypography

private val resilienceLevels = listOf(
    Level("Sprout", Icons.Default.Grass, Color(0xFF81C784), Color(0xFF4CAF50), 0),
    Level("Cultivator", Icons.Default.Agriculture, Color(0xFF80DEEA), Color(0xFF26C6DA), 100),
    Level("Guardian", Icons.Default.Shield, Color(0xFFFFF176), Color(0xFFFFEE58), 300),
    Level("Agri Leader", Icons.Default.EmojiEvents, Color(0xFFF06292), Color(0xFFEC407A), 600),
    Level("Resilience Master", Icons.Default.AutoAwesome, Color(0xFFE57373), Color(0xFFEF5350), 1200)
)

private fun getLevelForPoints(points: Int): Level {
    return resilienceLevels.lastOrNull { points >= it.threshold } ?: resilienceLevels.first()
}

@Composable
fun PulseBadgeCard(
    totalPoints: Int
) {
    val currentLevel = remember(totalPoints) { getLevelForPoints(totalPoints) }
    val nextLevel = resilienceLevels.getOrNull(resilienceLevels.indexOf(currentLevel) + 1)
    val progressToNextLevel = if (nextLevel != null) {
        val pointsInCurrentLevel = totalPoints - currentLevel.threshold
        val pointsForNextLevel = nextLevel.threshold - currentLevel.threshold
        (pointsInCurrentLevel.toFloat() / pointsForNextLevel).coerceIn(0f, 1f)
    } else {
        1f
    }

    val animatedProgress by animateFloatAsState(
        targetValue = progressToNextLevel,
        animationSpec = spring(dampingRatio = 0.8f, stiffness = 50f),
        label = "LevelProgressAnimation"
    )

    var oldPoints by remember { mutableStateOf(totalPoints) }
    val scale by animateFloatAsState(
        targetValue = if (totalPoints != oldPoints) 1.05f else 1f,
        animationSpec = spring(dampingRatio = 0.4f, stiffness = 400f),
        label = "PulseAnimation"
    )

    LaunchedEffect(totalPoints) {
        oldPoints = totalPoints
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            brush = Brush.linearGradient(listOf(currentLevel.color1, currentLevel.color2)),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = currentLevel.icon,
                        contentDescription = "Badge",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(Modifier.width(16.dp))

                Column {
                    Text(
                        text = currentLevel.name,
                        style = PoppinsTypography().titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Agri Resilience Level ${resilienceLevels.indexOf(currentLevel) + 1}",
                        style = PoppinsTypography().bodySmall,
                        color = currentLevel.color2,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            Column {
                LinearProgressIndicator(
                    progress = { animatedProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(CircleShape),
                    color = currentLevel.color1,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    strokeCap = StrokeCap.Round
                )
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "$totalPoints FIELD XP",
                        style = PoppinsTypography().labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Bold
                    )
                    if (nextLevel != null) {
                        Text(
                            text = "${nextLevel.threshold} XP to next level",
                            style = PoppinsTypography().labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Medium
                        )
                    } else {
                        Text(
                            "Max Level Reached!",
                            style = PoppinsTypography().labelSmall,
                            color = currentLevel.color2,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

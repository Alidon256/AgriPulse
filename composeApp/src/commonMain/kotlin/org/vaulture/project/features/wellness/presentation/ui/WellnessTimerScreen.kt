package org.vaulture.project.features.wellness.presentation.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.vaulture.project.core.theme.PoppinsTypography
import org.vaulture.project.features.wellness.domain.model.WellnessType
import org.vaulture.project.features.wellness.presentation.viewmodel.WellnessPhase
import org.vaulture.project.features.wellness.presentation.viewmodel.WellnessUiState
import org.vaulture.project.features.wellness.presentation.viewmodel.WellnessViewModel

@Composable
fun WellnessTimerScreen(viewModel: WellnessViewModel, onBack: () -> Unit) {
    val state by viewModel.uiState.collectAsState()
    val currentActivity = state.currentActivity ?: return

    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(
            currentActivity.gradientStartColor.copy(alpha = 0.3f),
            MaterialTheme.colorScheme.background
        )
    )

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundBrush),
        contentAlignment = Alignment.Center
    ) {
        val contentModifier = if (maxWidth > 920.dp) {
            Modifier
                .width(500.dp)
                .align(Alignment.Center)
        } else {
            Modifier.fillMaxSize()
        }

        IconButton(
            onClick = onBack,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
        ) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowBack,
                "Back"
            )
        }

        AnimatedContent(
            targetState = state.phase,
            transitionSpec = {
                fadeIn(animationSpec = tween(500)) togetherWith fadeOut(animationSpec = tween(500))
            },
            label = "WellnessPhaseAnimation"
        ) { phase ->
            when (phase) {
                WellnessPhase.SETUP -> SetupContent(
                    contentModifier,
                    viewModel,
                    onBack
                )
                WellnessPhase.ACTIVE -> ActiveTimerContent(
                    contentModifier,
                    state,
                    viewModel
                )
                WellnessPhase.SUMMARY -> SummaryContent(
                    contentModifier,
                    state,
                    onBack
                )
            }
        }
    }
}

@Composable
private fun SetupContent(modifier: Modifier, viewModel: WellnessViewModel, onBack: () -> Unit) {
    val activity = viewModel.uiState.value.currentActivity ?: return
    val steps = org.vaulture.project.features.wellness.domain.model.AGRONOMIC_PROTOCOLS[activity] ?: emptyList()
    var selectedDurationMinutes by remember { mutableStateOf(5) }

    Column(
        modifier = modifier
            .fillMaxHeight()
            .verticalScroll(androidx.compose.foundation.rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            activity.gradientStartColor.copy(alpha = 0.5f),
                            activity.gradientEndColor.copy(alpha = 0.3f)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = activity.icon,
                contentDescription = activity.label,
                modifier = Modifier.size(52.dp),
                tint = activity.gradientStartColor
            )
        }

        Spacer(Modifier.height(16.dp))

        Text(
            activity.label,
            style = PoppinsTypography().headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Text(
            "Field Diagnostic & Action Protocol",
            style = PoppinsTypography().bodySmall,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(Modifier.height(16.dp))

        ExpandableInfoCard(
            title = "Agronomic Purpose",
            description = activity.description
        )

        Spacer(Modifier.height(16.dp))

        // Steps Preview Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Systematic 4-Step Field Protocol 📋",
                    style = PoppinsTypography().titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.height(10.dp))
                steps.forEach { step ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = activity.gradientStartColor.copy(alpha = 0.2f),
                            modifier = Modifier.size(24.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "${step.stepNumber}",
                                    style = PoppinsTypography().labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = activity.gradientStartColor
                                )
                            }
                        }
                        Spacer(Modifier.width(10.dp))
                        Text(
                            text = step.title,
                            style = PoppinsTypography().bodySmall,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        Text(
            "Select Field Session Duration",
            style = PoppinsTypography().titleMedium,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf(3, 5, 10, 15).forEach { mins ->
                val isSelected = selectedDurationMinutes == mins
                OutlinedButton(
                    onClick = { selectedDurationMinutes = mins },
                    modifier = Modifier.weight(1f).height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = if (isSelected) ButtonDefaults.outlinedButtonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ) else ButtonDefaults.outlinedButtonColors()
                ) {
                    Text(
                        "$mins m",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = { viewModel.startTimer(selectedDurationMinutes) },
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(14.dp)
        ) {
            Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(8.dp))
            Text(
                "Begin Field Inspection 🌾",
                style = PoppinsTypography().titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(Modifier.height(12.dp))

        TextButton(onClick = onBack) {
            Text(
                "Return to Dashboard",
                style = PoppinsTypography().bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ExpandableInfoCard(title: String, description: String) {
    var isExpanded by remember { mutableStateOf(false) }
    val rotationAngle by animateFloatAsState(targetValue = if (isExpanded) 180f else 0f, label = "ExpandIconRotation")

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        onClick = { isExpanded = !isExpanded }
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    title,
                    style = PoppinsTypography().titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.weight(1f))
                Icon(
                    imageVector = Icons.Default.ExpandMore,
                    contentDescription = if (isExpanded) "Collapse" else "Expand",
                    modifier = Modifier.rotate(rotationAngle)
                )
            }

            AnimatedVisibility(visible = isExpanded) {
                Text(
                    text = description,
                    style = PoppinsTypography().bodyMedium,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun ActiveTimerContent(
    modifier: Modifier,
    state: WellnessUiState,
    viewModel: WellnessViewModel
) {
    val currentType = state.currentActivity ?: WellnessType.BREATHING
    val steps = org.vaulture.project.features.wellness.domain.model.AGRONOMIC_PROTOCOLS[currentType] ?: emptyList()
    val currentStep = steps.getOrNull(state.currentStepIndex) ?: steps.firstOrNull()

    var showDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(androidx.compose.foundation.rememberScrollState())
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Header: Activity & Timer Countdown
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = CircleShape,
                    color = currentType.gradientStartColor.copy(alpha = 0.2f),
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = currentType.icon,
                            contentDescription = null,
                            tint = currentType.gradientStartColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Spacer(Modifier.width(10.dp))
                Column {
                    Text(
                        text = currentType.label,
                        style = PoppinsTypography().titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Field Inspection Routine",
                        style = PoppinsTypography().labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Timer,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = formatTime(state.timeLeftSeconds),
                        style = PoppinsTypography().titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        // Progress Bar
        val animatedProgress by animateFloatAsState(
            targetValue = if (state.totalDurationSeconds > 0)
                1f - (state.timeLeftSeconds.toFloat() / state.totalDurationSeconds.toFloat())
            else 0f,
            animationSpec = tween(1000, easing = LinearEasing),
            label = "sessionProgress"
        )

        LinearProgressIndicator(
            progress = { animatedProgress },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(CircleShape),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )

        Spacer(Modifier.height(16.dp))

        // Step Navigation Pills
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            steps.forEachIndexed { index, step ->
                val isActive = index == state.currentStepIndex
                val isCompleted = index < state.currentStepIndex
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = when {
                        isActive -> MaterialTheme.colorScheme.primary
                        isCompleted -> MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)
                        else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(36.dp)
                        .clickable { viewModel.selectStep(index) }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "Step ${step.stepNumber}",
                            style = PoppinsTypography().labelSmall,
                            fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium,
                            color = if (isActive) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        // Active Step Diagnostic Protocol Card
        if (currentStep != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "${currentStep.stepNumber}",
                                    style = PoppinsTypography().titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        }
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Step ${currentStep.stepNumber} of ${steps.size}",
                                style = PoppinsTypography().labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = currentStep.title,
                                style = PoppinsTypography().titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    Spacer(Modifier.height(14.dp))

                    Text(
                        text = currentStep.instruction,
                        style = PoppinsTypography().bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 22.sp
                    )

                    Spacer(Modifier.height(16.dp))

                    // Agronomic Extension Tip Box
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(
                                Icons.Default.Lightbulb,
                                contentDescription = "Tip",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp).padding(top = 2.dp)
                            )
                            Spacer(Modifier.width(10.dp))
                            Text(
                                text = currentStep.tip,
                                style = PoppinsTypography().bodySmall,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                lineHeight = 18.sp
                            )
                        }
                    }

                    Spacer(Modifier.height(18.dp))

                    // Field Observations Checklist
                    Text(
                        text = "Field Observations Checklist",
                        style = PoppinsTypography().labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(Modifier.height(8.dp))

                    currentStep.checklist.forEach { item ->
                        val itemKey = "${currentStep.stepNumber}_$item"
                        val isChecked = state.checkedItems.contains(itemKey)

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isChecked) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
                            else MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable { viewModel.toggleChecklistItem(itemKey) }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = isChecked,
                                    onCheckedChange = { viewModel.toggleChecklistItem(itemKey) },
                                    colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary)
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    text = item,
                                    style = PoppinsTypography().bodyMedium,
                                    color = if (isChecked) MaterialTheme.colorScheme.onSurface
                                    else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        // Step navigation controls
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = { viewModel.previousStep() },
                enabled = state.currentStepIndex > 0,
                modifier = Modifier.weight(1f).height(48.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text("Previous")
            }

            val isLastStep = state.currentStepIndex == steps.size - 1
            Button(
                onClick = {
                    if (isLastStep) {
                        viewModel.finishActionNow()
                    } else {
                        viewModel.nextStep()
                    }
                },
                modifier = Modifier.weight(1f).height(48.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(if (isLastStep) "Finish Audit 🌾" else "Next Step")
                Spacer(Modifier.width(6.dp))
                Icon(
                    if (isLastStep) Icons.Default.TaskAlt else Icons.Default.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        Spacer(Modifier.height(14.dp))

        TextButton(onClick = { showDialog = true }) {
            Text(
                "Pause / End Field Audit Early",
                style = PoppinsTypography().bodySmall,
                color = MaterialTheme.colorScheme.error.copy(alpha = 0.8f)
            )
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = {
                    Text(
                        "End inspection early?",
                        style = PoppinsTypography().titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                text = {
                    Text(
                        "Field inspections protect your crop yield and earn Farm Resilience Points. Are you sure you want to end?",
                        style = PoppinsTypography().bodyMedium
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showDialog = false
                            viewModel.resetToSetup()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("End Early")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog = false }) {
                        Text("Continue Inspection")
                    }
                },
                shape = RoundedCornerShape(24.dp)
            )
        }
    }
}

@Composable
private fun SummaryContent(modifier: Modifier, state: WellnessUiState, onBack: () -> Unit) {
    Column(
        modifier
            .fillMaxSize()
            .verticalScroll(androidx.compose.foundation.rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                Icons.Default.AutoAwesome,
                null,
                modifier = Modifier.size(120.dp),
                tint = MaterialTheme.colorScheme.primary.copy(0.12f)
            )
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    "FIELD XP",
                    style = PoppinsTypography().labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "+${state.stats.resiliencePoints}",
                    style = PoppinsTypography().displaySmall,
                    fontWeight = FontWeight.Black
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        Text(
            "Field Inspection Completed! 🌾",
            style = PoppinsTypography().titleLarge,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "You verified ${state.checkedItems.size} agronomic parameters. Regular scouting prevents crop failure and optimizes yield.",
            style = PoppinsTypography().bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Row(
                modifier = Modifier.padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.LocalFireDepartment,
                    null,
                    tint = Color(0xFFFF5722),
                    modifier = Modifier.size(36.dp)
                )

                Spacer(Modifier.width(16.dp))

                Column {
                    Text(
                        "${state.stats.currentStreak} Day Field Streak!",
                        style = PoppinsTypography().titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "Consistent scouting builds agricultural resilience.",
                        style = PoppinsTypography().bodySmall
                    )
                }
            }
        }

        Spacer(Modifier.height(36.dp))

        Button(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(14.dp)
        ) {
            Text(
                "Return to Farmer Dashboard",
                style = PoppinsTypography().titleMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

private fun formatTime(seconds: Int): String {
    val m = seconds / 60
    val s = seconds % 60
    return "${m.toString().padStart(2, '0')}:${s.toString().padStart(2, '0')}"
}


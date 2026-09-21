package org.vaulture.project.features.onboarding.presentation.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mindsetpulse.composeapp.generated.resources.Res
import mindsetpulse.composeapp.generated.resources.val_1
import org.jetbrains.compose.resources.painterResource
import org.vaulture.project.core.theme.PoppinsTypography

@Composable
fun OnboardingScreenOne(
    onNext: () -> Unit = {},
    onSkip: () -> Unit = {}
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            val isWideScreen = maxWidth > 920.dp

            if (isWideScreen) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(Res.drawable.val_1),
                        contentDescription = "Crop Diagnostics",
                        modifier = Modifier
                            .weight(1.2f)
                            .fillMaxHeight(),
                        contentScale = ContentScale.Crop
                    )

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .padding(horizontal = 48.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(onClick = onSkip) {
                                Text(
                                    "Skip",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 16.sp
                                )
                            }
                        }

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.weight(1f)
                        ) {
                            OnboardingOneContent(isWideScreen = true)
                        }

                        OnboardingOneNavigation(
                            onNext = onNext,
                            modifier = Modifier.padding(vertical = 24.dp)
                        )
                    }
                }
            } else {

                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = onSkip) {
                            Text(
                                "Skip",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 16.sp
                            )
                        }
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 24.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Text(
                            "AI Crop Diagnostics & Health Tracking 🌾",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            style = PoppinsTypography().headlineLarge,
                            color = MaterialTheme.colorScheme.onBackground,
                            textAlign = TextAlign.Center,
                            lineHeight = 36.sp
                        )
                        Spacer(modifier = Modifier.height(24.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp)
                                .clip(RoundedCornerShape(24.dp))
                        ) {
                            Image(
                                painter = painterResource(Res.drawable.val_1),
                                contentDescription = "Crop Diagnostics",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(MaterialTheme.colorScheme.primary),
                                contentScale = ContentScale.Crop
                            )
                        }
                        Spacer(modifier = Modifier.height(24.dp))

                        Text(
                            "Log daily field observations—leaf yellowing, stem borers, or soil dryness. AgriPulse AI calculates crop stress risk and suggests immediate treatment.",
                            fontSize = 16.sp,
                            style = PoppinsTypography().bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            lineHeight = 24.sp
                        )
                    }

                    OnboardingOneNavigation(
                        onNext = onNext,
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 24.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun OnboardingOneContent(isWideScreen: Boolean) {
    Text(
        text = "AI Crop Diagnostics & Health Tracking 🌾",
        fontSize = if (isWideScreen) 36.sp else 28.sp,
        fontWeight = FontWeight.Bold,
        style = PoppinsTypography().headlineLarge,
        color = MaterialTheme.colorScheme.onBackground,
        textAlign = TextAlign.Center,
        lineHeight = if (isWideScreen) 44.sp else 36.sp
    )
    Spacer(modifier = Modifier.height(24.dp))
    Text(
        text = "Log daily field observations—leaf yellowing, stem borers, or soil dryness. AgriPulse AI calculates crop stress risk and suggests immediate treatment.",
        fontSize = 18.sp,
        style = PoppinsTypography().bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
        lineHeight = 26.sp
    )
}

@Composable
private fun OnboardingOneNavigation(onNext: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .height(8.dp)
                    .width(32.dp)
                    .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(4.dp))
            )
            Box(
                modifier = Modifier
                    .height(8.dp)
                    .width(16.dp)
                    .background(Color.Gray.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
            )
            Box(
                modifier = Modifier
                    .height(8.dp)
                    .width(16.dp)
                    .background(Color.Gray.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
            )
        }

        Button(
            onClick = onNext,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.height(50.dp)
        ) {
            Text(
                "Next",
                color = Color.White,
                fontSize = 16.sp,
                style = PoppinsTypography().bodyMedium,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}

package org.vaulture.project.features.onboarding.presentation.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mindsetpulse.composeapp.generated.resources.Res
import mindsetpulse.composeapp.generated.resources.bg_two
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.vaulture.project.core.theme.AppTheme
import org.vaulture.project.core.theme.AppThemeMode
import org.vaulture.project.core.theme.PoppinsTypography
import org.vaulture.project.core.theme.ThemePalette

@Composable
fun ConnectScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToSignUp: () -> Unit
) {
    AppTheme(
        content = {
            Scaffold(
                containerColor = MaterialTheme.colorScheme.background
            ) { paddingValues ->
                BoxWithConstraints(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    val isWideScreen = maxWidth > 920.dp

                    if (isWideScreen) {

                        Row(
                            modifier = Modifier.fillMaxSize(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                            ) {
                                Image(
                                    painter = painterResource(Res.drawable.bg_two),
                                    contentDescription = "Agricultural Field",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )

                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(Color.Black.copy(alpha = 0.2f))
                                )
                            }


                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .padding(48.dp)
                                    .verticalScroll(rememberScrollState()),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                ConnectContent(
                                    onNavigateToLogin = onNavigateToLogin,
                                    onNavigateToSignUp = onNavigateToSignUp,
                                    isWideScreen = true
                                )
                            }
                        }
                    } else {

                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                                .background(MaterialTheme.colorScheme.background),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 50.dp, bottom = 20.dp, start = 24.dp, end = 24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Join the AgriPulse Network 🌾",
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Bold,
                                    style = PoppinsTypography().titleLarge,
                                    color = MaterialTheme.colorScheme.onBackground,
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    text = "Connect with extension specialists and protect your farm yield.",
                                    fontSize = 16.sp,
                                    style = PoppinsTypography().bodyMedium,
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(top = 8.dp)
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(450.dp)
                            ) {
                                Image(
                                    painter = painterResource(Res.drawable.bg_two),
                                    contentDescription = "Agricultural Field",
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(AsymmetricBottomCurveShapeImproved())
                                        .background(MaterialTheme.colorScheme.primaryContainer),
                                    contentScale = ContentScale.Crop
                                )
                            }

                            Spacer(modifier = Modifier.height(40.dp))

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 24.dp)
                                    .padding(bottom = 40.dp)
                            ) {
                                ConnectContent(
                                    onNavigateToLogin = onNavigateToLogin,
                                    onNavigateToSignUp = onNavigateToSignUp,
                                    isWideScreen = false
                                )
                            }

                            Spacer(modifier = Modifier.navigationBarsPadding())
                        }
                    }
                }
            }
        },
        themeMode = AppThemeMode.DARK,
        themePalette = ThemePalette.NATURE
    )
}

@Composable
private fun ConnectContent(
    onNavigateToLogin: () -> Unit,
    onNavigateToSignUp: () -> Unit,
    isWideScreen: Boolean
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.widthIn(max = 400.dp)
    ) {
        if (isWideScreen) {
            Text(
                text = "Join the AgriPulse Network 🌾",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                style = PoppinsTypography().bodyLarge,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Connect with agricultural extension specialists, track harvest resilience, and share crop wisdom.",
                fontSize = 18.sp,
                style = PoppinsTypography().bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 48.dp)
            )
        }

        Button(
            onClick = onNavigateToLogin,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = "Log In as Farmer",
                fontSize = 16.sp,
                style = PoppinsTypography().bodyMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(
            onClick = onNavigateToSignUp,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.primary
            ),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = "Create Farmer Account",
                fontSize = 16.sp,
                style = PoppinsTypography().bodyMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

fun AsymmetricBottomCurveShapeImproved(): Shape {
    return GenericShape { size, _ ->
        val width = size.width
        val height = size.height
        val topCurveStart = height * 0.10f
        val bottomCurveEnd = height * 0.85f
        moveTo(0f, height * 0.60f)
        cubicTo(width * 0.20f, height * 0.95f, width * 0.70f, height * 0.90f, width, bottomCurveEnd)
        lineTo(width, topCurveStart)
        cubicTo(width * 0.70f, height * 0.20f, width * 0.20f, height * 0.05f, 0f, topCurveStart)
        lineTo(0f, height * 0.60f)
        close()
    }
}

@Preview()
@Composable
fun ConnectScreenPreview() {
    MaterialTheme {
        ConnectScreen(
            onNavigateToLogin = {},
            onNavigateToSignUp = {}
        )
    }
}

package org.vaulture.project.features.onboarding.presentation.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mindsetpulse.composeapp.generated.resources.Res
import mindsetpulse.composeapp.generated.resources.ic_img1
import mindsetpulse.composeapp.generated.resources.mindset_pulse_nobg_logo
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.vaulture.project.core.theme.PoppinsTypography

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WelcomeScreen(
    onGetStarted: () -> Unit = {},
    onLoginClicked: () -> Unit = {}
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(Res.drawable.ic_img1),
            contentDescription = "Agricultural Field",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.height(80.dp))
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {

                Image(
                    painter = painterResource(Res.drawable.mindset_pulse_nobg_logo),
                    contentDescription = "AgriPulse Logo",
                    modifier = Modifier.wrapContentSize(),
                    contentScale = ContentScale.Crop
                )

                Text(
                    text = "Empowering Smallholder Farmers. Boosting Harvest Yields.",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    style = PoppinsTypography().bodyMedium,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 16.dp, start = 24.dp, end = 24.dp)
                )

                Text(
                    text = "AI-powered crop stress diagnostics, weather-smart guidance, and peer farming communities across Africa.",
                    fontSize = 14.sp,
                    style = PoppinsTypography().bodySmall,
                    color = Color.White.copy(alpha = 0.85f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 8.dp, start = 24.dp, end = 24.dp)
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(bottom = 32.dp)
            ) {
                Button(
                    onClick = onGetStarted,
                    modifier = Modifier
                        .widthIn(min = 300.dp)
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Get Started as a Farmer 🌾",
                        fontSize = 16.sp,
                        style = PoppinsTypography().bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Already registered? ",
                        style = PoppinsTypography().bodyMedium,
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 14.sp
                    )
                    TextButton(
                        onClick = onLoginClicked,
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(
                            text = "Log in",
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            style = PoppinsTypography().bodyMedium,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
@Preview
fun WelcomeScreenPreview() {
    WelcomeScreen(
        onGetStarted = {},
        onLoginClicked = {}
    )
}

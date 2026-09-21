package org.vaulture.project.features.auth.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mindsetpulse.composeapp.generated.resources.Res
import mindsetpulse.composeapp.generated.resources.bg_two
import org.jetbrains.compose.resources.painterResource
import coil3.compose.AsyncImage
import org.vaulture.project.core.theme.PoppinsTypography
import org.vaulture.project.core.util.ImagePicker


@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onGoogleSignInRequest: () -> Unit,
    initialSignUpMode: Boolean = false
) {
    val uiState by viewModel.uiState.collectAsState()
    var isSignUpMode by remember(initialSignUpMode) { mutableStateOf(initialSignUpMode) }
    var showImagePicker by remember { mutableStateOf(false) }

    ImagePicker(
        show = showImagePicker,
        onImageSelected = { imageData ->
            showImagePicker = false
            if (imageData != null) {
                viewModel.onProfilePictureChange(imageData)
            }
        }
    )

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .systemBarsPadding()
    ) {
        if (maxWidth > 920.dp) {
            Row(modifier = Modifier.fillMaxSize()) {
                Box(
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(Res.drawable.bg_two),
                        contentDescription = "Decorative background",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .background(Color.Black.copy(0.3f))
                    )

                    Column(
                        Modifier.padding(48.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            "AgriPulse",
                            style = PoppinsTypography().headlineLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "Scout crops, track soil health, and connect with farmers",
                            style = PoppinsTypography().titleLarge,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }

                Box(
                    modifier = Modifier.weight(1f)
                        .fillMaxHeight(),
                    contentAlignment = Alignment.Center
                ) {
                    AuthForm(
                        viewModel = viewModel,
                        isSignUpMode = isSignUpMode,
                        onToggleMode = {
                            isSignUpMode = !isSignUpMode
                            viewModel.setSignUpStep(1)
                        },
                        onGoogleSignInClick = onGoogleSignInRequest,
                        onProfilePictureClick = { showImagePicker = true }
                    )
                }
            }
        } else {
            AuthForm(
                viewModel = viewModel,
                isSignUpMode = isSignUpMode,
                onToggleMode = {
                    isSignUpMode = !isSignUpMode
                    viewModel.setSignUpStep(1)
                },
                onGoogleSignInClick = onGoogleSignInRequest,
                onProfilePictureClick = { showImagePicker = true }
            )
        }
    }
}


val COUNTRY_PRESETS = listOf("Kenya", "Uganda", "Tanzania", "Rwanda", "Nigeria", "Ghana", "Zambia", "Ethiopia")

val FARM_SIZE_PRESETS = listOf("< 1 Acre", "1 - 3 Acres", "3 - 5 Acres", "5 - 10 Acres", "10+ Acres")
val CROP_PRESETS = listOf("Maize 🌽", "Beans 🫘", "Cassava 🍠", "Coffee ☕", "Bananas 🍌", "Tomatoes 🍅", "Sorghum 🌾", "Groundnuts 🥜", "Potatoes 🥔", "Vegetables 🥬")
val FARMING_TYPE_PRESETS = listOf("Smallholder Mixed", "Organic Regenerative", "Commercial Cash Crop", "Agroforestry")
val IRRIGATION_PRESETS = listOf("Rain-Fed 🌧️", "Gravity Drip 💧", "Solar Pump ☀️", "Furrow 🌊")
val SOIL_PRESETS = listOf("Loam 🌱", "Clay Loam 🪨", "Sandy 🏖️", "Volcanic Red 🌋")
val EXPERIENCE_PRESETS = listOf("Novice (1-2 yrs)", "Practicing (3-5 yrs)", "Experienced (6-10 yrs)", "Extension Mentor (10+ yrs)")

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AuthForm(
    viewModel: LoginViewModel,
    isSignUpMode: Boolean,
    onToggleMode: () -> Unit,
    onGoogleSignInClick: () -> Unit,
    onProfilePictureClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp)
                    )
                )
            )
            .padding(horizontal = 24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(Modifier.height(32.dp))

        if (isSignUpMode) {
            // Header for multi-step onboarding
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Farmer Onboarding 🌾",
                    style = PoppinsTypography().headlineLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = when (uiState.signUpStep) {
                        1 -> "Step 1/3: Create your account credentials"
                        2 -> "Step 2/3: Tell us about your farm & location"
                        else -> "Step 3/3: Select your primary crops & practices"
                    },
                    style = PoppinsTypography().bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(8.dp))
                // Progress indicator
                LinearProgressIndicator(
                    progress = { uiState.signUpStep / 3f },
                    modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                    color = MaterialTheme.colorScheme.primary,
                )
            }

            Spacer(Modifier.height(24.dp))

            when (uiState.signUpStep) {
                1 -> {
                    // Step 1: Account credentials
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .clickable { onProfilePictureClick() }
                            .border(width = 2.dp, color = MaterialTheme.colorScheme.primary, shape = CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        if (uiState.profilePicture != null) {
                            AsyncImage(
                                model = uiState.profilePicture,
                                contentDescription = "Profile Picture",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.AddAPhoto,
                                contentDescription = "Add profile picture",
                                modifier = Modifier.size(36.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                    OutlinedTextField(
                        value = uiState.username,
                        onValueChange = viewModel::onUsernameChange,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Farmer Name / Handle") },
                        leadingIcon = { Icon(Icons.Outlined.Person, contentDescription = null) },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp)
                    )
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(
                        value = uiState.email,
                        onValueChange = viewModel::onEmailChange,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Email Address") },
                        leadingIcon = { Icon(Icons.Outlined.Email, contentDescription = null) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp)
                    )
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(
                        value = uiState.password,
                        onValueChange = viewModel::onPasswordChange,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Password (min 6 chars)") },
                        leadingIcon = { Icon(Icons.Outlined.Lock, contentDescription = null) },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp)
                    )
                    Spacer(Modifier.height(20.dp))
                    Button(
                        onClick = {
                            if (uiState.username.isBlank() || uiState.email.isBlank() || uiState.password.length < 6) {
                                viewModel.onEmailChange(uiState.email) // trigger validation error
                            } else {
                                viewModel.setSignUpStep(2)
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text("Next: Farm Profile (1/3) ➡️", fontWeight = FontWeight.Bold, style = PoppinsTypography().bodyLarge)
                    }
                }

                2 -> {
                    // Step 2: Farm Location & Scale
                    OutlinedTextField(
                        value = uiState.farmName,
                        onValueChange = viewModel::onFarmNameChange,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Farm Name (Optional)") },
                        placeholder = { Text("e.g. Sunrise Organic Shamba") },
                        leadingIcon = { Text("🏡", modifier = Modifier.padding(start = 12.dp)) },
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp)
                    )
                    Spacer(Modifier.height(14.dp))
                    Text(
                        text = "Country 🌍",
                        style = PoppinsTypography().labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.align(Alignment.Start)
                    )
                    Spacer(Modifier.height(6.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        COUNTRY_PRESETS.forEach { country ->
                            FilterChip(
                                selected = uiState.country == country,
                                onClick = { viewModel.onCountryChange(country) },
                                label = { Text(country, fontSize = 12.sp) }
                            )
                        }
                    }
                    Spacer(Modifier.height(14.dp))
                    OutlinedTextField(
                        value = uiState.region,
                        onValueChange = viewModel::onRegionChange,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Region / County / District") },
                        placeholder = { Text("e.g. Nakuru County / Eldoret") },
                        leadingIcon = { Text("📍", modifier = Modifier.padding(start = 12.dp)) },
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp)
                    )
                    Spacer(Modifier.height(14.dp))
                    Text(
                        text = "Farm Acreage / Scale 📐",
                        style = PoppinsTypography().labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.align(Alignment.Start)
                    )
                    Spacer(Modifier.height(6.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        FARM_SIZE_PRESETS.forEach { size ->
                            FilterChip(
                                selected = uiState.farmSize == size,
                                onClick = { viewModel.onFarmSizeChange(size) },
                                label = { Text(size, fontSize = 12.sp) }
                            )
                        }
                    }
                    Spacer(Modifier.height(20.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedButton(
                            onClick = { viewModel.setSignUpStep(1) },
                            modifier = Modifier.weight(1f).height(52.dp),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text("⬅️ Back")
                        }
                        Button(
                            onClick = { viewModel.setSignUpStep(3) },
                            modifier = Modifier.weight(1.4f).height(52.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Text("Next: Crops (2/3) ➡️", fontWeight = FontWeight.Bold)
                        }
                    }
                }

                3 -> {
                    // Step 3: Crops & Agronomic practices
                    Text(
                        text = "Primary Crops (Select all that apply) 🌾",
                        style = PoppinsTypography().labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.align(Alignment.Start)
                    )
                    Spacer(Modifier.height(6.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        CROP_PRESETS.forEach { crop ->
                            val isSelected = uiState.primaryCrops.any { it.startsWith(crop.take(4)) }
                            FilterChip(
                                selected = isSelected,
                                onClick = { viewModel.toggleCrop(crop) },
                                label = { Text(crop, fontSize = 12.sp) }
                            )
                        }
                    }
                    Spacer(Modifier.height(14.dp))
                    Text(
                        text = "Irrigation Method 💧",
                        style = PoppinsTypography().labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.align(Alignment.Start)
                    )
                    Spacer(Modifier.height(6.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        IRRIGATION_PRESETS.forEach { irr ->
                            FilterChip(
                                selected = uiState.irrigationType == irr,
                                onClick = { viewModel.onIrrigationTypeChange(irr) },
                                label = { Text(irr, fontSize = 12.sp) }
                            )
                        }
                    }
                    Spacer(Modifier.height(14.dp))
                    Text(
                        text = "Predominant Soil Type 🌱",
                        style = PoppinsTypography().labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.align(Alignment.Start)
                    )
                    Spacer(Modifier.height(6.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        SOIL_PRESETS.forEach { soil ->
                            FilterChip(
                                selected = uiState.soilType == soil,
                                onClick = { viewModel.onSoilTypeChange(soil) },
                                label = { Text(soil, fontSize = 12.sp) }
                            )
                        }
                    }
                    Spacer(Modifier.height(20.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedButton(
                            onClick = { viewModel.setSignUpStep(2) },
                            modifier = Modifier.weight(1f).height(52.dp),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text("⬅️ Back")
                        }
                        Button(
                            onClick = viewModel::onCreateAccountClick,
                            modifier = Modifier.weight(1.8f).height(52.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            if (uiState.isLoading) {
                                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White, strokeWidth = 2.dp)
                            } else {
                                Text("Complete Registration 🌾", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        } else {
            // Sign In Mode
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Welcome Back",
                    style = PoppinsTypography().headlineLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Sign in to continue your farming journey",
                    style = PoppinsTypography().bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(Modifier.height(36.dp))

            OutlinedTextField(
                value = uiState.email,
                onValueChange = viewModel::onEmailChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Email Address", style = PoppinsTypography().bodyMedium) },
                leadingIcon = { Icon(Icons.Outlined.Email, contentDescription = "Email Icon") },
                isError = uiState.error != null,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                singleLine = true,
                shape = RoundedCornerShape(16.dp)
            )
            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = uiState.password,
                onValueChange = viewModel::onPasswordChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Password", style = PoppinsTypography().bodyMedium) },
                leadingIcon = { Icon(Icons.Outlined.Lock, contentDescription = "Password Icon") },
                isError = uiState.error != null,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = {
                    focusManager.clearFocus()
                    viewModel.onSignInClick()
                }),
                singleLine = true,
                shape = RoundedCornerShape(16.dp)
            )
            Spacer(Modifier.height(24.dp))

            Box(
                modifier = Modifier.fillMaxWidth().height(56.dp),
                contentAlignment = Alignment.Center
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(48.dp), strokeWidth = 3.dp)
                } else {
                    Button(
                        onClick = viewModel::onSignInClick,
                        modifier = Modifier.fillMaxSize(),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text("SIGN IN", fontWeight = FontWeight.Bold, style = PoppinsTypography().bodyLarge)
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            Text("or continue with", style = PoppinsTypography().bodyMedium)
            Spacer(Modifier.height(16.dp))

            OutlinedButton(
                onClick = onGoogleSignInClick,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Text(
                    "SIGN IN WITH GOOGLE",
                    fontWeight = FontWeight.Bold,
                    style = PoppinsTypography().bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        AnimatedVisibility(visible = uiState.error != null && uiState.error!!.isNotBlank()) {
            Spacer(Modifier.height(12.dp))
            Text(
                text = uiState.error ?: "",
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center,
                style = PoppinsTypography().bodyMedium,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
            )
        }

        Spacer(Modifier.height(24.dp))

        val toggleText = if (isSignUpMode)
            "Already have an account? Sign In"
        else
            "New here? Create an Account"

        TextButton(onClick = onToggleMode) {
            Text(
                toggleText,
                fontWeight = FontWeight.Bold,
                style = PoppinsTypography().bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(Modifier.height(32.dp))
    }
}


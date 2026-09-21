package org.vaulture.project.features.profile.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import org.vaulture.project.core.theme.PoppinsTypography
import org.vaulture.project.core.util.ImagePicker
import org.vaulture.project.features.space.presentation.viewmodel.SpaceViewModel

val COUNTRY_LIST = listOf("Kenya", "Uganda", "Tanzania", "Rwanda", "Nigeria", "Ghana", "Ethiopia", "Zambia", "Malawi", "Zimbabwe")
val FARM_SIZES = listOf("< 1 Acre", "1 - 3 Acres", "3 - 5 Acres", "5 - 10 Acres", "10+ Acres")
val CROP_LIST = listOf("Maize 🌽", "Beans 🫘", "Cassava 🍠", "Coffee ☕", "Bananas 🍌", "Tomatoes 🍅", "Sorghum 🌾", "Groundnuts 🥜", "Potatoes 🥔", "Vegetables 🥬")
val FARMING_TYPES = listOf("Smallholder Mixed", "Organic Regenerative", "Commercial Cash Crop", "Agroforestry", "Drip Horticulture")
val IRRIGATION_METHODS = listOf("Rain-Fed 🌧️", "Gravity Drip 💧", "Solar Pump ☀️", "Furrow / Flood 🌊", "Sprinkler 🚿")
val SOIL_TYPES = listOf("Loam 🌱", "Clay Loam 🪨", "Sandy Soil 🏖️", "Volcanic Red 🌋", "Black Cotton 🌑")
val EXPERIENCE_LEVELS = listOf("Novice (1-2 yrs)", "Practicing (3-5 yrs)", "Experienced (6-10 yrs)", "Extension Mentor (10+ yrs)")
val CERTIFICATION_LIST = listOf("GAP Certified 🏅", "Organic Certified 🌿", "Rainforest Alliance 🐸", "Fair Trade 🤝", "Climate-Smart Farmer 🌤️")

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun EditPortfolioScreen(
    viewModel: SpaceViewModel,
    onBack: () -> Unit
) {
    val user by viewModel.userProfile.collectAsState()
    val isUpdating by viewModel.isUpdatingProfile.collectAsState()

    val currentName = remember(user) {
        user?.effectiveName?.ifBlank { null }
            ?: viewModel.auth.currentUser?.displayName
            ?: ""
    }

    var displayName by remember(currentName) { mutableStateOf(currentName) }
    var farmName by remember(user) { mutableStateOf(user?.farmName ?: "") }
    var country by remember(user) { mutableStateOf(user?.country ?: "Kenya") }
    var region by remember(user) { mutableStateOf(user?.region ?: "") }
    var farmSize by remember(user) { mutableStateOf(user?.farmSize ?: "1 - 3 Acres") }
    var primaryCrops by remember(user) { mutableStateOf(user?.primaryCrops.takeIf { !it.isNullOrEmpty() } ?: listOf("Maize", "Beans")) }
    var farmingType by remember(user) { mutableStateOf(user?.farmingType ?: "Smallholder Mixed") }
    var irrigationType by remember(user) { mutableStateOf(user?.irrigationType ?: "Rain-Fed") }
    var soilType by remember(user) { mutableStateOf(user?.soilType ?: "Loam") }
    var experienceLevel by remember(user) { mutableStateOf(user?.experienceLevel ?: "Practicing (3-5 yrs)") }
    var certifications by remember(user) { mutableStateOf(user?.certifications ?: emptyList()) }
    var bio by remember(user) { mutableStateOf(user?.bio ?: "") }

    var selectedImageBytes by remember { mutableStateOf<ByteArray?>(null) }
    var showImagePicker by remember { mutableStateOf(false) }

    LaunchedEffect(user) {
        val name = user?.effectiveName?.ifBlank { null }
            ?: viewModel.auth.currentUser?.displayName
            ?: ""
        if (displayName.isBlank() && name.isNotBlank()) {
            displayName = name
        }
    }

    ImagePicker(
        show = showImagePicker,
        onImageSelected = {
            selectedImageBytes = it
            showImagePicker = false
        }
    )

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val screenWidth = maxWidth
        val isWide = screenWidth > 920.dp


        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            "Farmer Profile & Credentials",
                            style = PoppinsTypography().titleLarge
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    },
                    actions = {
                        if (isUpdating) {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .size(24.dp)
                                    .padding(end = 16.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            IconButton(
                                onClick = {
                                    viewModel.updatePortfolio(
                                        displayName = displayName,
                                        newPhotoBytes = selectedImageBytes,
                                        bio = bio,
                                        farmName = farmName,
                                        country = country,
                                        region = region,
                                        farmSize = farmSize,
                                        primaryCrops = primaryCrops,
                                        farmingType = farmingType,
                                        irrigationType = irrigationType,
                                        soilType = soilType,
                                        experienceLevel = experienceLevel,
                                        certifications = certifications,
                                        onSuccess = onBack,
                                        onError = { /* Handle Error */ }
                                    )
                                },
                                enabled = displayName.isNotBlank()
                            ) {
                                Icon(
                                    Icons.Default.Check,
                                    "Save",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent)
                )
            }
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = if (isWide) (screenWidth - 640.dp) / 2 else 16.dp)
                        .verticalScroll(rememberScrollState())

                        .padding(vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Profile Photo
                    Box(
                        modifier = Modifier
                            .size(110.dp)
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), CircleShape)
                            .clickable { showImagePicker = true },
                        contentAlignment = Alignment.BottomEnd
                    ) {
                        AsyncImage(
                            model = selectedImageBytes ?: user?.photoUrl ?: viewModel.auth.currentUser?.photoURL,
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                                .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape),
                            contentScale = ContentScale.Crop
                        )
                        Surface(
                            color = MaterialTheme.colorScheme.primary,
                            shape = CircleShape,
                            modifier = Modifier
                                .size(34.dp)
                                .border(2.dp, Color.White, CircleShape)
                        ) {
                            Icon(
                                Icons.Default.CameraAlt,
                                contentDescription = "Change photo",
                                tint = Color.White,
                                modifier = Modifier.padding(7.dp)
                            )
                        }
                    }

                    Spacer(Modifier.height(24.dp))

                    // Basic Details
                    OutlinedTextField(
                        value = displayName,
                        onValueChange = { displayName = it },
                        label = { Text("Farmer Display Name *") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        singleLine = true
                    )

                    Spacer(Modifier.height(12.dp))

                    OutlinedTextField(
                        value = farmName,
                        onValueChange = { farmName = it },
                        label = { Text("Farm / Shamba Name") },
                        placeholder = { Text("e.g. Green Valley Farm") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        singleLine = true
                    )

                    Spacer(Modifier.height(20.dp))

                    // Country Selection
                    Text(
                        text = "Country 🌍",
                        style = PoppinsTypography().titleSmall,
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
                        COUNTRY_LIST.forEach { item ->
                            FilterChip(
                                selected = country == item,
                                onClick = { country = item },
                                label = { Text(item, fontSize = 12.sp) }
                            )
                        }
                    }

                    Spacer(Modifier.height(14.dp))

                    OutlinedTextField(
                        value = region,
                        onValueChange = { region = it },
                        label = { Text("Region / County / District") },
                        placeholder = { Text("e.g. Nakuru County / Eldoret") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        singleLine = true
                    )

                    Spacer(Modifier.height(20.dp))

                    // Farm Acreage
                    Text(
                        text = "Farm Acreage / Scale 📐",
                        style = PoppinsTypography().titleSmall,
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
                        FARM_SIZES.forEach { item ->
                            FilterChip(
                                selected = farmSize == item,
                                onClick = { farmSize = item },
                                label = { Text(item, fontSize = 12.sp) }
                            )
                        }
                    }

                    Spacer(Modifier.height(20.dp))

                    // Primary Crops (Multi-select)
                    Text(
                        text = "Primary Crops (Multi-select) 🌾",
                        style = PoppinsTypography().titleSmall,
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
                        CROP_LIST.forEach { item ->
                            val cleanName = item.split(" ").first()
                            val isSelected = primaryCrops.any { it.startsWith(cleanName) }
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    primaryCrops = if (isSelected) {
                                        primaryCrops.filterNot { it.startsWith(cleanName) }
                                    } else {
                                        primaryCrops + cleanName
                                    }
                                },
                                label = { Text(item, fontSize = 12.sp) }
                            )
                        }
                    }

                    Spacer(Modifier.height(20.dp))

                    // Farming System
                    Text(
                        text = "Farming System & Model 🚜",
                        style = PoppinsTypography().titleSmall,
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
                        FARMING_TYPES.forEach { item ->
                            FilterChip(
                                selected = farmingType == item,
                                onClick = { farmingType = item },
                                label = { Text(item, fontSize = 12.sp) }
                            )
                        }
                    }

                    Spacer(Modifier.height(20.dp))

                    // Irrigation Method
                    Text(
                        text = "Irrigation System 💧",
                        style = PoppinsTypography().titleSmall,
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
                        IRRIGATION_METHODS.forEach { item ->
                            FilterChip(
                                selected = irrigationType == item,
                                onClick = { irrigationType = item },
                                label = { Text(item, fontSize = 12.sp) }
                            )
                        }
                    }

                    Spacer(Modifier.height(20.dp))

                    // Soil Type
                    Text(
                        text = "Predominant Soil Type 🌱",
                        style = PoppinsTypography().titleSmall,
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
                        SOIL_TYPES.forEach { item ->
                            FilterChip(
                                selected = soilType == item,
                                onClick = { soilType = item },
                                label = { Text(item, fontSize = 12.sp) }
                            )
                        }
                    }

                    Spacer(Modifier.height(20.dp))

                    // Experience Level
                    Text(
                        text = "Farming Experience Level ⭐",
                        style = PoppinsTypography().titleSmall,
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
                        EXPERIENCE_LEVELS.forEach { item ->
                            FilterChip(
                                selected = experienceLevel == item,
                                onClick = { experienceLevel = item },
                                label = { Text(item, fontSize = 12.sp) }
                            )
                        }
                    }

                    Spacer(Modifier.height(20.dp))

                    // Certifications & Accreditations (Multi-select)
                    Text(
                        text = "Farmer Certifications & Badges 🏅",
                        style = PoppinsTypography().titleSmall,
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
                        CERTIFICATION_LIST.forEach { item ->
                            val cleanName = item.split(" ").first()
                            val isSelected = certifications.any { it.startsWith(cleanName) }
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    certifications = if (isSelected) {
                                        certifications.filterNot { it.startsWith(cleanName) }
                                    } else {
                                        certifications + item
                                    }
                                },
                                label = { Text(item, fontSize = 12.sp) }
                            )
                        }
                    }

                    Spacer(Modifier.height(20.dp))

                    // Farm Bio / Statement
                    OutlinedTextField(
                        value = bio,
                        onValueChange = { bio = it },
                        label = { Text("Farm Bio & Focus") },
                        placeholder = { Text("Tell peer farmers about your agronomic practices, challenges, or mentoring goals...") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        minLines = 3,
                        maxLines = 5
                    )

                    Spacer(Modifier.height(32.dp))

                    Button(
                        onClick = {
                            viewModel.updatePortfolio(
                                displayName = displayName,
                                newPhotoBytes = selectedImageBytes,
                                bio = bio,
                                farmName = farmName,
                                country = country,
                                region = region,
                                farmSize = farmSize,
                                primaryCrops = primaryCrops,
                                farmingType = farmingType,
                                irrigationType = irrigationType,
                                soilType = soilType,
                                experienceLevel = experienceLevel,
                                certifications = certifications,
                                onSuccess = onBack,
                                onError = { /* Handle error */ }
                            )
                        },
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        enabled = displayName.isNotBlank() && !isUpdating
                    ) {
                        Text("Save Farmer Credentials 🌾", fontWeight = FontWeight.Bold, style = PoppinsTypography().bodyLarge)
                    }

                    Spacer(Modifier.height(40.dp))
                }
            }
        }
    }
}


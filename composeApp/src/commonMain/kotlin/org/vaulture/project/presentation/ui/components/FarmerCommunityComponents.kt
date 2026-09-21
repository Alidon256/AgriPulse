package org.vaulture.project.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil3.compose.AsyncImage
import org.vaulture.project.core.domain.User
import org.vaulture.project.core.theme.PoppinsTypography
import org.vaulture.project.features.space.domain.model.DEFAULT_SPACES
import org.vaulture.project.features.space.domain.model.Space

data class PeerFarmer(
    val id: String,
    val name: String,
    val location: String,
    val mainCrops: String,
    val avatarUrl: String,
    val resilienceBadge: String,
    val farmSize: String,
    val agronomicPractices: List<String>,
    val bio: String
)

fun User.toPeerFarmer(): PeerFarmer {
    val loc = when {
        !region.isNullOrBlank() && !country.isNullOrBlank() -> "$region, $country"
        !region.isNullOrBlank() -> region!!
        !country.isNullOrBlank() -> country!!
        else -> "East Africa"
    }
    val cropsStr = if (primaryCrops.isNotEmpty()) primaryCrops.joinToString(", ") else "Mixed Smallholder Crops"
    val badge = when {
        !farmingType.isNullOrBlank() -> farmingType!!
        !experienceLevel.isNullOrBlank() -> experienceLevel!!
        certifications.isNotEmpty() -> certifications.first()
        else -> "Verified Farmer 🌾"
    }
    val practices = mutableListOf<String>()
    if (!irrigationType.isNullOrBlank()) practices.add("Irrigation: $irrigationType")
    if (!soilType.isNullOrBlank()) practices.add("Soil: $soilType")
    if (certifications.isNotEmpty()) practices.addAll(certifications)
    if (practices.isEmpty()) practices.add("Sustainable Soil & Crop Scouting")

    return PeerFarmer(
        id = uid,
        name = effectiveName,
        location = loc,
        mainCrops = cropsStr,
        avatarUrl = photoUrl?.ifBlank { null }
            ?: "https://ui-avatars.com/api/?name=${effectiveName.replace(' ', '+')}&background=2e7d32&color=fff",
        resilienceBadge = badge,
        farmSize = farmSize ?: "Smallholder Plot",
        agronomicPractices = practices,
        bio = bio?.ifBlank { null } ?: "Smallholder farmer actively scouting crops and collaborating in AgriPulse community spaces."
    )
}


val DEFAULT_PEER_FARMERS = listOf(
    PeerFarmer(
        id = "farmer_grace",
        name = "Grace Wanjiru",
        location = "Eldoret, Kenya",
        mainCrops = "Maize, Beans & Dairy",
        avatarUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=400&auto=format&fit=crop&q=80",
        resilienceBadge = "Harvest Guard 🌾",
        farmSize = "3.5 Acres",
        agronomicPractices = listOf("Push-Pull Pest Trap", "Intercropping with Desmodium", "Hermetic Grain Storage"),
        bio = "Smallholder farmer pioneering biological Fall Armyworm control. Keen to exchange seed selection techniques."
    ),
    PeerFarmer(
        id = "farmer_emmanuel",
        name = "Emmanuel Kiprop",
        location = "Nakuru County, Kenya",
        mainCrops = "Drip Hort & Tomatoes",
        avatarUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=400&auto=format&fit=crop&q=80",
        resilienceBadge = "Soil Master 🍃",
        farmSize = "2.0 Acres",
        agronomicPractices = listOf("Gravity Drip Irrigation", "Grass Straw Mulch", "Compost Teas"),
        bio = "Specializing in dry-season off-grid drip irrigation. Happy to advise on water efficiency and drip maintenance."
    ),
    PeerFarmer(
        id = "farmer_amina",
        name = "Amina Nsubuga",
        location = "Mbarara, Uganda",
        mainCrops = "Cassava & Matooke",
        avatarUrl = "https://images.unsplash.com/photo-1573496359142-b8d87734a5a2?w=400&auto=format&fit=crop&q=80",
        resilienceBadge = "Agroforestry 🌳",
        farmSize = "5.0 Acres",
        agronomicPractices = listOf("Cassava Mosaic Scouting", "Shade Agroforestry", "Organic Bio-fertilizer"),
        bio = "Community extension mentor. Managing clean-stem disease-free cassava cuttings and organic soil building."
    ),
    PeerFarmer(
        id = "farmer_joseph",
        name = "Joseph Chanda",
        location = "Lusaka, Zambia",
        mainCrops = "Sorghum & Groundnuts",
        avatarUrl = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=400&auto=format&fit=crop&q=80",
        resilienceBadge = "Climate Sentinel ☀️",
        farmSize = "4.2 Acres",
        agronomicPractices = listOf("Zai Planting Pits", "Drought-Hardy Cereals", "Collective Market Aggregation"),
        bio = "Pioneering climate-smart pit planting for drought resilience. Active in grain pricing and buyer negotiations."
    )
)

@Composable
fun FarmerCommunityHubsSection(
    spaces: List<Space>,
    onSpaceClick: (Space) -> Unit,
    onSeeAllClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val displaySpaces = if (spaces.isNotEmpty()) spaces else DEFAULT_SPACES

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Farmer Community Hubs 🌾",
                    style = PoppinsTypography().headlineMedium.copy(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )
                Text(
                    text = "Peer discussion spaces on crops, pests, and markets",
                    style = PoppinsTypography().bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 12.sp
                    )
                )
            }
            Text(
                text = "See All",
                style = PoppinsTypography().bodySmall.copy(
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                ),
                modifier = Modifier.clickable { onSeeAllClick() }
            )
        }

        Spacer(Modifier.height(8.dp))

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(displaySpaces, key = { it.id }) { space ->
                FarmerSpaceCard(
                    space = space,
                    onClick = { onSpaceClick(space) }
                )
            }
        }
    }
}

@Composable
fun FarmerSpaceCard(
    space: Space,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .width(240.dp)
            .clip(RoundedCornerShape(18.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .background(MaterialTheme.colorScheme.primaryContainer)
            ) {
                if (space.coverImageUrl.startsWith("http")) {
                    AsyncImage(
                        model = space.coverImageUrl,
                        contentDescription = space.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.65f))
                            )
                        )
                )

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color.Black.copy(alpha = 0.7f),
                    modifier = Modifier
                        .padding(8.dp)
                        .align(Alignment.BottomEnd)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Group,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        val memberCount = space.memberIds.size
                        Text(
                            text = if (memberCount == 1) "1 farmer" else "$memberCount farmers",
                            style = PoppinsTypography().labelSmall,
                            color = Color.White,
                            fontSize = 10.sp
                        )
                    }
                }
            }

            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = space.name,
                    style = PoppinsTypography().titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = space.description,
                    style = PoppinsTypography().bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = 11.sp,
                    lineHeight = 15.sp
                )
                Spacer(Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Open Hub",
                        style = PoppinsTypography().labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun FarmerConnectSection(
    farmers: List<User> = emptyList(),
    onOpenSpaces: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedFarmer by remember { mutableStateOf<PeerFarmer?>(null) }

    val displayFarmers = remember(farmers) {
        if (farmers.isNotEmpty()) {
            farmers.map { it.toPeerFarmer() }
        } else {
            DEFAULT_PEER_FARMERS
        }
    }

    if (selectedFarmer != null) {
        PeerFarmerDetailDialog(
            farmer = selectedFarmer!!,
            onDismiss = { selectedFarmer = null },
            onConnect = {
                selectedFarmer = null
                onOpenSpaces()
            }
        )
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Meet & Connect with Fellow Farmers 👥",
                    style = PoppinsTypography().headlineMedium.copy(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )
                Text(
                    text = "Exchange crop observations with experienced smallholders",
                    style = PoppinsTypography().bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 12.sp
                    )
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(displayFarmers, key = { it.id }) { farmer ->
                PeerFarmerCard(
                    farmer = farmer,
                    onClick = { selectedFarmer = farmer }
                )
            }
        }
    }
}


@Composable
fun PeerFarmerCard(
    farmer: PeerFarmer,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .width(220.dp)
            .clip(RoundedCornerShape(18.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f))
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
            ) {
                AsyncImage(
                    model = farmer.avatarUrl,
                    contentDescription = farmer.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(Modifier.height(8.dp))

            Text(
                text = farmer.name,
                style = PoppinsTypography().titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(12.dp)
                )
                Spacer(Modifier.width(3.dp))
                Text(
                    text = farmer.location,
                    style = PoppinsTypography().labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(Modifier.height(8.dp))

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
            ) {
                Text(
                    text = farmer.resilienceBadge,
                    style = PoppinsTypography().labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 10.sp,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
            }

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Crops: ${farmer.mainCrops}",
                style = PoppinsTypography().bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 11.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(Modifier.height(12.dp))

            OutlinedButton(
                onClick = onClick,
                modifier = Modifier.fillMaxWidth().height(36.dp),
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(horizontal = 8.dp)
            ) {
                Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(14.dp))
                Spacer(Modifier.width(6.dp))
                Text(
                    "Connect",
                    style = PoppinsTypography().labelMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
fun PeerFarmerDetailDialog(
    farmer: PeerFarmer,
    onDismiss: () -> Unit,
    onConnect: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .border(3.dp, MaterialTheme.colorScheme.primary, CircleShape)
                ) {
                    AsyncImage(
                        model = farmer.avatarUrl,
                        contentDescription = farmer.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(Modifier.height(12.dp))

                Text(
                    text = farmer.name,
                    style = PoppinsTypography().titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = "${farmer.location} • Farm: ${farmer.farmSize}",
                    style = PoppinsTypography().bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(Modifier.height(8.dp))

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text = farmer.resilienceBadge,
                        style = PoppinsTypography().labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                Spacer(Modifier.height(14.dp))

                Text(
                    text = farmer.bio,
                    style = PoppinsTypography().bodySmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = 18.sp
                )

                Spacer(Modifier.height(14.dp))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = "Agronomic Practices & Focus:",
                        style = PoppinsTypography().labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.height(6.dp))
                    farmer.agronomicPractices.forEach { practice ->
                        Row(
                            modifier = Modifier.padding(vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = practice,
                                style = PoppinsTypography().bodySmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                Spacer(Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = onConnect,
                        modifier = Modifier.weight(1f).height(44.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Open Farmer Hub")
                    }
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(0.8f).height(44.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Close")
                    }
                }
            }
        }
    }
}

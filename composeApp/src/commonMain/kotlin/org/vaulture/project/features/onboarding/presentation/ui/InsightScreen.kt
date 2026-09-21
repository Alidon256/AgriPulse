package org.vaulture.project.features.onboarding.presentation.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mindsetpulse.composeapp.generated.resources.*
import org.jetbrains.compose.resources.painterResource
import org.vaulture.project.core.theme.PoppinsTypography
import org.vaulture.project.features.space.domain.model.PulseStory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InsightScreen(onGetStarted: () -> Unit) {

    val stories = listOf(
        PulseStory(
            "1",
            "Fall Armyworm Control",
            "https://images.pexels.com/photos/3772612/pexels-photo-3772612.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2",
            "Pest Management",
            isLarge = true,
            span = 2
        ),
        PulseStory(
            "2",
            "Drought Irrigation",
            "https://images.pexels.com/photos/1051838/pexels-photo-1051838.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2",
            "Soil & Water",
            span = 1
        ),
        PulseStory(
            "3",
            "Intercropping Tips",
            "https://images.pexels.com/photos/355863/pexels-photo-355863.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2",
            "Yield",
            span = 1
        ),
        PulseStory(
            "4",
            "Organic Neem Spray",
            "https://images.pexels.com/photos/15286/pexels-photo.jpg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2",
            "Natural Pesticide",
            isLarge = true,
            span = 2
        )
    )

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        modifier = Modifier.fillMaxSize(),
        bottomBar = {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Box(
                        modifier = Modifier.height(8.dp).width(16.dp)
                            .background(Color.Gray.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
                    )
                    Box(
                        modifier = Modifier.height(8.dp).width(16.dp)
                            .background(Color.Gray.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
                    )
                    Box(
                        modifier = Modifier.height(8.dp).width(32.dp)
                            .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(4.dp))
                    )
                }

                Button(
                    onClick = onGetStarted,
                    modifier = Modifier.height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text(
                        "Launch AgriPulse 🌾",
                        fontSize = 16.sp,
                        style = PoppinsTypography().bodyMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }
        }
    ) { paddingValues ->
        BoxWithConstraints(modifier = Modifier.padding(paddingValues)) {
            val isWideScreen = maxWidth > 920.dp
            val gridColumns = if (isWideScreen) 4 else 2

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .consumeWindowInsets(paddingValues)
            ) {
                Text(
                    "Explore Agricultural Hubs 🚜",
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp,
                    style = PoppinsTypography().bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 40.dp, bottom = 24.dp)
                )

                LazyVerticalGrid(
                    columns = GridCells.Fixed(gridColumns),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    itemsIndexed(
                        stories,
                        key = { _, item -> item.id },
                        span = { _, item ->
                            val spanCount = if (isWideScreen) {
                                if (item.isLarge) 2 else 1
                            } else {
                                if (item.isLarge) 2 else 1
                            }
                            GridItemSpan(spanCount)
                        }
                    ) { _, story ->
                        PulseStoryCard(story)
                    }
                }
            }
        }
    }
}

@Composable
fun PulseStoryCard(story: PulseStory) {
    val image = when (story.id) {
        "1" -> Res.drawable.val_1
        "2" -> Res.drawable.val_2
        "3" -> Res.drawable.bg_two
        "4" -> Res.drawable.ic_img1
        else -> Res.drawable.bg_two
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(if (story.isLarge) 240.dp else 200.dp)
            .clip(RoundedCornerShape(16.dp)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(image),
                contentDescription = story.title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f)),
                            startY = 200f
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Bottom
            ) {
                Text(
                    text = story.category.uppercase(),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    fontSize = 12.sp,
                    style = PoppinsTypography().labelSmall,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = story.title,
                    color = Color.White,
                    style = PoppinsTypography().bodyMedium,
                    fontSize = if (story.isLarge) 22.sp else 18.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}

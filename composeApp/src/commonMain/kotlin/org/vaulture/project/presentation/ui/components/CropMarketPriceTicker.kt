package org.vaulture.project.presentation.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.vaulture.project.core.network.MarketPriceService
import org.vaulture.project.core.network.RegionalCropMarketPrice
import org.vaulture.project.core.theme.PoppinsTypography

@Composable
fun CropMarketPriceTicker(
    modifier: Modifier = Modifier
) {
    val marketService = remember { MarketPriceService() }
    var prices by remember { mutableStateOf<List<RegionalCropMarketPrice>>(emptyList()) }
    var selectedPrice by remember { mutableStateOf<RegionalCropMarketPrice?>(null) }

    LaunchedEffect(Unit) {
        prices = marketService.getMarketPrices()
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Storefront,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    "Regional Market Prices 📈",
                    style = PoppinsTypography().titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Text(
                "Live Exchange Hubs",
                style = PoppinsTypography().labelSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            prices.forEach { crop ->
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                    ),
                    modifier = Modifier.clickable {
                        selectedPrice = crop
                    }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                crop.cropName,
                                style = PoppinsTypography().labelMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                crop.unitPrice,
                                style = PoppinsTypography().bodySmall,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Spacer(Modifier.width(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                if (crop.isPositiveTrend) Icons.AutoMirrored.Filled.TrendingUp else Icons.AutoMirrored.Filled.TrendingDown,
                                contentDescription = if (crop.isPositiveTrend) "Price Up" else "Price Down",
                                tint = if (crop.isPositiveTrend) Color(0xFF2E7D32) else Color(0xFFD32F2F),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(Modifier.width(2.dp))
                            Text(
                                text = crop.changePercent,
                                style = PoppinsTypography().labelSmall,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (crop.isPositiveTrend) Color(0xFF2E7D32) else Color(0xFFD32F2F)
                            )
                        }
                    }
                }
            }
        }
    }

    selectedPrice?.let { crop ->
        AlertDialog(
            onDismissRequest = { selectedPrice = null },
            title = {
                Text(
                    text = crop.cropName,
                    style = PoppinsTypography().titleMedium,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Unit Rate: ${crop.unitPrice}",
                        style = PoppinsTypography().bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Standard Bag: ${crop.perBagPrice}",
                        style = PoppinsTypography().bodyMedium
                    )
                    Text(
                        text = "Primary Trading Hub: ${crop.topMarketHub}",
                        style = PoppinsTypography().bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "24h Trend: ${crop.changePercent}",
                        style = PoppinsTypography().bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = if (crop.isPositiveTrend) Color(0xFF2E7D32) else Color(0xFFD32F2F)
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { selectedPrice = null }) {
                    Text("Close")
                }
            }
        )
    }
}

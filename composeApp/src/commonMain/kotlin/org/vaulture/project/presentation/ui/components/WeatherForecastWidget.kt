package org.vaulture.project.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import org.vaulture.project.core.network.AgronomicClimateData
import org.vaulture.project.core.network.ClimateService
import org.vaulture.project.core.theme.PoppinsTypography
import kotlin.math.roundToInt

@Composable
fun WeatherForecastWidget(
    modifier: Modifier = Modifier
) {
    val climateService = remember { ClimateService() }
    var climate by remember { mutableStateOf(ClimateService.DEFAULT_CLIMATE_DATA) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        try {
            val result = climateService.getRealtimeClimate()
            climate = result
        } catch (_: Exception) {
            climate = ClimateService.DEFAULT_CLIMATE_DATA
        } finally {
            isLoading = false
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(
                1.dp,
                MaterialTheme.colorScheme.primary.copy(alpha = 0.25f),
                RoundedCornerShape(20.dp)
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.WbSunny,
                        contentDescription = "Weather",
                        tint = Color(0xFFFFA726),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Climate & Rainfall Forecast",
                        style = PoppinsTypography().titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .background(
                                color = if (climate.isLive) Color(0xFF2E7D32) else Color(0xFFF57C00),
                                shape = androidx.compose.foundation.shape.CircleShape
                            )
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        if (climate.isLive) "Live Open-Meteo" else "Offline Cache",
                        style = PoppinsTypography().labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 10.sp
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                WeatherMetricItem(
                    label = "Temp",
                    value = "${climate.temperatureCelsius.roundToInt()}°C",
                    sub = climate.weatherCondition,
                    icon = Icons.Default.Thermostat,
                    color = Color(0xFFF57C00)
                )
                WeatherMetricItem(
                    label = "Precipitation",
                    value = "${climate.precipitationMm} mm",
                    sub = if (climate.isRaining) "Active Rain" else "Dry Period",
                    icon = Icons.Default.CloudQueue,
                    color = Color(0xFF0288D1)
                )
                WeatherMetricItem(
                    label = "Relative Humidity",
                    value = "${climate.humidityPercent.roundToInt()}%",
                    sub = "Soil Dew",
                    icon = Icons.Default.WaterDrop,
                    color = Color(0xFF2E7D32)
                )
            }

            Spacer(Modifier.height(12.dp))

            // Real-Time Agronomic Advice Box
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Info,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = climate.agronomicAdvice,
                    style = PoppinsTypography().bodySmall,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun WeatherMetricItem(
    label: String,
    value: String,
    sub: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = color,
            modifier = Modifier.size(22.dp)
        )
        Spacer(Modifier.width(8.dp))
        Column {
            Text(
                text = value,
                style = PoppinsTypography().labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = sub,
                style = PoppinsTypography().labelSmall,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

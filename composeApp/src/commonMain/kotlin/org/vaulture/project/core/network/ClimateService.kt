package org.vaulture.project.core.network

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class OpenMeteoResponse(
    val current: CurrentWeather? = null,
    val daily: DailyWeather? = null
)

@Serializable
data class CurrentWeather(
    val temperature_2m: Float = 26f,
    val relative_humidity_2m: Float = 65f,
    val precipitation: Float = 0f,
    val weather_code: Int = 1,
    val wind_speed_10m: Float = 8f
)

@Serializable
data class DailyWeather(
    val temperature_2m_max: List<Float> = emptyList(),
    val temperature_2m_min: List<Float> = emptyList(),
    val precipitation_sum: List<Float> = emptyList()
)

data class AgronomicClimateData(
    val temperatureCelsius: Float,
    val humidityPercent: Float,
    val precipitationMm: Float,
    val weatherCondition: String,
    val isRaining: Boolean,
    val agronomicAdvice: String,
    val isLive: Boolean = true
)

class ClimateService {
    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }
    }

    suspend fun getRealtimeClimate(
        latitude: Double = 0.3476,
        longitude: Double = 32.5825
    ): AgronomicClimateData {
        return try {
            val url = "https://api.open-meteo.com/v1/forecast?latitude=$latitude&longitude=$longitude&current=temperature_2m,relative_humidity_2m,precipitation,weather_code,wind_speed_10m&daily=temperature_2m_max,temperature_2m_min,precipitation_sum&timezone=auto"
            val response: OpenMeteoResponse = client.get(url).body()
            val current = response.current ?: throw IllegalStateException("Empty current weather payload")
            val condition = mapWeatherCodeToCondition(current.weather_code)
            val isRaining = current.precipitation > 0f || current.weather_code in 51..99
            
            val advice = when {
                current.precipitation > 3f || current.weather_code in 51..99 ->
                    "Rain expected today: hold off on granular fertilizer or spray applications to avoid nutrient wash-off."
                current.temperature_2m > 30f && current.relative_humidity_2m < 50f ->
                    "High crop evapotranspiration: replenish mulch barrier and irrigate early morning to prevent wilting."
                else ->
                    "Favorable field conditions: optimal timing for crop scouting, intercropping maintenance, and weeding."
            }

            AgronomicClimateData(
                temperatureCelsius = current.temperature_2m,
                humidityPercent = current.relative_humidity_2m,
                precipitationMm = current.precipitation,
                weatherCondition = condition,
                isRaining = isRaining,
                agronomicAdvice = advice,
                isLive = true
            )
        } catch (e: Exception) {
            // Resilient Agronomic Fallback
            DEFAULT_CLIMATE_DATA
        }
    }

    private fun mapWeatherCodeToCondition(code: Int): String = when (code) {
        0 -> "Clear Sky"
        1 -> "Mainly Clear"
        2 -> "Partly Cloudy"
        3 -> "Overcast"
        45, 48 -> "Foggy Horizon"
        51, 53, 55 -> "Light Drizzle"
        61, 63, 65 -> "Moderate Rain"
        80, 81, 82 -> "Rain Showers"
        95, 96, 99 -> "Thunderstorm Alert"
        else -> "Partly Cloudy"
    }

    companion object {
        val DEFAULT_CLIMATE_DATA = AgronomicClimateData(
            temperatureCelsius = 27.5f,
            humidityPercent = 64f,
            precipitationMm = 0.4f,
            weatherCondition = "Partly Cloudy",
            isRaining = false,
            agronomicAdvice = "Favorable field conditions: optimal timing for crop scouting, intercropping maintenance, and weeding.",
            isLive = false
        )
    }
}

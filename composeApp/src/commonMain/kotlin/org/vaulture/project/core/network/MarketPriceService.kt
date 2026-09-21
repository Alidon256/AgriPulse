package org.vaulture.project.core.network

import kotlinx.serialization.Serializable

@Serializable
data class RegionalCropMarketPrice(
    val cropName: String,
    val unitPrice: String,
    val perBagPrice: String,
    val changePercent: String,
    val isPositiveTrend: Boolean,
    val topMarketHub: String,
    val currency: String = "USD"
)

class MarketPriceService {
    // Open Market Commodities dataset for African agricultural trading corridors
    private val defaultPrices = listOf(
        RegionalCropMarketPrice(
            cropName = "White Maize (Grade 1)",
            unitPrice = "$0.26 / kg",
            perBagPrice = "$26.00 / 100kg",
            changePercent = "+3.4%",
            isPositiveTrend = true,
            topMarketHub = "Nairobi Wholesale Hub"
        ),
        RegionalCropMarketPrice(
            cropName = "Dry Cassava Chips",
            unitPrice = "$0.18 / kg",
            perBagPrice = "$18.50 / 100kg",
            changePercent = "+1.8%",
            isPositiveTrend = true,
            topMarketHub = "Kampala Kalerwe Market"
        ),
        RegionalCropMarketPrice(
            cropName = "Arabica Coffee (FAQ)",
            unitPrice = "$3.40 / kg",
            perBagPrice = "$204.00 / 60kg",
            changePercent = "+4.6%",
            isPositiveTrend = true,
            topMarketHub = "Addis Ababa / Kigali Board"
        ),
        RegionalCropMarketPrice(
            cropName = "Robusta Coffee",
            unitPrice = "$2.85 / kg",
            perBagPrice = "$171.00 / 60kg",
            changePercent = "+2.2%",
            isPositiveTrend = true,
            topMarketHub = "Kampala Coffee Terminal"
        ),
        RegionalCropMarketPrice(
            cropName = "Red Kidney Beans",
            unitPrice = "$0.65 / kg",
            perBagPrice = "$65.00 / 100kg",
            changePercent = "-0.8%",
            isPositiveTrend = false,
            topMarketHub = "Arusha Grain Exchange"
        ),
        RegionalCropMarketPrice(
            cropName = "Paddy Rice",
            unitPrice = "$0.44 / kg",
            perBagPrice = "$44.00 / 100kg",
            changePercent = "+2.1%",
            isPositiveTrend = true,
            topMarketHub = "Kilombero Trading Center"
        ),
        RegionalCropMarketPrice(
            cropName = "Soya Beans (High Oil)",
            unitPrice = "$0.52 / kg",
            perBagPrice = "$52.00 / 100kg",
            changePercent = "+1.5%",
            isPositiveTrend = true,
            topMarketHub = "Lusaka Commodity Exchange"
        )
    )

    suspend fun getMarketPrices(): List<RegionalCropMarketPrice> {
        return try {
            // Can sync with open agricultural exchange feeds or cached commodities
            defaultPrices
        } catch (_: Exception) {
            defaultPrices
        }
    }
}

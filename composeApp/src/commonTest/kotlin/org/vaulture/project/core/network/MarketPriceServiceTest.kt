package org.vaulture.project.core.network

import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class MarketPriceServiceTest {

    private val service = MarketPriceService()

    @Test
    fun testGetMarketPricesReturnsStapleAfricanCrops() = runTest {
        val prices = service.getMarketPrices()
        assertFalse(prices.isEmpty(), "Market prices list should not be empty")

        // Verify staple crops exist
        val cropNames = prices.map { it.cropName }
        assertTrue(cropNames.any { it.contains("Maize", ignoreCase = true) }, "Should contain Maize")
        assertTrue(cropNames.any { it.contains("Coffee", ignoreCase = true) }, "Should contain Coffee")
        assertTrue(cropNames.any { it.contains("Cassava", ignoreCase = true) }, "Should contain Cassava")
        assertTrue(cropNames.any { it.contains("Beans", ignoreCase = true) }, "Should contain Beans")
    }

    @Test
    fun testMarketPricesHaveValidCurrencyAndPricing() = runTest {
        val prices = service.getMarketPrices()
        for (price in prices) {
            assertTrue(price.cropName.isNotBlank(), "Crop name should not be blank")
            assertTrue(price.unitPrice.isNotBlank(), "Unit price should not be blank")
            assertTrue(price.perBagPrice.isNotBlank(), "Per bag price should not be blank")
            assertTrue(price.topMarketHub.isNotBlank(), "Market hub should not be blank")
            assertEquals("USD", price.currency)
        }
    }
}

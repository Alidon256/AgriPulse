package org.vaulture.project.features.home.domain.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CropHealthStatusTest {

    @Test
    fun testCropHealthStatusLabelsAndColors() {
        assertEquals("Optimal Growth", CropHealthStatus.OPTIMAL_VIGOR.label)
        assertEquals("Mild Crop Stress", CropHealthStatus.MILD_STRESS.label)
        assertEquals("High Pest Risk", CropHealthStatus.HIGH_PEST_RISK.label)
        assertEquals("Critical Threat", CropHealthStatus.CRITICAL_ALERT.label)

        assertEquals(0xFF4CAF50, CropHealthStatus.OPTIMAL_VIGOR.colorHex)
        assertEquals(0xFFD32F2F, CropHealthStatus.CRITICAL_ALERT.colorHex)
    }

    @Test
    fun testBackwardCompatibilityAliases() {
        assertEquals(CropHealthStatus.OPTIMAL_VIGOR, CropHealthStatus.STABLE)
        assertEquals(CropHealthStatus.HIGH_PEST_RISK, CropHealthStatus.HIGH_STRESS)
        assertEquals(CropHealthStatus.CRITICAL_ALERT, CropHealthStatus.BURNOUT_RISK)

        assertEquals(AgronomicActionType.CROP_DIAGNOSIS, AgronomicActionType.THOUGHT_RECORD)
        assertEquals(AgronomicActionType.PEST_SOIL_ACTION, AgronomicActionType.BEHAVIORAL_ACTIVATION)
    }

    @Test
    fun testCropSymptomsAndThreatsListsAreNonEmptyAndSorted() {
        assertTrue(cropSymptomsList.isNotEmpty(), "Crop symptoms list should not be empty")
        assertTrue(cropThreatsList.isNotEmpty(), "Crop threats list should not be empty")

        assertTrue(cropThreatsList.contains("Fall Armyworm"))
        assertTrue(cropThreatsList.contains("Cassava Mosaic Virus"))
        assertTrue(cropThreatsList.contains("Locust Swarm"))

        assertEquals(cropSymptomsList, commonEmotions)
        assertEquals(cropThreatsList, cognitiveDistortionsList)
    }
}

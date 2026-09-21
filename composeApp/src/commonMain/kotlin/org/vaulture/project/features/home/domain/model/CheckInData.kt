package org.vaulture.project.features.home.domain.model

import dev.gitlive.firebase.firestore.Timestamp
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class CropHealthStatus(val colorHex: Long, val label: String) {
    @SerialName("STABLE")
    OPTIMAL_VIGOR(0xFF4CAF50, "Optimal Growth"),          // Green

    @SerialName("MILD_STRESS")
    MILD_STRESS(0xFFFFC107, "Mild Crop Stress"),    // Amber

    @SerialName("HIGH_STRESS")
    HIGH_PEST_RISK(0xFFFF5722, "High Pest Risk"),      // Deep Orange

    @SerialName("BURNOUT_RISK")
    CRITICAL_ALERT(0xFFD32F2F, "Critical Threat");      // Red

    companion object {
        val STABLE get() = OPTIMAL_VIGOR
        val HIGH_STRESS get() = HIGH_PEST_RISK
        val BURNOUT_RISK get() = CRITICAL_ALERT
    }
}

typealias MentalState = CropHealthStatus

@Serializable
enum class AgronomicActionType(val displayName: String) {
    @SerialName("THOUGHT_RECORD")
    CROP_DIAGNOSIS("Crop Health Diagnosis"),

    @SerialName("BEHAVIORAL_ACTIVATION")
    PEST_SOIL_ACTION("Pest & Soil Action"),

    @SerialName("GRATITUDE_JOURNING")
    HARVEST_JOURNAL("Harvest Journal"),

    @SerialName("PROBLEM_SOLVING")
    DROUGHT_MITIGATION("Drought Mitigation"),

    @SerialName("MINDFULNESS_REFLECTION")
    SEASONAL_REFLECTION("Seasonal Reflection"),

    @SerialName("NONE")
    NONE("Daily Field Check-In");

    companion object {
        val THOUGHT_RECORD get() = CROP_DIAGNOSIS
        val BEHAVIORAL_ACTIVATION get() = PEST_SOIL_ACTION
        val GRATITUDE_JOURNALING get() = HARVEST_JOURNAL
        val PROBLEM_SOLVING get() = DROUGHT_MITIGATION
        val MINDFULNESS_REFLECTION get() = SEASONAL_REFLECTION
    }
}

typealias CbtExerciseType = AgronomicActionType

@Serializable
data class EmotionRating(
    val emotion: String = "",
    val intensity: Int = 5
)

@Serializable
data class AgronomicActionLogEntry(
    val situation: String = "",
    val automaticNegativeThought: String = "",
    val emotionsBefore: List<EmotionRating> = emptyList(),
    val cognitiveDistortions: List<String> = emptyList(),
    val evidenceForThought: String = "",
    val evidenceAgainstThought: String = "",
    val alternativeThought: String = "",
    val emotionsAfter: List<EmotionRating> = emptyList()
)

typealias ThoughtRecordEntry = AgronomicActionLogEntry

@Serializable
data class CheckInEntry(
    val id: String = "",
    val userId: String = "",
    val timestamp: Timestamp = Timestamp(0,0),
    val score: Int = 0,
    val state: CropHealthStatus = CropHealthStatus.OPTIMAL_VIGOR,
    val aiInsight: String = "",
    val sentimentScore: Float = 0f,
    val overallMood: String = "",
    val moodIntensity: Int = 5,
    val primaryEmotions: List<EmotionRating> = emptyList(),
    val generalThoughts: String = "",
    val positiveHighlights: String = "",
    val challengesFaced: String = "",
    val significantActivities: List<String> = emptyList(),
    val selfCareActivities: List<String> = emptyList(),
    val cbtExerciseType: AgronomicActionType = AgronomicActionType.NONE,
    val thoughtRecord: AgronomicActionLogEntry? = null,
    val cbtReflectionPrompt: String? = null,
    val cbtReflectionResponse: String? = null,
    val learnedFromCbt: String = ""
)

// Predefined list of crop symptoms for farmer selection
val cropSymptomsList = listOf(
    "Healthy Green", "Slight Yellowing", "Leaf Spotting", "Wilted Leaves",
    "Pest Infestation", "Stem Rot", "Dry Soil", "Drought Stressed",
    "Over-watered", "Insect Bites", "Root Rot", "Nutrient Deficient",
    "Strong Growth", "Bumper Yield Potential"
).sorted()

val commonEmotions = cropSymptomsList

// Predefined list of common agricultural threats
val cropThreatsList = listOf(
    "Fall Armyworm",
    "Locust Swarm",
    "Early Blight",
    "Late Blight",
    "Cassava Mosaic Virus",
    "Striga Weed",
    "Nutrient Deficiency",
    "Soil Erosion",
    "Irregular Rainfall",
    "Extreme Heatwave",
    "Post-Harvest Rot"
).sorted()

val cognitiveDistortionsList = cropThreatsList

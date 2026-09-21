package org.vaulture.project.data.remote

import dev.gitlive.firebase.firestore.Timestamp
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.delay
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.vaulture.project.features.home.domain.model.AgronomicActionType
import org.vaulture.project.features.home.domain.model.CheckInEntry

object AgriPulseConfig {
    /**
     * Gemini API Key provider.
     * Can be set dynamically via AgriPulseConfig.geminiApiKey = "..." or environment.
     */
    var geminiApiKey: String = ""
        get() = field.ifBlank {
            // Read from locally defined provider that is completely hidden from version control
            GeminiKeyProvider.API_KEY
        }
}

@Serializable
data class GeminiResponse(
    val candidates: List<Candidate>? = null,
    val error: GeminiError? = null,
    val promptFeedback: PromptFeedback? = null
)

@Serializable
data class GeminiError(
    val code: Int? = null,
    val message: String? = null,
    val status: String? = null
)

@Serializable
data class PromptFeedback(
    val blockReason: String? = null
)

@Serializable
data class GeminiRequest(val contents: List<Content>)

@Serializable
data class Content(val parts: List<Part>)

@Serializable
data class InlineData(
    val mimeType: String,
    val data: String // Base64 encoded bytes
)

@Serializable
data class Part(
    val text: String? = null,
    val inlineData: InlineData? = null
)

@Serializable
data class Candidate(val content: Content?)

data class AnalysisResult(val sentimentScore: Float, val insight: String)

@Serializable
data class CropVisionDiagnosis(
    val threatName: String,
    val scientificName: String = "",
    val confidenceScore: Float = 0.85f,
    val severity: String = "Moderate",
    val organicRemedy: String,
    val chemicalRemedy: String = "",
    val preventionTip: String
)

class GeminiService(
    private val apiKeyProvider: () -> String = { AgriPulseConfig.geminiApiKey }
) {
    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }
    }

    private val url: String
        get() = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=${apiKeyProvider()}"

    private suspend fun <T> retryWithBackoff(
        times: Int = 3,
        initialDelay: Long = 1000,
        maxDelay: Long = 5000,
        factor: Double = 2.0,
        block: suspend () -> T
    ): T {
        var currentDelay = initialDelay
        repeat(times - 1) { attempt ->
            try {
                return block()
            } catch (e: Exception) {
                val msg = e.message ?: ""
                if (msg.contains("API key", ignoreCase = true) ||
                    msg.contains("disabled", ignoreCase = true) ||
                    msg.contains("blocked", ignoreCase = true) ||
                    msg.contains("has not been used", ignoreCase = true)
                ) {
                    throw e
                }
                println("GeminiService: Attempt ${attempt + 1} failed: ${e.message}. Retrying in ${currentDelay}ms...")
                delay(currentDelay)
                currentDelay = (currentDelay * factor).toLong().coerceAtMost(maxDelay)
            }
        }
        return block()
    }

    suspend fun generateDailyQuestions(): List<String> {
        val promptText = """
            Generate 5 short, practical crop and field check-in questions for a smallholder farmer in Africa.
            Focus on soil moisture, crop leaf health, pest presence, and weather/rainfall impact.
            
            Strictly return them as a list separated by pipes "|".
            Example: How is the soil moisture in your fields today?|Have you noticed any leaf yellowing or spotting?|Are there signs of insect pests?|How has recent rainfall affected your crop growth?|What is your main farming priority today?
        """.trimIndent()

        val requestBody = GeminiRequest(listOf(Content(listOf(Part(text = promptText)))))

        return try {
            retryWithBackoff {
                val response: GeminiResponse = client.post(url) {
                    contentType(ContentType.Application.Json)
                    setBody(requestBody)
                }.body()

                if (response.error != null) {
                    throw Exception("API Error: ${response.error.message}")
                }

                val rawText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                    ?: throw Exception("Empty response candidates")
                val questions = rawText.replace("\n", "").split("|").map { it.trim() }

                if (questions.size < 3) {
                    throw Exception("Parsing failed or too few questions returned.")
                }

                questions.take(5)
            }
        } catch (e: Exception) {
            println("GeminiService: All retries failed. Falling back to defaults. Error: ${e.message}")
            getDefaultQuestions()
        }
    }

    suspend fun analyzeJournalEntry(text: String): AnalysisResult {
        val promptText = """
            Act as an expert African agronomist and extension specialist. Analyze this farmer's field observation: "$text"
            
            Output strictly in this format:
            SCORE: [float -1.0 to 1.0, where -1.0 indicates severe crop disease/pest outbreak and 1.0 indicates optimal crop health]
            INSIGHT: [2 supportive, practical agronomic treatment or soil management advice sentences]
        """.trimIndent()

        val requestBody = GeminiRequest(listOf(Content(listOf(Part(text = promptText)))))

        try {
            val response: GeminiResponse = client.post(url) {
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }.body()

            val rawText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: ""
            return parseAnalysisResponse(rawText)
        } catch (e: Exception) {
            println("GeminiService: Error analyzing entry: ${e.message}")
            return AnalysisResult(0.0f, "Your field observation has been recorded locally.")
        }
    }

    /**
     * Multimodal Vision Crop Diagnostic:
     * Accepts Base64 encoded photo of a crop leaf or pest, identifies the threat,
     * and recommends organic and agronomic interventions.
     */
    suspend fun diagnoseCropImage(
        base64Image: String,
        mimeType: String = "image/jpeg",
        farmerNote: String = ""
    ): CropVisionDiagnosis {
        val promptText = """
            Act as an expert African agronomist. Examine this photo of a crop plant / leaf.
            Farmer's note: "${farmerNote.ifBlank { "Please inspect this crop symptom." }}"
            
            Provide diagnostic output strictly in this labeled format:
            THREAT: [Common Name of Pest or Disease, e.g. Fall Armyworm, Early Blight, Maize Streak Virus, or Healthy Crop]
            SCIENTIFIC: [Scientific Name, or N/A]
            CONFIDENCE: [Float between 0.0 and 1.0, e.g. 0.92]
            SEVERITY: [Mild, Moderate, or Severe]
            ORGANIC_REMEDY: [Practical, low-cost organic remedy using local African materials like neem oil, wood ash, or push-pull intercropping]
            CHEMICAL_REMEDY: [Standard chemical alternative if organic fails, or N/A]
            PREVENTION: [Cultural prevention tip for subsequent planting season]
        """.trimIndent()

        val parts = listOf(
            Part(text = promptText),
            Part(inlineData = InlineData(mimeType = mimeType, data = base64Image))
        )
        val requestBody = GeminiRequest(listOf(Content(parts)))

        return try {
            val response: GeminiResponse = client.post(url) {
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }.body()

            val rawText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: ""
            parseCropVisionResponse(rawText)
        } catch (e: Exception) {
            println("GeminiService: Vision diagnosis failed: ${e.message}")
            CropVisionDiagnosis(
                threatName = "Suspected Crop Stress",
                scientificName = "Agronomic Observation",
                confidenceScore = 0.70f,
                severity = "Moderate",
                organicRemedy = "Spray with dilute neem leaf extract or wood ash powder. Ensure proper weed management.",
                chemicalRemedy = "Consult your local agro-dealer if symptoms persist across >15% of your plot.",
                preventionTip = "Practice crop rotation with legumes to improve soil nitrogen and disrupt pest life cycles."
            )
        }
    }

    private fun parseCropVisionResponse(rawText: String): CropVisionDiagnosis {
        var threat = "Crop Stress Detected"
        var scientific = ""
        var confidence = 0.85f
        var severity = "Moderate"
        var organic = "Apply dilute neem extract and remove severely affected lower leaves."
        var chemical = "Targeted contact pesticide if infestation spreads."
        var prevention = "Rotate crops and intercrop with legumes or Desmodium."

        rawText.lines().forEach { line ->
            val clean = line.trim()
            when {
                clean.startsWith("THREAT:", ignoreCase = true) -> threat = clean.substringAfter(":").trim()
                clean.startsWith("SCIENTIFIC:", ignoreCase = true) -> scientific = clean.substringAfter(":").trim()
                clean.startsWith("CONFIDENCE:", ignoreCase = true) -> confidence = clean.substringAfter(":").trim().toFloatOrNull() ?: 0.85f
                clean.startsWith("SEVERITY:", ignoreCase = true) -> severity = clean.substringAfter(":").trim()
                clean.startsWith("ORGANIC_REMEDY:", ignoreCase = true) -> organic = clean.substringAfter(":").trim()
                clean.startsWith("CHEMICAL_REMEDY:", ignoreCase = true) -> chemical = clean.substringAfter(":").trim()
                clean.startsWith("PREVENTION:", ignoreCase = true) -> prevention = clean.substringAfter(":").trim()
            }
        }

        return CropVisionDiagnosis(
            threatName = threat,
            scientificName = scientific,
            confidenceScore = confidence,
            severity = severity,
            organicRemedy = organic,
            chemicalRemedy = chemical,
            preventionTip = prevention
        )
    }

    suspend fun generateAnalyticsReport(checkIns: List<CheckInEntry>): String {
        if (checkIns.isEmpty()) return "No field data available for agronomic analysis yet."

        val promptText = constructAnalyticsPrompt(checkIns)
        val requestBody = GeminiRequest(listOf(Content(listOf(Part(text = promptText)))))

        return try {
            val httpResponse = client.post(url) {
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }

            val response: GeminiResponse = httpResponse.body()

            if (response.error != null) {
                println("Gemini API Error: ${response.error.message}")
                return "The AI agronomic service is currently unavailable: ${response.error.message}"
            }

            val rawText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text

            if (!rawText.isNullOrBlank()) {
                rawText
            } else {
                "We couldn't generate agronomic insights right now. Please try again later."
            }
        } catch (e: Exception) {
            println("GeminiService: Exception: ${e.message}")
            "Error connecting to AI Agronomist service. Please check your connection."
        }
    }

    private fun parseAnalysisResponse(rawText: String): AnalysisResult {
        var score = 0.0f
        var insight = "Continue monitoring your fields for early signs of pests."

        if (rawText.isBlank()) return AnalysisResult(score, insight)

        rawText.lines().forEach { line ->
            if (line.contains("SCORE:", ignoreCase = true)) {
                val scorePart = line.substringAfter(":").replace("[", "").replace("]", "").trim()
                score = scorePart.toFloatOrNull() ?: 0.0f
            }
            if (line.contains("INSIGHT:", ignoreCase = true)) {
                insight = line.substringAfter(":").replace("[", "").replace("]", "").trim()
            }
        }
        return AnalysisResult(score, insight)
    }

    private fun constructAnalyticsPrompt(checkIns: List<CheckInEntry>): String {
        val summary = checkIns.takeLast(15).joinToString("\n\n---\n\n") { entry ->
            val symptomsStr = if (entry.primaryEmotions.isNotEmpty()) {
                entry.primaryEmotions.joinToString { "${it.emotion} (${it.intensity}/10 severity)" }
            } else "N/A"

            val fieldActionName = if (entry.cbtExerciseType != AgronomicActionType.NONE) {
                entry.cbtExerciseType.displayName
            } else "Routine Scouting"

            """
            Date: ${formatTimestamp(entry.timestamp)}
            Field Risk Score: ${entry.score}/100 (${entry.state.label})
            Overall Crop Condition: ${entry.overallMood}
            Observed Symptoms: $symptomsStr
            Field Observations: ${entry.generalThoughts}
            Agronomic Action: $fieldActionName
            Previous AI Agronomic Insight: ${entry.aiInsight}
            """.trimIndent()
        }

        return """
            You are AgriPulse AI, an expert agricultural extension specialist for African smallholder farmers. 
            Your goal is to help a farmer optimize yield, prevent pest outbreaks, and build climate resilience based on their recent field observations. 🌾

            Here is a summary of their recent field check-ins:
            $summary

            Based on this data, please provide a response in well-formatted Markdown using H3 level headings (e.g. ### Section Title).

            ### 1. Crop Health & Soil Moisture Trends 📈
            Identify recurring field patterns or pest threats observed in recent records. (2-3 bullet points)

            ### 2. Pest & Climate Threat Analysis 🐛
            Offer early diagnostic warnings (e.g. Fall Armyworm, drought stress, or nutrient deficiencies). (1 short paragraph)

            ### 3. Actionable Agronomic Recommendations 🌱
            Suggest practical, low-cost organic or extension practices (e.g. mulching, neem spray, crop rotation). (2-3 bullet points)

            ### 4. Encouragement for Harvest Resilience ☀️
            End with an encouraging sentence for the farmer's hard work. (1 sentence)

            IMPORTANT:
            * Focus on smallholder tropical farming practices.
            * Format strictly as Markdown.
        """.trimIndent()
    }

    private fun getDefaultQuestions() = listOf(
        "How is the soil moisture in your fields today?",
        "Have you noticed any leaf yellowing or spotting on your crops?",
        "Are there signs of insect pests or worm damage?",
        "How has recent rainfall affected your crop growth?",
        "What is your main farming action priority today?"
    )

    private fun formatTimestamp(timestamp: Timestamp): String {
        val instant = kotlin.time.Instant.fromEpochMilliseconds(timestamp.seconds * 1000)
        val date = instant.toLocalDateTime(TimeZone.currentSystemDefault())
        return "${date.month.name.take(3)} ${date.dayOfMonth}"
    }
}

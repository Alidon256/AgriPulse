package org.vaulture.project.features.wellness.domain.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import dev.gitlive.firebase.firestore.Timestamp
import kotlinx.serialization.Serializable

enum class WellnessType(
    val label: String,
    val icon: ImageVector,
    val description: String,
    val gradientStartColor: Color,
    val gradientEndColor: Color
) {
    BREATHING(
        label = "Soil Health & Moisture",
        icon = Icons.Default.Air,
        description = "Guided soil moisture and nutrient checks help prevent crop dehydration and root rot. Monitoring soil organic matter ensures healthy root development.",
        gradientStartColor = Color(0xFF2E7D32),
        gradientEndColor = Color(0xFF1B5E20)
    ),
    YOGA(
        label = "Pest & Crop Diagnostics",
        icon = Icons.Default.BugReport,
        description = "Early pest detection prevents Fall Armyworm and locust infestations from destroying yields. Regular field scouting saves crops before severe damage occurs.",
        gradientStartColor = Color(0xFFF57C00),
        gradientEndColor = Color(0xFFE65100)
    ),
    MEDITATION(
        label = "Drought & Irrigation Care",
        icon = Icons.Default.WaterDrop,
        description = "Managing water conservation during dry spells protects crop growth. Controlled micro-irrigation and mulching preserve soil moisture during heatwaves.",
        gradientStartColor = Color(0xFF0288D1),
        gradientEndColor = Color(0xFF01579B)
    )
}

@Serializable
data class WellnessRecord(
    val id: String = "",
    val userId: String = "",
    val type: WellnessType,
    val durationSeconds: Int,
    val timestamp: Timestamp = Timestamp(0, 0)
)

@Serializable
data class WellnessStats(
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val lastActivityDate: String = "",
    val totalMinutes: Int = 0,
    val sessionsToday: Int = 0,
    val resiliencePoints: Int = 0,
    val totalCheckIns: Int = 0,
    val consistency: Float = 0f
)

data class AgronomicStep(
    val stepNumber: Int,
    val title: String,
    val instruction: String,
    val tip: String,
    val checklist: List<String>
)

val AGRONOMIC_PROTOCOLS: Map<WellnessType, List<AgronomicStep>> = mapOf(
    WellnessType.BREATHING to listOf(
        AgronomicStep(
            stepNumber = 1,
            title = "Topsoil Texture & Compaction",
            instruction = "Take a handful of topsoil (0-10 cm deep). Rub it between your fingers to evaluate crumb structure, aeration, and compaction.",
            tip = "Healthy soil breaks easily into crumbly aggregates like granola, allowing air and root respiration.",
            checklist = listOf(
                "Topsoil is loose and crumbly",
                "No impenetrable hardpan layer",
                "Fresh, earthy organic aroma"
            )
        ),
        AgronomicStep(
            stepNumber = 2,
            title = "Soil Moisture Ribbon Test",
            instruction = "Moisten a palmful of soil into a pliable ball. Push between your thumb and forefinger to squeeze out a soil ribbon.",
            tip = "Ribbon < 2.5 cm = Sand/Loam (needs mulch). Ribbon > 5 cm = Heavy Clay (risk of waterlogging).",
            checklist = listOf(
                "Soil formed a cohesive ball",
                "Measured ribbon length",
                "Soil is moist but not soggy"
            )
        ),
        AgronomicStep(
            stepNumber = 3,
            title = "Root Zone & Biological Life",
            instruction = "Dig 15 cm down near crop root line. Check for deep root branching, nodules (in legumes), and count earthworms.",
            tip = "Finding 3+ earthworms in a single spade slice indicates active aerobic biological cycling.",
            checklist = listOf(
                "Deep root penetration observed",
                "Counted earthworms or beneficial soil life",
                "No anaerobic sulfur odor"
            )
        ),
        AgronomicStep(
            stepNumber = 4,
            title = "Mulch Barrier & Evaporation Shield",
            instruction = "Examine crop rows. Ensure dry organic mulch covers the ground to shield root zone against midday heat.",
            tip = "A 5-8 cm organic mulch layer lowers soil temperature by 4-6°C and reduces moisture evaporation by 60%.",
            checklist = listOf(
                "Mulch layer at least 5 cm thick",
                "Soil shaded from direct baking sun",
                "Observation recorded in field log"
            )
        )
    ),
    WellnessType.YOGA to listOf(
        AgronomicStep(
            stepNumber = 1,
            title = "W-Pattern Field Traverse",
            instruction = "Walk in a 'W' or zigzag path across your parcel to inspect a representative cross-section of plants, not just borders.",
            tip = "Edge rows often harbor higher insect activity; internal rows reveal the true field infestation level.",
            checklist = listOf(
                "Walked zigzag W-route across field",
                "Inspected 10+ random plants",
                "Noted current crop vegetative stage"
            )
        ),
        AgronomicStep(
            stepNumber = 2,
            title = "Whorl & Funnel Inspection",
            instruction = "Part the upper leaf whorl of cereals (maize, sorghum). Check for sawdust-like frass and caterpillar feeding windows.",
            tip = "Window-pane leaf feeding with moist frass indicates Fall Armyworm (FAW) requiring bio-pesticide intervention.",
            checklist = listOf(
                "Checked leaf whorls for frass",
                "Looked for small larvae hiding in funnel",
                "Flagged damaged stalks for treatment"
            )
        ),
        AgronomicStep(
            stepNumber = 3,
            title = "Underside Leaf & Stem Borer Check",
            instruction = "Turn over leaves to examine undersides for aphid clusters or mites. Inspect lower stem collars for borer exit holes.",
            tip = "Cream-colored egg masses on leaf undersides hatch within 3-5 days. Crush or spray before larvae bore into stems.",
            checklist = listOf(
                "Inspected leaf undersides for eggs/aphids",
                "Checked stem collars for rot/borer holes",
                "Checked beneficial predator (ladybug/wasp) presence"
            )
        ),
        AgronomicStep(
            stepNumber = 4,
            title = "Economic Threshold & Action",
            instruction = "Count total infested plants. Determine whether threshold exceeds 10-20% before preparing neem spray or companion plants.",
            tip = "Below 10% infestation, natural predators (parasitoid wasps, ants) often control populations naturally.",
            checklist = listOf(
                "Calculated infestation percentage",
                "Neem oil or wood ash prepared if needed",
                "Shared pest observation in Farmer Space"
            )
        )
    ),
    WellnessType.MEDITATION to listOf(
        AgronomicStep(
            stepNumber = 1,
            title = "Emitter & Furrow Uniformity Audit",
            instruction = "Inspect 5 random drip emitters or furrow discharge points along your lines from head to tail end.",
            tip = "Pressure loss or silt sediment causes uneven flow, dehydrating row ends while overwatering heads.",
            checklist = listOf(
                "Uniform drip discharge across rows",
                "Filters checked and cleared of silt",
                "No torn drip pipes or standing puddles"
            )
        ),
        AgronomicStep(
            stepNumber = 2,
            title = "Infiltration Depth Verification",
            instruction = "Push a thin stick or finger into the soil 2 hours after watering to verify moisture infiltration depth.",
            tip = "Water should penetrate 15-20 cm deep to train roots downward rather than keeping them shallow and vulnerable.",
            checklist = listOf(
                "Moisture reached 15+ cm depth",
                "No hard soil crust blocking absorption",
                "Furrow water absorbed without standing pools"
            )
        ),
        AgronomicStep(
            stepNumber = 3,
            title = "Canopy Midday Stress Observation",
            instruction = "Observe crop foliage between 11 AM and 1 PM. Check if leaves curl temporarily to conserve moisture or wilt permanently.",
            tip = "Midday leaf curl that recovers by dusk is natural transpiration defense; morning wilting signals critical deficit.",
            checklist = listOf(
                "Observed leaf posture under heat",
                "Distinguished heat curl from wilting",
                "Identified high-stress parcel zones"
            )
        ),
        AgronomicStep(
            stepNumber = 4,
            title = "Climate-Resilient Irrigation Timing",
            instruction = "Schedule irrigation exclusively for early dawn (5:30 - 7:30 AM) or dusk (6:00 - 7:30 PM) to minimize evaporative loss.",
            tip = "Dusk watering allows crops 10 uninterrupted hours of cool absorption before direct scorching sunlight.",
            checklist = listOf(
                "Irrigation scheduled for dawn or dusk",
                "Root zone shaded with organic mulch",
                "Water storage tanks covered against evaporation"
            )
        )
    )
)


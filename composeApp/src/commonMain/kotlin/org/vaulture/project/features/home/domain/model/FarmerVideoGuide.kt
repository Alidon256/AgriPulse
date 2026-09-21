package org.vaulture.project.features.home.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class FarmerVideoGuide(
    val id: String,
    val title: String,
    val description: String,
    val instructor: String,
    val durationText: String,
    val category: String,
    val thumbnailUrl: String,
    val videoUrl: String,
    val youtubeVideoId: String,
    val keyTakeaways: List<String>
)

val DEFAULT_VIDEO_GUIDES = listOf(
    FarmerVideoGuide(
        id = "vid_fall_armyworm",
        title = "Managing Fall Armyworm with Push-Pull & Neem",
        description = "Step-by-step demonstration of biological Push-Pull technology and organic neem seed extract spray to defeat Fall Armyworm without chemical pesticide expenses.",
        instructor = "Access Agriculture / icipe",
        durationText = "05:20",
        category = "Pest Control",
        thumbnailUrl = "https://images.unsplash.com/photo-1592982537447-7440770cbfc9?w=800",
        videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4",
        youtubeVideoId = "ForBiggerBlazes",
        keyTakeaways = listOf(
            "Intercrop maize with Desmodium to repel armyworm moths.",
            "Plant Napier grass around plot borders to trap larvae.",
            "Spray neem extract in late afternoon directly into the whorl."
        )
    ),
    FarmerVideoGuide(
        id = "vid_zai_pits",
        title = "Zai Pits & Water Harvesting in Drylands",
        description = "How to dig and compost Zai planting basins to harvest seasonal rain and retain subsoil moisture in dry conditions.",
        instructor = "Sahel Agri Network",
        durationText = "04:45",
        category = "Irrigation",
        thumbnailUrl = "https://images.unsplash.com/photo-1500937386664-56d1dfef3854?w=800",
        videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4",
        youtubeVideoId = "ForBiggerEscapes",
        keyTakeaways = listOf(
            "Dig basins 20cm deep and 30cm wide spaced 75cm apart.",
            "Add two handfuls of cured organic manure per pit.",
            "Basins capture runoff and concentrate soil moisture directly at roots."
        )
    ),
    FarmerVideoGuide(
        id = "vid_biochar_compost",
        title = "Biochar & Fast Organic Compost Making",
        description = "Turn crop residues, maize stalks, and livestock manure into rich biochar compost to restore depleted African soils.",
        instructor = "ECHO Community East Africa",
        durationText = "06:10",
        category = "Soil Health",
        thumbnailUrl = "https://images.unsplash.com/photo-1595974482597-4b8da8879bc5?w=800",
        videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerFun.mp4",
        youtubeVideoId = "ForBiggerFun",
        keyTakeaways = listOf(
            "Slow-pyrolyze dry agricultural stalks in a simple pit or drum.",
            "Inoculate cooled biochar with liquid cattle manure or compost tea.",
            "Incorporate into topsoil to dramatically boost cation exchange and water holding."
        )
    ),
    FarmerVideoGuide(
        id = "vid_drip_irrigation",
        title = "Low-Cost Gravity Drip Irrigation Setup",
        description = "Installing a gravity-fed bucket and drum drip system on vegetable and maize plots with zero electricity required.",
        instructor = "FAO Smart Irrigation Hub",
        durationText = "05:35",
        category = "Irrigation",
        thumbnailUrl = "https://images.unsplash.com/photo-1585314062340-f1a5a7c9328d?w=800",
        videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerJoyBlazes.mp4",
        youtubeVideoId = "ForBiggerJoyBlazes",
        keyTakeaways = listOf(
            "Elevate a 200L drum 1.5 meters above ground for adequate gravity pressure.",
            "Install a fine mesh filter to prevent emitter clogging.",
            "Save up to 60% water compared to furrow or overhead watering."
        )
    ),
    FarmerVideoGuide(
        id = "vid_cassava_diagnostics",
        title = "Scouting Cassava Brown Streak & Mosaic Disease",
        description = "Identify foliar chlorosis, stem lesions, and root necrosis early to select clean virus-free stem cuttings.",
        instructor = "IITA Africa",
        durationText = "04:12",
        category = "Crop Yield",
        thumbnailUrl = "https://images.unsplash.com/photo-1628352081506-83c43123ed6d?w=800",
        videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerMeltdowns.mp4",
        youtubeVideoId = "ForBiggerMeltdowns",
        keyTakeaways = listOf(
            "Inspect leaves for yellow mosaic patterns caused by whiteflies.",
            "Rogue and burn infected plants before disease spreads.",
            "Plant certified disease-resistant stem varieties (e.g., NAROCASS)."
        )
    ),
    FarmerVideoGuide(
        id = "vid_hermetic_storage",
        title = "Zero Post-Harvest Loss with Hermetic Bags",
        description = "Protecting harvested maize, cowpeas, and beans from weevils without applying toxic chemical preservation dusts.",
        instructor = "Purdue PICS Network / WFP",
        durationText = "04:50",
        category = "Crop Yield",
        thumbnailUrl = "https://images.unsplash.com/photo-1574943320219-553eb213f72d?w=800",
        videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
        youtubeVideoId = "BigBuckBunny",
        keyTakeaways = listOf(
            "Ensure grain is dried to under 13.5% moisture before bagging.",
            "Seal each of the two inner polyethylene layers air-tight.",
            "Insects suffocate naturally from lack of oxygen within 10 days."
        )
    )
)


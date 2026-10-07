package com.horadricvault.core.model

import kotlinx.serialization.Serializable

@Serializable
enum class SanctuaryRegion(val regionId: String, val displayName: String, val centerLat: Float, val centerLng: Float) {
    FRACTURED_PEAKS("fractured_peaks", "Fractured Peaks", 0.50f, 0.65f),
    SCOSGLEN("scosglen", "Scosglen", 0.25f, 0.45f),
    DRY_STEPPES("dry_steppes", "Dry Steppes", 0.45f, 0.30f),
    KEHJISTAN("kehjistan", "Kehjistan", 0.65f, 0.25f),
    HAWEZAR("hawezar", "Hawezar", 0.70f, 0.55f),
    NAHANTU("nahantu", "Nahantu", 0.85f, 0.40f)
}

@Serializable
enum class MapNodeType(val displayName: String, val colorHex: Long) {
    ALTAR_OF_LILITH("Altar of Lilith", 0xFFE53935),
    TENET_OF_AKARAT("Tenet of Akarat", 0xFF00ACC1),
    WAYPOINT("Waypoint", 0xFF42A5F5),
    STRONGHOLD("Stronghold", 0xFFD81B60),
    DUNGEON("Dungeon / Aspect", 0xFFFFB300)
}

@Serializable
data class MapNode(
    val nodeId: String,
    val region: SanctuaryRegion,
    val name: String,
    val type: MapNodeType,
    val normalizedX: Float, // 0.0f .. 1.0f on region canvas
    val normalizedY: Float, // 0.0f .. 1.0f on region canvas
    val bonusDescription: String = "",
    val associatedAspect: String? = null
)

@Serializable
enum class QuestDropTrigger(val label: String) {
    CORPSE("Corpse Search"),
    ORE_VEIN("Mining Ore Vein"),
    HERB_CACHE("Harvesting Herbs"),
    CHEST("Treasure Chest"),
    ENEMY_DROP("Monster Drop"),
    TOWN_NPC("Town Questgiver")
}

@Serializable
data class RegionQuest(
    val questId: String,
    val region: SanctuaryRegion,
    val title: String,
    val triggerType: QuestDropTrigger,
    val locationDescription: String
)

@Serializable
data class RegionMapProgress(
    val regionId: String,
    val completedNodeIds: List<String> = emptyList(),
    val completedQuests: List<String> = emptyList(),
    val strongholdsUnlocked: List<String> = emptyList(),
    val updatedAtTimestamp: Long = 0L
)

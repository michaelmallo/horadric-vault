package com.horadricvault.core.model

import kotlinx.serialization.Serializable

@Serializable
data class TargetItemSpec(
    val slot: EquipmentSlot,
    val targetItemName: String,
    val rarity: ItemRarity = ItemRarity.LEGENDARY,
    val requiredAspect: LegendaryAspect? = null,
    val targetAffixes: List<String> = emptyList(),
    val targetTempers: List<String> = emptyList(),
    val bossSource: String? = null,
    val isMythicUnique: Boolean = false,
    val idealMasterworkCrits: List<String> = emptyList()
)

@Serializable
data class TargetBuild(
    val buildId: String = "",
    val buildName: String,
    val sourceUrl: String? = null,
    val sourcePlatform: String? = null, // "Maxroll", "D4Builds", "Mobalytics", "HoradricVault"
    val characterClass: CharacterClass,
    val skillBar: List<String> = emptyList(),
    val passives: Map<String, Int> = emptyMap(),
    val paragonBoards: List<String> = emptyList(),
    val targetEquipmentSlots: Map<String, TargetItemSpec> = emptyMap(),
    val tierRating: String = "S Tier", // S Tier, A Tier, Leveling, Pit 100+
    val updatedAtTimestamp: Long = 0L
)

@Serializable
data class AffixRerollRecommendation(
    val slot: EquipmentSlot,
    val currentDeadAffix: String,
    val recommendedRerollTarget: String,
    val occultistCostPriority: String // "High Priority", "Optimal", "Secondary"
)

@Serializable
data class BossFarmingTarget(
    val bossName: String,
    val targetItemName: String,
    val isMythic: Boolean,
    val requiredMaterials: String,
    val location: String
)

@Serializable
data class AspectFarmingTarget(
    val aspectName: String,
    val codexLocation: String,
    val slot: EquipmentSlot
)

@Serializable
data class SlotMasterworkGap(
    val slot: EquipmentSlot,
    val itemName: String,
    val currentRank: Int,
    val targetRank: Int = 12,
    val materialTierRequired: String // "Obducite (1-4)", "Ingolith (5-8)", "Neathiron (9-12)"
)

@Serializable
data class ProgressionDelta(
    val skillPointsDelta: Int = 0,
    val paragonPointsDelta: Int = 0,
    val affixRerollPriorities: List<AffixRerollRecommendation> = emptyList(),
    val targetBossFarmingList: List<BossFarmingTarget> = emptyList(),
    val missingAspectCodexList: List<AspectFarmingTarget> = emptyList(),
    val masterworkGaps: List<SlotMasterworkGap> = emptyList(),
    val completionPercentage: Int = 0
)

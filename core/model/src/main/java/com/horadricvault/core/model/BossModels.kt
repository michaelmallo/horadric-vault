package com.horadricvault.core.model

import kotlinx.serialization.Serializable

@Serializable
data class BossSummonRequirement(
    val materialName: String,
    val standardAmount: Int,
    val tormentedAmount: Int,
    val farmSourceDescription: String
)

@Serializable
data class BossTargetDrop(
    val itemName: String,
    val itemSlot: EquipmentSlot,
    val rarity: ItemRarity,
    val targetClasses: List<CharacterClass> = emptyList(),
    val isMythic: Boolean = false
)

@Serializable
data class EndgameBoss(
    val bossId: String,
    val name: String,
    val title: String,
    val dungeonLocation: String,
    val region: SanctuaryRegion,
    val materials: List<BossSummonRequirement>,
    val targetDrops: List<BossTargetDrop>,
    val minTormentRecommended: TormentTier = TormentTier.TORMENT_1
)

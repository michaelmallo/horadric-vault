package com.horadricvault.core.model

import kotlinx.serialization.Serializable

@Serializable
enum class ItemRarity {
    NORMAL,
    MAGIC,
    RARE,
    LEGENDARY,
    UNIQUE,
    MYTHIC_UNIQUE
}

@Serializable
enum class EquipmentSlot(val displayName: String) {
    HELM("Helm"),
    CHEST("Chest Armor"),
    GLOVES("Gloves"),
    PANTS("Pants"),
    BOOTS("Boots"),
    MAIN_HAND("Main Hand Weapon"),
    OFF_HAND("Off-Hand / Shield"),
    AMULET("Amulet"),
    RING_1("Ring 1"),
    RING_2("Ring 2")
}

@Serializable
data class ItemAffix(
    val id: String = "",
    val description: String,
    val value: Double = 0.0,
    val unit: String = "",
    val isGreaterAffix: Boolean = false,
    val isRerolled: Boolean = false,
    val isDeadAffix: Boolean = false
)

@Serializable
data class ItemTemper(
    val recipeName: String,
    val affixDescription: String,
    val rollValue: Double = 0.0,
    val isMaxRoll: Boolean = false
)

@Serializable
data class LegendaryAspect(
    val id: String = "",
    val name: String,
    val description: String,
    val powerRoll: Double = 0.0,
    val codexDungeonLocation: String? = null,
    val isCodexAvailable: Boolean = true
)

@Serializable
data class RunewordPair(
    val offeringRune: String,
    val invocationRune: String,
    val effectDescription: String
)

@Serializable
data class EquipmentItem(
    val id: String = "",
    val name: String,
    val slot: EquipmentSlot,
    val rarity: ItemRarity = ItemRarity.LEGENDARY,
    val itemPower: Int = 800,
    val baseArmor: Int = 0,
    val masterworkLevel: Int = 0, // 0..12
    val greaterAffixCount: Int = 0, // 0..4
    val affixes: List<ItemAffix> = emptyList(),
    val tempers: List<ItemTemper> = emptyList(),
    val aspect: LegendaryAspect? = null,
    val runeword: RunewordPair? = null,
    val socketCount: Int = 0,
    val gemsSocketed: List<String> = emptyList()
)

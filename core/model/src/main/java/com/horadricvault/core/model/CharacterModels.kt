package com.horadricvault.core.model

import kotlinx.serialization.Serializable

@Serializable
enum class CharacterClass(val displayName: String) {
    BARBARIAN("Barbarian"),
    DRUID("Druid"),
    NECROMANCER("Necromancer"),
    ROGUE("Rogue"),
    SORCERER("Sorcerer"),
    SPIRITBORN("Spiritborn")
}

@Serializable
enum class Realm(val displayName: String) {
    SEASONAL_SOFTCORE("Seasonal (Softcore)"),
    SEASONAL_HARDCORE("Seasonal (Hardcore)"),
    ETERNAL("Eternal Realm")
}

@Serializable
enum class TormentTier(val tierNumber: Int, val displayName: String) {
    NORMAL(0, "Normal / Penitent"),
    PENITENT(0, "Penitent"),
    TORMENT_1(1, "Torment I"),
    TORMENT_2(2, "Torment II"),
    TORMENT_3(3, "Torment III"),
    TORMENT_4(4, "Torment IV")
}

@Serializable
data class ArmoryLoadout(
    val helm: EquipmentItem? = null,
    val chest: EquipmentItem? = null,
    val gloves: EquipmentItem? = null,
    val pants: EquipmentItem? = null,
    val boots: EquipmentItem? = null,
    val mainHand: EquipmentItem? = null,
    val offHand: EquipmentItem? = null,
    val amulet: EquipmentItem? = null,
    val ring1: EquipmentItem? = null,
    val ring2: EquipmentItem? = null
) {
    fun getSlot(slot: EquipmentSlot): EquipmentItem? = when (slot) {
        EquipmentSlot.HELM -> helm
        EquipmentSlot.CHEST -> chest
        EquipmentSlot.GLOVES -> gloves
        EquipmentSlot.PANTS -> pants
        EquipmentSlot.BOOTS -> boots
        EquipmentSlot.MAIN_HAND -> mainHand
        EquipmentSlot.OFF_HAND -> offHand
        EquipmentSlot.AMULET -> amulet
        EquipmentSlot.RING_1 -> ring1
        EquipmentSlot.RING_2 -> ring2
    }

    fun withSlot(slot: EquipmentSlot, item: EquipmentItem?): ArmoryLoadout = when (slot) {
        EquipmentSlot.HELM -> copy(helm = item)
        EquipmentSlot.CHEST -> copy(chest = item)
        EquipmentSlot.GLOVES -> copy(gloves = item)
        EquipmentSlot.PANTS -> copy(pants = item)
        EquipmentSlot.BOOTS -> copy(boots = item)
        EquipmentSlot.MAIN_HAND -> copy(mainHand = item)
        EquipmentSlot.OFF_HAND -> copy(offHand = item)
        EquipmentSlot.AMULET -> copy(amulet = item)
        EquipmentSlot.RING_1 -> copy(ring1 = item)
        EquipmentSlot.RING_2 -> copy(ring2 = item)
    }

    fun getAllItems(): List<EquipmentItem> = listOfNotNull(
        helm, chest, gloves, pants, boots,
        mainHand, offHand, amulet, ring1, ring2
    )
}

@Serializable
data class D4Character(
    val characterId: String = "",
    val characterName: String,
    val characterClass: CharacterClass = CharacterClass.SORCERER,
    val level: Int = 60,
    val paragonLevel: Int = 100,
    val realm: Realm = Realm.SEASONAL_SOFTCORE,
    val activeTormentTier: TormentTier = TormentTier.TORMENT_4,
    val activeTargetBuildId: String? = null,
    val currentArmory: ArmoryLoadout = ArmoryLoadout(),
    val assignedSkillPoints: Int = 71,
    val updatedAtTimestamp: Long = 0L
)

@Serializable
data class UserSettings(
    val defaultRealm: Realm = Realm.SEASONAL_SOFTCORE,
    val currentTormentTier: TormentTier = TormentTier.TORMENT_4,
    val themeVariant: String = "DARK_HELLFIRE",
    val enableCapGlowWarnings: Boolean = true,
    val autoSyncBuilds: Boolean = true
)

@Serializable
data class UserProfile(
    val userId: String,
    val displayName: String,
    val email: String,
    val isAnonymous: Boolean = false,
    val lastSyncTimestamp: Long = 0L,
    val createdAtTimestamp: Long = 0L
)

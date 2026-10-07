package com.horadricvault.core.model

import kotlinx.serialization.Serializable

@Serializable
enum class ElementType(val displayName: String, val hexColor: Long) {
    FIRE("Fire", 0xFFE25822),
    COLD("Cold", 0xFF64B5F6),
    LIGHTNING("Lightning", 0xFF9575CD),
    POISON("Poison", 0xFF81C784),
    SHADOW("Shadow", 0xFFBA68C8)
}

@Serializable
enum class CapStatus {
    UNDER_CAP,
    OPTIMAL,
    OVERCAPPED
}

@Serializable
data class ArmorStatBreakdown(
    val grossArmor: Int,
    val tormentPenalty: Int,
    val effectiveArmor: Int,
    val capTargetGross: Int,
    val status: CapStatus,
    val remainingRequired: Int,
    val overcapAmount: Int
)

@Serializable
data class ResistanceStat(
    val element: ElementType,
    val grossResistance: Double,
    val tormentPenalty: Double,
    val effectiveResistance: Double,
    val standardCap: Double = GameConstants.STANDARD_RESISTANCE_CAP, // 70.0%
    val hardCap: Double = GameConstants.HARD_RESISTANCE_CAP, // 85.0%
    val status: CapStatus,
    val overcapAmount: Double
)

@Serializable
data class SpeedStatBreakdown(
    val movementSpeed: Double,
    val movementSpeedCap: Double = GameConstants.MOVEMENT_SPEED_CAP, // 200.0%
    val isMovementSpeedCapped: Boolean,
    
    val attackSpeedBucket1: Double, // Gear / Paragon (cap 100%)
    val attackSpeedBucket2: Double, // Skills / Buffs (cap 100%)
    val isBucket1Capped: Boolean,
    val isBucket2Capped: Boolean
)

@Serializable
data class CritStatBreakdown(
    val critChance: Double,
    val cap: Double = GameConstants.CRITICAL_STRIKE_CHANCE_CAP, // 100%
    val isOvercapped: Boolean,
    val overcapAmount: Double
)

@Serializable
data class CompleteStatReport(
    val armor: ArmorStatBreakdown,
    val resistances: List<ResistanceStat>,
    val speeds: SpeedStatBreakdown,
    val crit: CritStatBreakdown,
    val activeTormentTier: TormentTier
)

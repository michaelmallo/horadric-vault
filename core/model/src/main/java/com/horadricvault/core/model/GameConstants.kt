package com.horadricvault.core.model

object GameConstants {
    // Vessel of Hatred / Patch 2.0+ Character Progression Constants
    const val MAX_CHARACTER_LEVEL = 60
    const val MAX_PARAGON_LEVEL = 300
    const val BASE_SKILL_POINTS = 48
    const val RENOWN_SKILL_POINTS = 23
    const val TOTAL_SKILL_POINTS = BASE_SKILL_POINTS + RENOWN_SKILL_POINTS // 71

    // Stat Caps
    const val EFFECTIVE_ARMOR_CAP = 1000
    const val STANDARD_RESISTANCE_CAP = 70.0
    const val HARD_RESISTANCE_CAP = 85.0
    const val MOVEMENT_SPEED_CAP = 200.0
    const val ATTACK_SPEED_BUCKET_CAP = 100.0
    const val CRITICAL_STRIKE_CHANCE_CAP = 100.0
    const val MAX_MASTERWORK_RANK = 12

    // Torment penalties
    fun getArmorPenalty(tormentTier: TormentTier): Int = when (tormentTier) {
        TormentTier.NORMAL, TormentTier.PENITENT -> 0
        TormentTier.TORMENT_1 -> 250
        TormentTier.TORMENT_2 -> 500
        TormentTier.TORMENT_3 -> 750
        TormentTier.TORMENT_4 -> 1000
    }

    fun getResistancePenalty(tormentTier: TormentTier): Double = when (tormentTier) {
        TormentTier.NORMAL, TormentTier.PENITENT -> 0.0
        TormentTier.TORMENT_1 -> 25.0
        TormentTier.TORMENT_2 -> 50.0
        TormentTier.TORMENT_3 -> 75.0
        TormentTier.TORMENT_4 -> 100.0
    }

    fun getGrossArmorRequiredForCap(tormentTier: TormentTier): Int {
        return EFFECTIVE_ARMOR_CAP + getArmorPenalty(tormentTier)
    }
}

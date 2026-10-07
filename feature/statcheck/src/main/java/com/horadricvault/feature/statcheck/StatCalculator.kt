package com.horadricvault.feature.statcheck

import com.horadricvault.core.model.*

object StatCalculator {

    fun calculateArmor(grossArmor: Int, tormentTier: TormentTier): ArmorStatBreakdown {
        val penalty = GameConstants.getArmorPenalty(tormentTier)
        val targetGross = GameConstants.getGrossArmorRequiredForCap(tormentTier)
        val effectiveArmor = minOf(GameConstants.EFFECTIVE_ARMOR_CAP, maxOf(0, grossArmor - penalty))

        val status = when {
            grossArmor < targetGross -> CapStatus.UNDER_CAP
            grossArmor == targetGross -> CapStatus.OPTIMAL
            else -> CapStatus.OVERCAPPED
        }

        val remaining = maxOf(0, targetGross - grossArmor)
        val overcap = maxOf(0, grossArmor - targetGross)

        return ArmorStatBreakdown(
            grossArmor = grossArmor,
            tormentPenalty = penalty,
            effectiveArmor = effectiveArmor,
            capTargetGross = targetGross,
            status = status,
            remainingRequired = remaining,
            overcapAmount = overcap
        )
    }

    fun calculateResistance(
        element: ElementType,
        grossResistance: Double,
        tormentTier: TormentTier,
        customMaxResCap: Double = GameConstants.STANDARD_RESISTANCE_CAP
    ): ResistanceStat {
        val penalty = GameConstants.getResistancePenalty(tormentTier)
        val effective = maxOf(0.0, grossResistance - penalty)
        val effectiveCap = minOf(GameConstants.HARD_RESISTANCE_CAP, maxOf(GameConstants.STANDARD_RESISTANCE_CAP, customMaxResCap))

        val status = when {
            effective < effectiveCap -> CapStatus.UNDER_CAP
            effective == effectiveCap -> CapStatus.OPTIMAL
            else -> CapStatus.OVERCAPPED
        }

        val overcap = maxOf(0.0, effective - effectiveCap)

        return ResistanceStat(
            element = element,
            grossResistance = grossResistance,
            tormentPenalty = penalty,
            effectiveResistance = minOf(effectiveCap, effective),
            standardCap = GameConstants.STANDARD_RESISTANCE_CAP,
            hardCap = effectiveCap,
            status = status,
            overcapAmount = overcap
        )
    }

    fun calculateSpeeds(
        movementSpeed: Double,
        attackSpeedBucket1: Double,
        attackSpeedBucket2: Double
    ): SpeedStatBreakdown {
        return SpeedStatBreakdown(
            movementSpeed = movementSpeed,
            movementSpeedCap = GameConstants.MOVEMENT_SPEED_CAP,
            isMovementSpeedCapped = movementSpeed >= GameConstants.MOVEMENT_SPEED_CAP,
            attackSpeedBucket1 = attackSpeedBucket1,
            attackSpeedBucket2 = attackSpeedBucket2,
            isBucket1Capped = attackSpeedBucket1 >= GameConstants.ATTACK_SPEED_BUCKET_CAP,
            isBucket2Capped = attackSpeedBucket2 >= GameConstants.ATTACK_SPEED_BUCKET_CAP
        )
    }

    fun calculateCritChance(critChance: Double): CritStatBreakdown {
        val isOvercapped = critChance > GameConstants.CRITICAL_STRIKE_CHANCE_CAP
        val overcap = maxOf(0.0, critChance - GameConstants.CRITICAL_STRIKE_CHANCE_CAP)
        return CritStatBreakdown(
            critChance = critChance,
            cap = GameConstants.CRITICAL_STRIKE_CHANCE_CAP,
            isOvercapped = isOvercapped,
            overcapAmount = overcap
        )
    }

    fun calculateStatsFromArmory(
        armory: ArmoryLoadout,
        tormentTier: TormentTier
    ): CompleteStatReport {
        var grossArmor = 0
        var totalFireRes = 0.0
        var totalColdRes = 0.0
        var totalLightningRes = 0.0
        var totalPoisonRes = 0.0
        var totalShadowRes = 0.0
        var movementSpeed = 100.0 // Base 100%
        var attackSpeedB1 = 0.0
        var attackSpeedB2 = 0.0
        var critChance = 5.0 // Base 5%

        armory.getAllItems().forEach { item ->
            grossArmor += item.baseArmor

            item.affixes.forEach { affix ->
                val desc = affix.description.lowercase()
                when {
                    desc.contains("armor") -> grossArmor += affix.value.toInt()
                    desc.contains("fire resistance") -> totalFireRes += affix.value
                    desc.contains("cold resistance") -> totalColdRes += affix.value
                    desc.contains("lightning resistance") -> totalLightningRes += affix.value
                    desc.contains("poison resistance") -> totalPoisonRes += affix.value
                    desc.contains("shadow resistance") -> totalShadowRes += affix.value
                    desc.contains("all resistances") -> {
                        totalFireRes += affix.value
                        totalColdRes += affix.value
                        totalLightningRes += affix.value
                        totalPoisonRes += affix.value
                        totalShadowRes += affix.value
                    }
                    desc.contains("movement speed") -> movementSpeed += affix.value
                    desc.contains("attack speed") -> attackSpeedB1 += affix.value
                    desc.contains("critical strike chance") -> critChance += affix.value
                }
            }

            item.tempers.forEach { temper ->
                val desc = temper.affixDescription.lowercase()
                when {
                    desc.contains("movement speed") -> movementSpeed += temper.rollValue
                    desc.contains("critical strike chance") -> critChance += temper.rollValue
                    desc.contains("armor") -> grossArmor += temper.rollValue.toInt()
                }
            }
        }

        val armorStat = calculateArmor(grossArmor, tormentTier)
        val resistances = listOf(
            calculateResistance(ElementType.FIRE, totalFireRes, tormentTier),
            calculateResistance(ElementType.COLD, totalColdRes, tormentTier),
            calculateResistance(ElementType.LIGHTNING, totalLightningRes, tormentTier),
            calculateResistance(ElementType.POISON, totalPoisonRes, tormentTier),
            calculateResistance(ElementType.SHADOW, totalShadowRes, tormentTier)
        )
        val speeds = calculateSpeeds(movementSpeed, attackSpeedB1, attackSpeedB2)
        val crit = calculateCritChance(critChance)

        return CompleteStatReport(
            armor = armorStat,
            resistances = resistances,
            speeds = speeds,
            crit = crit,
            activeTormentTier = tormentTier
        )
    }
}

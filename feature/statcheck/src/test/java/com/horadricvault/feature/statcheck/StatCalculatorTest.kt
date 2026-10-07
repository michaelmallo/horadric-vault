package com.horadricvault.feature.statcheck

import com.horadricvault.core.model.*
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class StatCalculatorTest {

    @Test
    fun testArmorScalingNormalTier() {
        val breakdown = StatCalculator.calculateArmor(grossArmor = 1000, tormentTier = TormentTier.NORMAL)
        assertEquals(0, breakdown.tormentPenalty)
        assertEquals(1000, breakdown.effectiveArmor)
        assertEquals(1000, breakdown.capTargetGross)
        assertEquals(CapStatus.OPTIMAL, breakdown.status)
        assertEquals(0, breakdown.remainingRequired)
        assertEquals(0, breakdown.overcapAmount)
    }

    @Test
    fun testArmorScalingTorment1UnderCap() {
        // Torment 1: -250 penalty, 1250 gross required
        val breakdown = StatCalculator.calculateArmor(grossArmor = 1200, tormentTier = TormentTier.TORMENT_1)
        assertEquals(250, breakdown.tormentPenalty)
        assertEquals(950, breakdown.effectiveArmor)
        assertEquals(1250, breakdown.capTargetGross)
        assertEquals(CapStatus.UNDER_CAP, breakdown.status)
        assertEquals(50, breakdown.remainingRequired)
        assertEquals(0, breakdown.overcapAmount)
    }

    @Test
    fun testArmorScalingTorment2Overcap() {
        // Torment 2: -500 penalty, 1500 gross required
        val breakdown = StatCalculator.calculateArmor(grossArmor = 1650, tormentTier = TormentTier.TORMENT_2)
        assertEquals(500, breakdown.tormentPenalty)
        assertEquals(1000, breakdown.effectiveArmor) // Clamped at 1000 effective
        assertEquals(1500, breakdown.capTargetGross)
        assertEquals(CapStatus.OVERCAPPED, breakdown.status)
        assertEquals(0, breakdown.remainingRequired)
        assertEquals(150, breakdown.overcapAmount)
    }

    @Test
    fun testArmorScalingTorment4Target() {
        // Torment 4: -1000 penalty, 2000 gross required
        val optimal = StatCalculator.calculateArmor(grossArmor = 2000, tormentTier = TormentTier.TORMENT_4)
        assertEquals(1000, optimal.tormentPenalty)
        assertEquals(1000, optimal.effectiveArmor)
        assertEquals(2000, optimal.capTargetGross)
        assertEquals(CapStatus.OPTIMAL, optimal.status)
        assertEquals(0, optimal.remainingRequired)

        val overcapped = StatCalculator.calculateArmor(grossArmor = 2300, tormentTier = TormentTier.TORMENT_4)
        assertEquals(CapStatus.OVERCAPPED, overcapped.status)
        assertEquals(300, overcapped.overcapAmount)
    }

    @Test
    fun testResistanceCalculationTorment4() {
        // Torment 4 has -100% resistance penalty
        val underCap = StatCalculator.calculateResistance(
            element = ElementType.FIRE,
            grossResistance = 150.0,
            tormentTier = TormentTier.TORMENT_4
        )
        assertEquals(100.0, underCap.tormentPenalty, 0.01)
        assertEquals(50.0, underCap.effectiveResistance, 0.01)
        assertEquals(CapStatus.UNDER_CAP, underCap.status)

        val optimal = StatCalculator.calculateResistance(
            element = ElementType.COLD,
            grossResistance = 170.0,
            tormentTier = TormentTier.TORMENT_4
        )
        assertEquals(70.0, optimal.effectiveResistance, 0.01)
        assertEquals(CapStatus.OPTIMAL, optimal.status)

        val overcapped = StatCalculator.calculateResistance(
            element = ElementType.LIGHTNING,
            grossResistance = 195.0,
            tormentTier = TormentTier.TORMENT_4,
            customMaxResCap = 85.0
        )
        assertEquals(85.0, overcapped.effectiveResistance, 0.01)
        assertEquals(CapStatus.OVERCAPPED, overcapped.status)
        assertEquals(10.0, overcapped.overcapAmount, 0.01) // 95% - 85% = 10%
    }

    @Test
    fun testMovementAndAttackSpeedCaps() {
        val speeds = StatCalculator.calculateSpeeds(
            movementSpeed = 215.0,
            attackSpeedBucket1 = 105.0,
            attackSpeedBucket2 = 80.0
        )
        assertTrue(speeds.isMovementSpeedCapped)
        assertTrue(speeds.isBucket1Capped)
        assertFalse(speeds.isBucket2Capped)
    }

    @Test
    fun testCritChanceOvercap() {
        val safeCrit = StatCalculator.calculateCritChance(78.5)
        assertFalse(safeCrit.isOvercapped)
        assertEquals(0.0, safeCrit.overcapAmount, 0.01)

        val overCrit = StatCalculator.calculateCritChance(108.2)
        assertTrue(overCrit.isOvercapped)
        assertEquals(8.2, overCrit.overcapAmount, 0.01)
    }

    @Test
    fun testArmoryAggregation() {
        val helm = EquipmentItem(
            name = "Harlequin Crest",
            slot = EquipmentSlot.HELM,
            rarity = ItemRarity.MYTHIC_UNIQUE,
            baseArmor = 450,
            affixes = listOf(
                ItemAffix(description = "+1,200 Armor", value = 1200.0),
                ItemAffix(description = "+35% All Resistances", value = 35.0)
            )
        )
        val boots = EquipmentItem(
            name = "Runic Striders",
            slot = EquipmentSlot.BOOTS,
            baseArmor = 350,
            affixes = listOf(
                ItemAffix(description = "+25.5% Movement Speed", value = 25.5)
            )
        )

        val loadout = ArmoryLoadout(helm = helm, boots = boots)
        val report = StatCalculator.calculateStatsFromArmory(loadout, TormentTier.TORMENT_4)

        // Gross armor: 450 + 1200 + 350 = 2000. Under Torment 4 (-1000), target is 2000 gross. Exactly OPTIMAL!
        assertEquals(2000, report.armor.grossArmor)
        assertEquals(1000, report.armor.effectiveArmor)
        assertEquals(CapStatus.OPTIMAL, report.armor.status)
        assertEquals(125.5, report.speeds.movementSpeed, 0.01)
    }
}

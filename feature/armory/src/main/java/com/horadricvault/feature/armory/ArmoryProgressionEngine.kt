package com.horadricvault.feature.armory

import com.horadricvault.core.model.*

object ArmoryProgressionEngine {

    fun calculateProgressionDelta(
        character: D4Character,
        targetBuild: TargetBuild?,
        allBosses: List<EndgameBoss>
    ): ProgressionDelta {
        if (targetBuild == null) {
            return ProgressionDelta(completionPercentage = 100)
        }

        // 1. Skill & Paragon Point Delta
        val currentSkillPoints = character.assignedSkillPoints
        val targetSkillPoints = GameConstants.TOTAL_SKILL_POINTS
        val skillPointsDelta = maxOf(0, targetSkillPoints - currentSkillPoints)

        val currentParagon = character.paragonLevel
        val targetParagon = 300 // Vessel of Hatred max
        val paragonPointsDelta = maxOf(0, targetParagon - currentParagon)

        // 2. Gear & Slot Comparison
        val missingBossTargets = mutableListOf<BossFarmingTarget>()
        val missingAspects = mutableListOf<AspectFarmingTarget>()
        val affixRerolls = mutableListOf<AffixRerollRecommendation>()
        val masterworkGaps = mutableListOf<SlotMasterworkGap>()

        var totalTargetScore = 0
        var achievedScore = 0

        targetBuild.targetEquipmentSlots.forEach { (slotKey, targetSpec) ->
            val slot = targetSpec.slot
            val equippedItem = character.currentArmory.getSlot(slot)

            totalTargetScore += 10 // Base slot score

            if (equippedItem == null) {
                // Completely missing item in slot
                if (targetSpec.rarity == ItemRarity.MYTHIC_UNIQUE || targetSpec.rarity == ItemRarity.UNIQUE) {
                    val matchingBoss = allBosses.find { boss ->
                        boss.targetDrops.any { it.itemName.equals(targetSpec.targetItemName, ignoreCase = true) }
                    }
                    missingBossTargets.add(
                        BossFarmingTarget(
                            bossName = matchingBoss?.name ?: (targetSpec.bossSource ?: "Tormented Ladder"),
                            targetItemName = targetSpec.targetItemName,
                            isMythic = targetSpec.isMythicUnique,
                            requiredMaterials = matchingBoss?.materials?.joinToString { "${it.materialName} x${it.tormentedAmount}" } ?: "Summoning Mats",
                            location = matchingBoss?.dungeonLocation ?: "Sanctuary Dungeon"
                        )
                    )
                } else if (targetSpec.requiredAspect != null) {
                    missingAspects.add(
                        AspectFarmingTarget(
                            aspectName = targetSpec.requiredAspect?.name ?: "Aspect",
                            codexLocation = targetSpec.requiredAspect?.codexDungeonLocation ?: "Codex of Power",
                            slot = slot
                        )
                    )
                }
            } else {
                // Item is present: verify item name / aspect / affixes
                val nameMatch = equippedItem.name.contains(targetSpec.targetItemName, ignoreCase = true) ||
                        targetSpec.targetItemName.contains(equippedItem.name, ignoreCase = true)
                if (nameMatch) {
                    achievedScore += 5
                } else {
                    achievedScore += 2
                    if (targetSpec.rarity == ItemRarity.MYTHIC_UNIQUE || targetSpec.rarity == ItemRarity.UNIQUE) {
                        val matchingBoss = allBosses.find { boss ->
                            boss.targetDrops.any { it.itemName.equals(targetSpec.targetItemName, ignoreCase = true) }
                        }
                        missingBossTargets.add(
                            BossFarmingTarget(
                                bossName = matchingBoss?.name ?: (targetSpec.bossSource ?: "Tormented Ladder"),
                                targetItemName = targetSpec.targetItemName,
                                isMythic = targetSpec.isMythicUnique,
                                requiredMaterials = matchingBoss?.materials?.joinToString { "${it.materialName} x${it.tormentedAmount}" } ?: "Boss Mats",
                                location = matchingBoss?.dungeonLocation ?: "Sanctuary"
                            )
                        )
                    }
                }

                // Check Aspects
                if (targetSpec.requiredAspect != null && equippedItem.aspect?.name != targetSpec.requiredAspect?.name) {
                    missingAspects.add(
                        AspectFarmingTarget(
                            aspectName = targetSpec.requiredAspect?.name ?: "",
                            codexLocation = targetSpec.requiredAspect?.codexDungeonLocation ?: "Codex of Power",
                            slot = slot
                        )
                    )
                } else if (targetSpec.requiredAspect != null) {
                    achievedScore += 2
                }

                // Check Masterworking (0..12)
                if (equippedItem.masterworkLevel < GameConstants.MAX_MASTERWORK_RANK) {
                    val matTier = when {
                        equippedItem.masterworkLevel < 4 -> "Obducite (Ranks 1-4, Nightmare Dungeons / Undercity)"
                        equippedItem.masterworkLevel < 8 -> "Ingolith (Ranks 5-8, Infernal Hordes / Pit 30+)"
                        else -> "Neathiron (Ranks 9-12, Pit 60+ / Undercity Tributes)"
                    }
                    masterworkGaps.add(
                        SlotMasterworkGap(
                            slot = slot,
                            itemName = equippedItem.name,
                            currentRank = equippedItem.masterworkLevel,
                            targetRank = GameConstants.MAX_MASTERWORK_RANK,
                            materialTierRequired = matTier
                        )
                    )
                } else {
                    achievedScore += 3
                }

                // Check Dead Affixes & Rerolls
                val deadAffixes = equippedItem.affixes.filter { it.isDeadAffix }
                deadAffixes.forEach { dead ->
                    val idealTarget = targetSpec.targetAffixes.firstOrNull() ?: "+Critical Strike Damage / Main Stat"
                    affixRerolls.add(
                        AffixRerollRecommendation(
                            slot = slot,
                            currentDeadAffix = dead.description,
                            recommendedRerollTarget = idealTarget,
                            occultistCostPriority = if (equippedItem.greaterAffixCount > 0) "High Priority (Greater Affix Base)" else "Optimal"
                        )
                    )
                }
            }
        }

        val percentage = if (totalTargetScore > 0) {
            ((achievedScore.toFloat() / totalTargetScore.toFloat()) * 100).toInt().coerceIn(0, 100)
        } else 100

        return ProgressionDelta(
            skillPointsDelta = skillPointsDelta,
            paragonPointsDelta = paragonPointsDelta,
            affixRerollPriorities = affixRerolls,
            targetBossFarmingList = missingBossTargets,
            missingAspectCodexList = missingAspects,
            masterworkGaps = masterworkGaps,
            completionPercentage = percentage
        )
    }
}

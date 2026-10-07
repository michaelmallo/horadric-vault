package com.horadricvault.core.network

import com.horadricvault.core.model.*

object DefaultBuildCatalog {

    val preloadedBuilds: List<TargetBuild> = listOf(
        // 1. Spiritborn (Vessel of Hatred S-Tier)
        TargetBuild(
            buildId = "build_sb_quill_volley",
            buildName = "Quill Volley Spiritborn (Endgame S-Tier)",
            sourceUrl = "https://maxroll.gg/d4/build-guides/quill-volley-spiritborn-guide",
            sourcePlatform = "Maxroll",
            characterClass = CharacterClass.SPIRITBORN,
            tierRating = "S Tier (Pit 120+)",
            skillBar = listOf("Quill Volley", "Stinger", "Razor Wings", "Vortex", "The Hunter", "Armored Hide"),
            passives = mapOf(
                "Adaptive Stance" to 3,
                "Apex Predator" to 3,
                "Resilient" to 3,
                "Brilliance" to 3
            ),
            paragonBoards = listOf("Starting Board", "Spiritual Harmony", "Jaguar Prowess", "Eagle Talon", "Bitter Medicine"),
            targetEquipmentSlots = mapOf(
                EquipmentSlot.HELM.name to TargetItemSpec(
                    slot = EquipmentSlot.HELM,
                    targetItemName = "Harlequin Crest",
                    rarity = ItemRarity.MYTHIC_UNIQUE,
                    isMythicUnique = true,
                    bossSource = "Tormented Duriel / Andariel",
                    targetAffixes = listOf("+20% Damage Reduction", "+4 to All Skills", "+1,200 Maximum Life", "+25% Resource Generation")
                ),
                EquipmentSlot.CHEST.name to TargetItemSpec(
                    slot = EquipmentSlot.CHEST,
                    targetItemName = "Shroud of False Death",
                    rarity = ItemRarity.MYTHIC_UNIQUE,
                    isMythicUnique = true,
                    bossSource = "Tormented Andariel",
                    targetAffixes = listOf("+1 to All Passives", "+40% Movement Speed", "+1,500 Maximum Life")
                ),
                EquipmentSlot.GLOVES.name to TargetItemSpec(
                    slot = EquipmentSlot.GLOVES,
                    targetItemName = "Runic Wraps",
                    rarity = ItemRarity.LEGENDARY,
                    requiredAspect = LegendaryAspect(
                        id = "asp_moonrise",
                        name = "Aspect of Moonrise",
                        description = "Damaging with Basic skills grants Attack Speed and Basic damage."
                    ),
                    targetAffixes = listOf("+Ranks to Quill Volley", "+Attack Speed", "+Critical Strike Chance")
                ),
                EquipmentSlot.PANTS.name to TargetItemSpec(
                    slot = EquipmentSlot.PANTS,
                    targetItemName = "Vessel Leggings",
                    rarity = ItemRarity.LEGENDARY,
                    requiredAspect = LegendaryAspect(
                        id = "asp_unyielding_hits",
                        name = "Aspect of Unyielding Hits",
                        description = "Weapon Mastery or Focus grants 1,250 Armor."
                    ),
                    targetAffixes = listOf("+Maximum Life", "+Total Armor", "+Armor")
                ),
                EquipmentSlot.BOOTS.name to TargetItemSpec(
                    slot = EquipmentSlot.BOOTS,
                    targetItemName = "Yen's Blessing",
                    rarity = ItemRarity.UNIQUE,
                    bossSource = "Lord Zir",
                    targetAffixes = listOf("+Movement Speed", "+Resistance to All Elements", "+Damage Reduction")
                ),
                EquipmentSlot.MAIN_HAND.name to TargetItemSpec(
                    slot = EquipmentSlot.MAIN_HAND,
                    targetItemName = "Nesekem, The Herald",
                    rarity = ItemRarity.MYTHIC_UNIQUE,
                    isMythicUnique = true,
                    bossSource = "Tormented Andariel",
                    targetAffixes = listOf("+200% Outburst Damage", "+Critical Strike Damage", "+All Stats")
                ),
                EquipmentSlot.AMULET.name to TargetItemSpec(
                    slot = EquipmentSlot.AMULET,
                    targetItemName = "Amulet of Adaptability",
                    rarity = ItemRarity.LEGENDARY,
                    requiredAspect = LegendaryAspect(
                        id = "asp_adaptability",
                        name = "Aspect of Adaptability",
                        description = "Below 50% Resource, skills gain +80% damage."
                    ),
                    targetAffixes = listOf("+Cooldown Reduction", "+Movement Speed", "+Ranks to Focus Passives")
                ),
                EquipmentSlot.RING_1.name to TargetItemSpec(
                    slot = EquipmentSlot.RING_1,
                    targetItemName = "Ring of Starless Skies",
                    rarity = ItemRarity.MYTHIC_UNIQUE,
                    isMythicUnique = true,
                    bossSource = "Tormented Duriel / Andariel",
                    targetAffixes = listOf("+Critical Strike Chance", "+Attack Speed", "+Resource Generation")
                ),
                EquipmentSlot.RING_2.name to TargetItemSpec(
                    slot = EquipmentSlot.RING_2,
                    targetItemName = "Band of Elements",
                    rarity = ItemRarity.LEGENDARY,
                    requiredAspect = LegendaryAspect(
                        id = "asp_elements",
                        name = "Aspect of the Elements",
                        description = "Cycles through elemental damage bonuses."
                    ),
                    targetAffixes = listOf("+Critical Strike Chance", "+Critical Strike Damage", "+Maximum Resource")
                )
            )
        ),

        // 2. Sorcerer (Season 6 S-Tier)
        TargetBuild(
            buildId = "build_sorc_lightning_spear",
            buildName = "Lightning Spear Sorcerer (Pit Pusher)",
            sourceUrl = "https://mobalytics.gg/diablo-4/builds/sorcerer/lightning-spear",
            sourcePlatform = "Mobalytics",
            characterClass = CharacterClass.SORCERER,
            tierRating = "S Tier (Pit 115+)",
            skillBar = listOf("Lightning Spear", "Unstable Currents", "Teleport", "Flame Shield", "Ice Armor", "Ice Blades"),
            passives = mapOf(
                "Glass Cannon" to 3,
                "Elemental Dominance" to 3,
                "Devouring Blaze" to 3,
                "Conjuration Mastery" to 3
            ),
            paragonBoards = listOf("Starting Board", "Frigid Fate", "Enchantment Master", "Static Surge", "Burning Instinct"),
            targetEquipmentSlots = mapOf(
                EquipmentSlot.HELM.name to TargetItemSpec(
                    slot = EquipmentSlot.HELM,
                    targetItemName = "Harlequin Crest",
                    rarity = ItemRarity.MYTHIC_UNIQUE,
                    isMythicUnique = true,
                    bossSource = "Tormented Duriel / Andariel",
                    targetAffixes = listOf("+20% Damage Reduction", "+4 to All Skills", "+Cooldown Reduction")
                ),
                EquipmentSlot.CHEST.name to TargetItemSpec(
                    slot = EquipmentSlot.CHEST,
                    targetItemName = "Raiment of the Infinite",
                    rarity = ItemRarity.UNIQUE,
                    bossSource = "Lord Zir",
                    targetAffixes = listOf("+Ranks to Teleport", "+Damage to Close Enemies", "+Stun Duration")
                ),
                EquipmentSlot.RING_1.name to TargetItemSpec(
                    slot = EquipmentSlot.RING_1,
                    targetItemName = "Tal Rasha's Iridescent Loop",
                    rarity = ItemRarity.UNIQUE,
                    bossSource = "Echo of Varshan",
                    targetAffixes = listOf("+Cooldown Reduction", "+Resource Generation", "+Non-Physical Damage")
                )
            )
        ),

        // 3. Barbarian (Season 6 S-Tier)
        TargetBuild(
            buildId = "build_barb_whirlwind_dust",
            buildName = "Whirlwind Dust Devil Barbarian",
            sourceUrl = "https://d4builds.gg/builds/barbarian-whirlwind-dust-devil",
            sourcePlatform = "D4Builds",
            characterClass = CharacterClass.BARBARIAN,
            tierRating = "S Tier",
            skillBar = listOf("Whirlwind", "Rallying Cry", "War Cry", "Challenging Shout", "Wrath of the Berserker", "Iron Skin"),
            passives = mapOf(
                "Heavy Handed" to 3,
                "Pit Fighter" to 3,
                "No Mercy" to 3
            ),
            paragonBoards = listOf("Starting Board", "Warbringer", "Carnage", "Decimator", "Flawless Technique"),
            targetEquipmentSlots = mapOf(
                EquipmentSlot.MAIN_HAND.name to TargetItemSpec(
                    slot = EquipmentSlot.MAIN_HAND,
                    targetItemName = "The Grandfather",
                    rarity = ItemRarity.MYTHIC_UNIQUE,
                    isMythicUnique = true,
                    bossSource = "Tormented Duriel",
                    targetAffixes = listOf("+100% Critical Strike Damage", "+All Stats", "+Maximum Life")
                ),
                EquipmentSlot.BOOTS.name to TargetItemSpec(
                    slot = EquipmentSlot.BOOTS,
                    targetItemName = "Yen's Blessing",
                    rarity = ItemRarity.UNIQUE,
                    bossSource = "Lord Zir"
                )
            )
        )
    )
}

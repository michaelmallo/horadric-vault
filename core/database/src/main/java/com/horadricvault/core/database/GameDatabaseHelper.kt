package com.horadricvault.core.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.horadricvault.core.model.*

class GameDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "horadric_sanctuary_definitions.db"
        const val DATABASE_VERSION = 1

        const val TABLE_SKILLS = "skills"
        const val TABLE_ASPECTS = "aspects"
        const val TABLE_UNIQUES = "uniques"
        const val TABLE_MAP_NODES = "map_nodes"
        const val TABLE_BOSSES = "bosses"
        const val TABLE_BOSS_DROPS = "boss_drops"
        const val TABLE_BOSS_MATERIALS = "boss_materials"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE $TABLE_SKILLS (
                id TEXT PRIMARY KEY,
                character_class TEXT,
                name TEXT,
                category TEXT,
                max_ranks INTEGER,
                description TEXT
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE $TABLE_ASPECTS (
                id TEXT PRIMARY KEY,
                name TEXT,
                description TEXT,
                codex_dungeon TEXT,
                target_slot TEXT
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE $TABLE_UNIQUES (
                id TEXT PRIMARY KEY,
                name TEXT,
                rarity TEXT,
                slot TEXT,
                target_classes TEXT,
                boss_source TEXT,
                unique_power TEXT
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE $TABLE_MAP_NODES (
                id TEXT PRIMARY KEY,
                region_id TEXT,
                name TEXT,
                node_type TEXT,
                normalized_x REAL,
                normalized_y REAL,
                bonus_desc TEXT,
                associated_aspect TEXT
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE $TABLE_BOSSES (
                id TEXT PRIMARY KEY,
                name TEXT,
                title TEXT,
                dungeon_location TEXT,
                region_id TEXT,
                min_torment TEXT
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE $TABLE_BOSS_DROPS (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                boss_id TEXT,
                item_name TEXT,
                item_slot TEXT,
                rarity TEXT,
                target_classes TEXT,
                is_mythic INTEGER
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE $TABLE_BOSS_MATERIALS (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                boss_id TEXT,
                material_name TEXT,
                standard_amount INTEGER,
                tormented_amount INTEGER,
                farm_source TEXT
            )
            """.trimIndent()
        )

        seedDatabase(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_SKILLS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_ASPECTS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_UNIQUES")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_MAP_NODES")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_BOSSES")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_BOSS_DROPS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_BOSS_MATERIALS")
        onCreate(db)
    }

    private fun seedDatabase(db: SQLiteDatabase) {
        seedSkills(db)
        seedAspects(db)
        seedUniques(db)
        seedBosses(db)
        seedMapNodes(db)
    }

    private fun seedSkills(db: SQLiteDatabase) {
        val skills = listOf(
            // Spiritborn (Vessel of Hatred)
            Triple("sb_quill", CharacterClass.SPIRITBORN.name to "Quill Volley", "Core|5|Hurl a barrage of feathers that ricochet between targets."),
            Triple("sb_stinger", CharacterClass.SPIRITBORN.name to "Stinger", "Core|5|Strike forward with scorpion tail venom, exploding on poisoned targets."),
            Triple("sb_razor_wings", CharacterClass.SPIRITBORN.name to "Razor Wings", "Focus|5|Launch razor sharp spiraling eagle wings that return."),
            Triple("sb_vortex", CharacterClass.SPIRITBORN.name to "Vortex", "Focus|5|Conjure a swirling gust pulling enemies inwards."),
            Triple("sb_the_hunter", CharacterClass.SPIRITBORN.name to "The Hunter", "Ultimate|1|Leap across the battlefield slicing all nearby foes with Jaguar fury."),

            // Sorcerer
            Triple("sorc_lightning_spear", CharacterClass.SORCERER.name to "Lightning Spear", "Conjuration|5|Conjure a spear of crackling lightning that seeks enemies."),
            Triple("sorc_unstable_currents", CharacterClass.SORCERER.name to "Unstable Currents", "Ultimate|1|Lightning surges through you casting random Shock skills."),
            Triple("sorc_teleport", CharacterClass.SORCERER.name to "Teleport", "Defensive|5|Transform into lightning, surging to target location dealing damage."),
            Triple("sorc_flame_shield", CharacterClass.SORCERER.name to "Flame Shield", "Defensive|5|Engulf yourself in flames becoming Immune for 2 seconds."),
            Triple("sorc_ice_armor", CharacterClass.SORCERER.name to "Ice Armor", "Defensive|5|A barrier of ice forms around you absorbing damage."),

            // Barbarian
            Triple("barb_whirlwind", CharacterClass.BARBARIAN.name to "Whirlwind", "Core|5|Rapidly attack surrounding enemies, spinning relentlessly."),
            Triple("barb_rallying_cry", CharacterClass.BARBARIAN.name to "Rallying Cry", "Defensive|5|Bellow a rallying cry, boosting Movement Speed and Resource Generation."),
            Triple("barb_war_cry", CharacterClass.BARBARIAN.name to "War Cry", "Brawling|5|Bellow a mighty war cry, increasing damage dealt and granting Berserking."),
            Triple("barb_wrath_berserker", CharacterClass.BARBARIAN.name to "Wrath of the Berserker", "Ultimate|1|Enter Berserking and Unstoppable for 10 seconds."),

            // Necromancer
            Triple("necro_bone_spear", CharacterClass.NECROMANCER.name to "Bone Spear", "Core|5|Conjure a bone spear from the ground piercing all enemies."),
            Triple("necro_corpse_tendrils", CharacterClass.NECROMANCER.name to "Corpse Tendrils", "Corpse|5|Veins burst from a corpse, pulling in and Stunning enemies."),
            Triple("necro_blood_mist", CharacterClass.NECROMANCER.name to "Blood Mist", "Macabre|5|Disperse into a bloody mist, becoming Immune for 3 seconds."),

            // Rogue
            Triple("rogue_heartseeker", CharacterClass.ROGUE.name to "Heartseeker", "Basic|5|Fire an arrow that seeks an enemy, increasing Critical Strike Chance."),
            Triple("rogue_dash", CharacterClass.ROGUE.name to "Dash", "Agility|5|Dash forward, slashing enemies along the way."),
            Triple("rogue_shadow_step", CharacterClass.ROGUE.name to "Shadow Step", "Agility|5|Become Unstoppable and teleport behind target enemy."),

            // Druid
            Triple("druid_landslide", CharacterClass.DRUID.name to "Landslide", "Core|5|Crush enemies between 2 pillars of earth dealing devastating Physical damage."),
            Triple("druid_earthen_bulwark", CharacterClass.DRUID.name to "Earthen Bulwark", "Defensive|5|Rocks surround you for 3 seconds, granting a barrier.")
        )

        skills.forEach { (id, pair, meta) ->
            val (cls, name) = pair
            val parts = meta.split("|")
            val cv = ContentValues().apply {
                put("id", id)
                put("character_class", cls)
                put("name", name)
                put("category", parts[0])
                put("max_ranks", parts[1].toInt())
                put("description", parts[2])
            }
            db.insert(TABLE_SKILLS, null, cv)
        }
    }

    private fun seedAspects(db: SQLiteDatabase) {
        val aspects = listOf(
            listOf("asp_unyielding_hits", "Aspect of Unyielding Hits", "Casting a Weapon Mastery or Focus skill grants 1,250 Armor for 6 seconds.", "Iron Hold (Hawezar)", EquipmentSlot.CHEST.name),
            listOf("asp_moonrise", "Aspect of Moonrise", "Damaging an enemy with a Basic skill grants +4% Attack Speed and +160% Basic damage.", "Season Journey / Codex", EquipmentSlot.GLOVES.name),
            listOf("asp_adaptability", "Aspect of Adaptability", "When cast below 50% Maximum Resource, Basic skills gain +80% damage.", "Codex of Power", EquipmentSlot.AMULET.name),
            listOf("asp_splintering", "Splintering Aspect", "Bone Spear's primary attack makes enemies hit beyond the first Vulnerable.", "Guulrahn Slums (Dry Steppes)", EquipmentSlot.MAIN_HAND.name),
            listOf("asp_disobedience", "Aspect of Disobedience", "You gain +0.4% Armor for 4 seconds when you deal damage, stacking up to 40%.", "Halls of the Damned (Kehjistan)", EquipmentSlot.HELM.name),
            listOf("asp_juggernaut", "Juggernaut's Aspect", "You gain 3,500 Armor, but your Evade Cooldown is increased by 100%.", "Codex of Power", EquipmentSlot.BOOTS.name),
            listOf("asp_elements", "Aspect of the Elements", "Gain +30% increased damage to a set of damage types for 7 seconds, cycling through.", "Codex of Power", EquipmentSlot.RING_1.name),
            listOf("asp_concussive_strikes", "Aspect of Concussive Strikes", "Damaging an enemy has up to a 20% chance to Daze them. You deal +30% damage to Dazed.", "Codex of Power", EquipmentSlot.GLOVES.name)
        )

        aspects.forEach { a ->
            val cv = ContentValues().apply {
                put("id", a[0])
                put("name", a[1])
                put("description", a[2])
                put("codex_dungeon", a[3])
                put("target_slot", a[4])
            }
            db.insert(TABLE_ASPECTS, null, cv)
        }
    }

    private fun seedUniques(db: SQLiteDatabase) {
        val uniques = listOf(
            // Mythic Uniques
            listOf("mythic_harlequin", "Harlequin Crest", ItemRarity.MYTHIC_UNIQUE.name, EquipmentSlot.HELM.name, "ALL", "Duriel / Andariel", "Gain 20% Damage Reduction. In addition, gain +4 Ranks to all Skills."),
            listOf("mythic_tyrael", "Tyrael's Might", ItemRarity.MYTHIC_UNIQUE.name, EquipmentSlot.CHEST.name, "ALL", "Duriel / Andariel", "While at full Life, your skills unleash a divine barrage. +5% Maximum All Resistances."),
            listOf("mythic_shroud_death", "Shroud of False Death", ItemRarity.MYTHIC_UNIQUE.name, EquipmentSlot.CHEST.name, "ALL", "Duriel / Andariel", "Gain +1 to all Passives. If you haven't attacked for 2 seconds, gain Stealth and +40% Movement Speed."),
            listOf("mythic_grandfather", "The Grandfather", ItemRarity.MYTHIC_UNIQUE.name, EquipmentSlot.MAIN_HAND.name, "BARBARIAN,NECROMANCER", "Duriel / Andariel", "Increases your Critical Strike Damage by 100%. Other properties on this weapon can roll higher than normal."),
            listOf("mythic_starless_skies", "Ring of Starless Skies", ItemRarity.MYTHIC_UNIQUE.name, EquipmentSlot.RING_1.name, "ALL", "Duriel / Andariel", "Spending Core or Mastery skills reduces Resource Costs and increases damage by up to 50%."),
            listOf("mythic_doombringer", "Doombringer", ItemRarity.MYTHIC_UNIQUE.name, EquipmentSlot.MAIN_HAND.name, "BARBARIAN,ROGUE,NECROMANCER", "Duriel / Andariel", "Lucky Hit: Up to 25% chance to deal Shadow damage and reduce enemy damage dealt by 20%."),
            listOf("mythic_nesekem", "Nesekem, The Herald", ItemRarity.MYTHIC_UNIQUE.name, EquipmentSlot.MAIN_HAND.name, "SPIRITBORN", "Duriel / Andariel", "Every 5 seconds, an Outburst marks an enemy. Hitting them triggers an eruption dealing massive damage."),

            // Boss Target Uniques
            listOf("unique_tal_rasha", "Tal Rasha's Iridescent Loop", ItemRarity.UNIQUE.name, EquipmentSlot.RING_1.name, "SORCERER", "Echo of Varshan", "For each type of Elemental damage you deal, gain +20% damage for 4 seconds."),
            listOf("unique_raiment", "Raiment of the Infinite", ItemRarity.UNIQUE.name, EquipmentSlot.CHEST.name, "SORCERER", "Lord Zir", "After using Teleport, close enemies are pulled to you and Stunned for 2.5 seconds."),
            listOf("unique_fists_of_fate", "Fists of Fate", ItemRarity.UNIQUE.name, EquipmentSlot.GLOVES.name, "ALL", "The Beast in the Ice", "Your attacks randomly deal 1% to 300% of their normal damage."),
            listOf("unique_beastfall_boots", "Beastfall Boots", ItemRarity.UNIQUE.name, EquipmentSlot.BOOTS.name, "ROGUE", "The Beast in the Ice", "When you cast an Ultimate skill, your next Core skill consumes all energy and deals up to +300% damage."),
            listOf("unique_ramaladni", "Ramaladni's Magnum Opus", ItemRarity.UNIQUE.name, EquipmentSlot.MAIN_HAND.name, "BARBARIAN", "Grigoire", "Skills using this weapon deal +0.4% increased damage per point of Fury you have."),
            listOf("unique_yen_blessing", "Yen's Blessing", ItemRarity.UNIQUE.name, EquipmentSlot.BOOTS.name, "ALL", "Lord Zir", "Casting a Skill has a 40% chance to cast a non-Mobility, non-Ultimate Skill on Cooldown.")
        )

        uniques.forEach { u ->
            val cv = ContentValues().apply {
                put("id", u[0])
                put("name", u[1])
                put("rarity", u[2])
                put("slot", u[3])
                put("target_classes", u[4])
                put("boss_source", u[5])
                put("unique_power", u[6])
            }
            db.insert(TABLE_UNIQUES, null, cv)
        }
    }

    private fun seedBosses(db: SQLiteDatabase) {
        val bosses = listOf(
            Triple("boss_duriel", "Tormented Duriel", "Maggot King|Gaping Crevasse|kehjistan|TORMENT_1"),
            Triple("boss_andariel", "Tormented Andariel", "Maiden of Anguish|Hanged Man's Hall|kehjistan|TORMENT_1"),
            Triple("boss_zir", "Lord Zir", "The Dark Master|The Darkened Way|fractured_peaks|TORMENT_1"),
            Triple("boss_beast_ice", "The Beast in the Ice", "Glacial Terror|Glacial Fissure|fractured_peaks|TORMENT_1"),
            Triple("boss_grigoire", "Grigoire", "The Galvanic Saint|Hall of the Penitent|dry_steppes|TORMENT_1"),
            Triple("boss_varshan", "Echo of Varshan", "Corrupted Heart|Malignant Burrow|hawezar|TORMENT_1")
        )

        bosses.forEach { (id, name, meta) ->
            val parts = meta.split("|")
            val cv = ContentValues().apply {
                put("id", id)
                put("name", name)
                put("title", parts[0])
                put("dungeon_location", parts[1])
                put("region_id", parts[2])
                put("min_torment", parts[3])
            }
            db.insert(TABLE_BOSSES, null, cv)
        }

        // Materials
        val materials = listOf(
            Triple("boss_duriel", "Mucus-Slick Eggs", "2|5|Dropped by Echo of Varshan"),
            Triple("boss_duriel", "Shards of Agony", "2|5|Dropped by Grigoire"),
            Triple("boss_andariel", "Sandscorched Shackles", "2|5|Dropped by Lord Zir"),
            Triple("boss_andariel", "Pincushioned Dolls", "2|5|Dropped by The Beast in the Ice"),
            Triple("boss_zir", "Exquisite Blood", "9|27|Legion Events, World Bosses & Blood Maiden"),
            Triple("boss_beast_ice", "Distilled Fear", "9|27|Tier 30+ Nightmare Dungeons"),
            Triple("boss_grigoire", "Living Steel", "5|15|Tortured Gifts of Living Steel in Helltides"),
            Triple("boss_varshan", "Malignant Hearts", "4|12|Whisper Caches & Grotesque Debtors")
        )

        materials.forEach { (bossId, matName, meta) ->
            val parts = meta.split("|")
            val cv = ContentValues().apply {
                put("boss_id", bossId)
                put("material_name", matName)
                put("standard_amount", parts[0].toInt())
                put("tormented_amount", parts[1].toInt())
                put("farm_source", parts[2])
            }
            db.insert(TABLE_BOSS_MATERIALS, null, cv)
        }

        // Drops
        val drops = listOf(
            listOf("boss_duriel", "Harlequin Crest", "HELM", ItemRarity.MYTHIC_UNIQUE.name, "ALL", "1"),
            listOf("boss_duriel", "Tyrael's Might", "CHEST", ItemRarity.MYTHIC_UNIQUE.name, "ALL", "1"),
            listOf("boss_duriel", "The Grandfather", "MAIN_HAND", ItemRarity.MYTHIC_UNIQUE.name, "BARBARIAN,NECROMANCER", "1"),
            listOf("boss_duriel", "Ring of Starless Skies", "RING_1", ItemRarity.MYTHIC_UNIQUE.name, "ALL", "1"),
            listOf("boss_andariel", "Shroud of False Death", "CHEST", ItemRarity.MYTHIC_UNIQUE.name, "ALL", "1"),
            listOf("boss_andariel", "Nesekem, The Herald", "MAIN_HAND", ItemRarity.MYTHIC_UNIQUE.name, "SPIRITBORN", "1"),
            listOf("boss_zir", "Raiment of the Infinite", "CHEST", ItemRarity.UNIQUE.name, "SORCERER", "0"),
            listOf("boss_zir", "Yen's Blessing", "BOOTS", ItemRarity.UNIQUE.name, "ALL", "0"),
            listOf("boss_beast_ice", "Fists of Fate", "GLOVES", ItemRarity.UNIQUE.name, "ALL", "0"),
            listOf("boss_beast_ice", "Beastfall Boots", "BOOTS", ItemRarity.UNIQUE.name, "ROGUE", "0"),
            listOf("boss_grigoire", "Ramaladni's Magnum Opus", "MAIN_HAND", ItemRarity.UNIQUE.name, "BARBARIAN", "0"),
            listOf("boss_varshan", "Tal Rasha's Iridescent Loop", "RING_1", ItemRarity.UNIQUE.name, "SORCERER", "0")
        )

        drops.forEach { d ->
            val cv = ContentValues().apply {
                put("boss_id", d[0])
                put("item_name", d[1])
                put("item_slot", d[2])
                put("rarity", d[3])
                put("target_classes", d[4])
                put("is_mythic", d[5].toInt())
            }
            db.insert(TABLE_BOSS_DROPS, null, cv)
        }
    }

    private fun seedMapNodes(db: SQLiteDatabase) {
        val nodes = listOf(
            // Fractured Peaks
            listOf("node_fp_altar_1", "fractured_peaks", "Altar of Lilith - Desolate Highlands", MapNodeType.ALTAR_OF_LILITH.name, "0.45", "0.60", "+2 Willpower", null),
            listOf("node_fp_altar_2", "fractured_peaks", "Altar of Lilith - Dobrev Taiga", MapNodeType.ALTAR_OF_LILITH.name, "0.55", "0.68", "+2 Dexterity", null),
            listOf("node_fp_wp_kyovashad", "fractured_peaks", "Kyovashad Waypoint", MapNodeType.WAYPOINT.name, "0.50", "0.65", "Primary Sanctuary Capital", null),
            listOf("node_fp_sh_nostrava", "fractured_peaks", "Nostrava Stronghold", MapNodeType.STRONGHOLD.name, "0.38", "0.62", "Cultist infested settlement", null),
            listOf("node_fp_dung_lost_archive", "fractured_peaks", "Lost Archives", MapNodeType.DUNGEON.name, "0.48", "0.72", "Codex Dungeon", "Aspect of the Protector"),

            // Scosglen
            listOf("node_sc_altar_1", "scosglen", "Altar of Lilith - Northshore", MapNodeType.ALTAR_OF_LILITH.name, "0.22", "0.42", "+2 Intelligence", null),
            listOf("node_sc_wp_cerrigar", "scosglen", "Cerrigar Waypoint", MapNodeType.WAYPOINT.name, "0.25", "0.45", "Druidic Trade Hub", null),
            listOf("node_sc_sh_tur_dulra", "scosglen", "Túr Dúlra Stronghold", MapNodeType.STRONGHOLD.name, "0.20", "0.40", "Ancient Great Oak", null),

            // Dry Steppes
            listOf("node_ds_altar_1", "dry_steppes", "Altar of Lilith - Dindai Flats", MapNodeType.ALTAR_OF_LILITH.name, "0.42", "0.28", "+2 Strength", null),
            listOf("node_ds_wp_ked_bardu", "dry_steppes", "Ked Bardu Waypoint", MapNodeType.WAYPOINT.name, "0.45", "0.30", "Steppes Trade Hub", null),
            listOf("node_ds_sh_onyx_watchtower", "dry_steppes", "The Onyx Watchtower", MapNodeType.STRONGHOLD.name, "0.48", "0.32", "Bandit Fortress", null),

            // Kehjistan
            listOf("node_kj_altar_1", "kehjistan", "Altar of Lilith - Caldeum", MapNodeType.ALTAR_OF_LILITH.name, "0.62", "0.24", "+5 Max Obols", null),
            listOf("node_kj_wp_geakul", "kehjistan", "Gea Kul Waypoint", MapNodeType.WAYPOINT.name, "0.65", "0.25", "Desert Coastal Haven", null),
            listOf("node_kj_sh_alcarnus", "kehjistan", "Alcarnus Stronghold", MapNodeType.STRONGHOLD.name, "0.60", "0.28", "Plagued city", null),

            // Hawezar
            listOf("node_hw_altar_1", "hawezar", "Altar of Lilith - Dismal Foothills", MapNodeType.ALTAR_OF_LILITH.name, "0.68", "0.52", "+2 Dexterity", null),
            listOf("node_hw_wp_zarbinzet", "hawezar", "Zarbinzet Waypoint", MapNodeType.WAYPOINT.name, "0.70", "0.55", "Crusader Fortress Capital", null),
            listOf("node_hw_sh_vyeresz", "hawezar", "Vyeresz Stronghold", MapNodeType.STRONGHOLD.name, "0.74", "0.58", "Snake Cultist Lair", null),

            // Nahantu (Vessel of Hatred)
            listOf("node_nh_tenet_1", "nahantu", "Tenet of Akarat - Kurast Docks", MapNodeType.TENET_OF_AKARAT.name, "0.82", "0.38", "+2 All Stats", null),
            listOf("node_nh_tenet_2", "nahantu", "Tenet of Akarat - Five Hills", MapNodeType.TENET_OF_AKARAT.name, "0.86", "0.42", "+2 All Stats", null),
            listOf("node_nh_wp_kurast", "nahantu", "Kurast Bazaar Waypoint", MapNodeType.WAYPOINT.name, "0.85", "0.40", "Jungle Capital", null),
            listOf("node_nh_sh_chakhir", "nahantu", "Chakhir Stronghold", MapNodeType.STRONGHOLD.name, "0.88", "0.45", "Hollow Dwellers Stronghold", null)
        )

        nodes.forEach { n ->
            val cv = ContentValues().apply {
                put("id", n[0])
                put("region_id", n[1])
                put("name", n[2])
                put("node_type", n[3])
                put("normalized_x", n[4]?.toFloat() ?: 0f)
                put("normalized_y", n[5]?.toFloat() ?: 0f)
                put("bonus_desc", n[6])
                put("associated_aspect", n[7])
            }
            db.insert(TABLE_MAP_NODES, null, cv)
        }
    }
}

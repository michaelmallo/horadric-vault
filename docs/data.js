// Horadric Vault - Game Data Catalog & Constants

export const GameConstants = {
    EFFECTIVE_ARMOR_CAP: 1000,
    PHYSICAL_DR_CAP: 85.0,
    STANDARD_RESISTANCE_CAP: 70.0,
    HARD_RESISTANCE_CAP: 85.0,
    MOVEMENT_SPEED_CAP: 200.0,
    CRITICAL_STRIKE_CHANCE_CAP: 100.0,
    ATTACK_SPEED_BUCKET_CAP: 100.0
};

export const TormentTiers = [
    { id: "NORMAL", name: "Normal / Penitent", penaltyArmor: 0, penaltyRes: 0, icon: "⚔️" },
    { id: "TORMENT_1", name: "Torment I", penaltyArmor: 250, penaltyRes: 25.0, icon: "🔥" },
    { id: "TORMENT_2", name: "Torment II", penaltyArmor: 500, penaltyRes: 50.0, icon: "🔥🔥" },
    { id: "TORMENT_3", name: "Torment III", penaltyArmor: 750, penaltyRes: 75.0, icon: "🔥🔥🔥" },
    { id: "TORMENT_4", name: "Torment IV", penaltyArmor: 1000, penaltyRes: 100.0, icon: "💀💀💀" }
];

export const CharacterClasses = [
    { id: "SPIRITBORN", name: "Spiritborn", role: "Vessel of Hatred Melee / Mystic", icon: "🦅" },
    { id: "SORCERER", name: "Sorcerer", role: "Elemental Master", icon: "⚡" },
    { id: "BARBARIAN", name: "Barbarian", role: "Arsenal Brawler", icon: "🪓" },
    { id: "ROGUE", name: "Rogue", role: "Shadow Assassin & Marksman", icon: "🏹" },
    { id: "NECROMANCER", name: "Necromancer", role: "Lord of the Dead", icon: "💀" },
    { id: "DRUID", name: "Druid", role: "Shapeshifter & Stormcaller", icon: "🐻" }
];

export const EquipmentSlots = [
    { id: "helm", name: "Helm", icon: "🪖", defaultBaseArmor: 450 },
    { id: "chest", name: "Chest Armor", icon: "🥋", defaultBaseArmor: 700 },
    { id: "gloves", name: "Gloves", icon: "🧤", defaultBaseArmor: 300 },
    { id: "pants", name: "Pants", icon: "👖", defaultBaseArmor: 550 },
    { id: "boots", name: "Boots", icon: "🥾", defaultBaseArmor: 320 },
    { id: "mainHand", name: "Main Hand Weapon", icon: "🗡️", defaultBaseArmor: 0 },
    { id: "offHand", name: "Off-Hand / Shield", icon: "🛡️", defaultBaseArmor: 250 },
    { id: "amulet", name: "Amulet", icon: "📿", defaultBaseArmor: 0 },
    { id: "ring1", name: "Ring (Left)", icon: "💍", defaultBaseArmor: 0 },
    { id: "ring2", name: "Ring (Right)", icon: "💍", defaultBaseArmor: 0 }
];

export const ItemRarities = [
    { id: "MAGIC", name: "Magic", color: "#3F51B5", maxAffixes: 2 },
    { id: "RARE", name: "Rare", color: "#FFD700", maxAffixes: 3 },
    { id: "LEGENDARY", name: "Legendary", color: "#FF9800", maxAffixes: 3 },
    { id: "UNIQUE", name: "Unique", color: "#D32F2F", maxAffixes: 4 },
    { id: "MYTHIC_UNIQUE", name: "Mythic Unique", color: "#AA00FF", maxAffixes: 4 }
];

export const PreloadedBuilds = [
    {
        id: "build_sb_quill_volley",
        name: "Quill Volley Spiritborn (Endgame S-Tier)",
        classId: "SPIRITBORN",
        tier: "S Tier (Pit 120+)",
        author: "Maxroll D4 Team",
        description: "Devastating feather projectile shotgun build dominating Torment IV Pit pushes with high barrier and mobility.",
        skills: ["Quill Volley", "Stinger", "Razor Wings", "Vortex", "The Hunter", "Armored Hide"],
        passives: ["Adaptive Stance (3/3)", "Apex Predator (3/3)", "Resilient (3/3)", "Brilliance (3/3)"],
        paragonBoards: ["Starter Board", "Spiritual Harmony", "Jaguar Prowess", "Eagle Talon", "Bitter Medicine"],
        gearSpecs: {
            helm: { name: "Harlequin Crest (Shako)", rarity: "MYTHIC_UNIQUE", itemPower: 800, masterwork: 12, affixes: ["+20% Damage Reduction", "+4 to All Skills", "+1,200 Maximum Life", "+25% Resource Generation"], aspect: "Gain 20% Damage Reduction. Gain +4 Ranks to all Skills." },
            chest: { name: "Shroud of False Death", rarity: "MYTHIC_UNIQUE", itemPower: 800, masterwork: 12, affixes: ["+1 to All Passives", "+40% Movement Speed", "+1,500 Maximum Life", "+800 Armor"], aspect: "Gain Stealth when out of combat. All passives +1." },
            gloves: { name: "Runic Wraps of Moonrise", rarity: "LEGENDARY", itemPower: 750, masterwork: 8, affixes: ["+4 Ranks to Quill Volley", "+18% Attack Speed", "+12% Critical Strike Chance"], aspect: "Aspect of Moonrise: Basic skills grant +40% Attack Speed and +80% Damage." },
            pants: { name: "Vessel Leggings of Unyielding Hits", rarity: "LEGENDARY", itemPower: 750, masterwork: 8, affixes: ["+1,100 Maximum Life", "+18% Total Armor", "+85 All Resistances"], aspect: "Aspect of Unyielding Hits: Casting Weapon Mastery grants 1,250 Armor for 6s." },
            boots: { name: "Yen's Blessing", rarity: "UNIQUE", itemPower: 750, masterwork: 8, affixes: ["+25% Movement Speed", "+65% Fire Resistance", "+65% Poison Resistance"], aspect: "Casting a Skill has a 40% chance to cast a non-Mobility skill on cooldown." },
            mainHand: { name: "Nesekem, The Herald", rarity: "MYTHIC_UNIQUE", itemPower: 800, masterwork: 12, affixes: ["+250% Overpower Damage", "+120 All Stats", "+35% Attack Speed"], aspect: "Your next attack after 5s Overpowers and triggers a colossal shockwave." },
            offHand: { name: "Rod of Kehjistan", rarity: "LEGENDARY", itemPower: 750, masterwork: 8, affixes: ["+450 Armor", "+15% Critical Strike Chance", "+75% Lightning Resistance"], aspect: "Aspect of Retaliation: Skills deal up to 40% increased damage based on Fortify." },
            amulet: { name: "Amulet of Adaptability", rarity: "LEGENDARY", itemPower: 750, masterwork: 10, affixes: ["+25% Cooldown Reduction", "+3 to Focus Passives", "+20% Movement Speed"], aspect: "Aspect of Adaptability: Below 50% Vigor, Basic skills gain +80% damage." },
            ring1: { name: "Ring of Starless Skies", rarity: "MYTHIC_UNIQUE", itemPower: 800, masterwork: 12, affixes: ["+15% Critical Strike Chance", "+15% Attack Speed", "+25% Resource Cost Reduction"], aspect: "Spending Primary Resource reduces Core cost and increases damage by 50%." },
            ring2: { name: "Band of the Midnight Sun", rarity: "UNIQUE", itemPower: 750, masterwork: 8, affixes: ["+12% Critical Strike Chance", "+45% Shadow Resistance", "+35% Cold Resistance"], aspect: "When you Critical Strike, recover 40% of Primary Resource spent." }
        }
    },
    {
        id: "build_sorc_lightning_spear",
        name: "Lightning Spear Sorcerer (Boss Slayer S-Tier)",
        classId: "SORCERER",
        tier: "S Tier (Bossing & Speed)",
        author: "Mobalytics D4",
        description: "Automated conjuration barrage triggering thousands of Splintering Aspect critical shocks.",
        skills: ["Lightning Spear", "Ice Blades", "Teleport", "Flame Shield", "Ice Armor", "Unstable Currents"],
        passives: ["Devouring Blaze (3/3)", "Conjuration Mastery (3/3)", "Protection (3/3)", "Permafrost (3/3)"],
        paragonBoards: ["Elemental Summoner", "Frigid Fate", "Burning Instinct", "Ceaseless Conduit"],
        gearSpecs: {
            helm: { name: "Harlequin Crest", rarity: "MYTHIC_UNIQUE", itemPower: 800, masterwork: 12, affixes: ["+20% Damage Reduction", "+4 to All Skills", "+1,200 Maximum Life", "+25% Resource Generation"], aspect: "Gain 20% Damage Reduction. Gain +4 Ranks to all Skills." },
            chest: { name: "Raiment of the Infinite", rarity: "UNIQUE", itemPower: 750, masterwork: 8, affixes: ["+15% Teleport Cooldown", "+600 Armor", "+18% Damage to Stunned"], aspect: "Teleport pulls Close enemies to you and Stuns them for 2.5 seconds." },
            gloves: { name: "Storm Swell Gloves", rarity: "LEGENDARY", itemPower: 750, masterwork: 8, affixes: ["+16% Attack Speed", "+14% Critical Strike Chance", "+75% Cold Resistance"], aspect: "Aspect of Storm Swell: Deal 30% increased damage to Vulnerable enemies while Barriered." },
            pants: { name: "Tibault's Will", rarity: "UNIQUE", itemPower: 750, masterwork: 10, affixes: ["+20% Damage while Unstoppable", "+850 Armor", "+20 Max Resource"], aspect: "Gain 50 Vigor/Mana and deal 20% increased damage while Unstoppable and 4s after." },
            boots: { name: "Esu's Heirloom", rarity: "UNIQUE", itemPower: 750, masterwork: 8, affixes: ["+30% Movement Speed", "+55% Fire Resistance", "+2 Ranks to Flame Shield"], aspect: "Your Critical Strike Chance is increased by 30% of your Movement Speed bonus." },
            mainHand: { name: "Fractured Winterglass", rarity: "UNIQUE", itemPower: 750, masterwork: 12, affixes: ["+4 to Conjuration Mastery", "+18% Cooldown Reduction", "+35% Non-Physical Damage"], aspect: "Casting Frozen Orb has a 65% chance to spawn a random Conjuration on detonation." },
            offHand: { name: "Splintering Focus", rarity: "LEGENDARY", itemPower: 750, masterwork: 8, affixes: ["+12% Critical Strike Chance", "+15% Cooldown Reduction", "+70% Lightning Resistance"], aspect: "Splintering Aspect: Lightning Spear causes shockwaves dealing 12,000 damage." },
            amulet: { name: "Tal Rasha's Iridescent Loop (Slot Refactor)", rarity: "UNIQUE", itemPower: 750, masterwork: 8, affixes: ["+15% Cooldown Reduction", "+25% Resource Generation", "+70% All Resistances"], aspect: "For each unique element dealt, gain 20% increased damage for 4 seconds." },
            ring1: { name: "Ring of Starless Skies", rarity: "MYTHIC_UNIQUE", itemPower: 800, masterwork: 12, affixes: ["+15% Critical Strike Chance", "+15% Attack Speed", "+25% Core Cost Reduction"], aspect: "Resource-spending skills stack up to 50% damage bonus." },
            ring2: { name: "Conceited Ring", rarity: "LEGENDARY", itemPower: 750, masterwork: 8, affixes: ["+12% Critical Strike Chance", "+55% Poison Resistance", "+500 Armor"], aspect: "Conceited Aspect: Deal 25% increased damage while you have a Barrier active." }
        }
    },
    {
        id: "build_barb_whirlwind_dust",
        name: "Whirlwind Dust Devil Barbarian (Speed Farm A-Tier)",
        classId: "BARBARIAN",
        tier: "A Tier (Speed Farm)",
        author: "Horadric Vault Team",
        description: "Channels continuous Whirlwind while spawning tornados that sweep entire dungeon screens.",
        skills: ["Whirlwind", "Rallying Cry", "War Cry", "Challenging Shout", "Wrath of the Berserker", "Iron Skin"],
        passives: ["Heavy Handed (3/3)", "Wallop (3/3)", "Pit Fighter (3/3)", "Counteroffensive (3/3)"],
        paragonBoards: ["Warbringer", "Carnage", "Decimator", "Flawless Technique"],
        gearSpecs: {
            helm: { name: "Tuskhelm of Joritz the Mighty", rarity: "UNIQUE", itemPower: 750, masterwork: 8, affixes: ["+15% Cooldown Reduction", "+600 Armor", "+3 to Aggressive Resistance"], aspect: "When entering Berserking, gain 40% increased damage and 2 Fury per second." },
            chest: { name: "Tyrael's Might", rarity: "MYTHIC_UNIQUE", itemPower: 800, masterwork: 12, affixes: ["+100% All Resistances", "+20% Damage Reduction", "+15% Max All Resistance"], aspect: "While at Full Life, your skills unleash Divine Barrages dealing holy fire." },
            gloves: { name: "Dust Devil's Grips", rarity: "LEGENDARY", itemPower: 750, masterwork: 8, affixes: ["+15% Attack Speed", "+14% Critical Strike Chance", "+4 to Whirlwind"], aspect: "Dust Devil's Aspect: Whirlwind leaves behind Dust Devils that deal 15,000 damage." },
            pants: { name: "Tibault's Will", rarity: "UNIQUE", itemPower: 750, masterwork: 10, affixes: ["+20% Damage while Unstoppable", "+800 Armor", "+70% Shadow Resistance"], aspect: "Unstoppable grants 50 Fury and 20% multiplier." },
            boots: { name: "Yen's Blessing", rarity: "UNIQUE", itemPower: 750, masterwork: 8, affixes: ["+25% Movement Speed", "+65% Fire Resistance", "+600 Armor"], aspect: "Auto-casts shouts on cooldown." },
            mainHand: { name: "The Grandfather", rarity: "MYTHIC_UNIQUE", itemPower: 800, masterwork: 12, affixes: ["+100% Critical Strike Damage", "+2,500 Maximum Life", "+80 All Stats"], aspect: "Increases your Critical Strike Damage by 100%[x]. Your weapon durability never breaks." },
            offHand: { name: "Fierce Winds War Axe", rarity: "LEGENDARY", itemPower: 750, masterwork: 8, affixes: ["+120 Strength", "+75% Vulnerable Damage", "+14% Critical Strike Chance"], aspect: "Aspect of Fierce Winds: Your Dust Devils are 25% larger and pull enemies in." },
            amulet: { name: "Locran's Talisman", rarity: "UNIQUE", itemPower: 750, masterwork: 8, affixes: ["+3 to Weapon Mastery", "+18% Resource Generation", "+70% Lightning Resistance"], aspect: "Your skills gain Critical Strike Chance and Damage per point of primary resource." },
            ring1: { name: "Ring of Starless Skies", rarity: "MYTHIC_UNIQUE", itemPower: 800, masterwork: 12, affixes: ["+15% Critical Strike Chance", "+15% Attack Speed", "+25% Cost Reduction"], aspect: "Stacking 50% damage boost on spending Fury." },
            ring2: { name: "Bold Chieftain's Ring", rarity: "LEGENDARY", itemPower: 750, masterwork: 8, affixes: ["+12% Critical Strike Chance", "+75% Cold Resistance", "+500 Armor"], aspect: "Whenever you cast a Shout, its cooldown is reduced by 6s based on nearby enemies." }
        }
    }
];

export const SanctuaryRegions = [
    { id: "all", name: "All Sanctuary" },
    { id: "fractured_peaks", name: "Fractured Peaks" },
    { id: "scosglen", name: "Scosglen" },
    { id: "dry_steppes", name: "Dry Steppes" },
    { id: "kehjistan", name: "Kehjistan" },
    { id: "hawezar", name: "Hawezar" },
    { id: "nahantu", name: "Nahantu" }
];

export const MapNodesCatalog = [
    // Fractured Peaks
    { id: "fp_altar_1", region: "fractured_peaks", name: "Altar of Lilith - Desolate Highlands", type: "altar", x: 0.58, y: 0.48, bonus: "+2 Strength", aspect: null },
    { id: "fp_altar_2", region: "fractured_peaks", name: "Altar of Lilith - Kyovashad Gates", type: "altar", x: 0.62, y: 0.52, bonus: "+2 Willpower", aspect: null },
    { id: "fp_altar_3", region: "fractured_peaks", name: "Altar of Lilith - Seat of the Heavens", type: "altar", x: 0.66, y: 0.42, bonus: "+5 Max Obols", aspect: null },
    { id: "fp_waypoint_1", region: "fractured_peaks", name: "Kyovashad Waypoint", type: "waypoint", x: 0.60, y: 0.50, bonus: "Capital City Portal", aspect: null },
    { id: "fp_waypoint_2", region: "fractured_peaks", name: "Menestad Waypoint", type: "waypoint", x: 0.54, y: 0.44, bonus: "Outpost Fast Travel", aspect: null },
    { id: "fp_stronghold_1", region: "fractured_peaks", name: "Nostrava Stronghold", type: "stronghold", x: 0.52, y: 0.52, bonus: "+100 Renown, Unlocks Town Vendor", aspect: null },
    { id: "fp_dungeon_1", region: "fractured_peaks", name: "Forsaken Quarry", type: "dungeon", x: 0.64, y: 0.46, bonus: "+30 Renown", aspect: "Aspect of Encircling Blades (Rogue)" },
    { id: "fp_boss_1", region: "fractured_peaks", name: "The Beast in the Ice (Glacial Fissure)", type: "boss", x: 0.58, y: 0.42, bonus: "Tormented Boss", drops: "Fist of Fate, Paingorger's Gauntlets, Mythic Uniques" },

    // Scosglen
    { id: "sc_altar_1", region: "scosglen", name: "Altar of Lilith - Highland Wilds", type: "altar", x: 0.48, y: 0.28, bonus: "+2 Intelligence", aspect: null },
    { id: "sc_altar_2", region: "scosglen", name: "Altar of Lilith - The Emerald Chases", type: "altar", x: 0.42, y: 0.22, bonus: "+2 Dexterity", aspect: null },
    { id: "sc_waypoint_1", region: "scosglen", name: "Cerrigar Waypoint", type: "waypoint", x: 0.45, y: 0.25, bonus: "Masterworking & Pit Hub", aspect: null },
    { id: "sc_stronghold_1", region: "scosglen", name: "Tur Dulra Stronghold", type: "stronghold", x: 0.38, y: 0.26, bonus: "Druid Class Quest Haven", aspect: null },
    { id: "sc_dungeon_1", region: "scosglen", name: "Sunken Ruins", type: "dungeon", x: 0.50, y: 0.20, bonus: "+30 Renown", aspect: "Aspect of Ancestral Force (Barbarian)" },

    // Dry Steppes
    { id: "ds_altar_1", region: "dry_steppes", name: "Altar of Lilith - Jakha Basin", type: "altar", x: 0.32, y: 0.45, bonus: "+2 Strength", aspect: null },
    { id: "ds_waypoint_1", region: "dry_steppes", name: "Ked Bardu Waypoint", type: "waypoint", x: 0.30, y: 0.42, bonus: "Steppes Trade Hub", aspect: null },
    { id: "ds_stronghold_1", region: "dry_steppes", name: "The Onyx Watchtower", type: "stronghold", x: 0.35, y: 0.48, bonus: "+100 Renown", aspect: null },
    { id: "ds_boss_1", region: "dry_steppes", name: "Grigoire, The Galvanic Saint (Hall of the Penitent)", type: "boss", x: 0.28, y: 0.46, bonus: "Tormented Boss", drops: "Shard of Verathiel, Ramaladni's Magnum Opus" },

    // Kehjistan
    { id: "kj_altar_1", region: "kehjistan", name: "Altar of Lilith - Amber Sands", type: "altar", x: 0.22, y: 0.65, bonus: "+2 Dexterity", aspect: null },
    { id: "kj_waypoint_1", region: "kehjistan", name: "Gea Kul Waypoint", type: "waypoint", x: 0.18, y: 0.72, bonus: "Southern Port Hub", aspect: null },
    { id: "kj_boss_1", region: "kehjistan", name: "Echo of Duriel, Maggot King (Gaping Crevasse)", type: "boss", x: 0.24, y: 0.68, bonus: "Prime Torment Boss", drops: "Harlequin Crest, The Grandfather, Ring of Starless Skies, Andariel's Visage" },

    // Hawezar
    { id: "hw_altar_1", region: "hawezar", name: "Altar of Lilith - Dismal Foothills", type: "altar", x: 0.65, y: 0.70, bonus: "+2 Willpower", aspect: null },
    { id: "hw_waypoint_1", region: "hawezar", name: "Zarbinzet Waypoint", type: "waypoint", x: 0.58, y: 0.68, bonus: "Crusader Fortress Hub", aspect: null },
    { id: "hw_boss_1", region: "hawezar", name: "Lord Zir, The Dark Master (The Darkened Way)", type: "boss", x: 0.68, y: 0.74, bonus: "Tormented Boss", drops: "Yen's Blessing, Fractured Winterglass, Scoundrel's Kiss" },

    // Nahantu (Vessel of Hatred Expansion)
    { id: "nh_altar_1", region: "nahantu", name: "Tenet of Akarat - Kurast Docks", type: "altar", x: 0.40, y: 0.85, bonus: "+3 All Stats", aspect: null },
    { id: "nh_waypoint_1", region: "nahantu", name: "Kurast Bazaar Waypoint", type: "waypoint", x: 0.42, y: 0.88, bonus: "Kurast Undercity Hub", aspect: null },
    { id: "nh_boss_1", region: "nahantu", name: "Tormented Andariel (Hanged Man's Hall)", type: "boss", x: 0.46, y: 0.82, bonus: "Prime Torment Boss", drops: "Tyrael's Might, Shroud of False Death, Nesekem" }
];

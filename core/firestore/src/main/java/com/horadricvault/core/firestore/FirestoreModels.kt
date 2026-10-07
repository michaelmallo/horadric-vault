package com.horadricvault.core.firestore

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import com.horadricvault.core.model.*

data class UserProfileDto(
    @DocumentId
    val userId: String = "",
    val displayName: String = "",
    val email: String = "",
    @ServerTimestamp
    val lastSyncTimestamp: Timestamp? = null,
    @ServerTimestamp
    val createdAt: Timestamp? = null
) {
    fun toDomain(isAnonymous: Boolean = false): UserProfile = UserProfile(
        userId = userId,
        displayName = displayName,
        email = email,
        isAnonymous = isAnonymous,
        lastSyncTimestamp = lastSyncTimestamp?.toDate()?.time ?: System.currentTimeMillis(),
        createdAtTimestamp = createdAt?.toDate()?.time ?: System.currentTimeMillis()
    )
}

data class UserSettingsDto(
    val defaultRealm: String = "SEASONAL_SOFTCORE",
    val currentTormentTier: Int = 4,
    val themeVariant: String = "DARK_HELLFIRE",
    val enableCapGlowWarnings: Boolean = true,
    val autoSyncBuilds: Boolean = true
) {
    fun toDomain(): UserSettings = UserSettings(
        defaultRealm = runCatching { Realm.valueOf(defaultRealm) }.getOrDefault(Realm.SEASONAL_SOFTCORE),
        currentTormentTier = when (currentTormentTier) {
            1 -> TormentTier.TORMENT_1
            2 -> TormentTier.TORMENT_2
            3 -> TormentTier.TORMENT_3
            4 -> TormentTier.TORMENT_4
            else -> TormentTier.NORMAL
        },
        themeVariant = themeVariant,
        enableCapGlowWarnings = enableCapGlowWarnings,
        autoSyncBuilds = autoSyncBuilds
    )

    companion object {
        fun fromDomain(settings: UserSettings): UserSettingsDto = UserSettingsDto(
            defaultRealm = settings.defaultRealm.name,
            currentTormentTier = settings.currentTormentTier.tierNumber,
            themeVariant = settings.themeVariant,
            enableCapGlowWarnings = settings.enableCapGlowWarnings,
            autoSyncBuilds = settings.autoSyncBuilds
        )
    }
}

data class CharacterDto(
    @DocumentId
    val characterId: String = "",
    val characterName: String = "",
    val characterClass: String = "SORCERER",
    val level: Int = 60,
    val paragonLevel: Int = 100,
    val realm: String = "SEASONAL_SOFTCORE",
    val activeTormentTier: Int = 4,
    val activeTargetBuildId: String? = null,
    val assignedSkillPoints: Int = 71,
    val currentArmory: Map<String, EquipmentItemDto?> = emptyMap(),
    @ServerTimestamp
    val updatedAt: Timestamp? = null
) {
    fun toDomain(): D4Character {
        val loadoutMap = currentArmory.mapNotNull { (slotKey, itemDto) ->
            val slot = runCatching { EquipmentSlot.valueOf(slotKey) }.getOrNull()
            if (slot != null && itemDto != null) slot to itemDto.toDomain() else null
        }.toMap()

        var loadout = ArmoryLoadout()
        loadoutMap.forEach { (slot, item) ->
            loadout = loadout.withSlot(slot, item)
        }

        return D4Character(
            characterId = characterId,
            characterName = characterName,
            characterClass = runCatching { CharacterClass.valueOf(characterClass) }.getOrDefault(CharacterClass.SORCERER),
            level = level,
            paragonLevel = paragonLevel,
            realm = runCatching { Realm.valueOf(realm) }.getOrDefault(Realm.SEASONAL_SOFTCORE),
            activeTormentTier = when (activeTormentTier) {
                1 -> TormentTier.TORMENT_1
                2 -> TormentTier.TORMENT_2
                3 -> TormentTier.TORMENT_3
                4 -> TormentTier.TORMENT_4
                else -> TormentTier.NORMAL
            },
            activeTargetBuildId = activeTargetBuildId,
            currentArmory = loadout,
            assignedSkillPoints = assignedSkillPoints,
            updatedAtTimestamp = updatedAt?.toDate()?.time ?: System.currentTimeMillis()
        )
    }

    companion object {
        fun fromDomain(char: D4Character): CharacterDto {
            val armoryDtoMap = mutableMapOf<String, EquipmentItemDto?>()
            EquipmentSlot.entries.forEach { slot ->
                val item = char.currentArmory.getSlot(slot)
                armoryDtoMap[slot.name] = item?.let { EquipmentItemDto.fromDomain(it) }
            }

            return CharacterDto(
                characterId = char.characterId,
                characterName = char.characterName,
                characterClass = char.characterClass.name,
                level = char.level,
                paragonLevel = char.paragonLevel,
                realm = char.realm.name,
                activeTormentTier = char.activeTormentTier.tierNumber,
                activeTargetBuildId = char.activeTargetBuildId,
                assignedSkillPoints = char.assignedSkillPoints,
                currentArmory = armoryDtoMap
            )
        }
    }
}

data class EquipmentItemDto(
    val id: String = "",
    val name: String = "",
    val slot: String = "",
    val rarity: String = "LEGENDARY",
    val itemPower: Int = 800,
    val baseArmor: Int = 0,
    val masterworkLevel: Int = 0,
    val greaterAffixCount: Int = 0,
    val affixes: List<ItemAffixDto> = emptyList(),
    val tempers: List<ItemTemperDto> = emptyList(),
    val aspect: AspectDto? = null,
    val runeword: RunewordDto? = null
) {
    fun toDomain(): EquipmentItem = EquipmentItem(
        id = id,
        name = name,
        slot = runCatching { EquipmentSlot.valueOf(slot) }.getOrDefault(EquipmentSlot.HELM),
        rarity = runCatching { ItemRarity.valueOf(rarity) }.getOrDefault(ItemRarity.LEGENDARY),
        itemPower = itemPower,
        baseArmor = baseArmor,
        masterworkLevel = masterworkLevel,
        greaterAffixCount = greaterAffixCount,
        affixes = affixes.map { it.toDomain() },
        tempers = tempers.map { it.toDomain() },
        aspect = aspect?.toDomain(),
        runeword = runeword?.toDomain()
    )

    companion object {
        fun fromDomain(item: EquipmentItem): EquipmentItemDto = EquipmentItemDto(
            id = item.id,
            name = item.name,
            slot = item.slot.name,
            rarity = item.rarity.name,
            itemPower = item.itemPower,
            baseArmor = item.baseArmor,
            masterworkLevel = item.masterworkLevel,
            greaterAffixCount = item.greaterAffixCount,
            affixes = item.affixes.map { ItemAffixDto.fromDomain(it) },
            tempers = item.tempers.map { ItemTemperDto.fromDomain(it) },
            aspect = item.aspect?.let { AspectDto.fromDomain(it) },
            runeword = item.runeword?.let { RunewordDto.fromDomain(it) }
        )
    }
}

data class ItemAffixDto(
    val id: String = "",
    val description: String = "",
    val value: Double = 0.0,
    val unit: String = "",
    val isGreaterAffix: Boolean = false,
    val isRerolled: Boolean = false,
    val isDeadAffix: Boolean = false
) {
    fun toDomain(): ItemAffix = ItemAffix(
        id = id,
        description = description,
        value = value,
        unit = unit,
        isGreaterAffix = isGreaterAffix,
        isRerolled = isRerolled,
        isDeadAffix = isDeadAffix
    )

    companion object {
        fun fromDomain(affix: ItemAffix): ItemAffixDto = ItemAffixDto(
            id = affix.id,
            description = affix.description,
            value = affix.value,
            unit = affix.unit,
            isGreaterAffix = affix.isGreaterAffix,
            isRerolled = affix.isRerolled,
            isDeadAffix = affix.isDeadAffix
        )
    }
}

data class ItemTemperDto(
    val recipeName: String = "",
    val affixDescription: String = "",
    val rollValue: Double = 0.0,
    val isMaxRoll: Boolean = false
) {
    fun toDomain(): ItemTemper = ItemTemper(
        recipeName = recipeName,
        affixDescription = affixDescription,
        rollValue = rollValue,
        isMaxRoll = isMaxRoll
    )

    companion object {
        fun fromDomain(temper: ItemTemper): ItemTemperDto = ItemTemperDto(
            recipeName = temper.recipeName,
            affixDescription = temper.affixDescription,
            rollValue = temper.rollValue,
            isMaxRoll = temper.isMaxRoll
        )
    }
}

data class AspectDto(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val powerRoll: Double = 0.0,
    val codexDungeonLocation: String? = null
) {
    fun toDomain(): LegendaryAspect = LegendaryAspect(
        id = id,
        name = name,
        description = description,
        powerRoll = powerRoll,
        codexDungeonLocation = codexDungeonLocation
    )

    companion object {
        fun fromDomain(aspect: LegendaryAspect): AspectDto = AspectDto(
            id = aspect.id,
            name = aspect.name,
            description = aspect.description,
            powerRoll = aspect.powerRoll,
            codexDungeonLocation = aspect.codexDungeonLocation
        )
    }
}

data class RunewordDto(
    val offeringRune: String = "",
    val invocationRune: String = "",
    val effectDescription: String = ""
) {
    fun toDomain(): RunewordPair = RunewordPair(
        offeringRune = offeringRune,
        invocationRune = invocationRune,
        effectDescription = effectDescription
    )

    companion object {
        fun fromDomain(rw: RunewordPair): RunewordDto = RunewordDto(
            offeringRune = rw.offeringRune,
            invocationRune = rw.invocationRune,
            effectDescription = rw.effectDescription
        )
    }
}

data class BuildDto(
    @DocumentId
    val buildId: String = "",
    val buildName: String = "",
    val sourceUrl: String? = null,
    val sourcePlatform: String? = null,
    val characterClass: String = "SORCERER",
    val skillBar: List<String> = emptyList(),
    val passives: Map<String, Int> = emptyMap(),
    val paragonBoards: List<String> = emptyList(),
    val targetEquipmentSlots: Map<String, TargetItemSpecDto> = emptyMap(),
    val tierRating: String = "S Tier",
    @ServerTimestamp
    val updatedAt: Timestamp? = null
) {
    fun toDomain(): TargetBuild = TargetBuild(
        buildId = buildId,
        buildName = buildName,
        sourceUrl = sourceUrl,
        sourcePlatform = sourcePlatform,
        characterClass = runCatching { CharacterClass.valueOf(characterClass) }.getOrDefault(CharacterClass.SORCERER),
        skillBar = skillBar,
        passives = passives,
        paragonBoards = paragonBoards,
        targetEquipmentSlots = targetEquipmentSlots.mapValues { it.value.toDomain() },
        tierRating = tierRating,
        updatedAtTimestamp = updatedAt?.toDate()?.time ?: System.currentTimeMillis()
    )

    companion object {
        fun fromDomain(build: TargetBuild): BuildDto = BuildDto(
            buildId = build.buildId,
            buildName = build.buildName,
            sourceUrl = build.sourceUrl,
            sourcePlatform = build.sourcePlatform,
            characterClass = build.characterClass.name,
            skillBar = build.skillBar,
            passives = build.passives,
            paragonBoards = build.paragonBoards,
            targetEquipmentSlots = build.targetEquipmentSlots.mapValues { TargetItemSpecDto.fromDomain(it.value) },
            tierRating = build.tierRating
        )
    }
}

data class TargetItemSpecDto(
    val slot: String = "",
    val targetItemName: String = "",
    val rarity: String = "LEGENDARY",
    val requiredAspect: AspectDto? = null,
    val targetAffixes: List<String> = emptyList(),
    val targetTempers: List<String> = emptyList(),
    val bossSource: String? = null,
    val isMythicUnique: Boolean = false
) {
    fun toDomain(): TargetItemSpec = TargetItemSpec(
        slot = runCatching { EquipmentSlot.valueOf(slot) }.getOrDefault(EquipmentSlot.HELM),
        targetItemName = targetItemName,
        rarity = runCatching { ItemRarity.valueOf(rarity) }.getOrDefault(ItemRarity.LEGENDARY),
        requiredAspect = requiredAspect?.toDomain(),
        targetAffixes = targetAffixes,
        targetTempers = targetTempers,
        bossSource = bossSource,
        isMythicUnique = isMythicUnique
    )

    companion object {
        fun fromDomain(spec: TargetItemSpec): TargetItemSpecDto = TargetItemSpecDto(
            slot = spec.slot.name,
            targetItemName = spec.targetItemName,
            rarity = spec.rarity.name,
            requiredAspect = spec.requiredAspect?.let { AspectDto.fromDomain(it) },
            targetAffixes = spec.targetAffixes,
            targetTempers = spec.targetTempers,
            bossSource = spec.bossSource,
            isMythicUnique = spec.isMythicUnique
        )
    }
}

data class MapProgressDto(
    @DocumentId
    val regionId: String = "",
    val completedNodeIds: List<String> = emptyList(),
    val completedQuests: List<String> = emptyList(),
    val strongholdsUnlocked: List<String> = emptyList(),
    @ServerTimestamp
    val updatedAt: Timestamp? = null
) {
    fun toDomain(): RegionMapProgress = RegionMapProgress(
        regionId = regionId,
        completedNodeIds = completedNodeIds,
        completedQuests = completedQuests,
        strongholdsUnlocked = strongholdsUnlocked,
        updatedAtTimestamp = updatedAt?.toDate()?.time ?: System.currentTimeMillis()
    )

    companion object {
        fun fromDomain(domain: RegionMapProgress): MapProgressDto = MapProgressDto(
            regionId = domain.regionId,
            completedNodeIds = domain.completedNodeIds,
            completedQuests = domain.completedQuests,
            strongholdsUnlocked = domain.strongholdsUnlocked
        )
    }
}

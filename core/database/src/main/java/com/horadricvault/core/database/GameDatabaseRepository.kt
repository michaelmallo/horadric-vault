package com.horadricvault.core.database

import android.content.Context
import com.horadricvault.core.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class GameSkillDefinition(
    val id: String,
    val characterClass: CharacterClass,
    val name: String,
    val category: String,
    val maxRanks: Int,
    val description: String
)

interface GameDefinitionsRepository {
    suspend fun getSkills(characterClass: CharacterClass? = null): List<GameSkillDefinition>
    suspend fun getAspects(): List<LegendaryAspect>
    suspend fun getUniques(characterClass: CharacterClass? = null): List<EquipmentItem>
    suspend fun getMapNodes(region: SanctuaryRegion): List<MapNode>
    suspend fun getAllBosses(): List<EndgameBoss>
    suspend fun getBoss(bossId: String): EndgameBoss?
}

class SqliteGameDefinitionsRepository(
    context: Context
) : GameDefinitionsRepository {

    private val dbHelper = GameDatabaseHelper(context.applicationContext)

    override suspend fun getSkills(characterClass: CharacterClass?): List<GameSkillDefinition> = withContext(Dispatchers.IO) {
        val db = dbHelper.readableDatabase
        val list = mutableListOf<GameSkillDefinition>()

        val selection = if (characterClass != null) "character_class = ?" else null
        val selectionArgs = if (characterClass != null) arrayOf(characterClass.name) else null

        val cursor = db.query(
            GameDatabaseHelper.TABLE_SKILLS,
            null,
            selection,
            selectionArgs,
            null, null, "category, name ASC"
        )

        cursor.use { c ->
            val idCol = c.getColumnIndexOrThrow("id")
            val clsCol = c.getColumnIndexOrThrow("character_class")
            val nameCol = c.getColumnIndexOrThrow("name")
            val catCol = c.getColumnIndexOrThrow("category")
            val rankCol = c.getColumnIndexOrThrow("max_ranks")
            val descCol = c.getColumnIndexOrThrow("description")

            while (c.moveToNext()) {
                val cls = runCatching { CharacterClass.valueOf(c.getString(clsCol)) }.getOrDefault(CharacterClass.SORCERER)
                list.add(
                    GameSkillDefinition(
                        id = c.getString(idCol),
                        characterClass = cls,
                        name = c.getString(nameCol),
                        category = c.getString(catCol),
                        maxRanks = c.getInt(rankCol),
                        description = c.getString(descCol)
                    )
                )
            }
        }
        list
    }

    override suspend fun getAspects(): List<LegendaryAspect> = withContext(Dispatchers.IO) {
        val db = dbHelper.readableDatabase
        val list = mutableListOf<LegendaryAspect>()

        val cursor = db.query(GameDatabaseHelper.TABLE_ASPECTS, null, null, null, null, null, "name ASC")
        cursor.use { c ->
            val idCol = c.getColumnIndexOrThrow("id")
            val nameCol = c.getColumnIndexOrThrow("name")
            val descCol = c.getColumnIndexOrThrow("description")
            val dungeonCol = c.getColumnIndexOrThrow("codex_dungeon")

            while (c.moveToNext()) {
                list.add(
                    LegendaryAspect(
                        id = c.getString(idCol),
                        name = c.getString(nameCol),
                        description = c.getString(descCol),
                        codexDungeonLocation = c.getString(dungeonCol)
                    )
                )
            }
        }
        list
    }

    override suspend fun getUniques(characterClass: CharacterClass?): List<EquipmentItem> = withContext(Dispatchers.IO) {
        val db = dbHelper.readableDatabase
        val list = mutableListOf<EquipmentItem>()

        val cursor = db.query(GameDatabaseHelper.TABLE_UNIQUES, null, null, null, null, null, "name ASC")
        cursor.use { c ->
            val idCol = c.getColumnIndexOrThrow("id")
            val nameCol = c.getColumnIndexOrThrow("name")
            val rarityCol = c.getColumnIndexOrThrow("rarity")
            val slotCol = c.getColumnIndexOrThrow("slot")
            val clsCol = c.getColumnIndexOrThrow("target_classes")
            val powerCol = c.getColumnIndexOrThrow("unique_power")

            while (c.moveToNext()) {
                val allowedClasses = c.getString(clsCol)
                if (characterClass != null && allowedClasses != "ALL" && !allowedClasses.contains(characterClass.name)) {
                    continue
                }

                val slot = runCatching { EquipmentSlot.valueOf(c.getString(slotCol)) }.getOrDefault(EquipmentSlot.HELM)
                val rarity = runCatching { ItemRarity.valueOf(c.getString(rarityCol)) }.getOrDefault(ItemRarity.UNIQUE)

                list.add(
                    EquipmentItem(
                        id = c.getString(idCol),
                        name = c.getString(nameCol),
                        slot = slot,
                        rarity = rarity,
                        itemPower = if (rarity == ItemRarity.MYTHIC_UNIQUE) 800 else 750,
                        baseArmor = if (slot == EquipmentSlot.HELM || slot == EquipmentSlot.CHEST || slot == EquipmentSlot.PANTS) 450 else 250,
                        affixes = listOf(
                            ItemAffix(description = c.getString(powerCol), value = 0.0)
                        )
                    )
                )
            }
        }
        list
    }

    override suspend fun getMapNodes(region: SanctuaryRegion): List<MapNode> = withContext(Dispatchers.IO) {
        val db = dbHelper.readableDatabase
        val list = mutableListOf<MapNode>()

        val cursor = db.query(
            GameDatabaseHelper.TABLE_MAP_NODES,
            null,
            "region_id = ?",
            arrayOf(region.regionId),
            null, null, "name ASC"
        )

        cursor.use { c ->
            val idCol = c.getColumnIndexOrThrow("id")
            val nameCol = c.getColumnIndexOrThrow("name")
            val typeCol = c.getColumnIndexOrThrow("node_type")
            val xCol = c.getColumnIndexOrThrow("normalized_x")
            val yCol = c.getColumnIndexOrThrow("normalized_y")
            val bonusCol = c.getColumnIndexOrThrow("bonus_desc")
            val aspectCol = c.getColumnIndexOrThrow("associated_aspect")

            while (c.moveToNext()) {
                val nodeType = runCatching { MapNodeType.valueOf(c.getString(typeCol)) }.getOrDefault(MapNodeType.ALTAR_OF_LILITH)
                list.add(
                    MapNode(
                        nodeId = c.getString(idCol),
                        region = region,
                        name = c.getString(nameCol),
                        type = nodeType,
                        normalizedX = c.getFloat(xCol),
                        normalizedY = c.getFloat(yCol),
                        bonusDescription = c.getString(bonusCol) ?: "",
                        associatedAspect = c.getString(aspectCol)
                    )
                )
            }
        }
        list
    }

    override suspend fun getAllBosses(): List<EndgameBoss> = withContext(Dispatchers.IO) {
        val db = dbHelper.readableDatabase
        val bossList = mutableListOf<EndgameBoss>()

        val cursor = db.query(GameDatabaseHelper.TABLE_BOSSES, null, null, null, null, null, "name ASC")
        val bossBasicList = mutableListOf<BossMeta>()
        cursor.use { c ->
            val idCol = c.getColumnIndexOrThrow("id")
            val nameCol = c.getColumnIndexOrThrow("name")
            val titleCol = c.getColumnIndexOrThrow("title")
            val locCol = c.getColumnIndexOrThrow("dungeon_location")
            val regCol = c.getColumnIndexOrThrow("region_id")
            val torCol = c.getColumnIndexOrThrow("min_torment")

            while (c.moveToNext()) {
                bossBasicList.add(
                    BossMeta(
                        id = c.getString(idCol),
                        name = c.getString(nameCol),
                        title = c.getString(titleCol),
                        location = c.getString(locCol),
                        regionId = c.getString(regCol),
                        minTorment = c.getString(torCol)
                    )
                )
            }
        }

        bossBasicList.forEach { meta ->
            val region = SanctuaryRegion.entries.find { it.regionId == meta.regionId } ?: SanctuaryRegion.FRACTURED_PEAKS
            val torment = runCatching { TormentTier.valueOf(meta.minTorment) }.getOrDefault(TormentTier.TORMENT_1)

            // Materials
            val matList = mutableListOf<BossSummonRequirement>()
            val matCursor = db.query(
                GameDatabaseHelper.TABLE_BOSS_MATERIALS,
                null,
                "boss_id = ?",
                arrayOf(meta.id),
                null, null, null
            )
            matCursor.use { mc ->
                val nameCol = mc.getColumnIndexOrThrow("material_name")
                val stdCol = mc.getColumnIndexOrThrow("standard_amount")
                val torCol = mc.getColumnIndexOrThrow("tormented_amount")
                val srcCol = mc.getColumnIndexOrThrow("farm_source")
                while (mc.moveToNext()) {
                    matList.add(
                        BossSummonRequirement(
                            materialName = mc.getString(nameCol),
                            standardAmount = mc.getInt(stdCol),
                            tormentedAmount = mc.getInt(torCol),
                            farmSourceDescription = mc.getString(srcCol)
                        )
                    )
                }
            }

            // Drops
            val dropList = mutableListOf<BossTargetDrop>()
            val dropCursor = db.query(
                GameDatabaseHelper.TABLE_BOSS_DROPS,
                null,
                "boss_id = ?",
                arrayOf(meta.id),
                null, null, null
            )
            dropCursor.use { dc ->
                val nameCol = dc.getColumnIndexOrThrow("item_name")
                val slotCol = dc.getColumnIndexOrThrow("item_slot")
                val rarCol = dc.getColumnIndexOrThrow("rarity")
                val mythCol = dc.getColumnIndexOrThrow("is_mythic")
                while (dc.moveToNext()) {
                    val slot = runCatching { EquipmentSlot.valueOf(dc.getString(slotCol)) }.getOrDefault(EquipmentSlot.HELM)
                    val rarity = runCatching { ItemRarity.valueOf(dc.getString(rarCol)) }.getOrDefault(ItemRarity.UNIQUE)
                    val isMyth = dc.getInt(mythCol) == 1
                    dropList.add(
                        BossTargetDrop(
                            itemName = dc.getString(nameCol),
                            itemSlot = slot,
                            rarity = rarity,
                            isMythic = isMyth
                        )
                    )
                }
            }

            bossList.add(
                EndgameBoss(
                    bossId = meta.id,
                    name = meta.name,
                    title = meta.title,
                    dungeonLocation = meta.location,
                    region = region,
                    materials = matList,
                    targetDrops = dropList,
                    minTormentRecommended = torment
                )
            )
        }

        bossList
    }

    override suspend fun getBoss(bossId: String): EndgameBoss? {
        return getAllBosses().find { it.bossId == bossId }
    }

    private data class BossMeta(
        val id: String,
        val name: String,
        val title: String,
        val location: String,
        val regionId: String,
        val minTorment: String
    )
}

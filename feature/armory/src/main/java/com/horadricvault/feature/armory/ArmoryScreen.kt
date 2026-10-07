package com.horadricvault.feature.armory

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.horadricvault.core.model.*
import com.horadricvault.feature.statcheck.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArmoryScreen(
    characters: List<D4Character>,
    activeCharacter: D4Character?,
    targetBuilds: List<TargetBuild>,
    allBosses: List<EndgameBoss>,
    onSelectCharacter: (D4Character) -> Unit,
    onCreateCharacter: (String, CharacterClass, Realm, TormentTier) -> Unit,
    onUpdateTormentTier: (TormentTier) -> Unit,
    onLinkTargetBuild: (String) -> Unit,
    onSaveEquipmentItem: (EquipmentSlot, EquipmentItem?) -> Unit,
    modifier: Modifier = Modifier
) {
    var showNewCharDialog by remember { mutableStateOf(false) }
    var editingSlot by remember { mutableStateOf<EquipmentSlot?>(null) }
    var showBuildSelectDialog by remember { mutableStateOf(false) }

    val statReport = remember(activeCharacter, activeCharacter?.activeTormentTier) {
        if (activeCharacter != null) {
            StatCalculator.calculateStatsFromArmory(
                armory = activeCharacter.currentArmory,
                tormentTier = activeCharacter.activeTormentTier
            )
        } else null
    }

    val activeBuild = remember(activeCharacter, targetBuilds) {
        targetBuilds.find { it.buildId == activeCharacter?.activeTargetBuildId }
            ?: targetBuilds.firstOrNull { it.characterClass == activeCharacter?.characterClass }
    }

    val progressionDelta = remember(activeCharacter, activeBuild, allBosses) {
        if (activeCharacter != null && activeBuild != null) {
            ArmoryProgressionEngine.calculateProgressionDelta(activeCharacter, activeBuild, allBosses)
        } else null
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(VaultDesignTokens.AbyssalBlack)
            .padding(horizontal = 14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            // Top Bar: Character Selector & Add Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "HORADRIC ARMORY",
                        color = VaultDesignTokens.HellfireGold,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = "Vessel of Hatred • Patch 2.0+ Loadout Tracker",
                        color = VaultDesignTokens.DimParchment,
                        fontSize = 11.sp
                    )
                }

                Button(
                    onClick = { showNewCharDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = VaultDesignTokens.EmberRed),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add Hero", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "New Hero", fontSize = 12.sp)
                }
            }
        }

        // Character Switcher Chips
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                characters.forEach { char ->
                    val isSelected = char.characterId == activeCharacter?.characterId
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) VaultDesignTokens.EmberRed.copy(alpha = 0.3f) else VaultDesignTokens.SlateIron)
                            .border(
                                1.dp,
                                if (isSelected) VaultDesignTokens.HellfireGold else VaultDesignTokens.CharcoalBorder,
                                RoundedCornerShape(8.dp)
                            )
                            .clickable { onSelectCharacter(char) }
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Column {
                            Text(
                                text = char.characterName,
                                color = if (isSelected) VaultDesignTokens.HellfireGold else VaultDesignTokens.SanctuaryParchment,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${char.characterClass.displayName} • Lvl ${char.level} (${char.paragonLevel})",
                                color = VaultDesignTokens.DimParchment,
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }
        }

        if (activeCharacter != null) {
            // Torment Tier Selector Pill Row
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = VaultDesignTokens.SlateIron),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "DIFFICULTY TIER:",
                            color = VaultDesignTokens.SanctuaryParchment,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            TormentTier.entries.filter { it != TormentTier.PENITENT }.forEach { tier ->
                                val selected = activeCharacter.activeTormentTier == tier
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (selected) VaultDesignTokens.CrimsonHighlight else VaultDesignTokens.AbyssalBlack)
                                        .border(
                                            1.dp,
                                            if (selected) VaultDesignTokens.HellfireGold else VaultDesignTokens.CharcoalBorder,
                                            RoundedCornerShape(6.dp)
                                        )
                                        .clickable { onUpdateTormentTier(tier) }
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = when (tier) {
                                            TormentTier.NORMAL, TormentTier.PENITENT -> "Normal"
                                            TormentTier.TORMENT_1 -> "T1"
                                            TormentTier.TORMENT_2 -> "T2"
                                            TormentTier.TORMENT_3 -> "T3"
                                            TormentTier.TORMENT_4 -> "T4"
                                        },
                                        color = if (selected) Color.White else VaultDesignTokens.DimParchment,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Stat Cap Check Summary Card
            if (statReport != null) {
                item {
                    ArmorCapMeter(armor = statReport.armor)
                }

                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(VaultDesignTokens.SlateIron)
                            .border(1.dp, VaultDesignTokens.CharcoalBorder, RoundedCornerShape(8.dp))
                            .padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "ELEMENTAL RESISTANCES (${activeCharacter.activeTormentTier.displayName} Penalty: -${GameConstants.getResistancePenalty(activeCharacter.activeTormentTier).toInt()}%)",
                            color = VaultDesignTokens.DimParchment,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                        statReport.resistances.forEach { res ->
                            ResistanceRow(res = res)
                        }
                    }
                }
            }

            // Target Build Banner
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(VaultDesignTokens.SlateIron)
                        .border(1.dp, VaultDesignTokens.HellfireGold, RoundedCornerShape(8.dp))
                        .clickable { showBuildSelectDialog = true }
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "TARGET BUILD GOAL",
                            color = VaultDesignTokens.DimParchment,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = activeBuild?.buildName ?: "No Target Build Linked (Tap to select)",
                            color = VaultDesignTokens.HellfireGold,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Switch Build",
                        tint = VaultDesignTokens.HellfireGold,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            // Progression Roadmap Delta
            if (progressionDelta != null) {
                item {
                    DeltaRoadmapCard(delta = progressionDelta)
                }
            }

            // 10 Equipment Slots (2 columns layout)
            item {
                Text(
                    text = "EQUIPPED WARDROBE SLOTS (TAP TO MODIFY)",
                    color = VaultDesignTokens.SanctuaryParchment,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))

                val leftSlots = listOf(EquipmentSlot.HELM, EquipmentSlot.CHEST, EquipmentSlot.GLOVES, EquipmentSlot.PANTS, EquipmentSlot.BOOTS)
                val rightSlots = listOf(EquipmentSlot.MAIN_HAND, EquipmentSlot.OFF_HAND, EquipmentSlot.AMULET, EquipmentSlot.RING_1, EquipmentSlot.RING_2)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        leftSlots.forEach { slot ->
                            GearSlotCard(
                                slot = slot,
                                item = activeCharacter.currentArmory.getSlot(slot),
                                onClick = { editingSlot = slot }
                            )
                        }
                    }

                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        rightSlots.forEach { slot ->
                            GearSlotCard(
                                slot = slot,
                                item = activeCharacter.currentArmory.getSlot(slot),
                                onClick = { editingSlot = slot }
                            )
                        }
                    }
                }
            }
        } else {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No characters created yet. Tap 'New Hero' to start tracking your build!",
                        color = VaultDesignTokens.DimParchment,
                        fontSize = 13.sp
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Modal: New Character Dialog
    if (showNewCharDialog) {
        var newName by remember { mutableStateOf("") }
        var selectedClass by remember { mutableStateOf(CharacterClass.SPIRITBORN) }
        var selectedRealm by remember { mutableStateOf(Realm.SEASONAL_SOFTCORE) }
        var selectedTorment by remember { mutableStateOf(TormentTier.TORMENT_4) }

        AlertDialog(
            onDismissRequest = { showNewCharDialog = false },
            containerColor = VaultDesignTokens.SlateIron,
            title = {
                Text(text = "Summon New Nephalem", color = VaultDesignTokens.HellfireGold, fontWeight = FontWeight.Bold)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = newName,
                        onValueChange = { newName = it },
                        label = { Text("Hero Name") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = VaultDesignTokens.HellfireGold,
                            unfocusedBorderColor = VaultDesignTokens.CharcoalBorder
                        )
                    )

                    Text(text = "Class:", color = VaultDesignTokens.DimParchment, fontSize = 12.sp)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        CharacterClass.entries.take(3).forEach { cls ->
                            FilterChip(
                                selected = selectedClass == cls,
                                onClick = { selectedClass = cls },
                                label = { Text(cls.displayName, fontSize = 10.sp) }
                            )
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        CharacterClass.entries.drop(3).forEach { cls ->
                            FilterChip(
                                selected = selectedClass == cls,
                                onClick = { selectedClass = cls },
                                label = { Text(cls.displayName, fontSize = 10.sp) }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newName.isNotBlank()) {
                            onCreateCharacter(newName, selectedClass, selectedRealm, selectedTorment)
                            showNewCharDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = VaultDesignTokens.EmberRed)
                ) {
                    Text("Create Hero")
                }
            },
            dismissButton = {
                TextButton(onClick = { showNewCharDialog = false }) {
                    Text("Cancel", color = VaultDesignTokens.DimParchment)
                }
            }
        )
    }

    // Modal: Target Build Selector
    if (showBuildSelectDialog) {
        AlertDialog(
            onDismissRequest = { showBuildSelectDialog = false },
            containerColor = VaultDesignTokens.SlateIron,
            title = {
                Text(text = "Select Target Build Goal", color = VaultDesignTokens.HellfireGold, fontWeight = FontWeight.Bold)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    targetBuilds.forEach { build ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .background(VaultDesignTokens.AbyssalBlack)
                                .clickable {
                                    onLinkTargetBuild(build.buildId)
                                    showBuildSelectDialog = false
                                }
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = build.buildName,
                                    color = VaultDesignTokens.SanctuaryParchment,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${build.characterClass.displayName} • ${build.sourcePlatform ?: "Community"}",
                                    color = VaultDesignTokens.DimParchment,
                                    fontSize = 10.sp
                                )
                            }
                            Text(
                                text = build.tierRating,
                                color = VaultDesignTokens.RunicTeal,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showBuildSelectDialog = false }) {
                    Text("Close", color = VaultDesignTokens.DimParchment)
                }
            }
        )
    }

    // Modal: Edit Slot Dialog
    editingSlot?.let { slot ->
        val currentItem = activeCharacter?.currentArmory?.getSlot(slot)
        var itemName by remember { mutableStateOf(currentItem?.name ?: "") }
        var rarity by remember { mutableStateOf(currentItem?.rarity ?: ItemRarity.LEGENDARY) }
        var baseArmor by remember { mutableStateOf(currentItem?.baseArmor?.toString() ?: "350") }
        var gaCount by remember { mutableStateOf(currentItem?.greaterAffixCount ?: 0) }
        var masterwork by remember { mutableStateOf(currentItem?.masterworkLevel ?: 0) }
        var hasDeadAffix by remember { mutableStateOf(currentItem?.affixes?.any { it.isDeadAffix } ?: false) }

        AlertDialog(
            onDismissRequest = { editingSlot = null },
            containerColor = VaultDesignTokens.SlateIron,
            title = {
                Text(text = "Configure ${slot.displayName}", color = VaultDesignTokens.HellfireGold, fontWeight = FontWeight.Bold)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = itemName,
                        onValueChange = { itemName = it },
                        label = { Text("Item Name") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = VaultDesignTokens.HellfireGold,
                            unfocusedBorderColor = VaultDesignTokens.CharcoalBorder
                        )
                    )

                    OutlinedTextField(
                        value = baseArmor,
                        onValueChange = { baseArmor = it },
                        label = { Text("Base Armor Roll") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = VaultDesignTokens.HellfireGold,
                            unfocusedBorderColor = VaultDesignTokens.CharcoalBorder
                        )
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Masterwork Rank: $masterwork/12", color = VaultDesignTokens.SanctuaryParchment, fontSize = 12.sp)
                        Row {
                            IconButton(onClick = { if (masterwork > 0) masterwork-- }) {
                                Icon(imageVector = Icons.Default.Remove, contentDescription = "Decrease", tint = VaultDesignTokens.DimParchment)
                            }
                            IconButton(onClick = { if (masterwork < 12) masterwork++ }) {
                                Icon(imageVector = Icons.Default.Add, contentDescription = "Increase", tint = VaultDesignTokens.RunicTeal)
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Greater Affixes (GA): $gaCount", color = VaultDesignTokens.CrimsonHighlight, fontSize = 12.sp)
                        Row {
                            IconButton(onClick = { if (gaCount > 0) gaCount-- }) {
                                Icon(imageVector = Icons.Default.Remove, contentDescription = "Decrease", tint = VaultDesignTokens.DimParchment)
                            }
                            IconButton(onClick = { if (gaCount < 4) gaCount++ }) {
                                Icon(imageVector = Icons.Default.Add, contentDescription = "Increase", tint = VaultDesignTokens.CrimsonHighlight)
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Flag Dead Affix (Needs Occultist)", color = VaultDesignTokens.OvercapAmber, fontSize = 12.sp)
                        Switch(
                            checked = hasDeadAffix,
                            onCheckedChange = { hasDeadAffix = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = VaultDesignTokens.OvercapAmber,
                                checkedTrackColor = VaultDesignTokens.OvercapAmber.copy(alpha = 0.5f)
                            )
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val newItem = EquipmentItem(
                            id = currentItem?.id ?: "",
                            name = itemName.ifBlank { "Equipped ${slot.displayName}" },
                            slot = slot,
                            rarity = rarity,
                            itemPower = 800,
                            baseArmor = baseArmor.toIntOrNull() ?: 350,
                            masterworkLevel = masterwork,
                            greaterAffixCount = gaCount,
                            affixes = listOf(
                                ItemAffix(
                                    description = if (hasDeadAffix) "+Resistance to Elements (Dead)" else "+Critical Strike Damage",
                                    value = 50.0,
                                    isDeadAffix = hasDeadAffix
                                )
                            )
                        )
                        onSaveEquipmentItem(slot, newItem)
                        editingSlot = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = VaultDesignTokens.EmberRed)
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    onSaveEquipmentItem(slot, null)
                    editingSlot = null
                }) {
                    Text("Unequip", color = VaultDesignTokens.DangerRed)
                }
            }
        )
    }
}

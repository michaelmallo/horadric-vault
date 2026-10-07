package com.horadricvault.feature.map

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Map
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
import com.horadricvault.feature.statcheck.VaultDesignTokens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SanctuaryMapScreen(
    nodes: List<MapNode>,
    completedNodeIds: Set<String>,
    completedQuests: Set<String>,
    onToggleNode: (MapNode) -> Unit,
    onToggleQuest: (RegionQuest) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedRegion by remember { mutableStateOf(SanctuaryRegion.FRACTURED_PEAKS) }
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Atlas, 1: Drop Quests

    var visibleTypes by remember {
        mutableStateOf(
            setOf(
                MapNodeType.ALTAR_OF_LILITH,
                MapNodeType.TENET_OF_AKARAT,
                MapNodeType.WAYPOINT,
                MapNodeType.STRONGHOLD,
                MapNodeType.DUNGEON
            )
        )
    }

    val regionNodes = remember(nodes, selectedRegion) {
        nodes.filter { it.region == selectedRegion }
    }

    // Pre-bundled drop trigger side quests per region
    val sampleQuests = remember(selectedRegion) {
        listOf(
            RegionQuest("q_1", selectedRegion, "Icebound Geode", QuestDropTrigger.ORE_VEIN, "Harvesting Iron Ore deposits"),
            RegionQuest("q_2", selectedRegion, "Chained Zakarum Curio", QuestDropTrigger.CORPSE, "Looting desecrated corpses in marshland"),
            RegionQuest("q_3", selectedRegion, "Gilded Chalice of the Cathedral", QuestDropTrigger.CHEST, "Opening Tortured Chests in Helltide"),
            RegionQuest("q_4", selectedRegion, "Hungering Bone Cache", QuestDropTrigger.ENEMY_DROP, "Killing Skeleton Ballistas and Knights"),
            RegionQuest("q_5", selectedRegion, "Exorcist's Cache", QuestDropTrigger.TOWN_NPC, "Speaking with Father Doryan in town")
        )
    }

    val completedCount = regionNodes.count { completedNodeIds.contains(it.nodeId) }
    val totalCount = regionNodes.size

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(VaultDesignTokens.AbyssalBlack)
            .padding(horizontal = 14.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "SANCTUARY ATLAS",
                    color = VaultDesignTokens.HellfireGold,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = "Offline Vector Cartography • Cloud Progress Sync",
                    color = VaultDesignTokens.DimParchment,
                    fontSize = 11.sp
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(VaultDesignTokens.SlateIron)
                    .border(1.dp, VaultDesignTokens.CharcoalBorder, RoundedCornerShape(6.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "$completedCount / $totalCount Nodes",
                    color = if (completedCount == totalCount && totalCount > 0) VaultDesignTokens.RunicTeal else VaultDesignTokens.HellfireGold,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Region Selection Horizontal Chips
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            SanctuaryRegion.entries.forEach { region ->
                FilterChip(
                    selected = selectedRegion == region,
                    onClick = { selectedRegion = region },
                    label = { Text(region.displayName, fontSize = 10.sp) }
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Tab Selector (Atlas vs Side Quests)
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = VaultDesignTokens.SlateIron,
            contentColor = VaultDesignTokens.HellfireGold
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("Interactive Atlas", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("Drop-Trigger Quests", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (selectedTab == 0) {
            // Filter Toggles Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                MapNodeType.entries.forEach { type ->
                    val isChecked = visibleTypes.contains(type)
                    FilterChip(
                        selected = isChecked,
                        onClick = {
                            visibleTypes = if (isChecked) visibleTypes - type else visibleTypes + type
                        },
                        label = {
                            Text(
                                text = type.displayName,
                                fontSize = 9.sp,
                                color = if (isChecked) Color.White else VaultDesignTokens.DimParchment
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Map Canvas
            SanctuaryMapCanvas(
                region = selectedRegion,
                nodes = regionNodes,
                completedNodeIds = completedNodeIds,
                visibleTypes = visibleTypes,
                onToggleNodeCompletion = onToggleNode,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )
            Spacer(modifier = Modifier.height(12.dp))
        } else {
            // Drop-Trigger Quest Checklist
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    Text(
                        text = "SIDE QUEST DROP TRIGGERS (${selectedRegion.displayName})",
                        color = VaultDesignTokens.DimParchment,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                items(sampleQuests) { quest ->
                    val isDone = completedQuests.contains(quest.questId)
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onToggleQuest(quest) },
                        colors = CardDefaults.cardColors(containerColor = VaultDesignTokens.SlateIron),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = quest.title,
                                    color = if (isDone) VaultDesignTokens.DimParchment else VaultDesignTokens.SanctuaryParchment,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Trigger: ${quest.triggerType.label} • ${quest.locationDescription}",
                                    color = VaultDesignTokens.HellfireGold,
                                    fontSize = 11.sp
                                )
                            }

                            Checkbox(
                                checked = isDone,
                                onCheckedChange = { onToggleQuest(quest) },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = VaultDesignTokens.RunicTeal,
                                    checkmarkColor = VaultDesignTokens.AbyssalBlack
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

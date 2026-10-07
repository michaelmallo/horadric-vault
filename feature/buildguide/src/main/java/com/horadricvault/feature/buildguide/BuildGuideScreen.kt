package com.horadricvault.feature.buildguide

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Download
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
fun BuildGuideScreen(
    builds: List<TargetBuild>,
    activeCharacter: D4Character?,
    onImportUrl: (String) -> Unit,
    onSelectActiveBuild: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedClassFilter by remember { mutableStateOf<CharacterClass?>(null) }
    var showImportDialog by remember { mutableStateOf(false) }
    var selectedBuildDetails by remember { mutableStateOf<TargetBuild?>(null) }

    val filteredBuilds = remember(builds, selectedClassFilter) {
        if (selectedClassFilter != null) {
            builds.filter { it.characterClass == selectedClassFilter }
        } else builds
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(VaultDesignTokens.AbyssalBlack)
            .padding(horizontal = 14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "BUILD VAULT & GUIDES",
                        color = VaultDesignTokens.HellfireGold,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = "Community Meta • Maxroll, D4Builds & Mobalytics",
                        color = VaultDesignTokens.DimParchment,
                        fontSize = 11.sp
                    )
                }

                Button(
                    onClick = { showImportDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = VaultDesignTokens.EmberRed),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Icon(imageVector = Icons.Default.Download, contentDescription = "Import", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Import URL", fontSize = 12.sp)
                }
            }
        }

        // Class Filter Chips Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                FilterChip(
                    selected = selectedClassFilter == null,
                    onClick = { selectedClassFilter = null },
                    label = { Text("All", fontSize = 11.sp) }
                )
                CharacterClass.entries.forEach { cls ->
                    FilterChip(
                        selected = selectedClassFilter == cls,
                        onClick = { selectedClassFilter = if (selectedClassFilter == cls) null else cls },
                        label = { Text(cls.displayName, fontSize = 11.sp) }
                    )
                }
            }
        }

        // Build List
        items(filteredBuilds) { build ->
            val isActive = activeCharacter?.activeTargetBuildId == build.buildId
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { selectedBuildDetails = build },
                colors = CardDefaults.cardColors(containerColor = VaultDesignTokens.SlateIron),
                shape = RoundedCornerShape(8.dp),
                border = if (isActive) CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(VaultDesignTokens.HellfireGold)) else null
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = build.characterClass.displayName,
                                color = VaultDesignTokens.DimParchment,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(VaultDesignTokens.RunicTeal.copy(alpha = 0.2f))
                                    .padding(horizontal = 5.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = build.tierRating,
                                    color = VaultDesignTokens.RunicTeal,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        if (isActive) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(3.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Check, contentDescription = "Active", tint = VaultDesignTokens.HellfireGold, modifier = Modifier.size(14.dp))
                                Text(text = "Active Goal", color = VaultDesignTokens.HellfireGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = build.buildName,
                        color = VaultDesignTokens.SanctuaryParchment,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // Skill Bar Preview
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        build.skillBar.take(4).forEach { skill ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(VaultDesignTokens.AbyssalBlack)
                                    .border(1.dp, VaultDesignTokens.CharcoalBorder, RoundedCornerShape(4.dp))
                                    .padding(horizontal = 6.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = skill,
                                    color = VaultDesignTokens.DimParchment,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
    }

    // Import URL Dialog
    if (showImportDialog) {
        var inputUrl by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showImportDialog = false },
            containerColor = VaultDesignTokens.SlateIron,
            title = {
                Text(text = "Import Build Guide", color = VaultDesignTokens.HellfireGold, fontWeight = FontWeight.Bold)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "Paste a URL from Maxroll.gg, D4Builds.gg, Mobalytics.gg, or Icy-Veins.com:",
                        color = VaultDesignTokens.DimParchment,
                        fontSize = 12.sp
                    )
                    OutlinedTextField(
                        value = inputUrl,
                        onValueChange = { inputUrl = it },
                        label = { Text("https://maxroll.gg/d4/build-guides/...") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = VaultDesignTokens.HellfireGold,
                            unfocusedBorderColor = VaultDesignTokens.CharcoalBorder
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (inputUrl.isNotBlank()) {
                            onImportUrl(inputUrl)
                            showImportDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = VaultDesignTokens.EmberRed)
                ) {
                    Text("Import")
                }
            },
            dismissButton = {
                TextButton(onClick = { showImportDialog = false }) {
                    Text("Cancel", color = VaultDesignTokens.DimParchment)
                }
            }
        )
    }

    // Build Details Sheet / Dialog
    selectedBuildDetails?.let { detail ->
        AlertDialog(
            onDismissRequest = { selectedBuildDetails = null },
            containerColor = VaultDesignTokens.SlateIron,
            title = {
                Text(text = detail.buildName, color = VaultDesignTokens.HellfireGold, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            },
            text = {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth().heightIn(max = 400.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        Text(
                            text = "SKILL BAR LOADOUT",
                            color = VaultDesignTokens.DimParchment,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            detail.skillBar.forEach { skill ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(VaultDesignTokens.AbyssalBlack)
                                        .padding(horizontal = 6.dp, vertical = 3.dp)
                                ) {
                                    Text(text = skill, color = VaultDesignTokens.SanctuaryParchment, fontSize = 10.sp)
                                }
                            }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "TARGET BI-S EQUIPMENT SLOTS",
                            color = VaultDesignTokens.DimParchment,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    items(detail.targetEquipmentSlots.values.toList()) { spec ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .background(VaultDesignTokens.AbyssalBlack)
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "[${spec.slot.displayName}] ${spec.targetItemName}",
                                    color = when (spec.rarity) {
                                        ItemRarity.MYTHIC_UNIQUE -> VaultDesignTokens.MythicViolet
                                        ItemRarity.UNIQUE -> VaultDesignTokens.UniqueBronze
                                        else -> VaultDesignTokens.HellfireGold
                                    },
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                spec.bossSource?.let {
                                    Text(text = "Boss Drop: $it", color = VaultDesignTokens.RunicTeal, fontSize = 10.sp)
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onSelectActiveBuild(detail.buildId)
                        selectedBuildDetails = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = VaultDesignTokens.EmberRed)
                ) {
                    Text("Set as Active Goal")
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedBuildDetails = null }) {
                    Text("Close", color = VaultDesignTokens.DimParchment)
                }
            }
        )
    }
}

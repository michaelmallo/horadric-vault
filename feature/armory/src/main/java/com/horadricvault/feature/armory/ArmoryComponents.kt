package com.horadricvault.feature.armory

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.horadricvault.core.model.*
import com.horadricvault.feature.statcheck.VaultDesignTokens

@Composable
fun GearSlotCard(
    slot: EquipmentSlot,
    item: EquipmentItem?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor = when (item?.rarity) {
        ItemRarity.MYTHIC_UNIQUE -> VaultDesignTokens.MythicViolet
        ItemRarity.UNIQUE -> VaultDesignTokens.UniqueBronze
        ItemRarity.LEGENDARY -> VaultDesignTokens.HellfireGold
        ItemRarity.RARE -> Color(0xFFFFD54F)
        ItemRarity.MAGIC -> Color(0xFF64B5F6)
        ItemRarity.NORMAL, null -> VaultDesignTokens.CharcoalBorder
    }

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(VaultDesignTokens.SlateIron)
            .border(1.dp, borderColor, RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = slot.displayName.uppercase(),
                color = VaultDesignTokens.DimParchment,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )

            if (item != null && item.greaterAffixCount > 0) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(VaultDesignTokens.EmberRed.copy(alpha = 0.2f))
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Greater Affix",
                        tint = VaultDesignTokens.CrimsonHighlight,
                        modifier = Modifier.size(10.dp)
                    )
                    Text(
                        text = "${item.greaterAffixCount} GA",
                        color = VaultDesignTokens.CrimsonHighlight,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        if (item != null) {
            Text(
                text = item.name,
                color = when (item.rarity) {
                    ItemRarity.MYTHIC_UNIQUE -> VaultDesignTokens.MythicViolet
                    ItemRarity.UNIQUE -> VaultDesignTokens.UniqueBronze
                    ItemRarity.LEGENDARY -> VaultDesignTokens.HellfireGold
                    else -> VaultDesignTokens.SanctuaryParchment
                },
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${item.itemPower} IP",
                    color = VaultDesignTokens.DimParchment,
                    fontSize = 11.sp
                )
                if (item.baseArmor > 0) {
                    Text(
                        text = "+${item.baseArmor} Armor",
                        color = VaultDesignTokens.RunicTeal,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            if (item.masterworkLevel > 0) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "MW: ${item.masterworkLevel}/12",
                    color = if (item.masterworkLevel == 12) VaultDesignTokens.RunicTeal else VaultDesignTokens.HellfireGold,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            val aspect = item.aspect
            if (aspect != null) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = aspect.name,
                    color = VaultDesignTokens.HellfireGold,
                    fontSize = 11.sp,
                    maxLines = 1
                )
            }
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Equip Item",
                    tint = VaultDesignTokens.DimParchment,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Empty Slot",
                    color = VaultDesignTokens.DimParchment,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
fun DeltaRoadmapCard(
    delta: ProgressionDelta,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = VaultDesignTokens.SlateIron),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "PROGRESSION ROADMAP DELTA",
                    color = VaultDesignTokens.HellfireGold,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${delta.completionPercentage}% BiS Matching",
                    color = if (delta.completionPercentage >= 90) VaultDesignTokens.RunicTeal else VaultDesignTokens.OvercapAmber,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Points Delta
            if (delta.skillPointsDelta > 0 || delta.paragonPointsDelta > 0) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(VaultDesignTokens.AbyssalBlack)
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    if (delta.skillPointsDelta > 0) {
                        Text(
                            text = "+${delta.skillPointsDelta} Skill Points Needed",
                            color = VaultDesignTokens.SanctuaryParchment,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    if (delta.paragonPointsDelta > 0) {
                        Text(
                            text = "+${delta.paragonPointsDelta} Paragon Points to Cap",
                            color = VaultDesignTokens.HellfireGold,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Target Boss Farming Locator
            if (delta.targetBossFarmingList.isNotEmpty()) {
                Text(
                    text = "TARGET BOSS & FARMING LOCATOR",
                    color = VaultDesignTokens.SanctuaryParchment,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                delta.targetBossFarmingList.forEach { target ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(VaultDesignTokens.AbyssalBlack)
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "${target.targetItemName} ➔ ${target.bossName}",
                                color = if (target.isMythic) VaultDesignTokens.MythicViolet else VaultDesignTokens.UniqueBronze,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Location: ${target.location} | Mats: ${target.requiredMaterials}",
                                color = VaultDesignTokens.DimParchment,
                                fontSize = 10.sp
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Affix Reroll Priorities
            if (delta.affixRerollPriorities.isNotEmpty()) {
                Text(
                    text = "OCCULTIST AFFIX RE-ROLL PRIORITIES",
                    color = VaultDesignTokens.SanctuaryParchment,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                delta.affixRerollPriorities.forEach { reroll ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(VaultDesignTokens.AbyssalBlack)
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Dead Affix",
                            tint = VaultDesignTokens.OvercapAmber,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Text(
                                text = "[${reroll.slot.displayName}] Replace: ${reroll.currentDeadAffix}",
                                color = VaultDesignTokens.OvercapAmber,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Optimal Target: ${reroll.recommendedRerollTarget} (${reroll.occultistCostPriority})",
                                color = VaultDesignTokens.RunicTeal,
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

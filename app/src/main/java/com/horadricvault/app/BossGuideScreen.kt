package com.horadricvault.app

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.horadricvault.core.model.EndgameBoss
import com.horadricvault.core.model.ItemRarity
import com.horadricvault.feature.statcheck.VaultDesignTokens

@Composable
fun BossGuideScreen(
    bosses: List<EndgameBoss>,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(VaultDesignTokens.AbyssalBlack)
            .padding(horizontal = 14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Column {
                Text(
                    text = "ENDGAME BOSS LADDER",
                    color = VaultDesignTokens.HellfireGold,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = "Tormented Summoning Costs & Target Unique Drop Tables",
                    color = VaultDesignTokens.DimParchment,
                    fontSize = 11.sp
                )
            }
        }

        items(bosses) { boss ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = VaultDesignTokens.SlateIron),
                shape = RoundedCornerShape(8.dp),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = androidx.compose.ui.graphics.SolidColor(VaultDesignTokens.CharcoalBorder)
                )
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = boss.name,
                                color = VaultDesignTokens.HellfireGold,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${boss.title} • ${boss.dungeonLocation} (${boss.region.displayName})",
                                color = VaultDesignTokens.DimParchment,
                                fontSize = 11.sp
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(VaultDesignTokens.EmberRed.copy(alpha = 0.2f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "Torment I-IV",
                                color = VaultDesignTokens.CrimsonHighlight,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Summoning Mats
                    Text(
                        text = "SUMMONING MATERIALS",
                        color = VaultDesignTokens.SanctuaryParchment,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    boss.materials.forEach { mat ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(VaultDesignTokens.AbyssalBlack)
                                .padding(6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "${mat.materialName} (Normal: x${mat.standardAmount} | Tormented: x${mat.tormentedAmount})",
                                color = VaultDesignTokens.SanctuaryParchment,
                                fontSize = 11.sp
                            )
                            Text(
                                text = mat.farmSourceDescription,
                                color = VaultDesignTokens.DimParchment,
                                fontSize = 10.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Target Drops
                    Text(
                        text = "EXCLUSIVE TARGET DROPS",
                        color = VaultDesignTokens.SanctuaryParchment,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    boss.targetDrops.forEach { drop ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(VaultDesignTokens.AbyssalBlack)
                                .padding(6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "[${drop.itemSlot.displayName}] ${drop.itemName}",
                                color = if (drop.isMythic) VaultDesignTokens.MythicViolet else VaultDesignTokens.UniqueBronze,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                            if (drop.isMythic) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(3.dp))
                                        .background(VaultDesignTokens.MythicViolet.copy(alpha = 0.2f))
                                        .padding(horizontal = 4.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "Mythic Unique",
                                        color = VaultDesignTokens.MythicViolet,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
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
}

package com.horadricvault.feature.statcheck

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.horadricvault.core.model.ArmorStatBreakdown
import com.horadricvault.core.model.CapStatus
import com.horadricvault.core.model.ResistanceStat

@Composable
fun VaultStatBadge(
    status: CapStatus,
    label: String,
    modifier: Modifier = Modifier
) {
    val (backgroundColor, borderColor, textColor) = when (status) {
        CapStatus.OPTIMAL -> Triple(
            VaultDesignTokens.RunicTeal.copy(alpha = 0.15f),
            VaultDesignTokens.RunicTeal,
            VaultDesignTokens.RunicTeal
        )
        CapStatus.OVERCAPPED -> Triple(
            VaultDesignTokens.OvercapAmber.copy(alpha = 0.15f),
            VaultDesignTokens.OvercapAmber,
            VaultDesignTokens.OvercapAmber
        )
        CapStatus.UNDER_CAP -> Triple(
            VaultDesignTokens.CharcoalBorder,
            VaultDesignTokens.DimParchment.copy(alpha = 0.3f),
            VaultDesignTokens.SanctuaryParchment
        )
    }

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(backgroundColor)
            .border(1.dp, borderColor, RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        when (status) {
            CapStatus.OPTIMAL -> {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Optimal",
                    tint = VaultDesignTokens.RunicTeal,
                    modifier = Modifier.size(14.dp)
                )
            }
            CapStatus.OVERCAPPED -> {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "Overcap",
                    tint = VaultDesignTokens.OvercapAmber,
                    modifier = Modifier.size(14.dp)
                )
            }
            CapStatus.UNDER_CAP -> Unit
        }

        Text(
            text = label,
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            fontFamily = FontFamily.SansSerif
        )
    }
}

@Composable
fun ArmorCapMeter(
    armor: ArmorStatBreakdown,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(VaultDesignTokens.SlateIron)
            .border(1.dp, VaultDesignTokens.CharcoalBorder, RoundedCornerShape(8.dp))
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "ARMOR RATING",
                    color = VaultDesignTokens.SanctuaryParchment,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${armor.grossArmor} Gross (Penalty: -${armor.tormentPenalty} ➔ ${armor.effectiveArmor} Effective)",
                    color = VaultDesignTokens.DimParchment,
                    fontSize = 11.sp
                )
            }

            when (armor.status) {
                CapStatus.OPTIMAL -> {
                    VaultStatBadge(
                        status = CapStatus.OPTIMAL,
                        label = "Capped (1,000 Effective)"
                    )
                }
                CapStatus.OVERCAPPED -> {
                    VaultStatBadge(
                        status = CapStatus.OVERCAPPED,
                        label = "+${armor.overcapAmount} Over Cap — Stats Can Be Reallocated"
                    )
                }
                CapStatus.UNDER_CAP -> {
                    VaultStatBadge(
                        status = CapStatus.UNDER_CAP,
                        label = "Needs +${armor.remainingRequired} Armor"
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        val progress = if (armor.capTargetGross > 0) {
            (armor.grossArmor.toFloat() / armor.capTargetGross.toFloat()).coerceIn(0f, 1f)
        } else 1f

        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = when (armor.status) {
                CapStatus.OPTIMAL -> VaultDesignTokens.RunicTeal
                CapStatus.OVERCAPPED -> VaultDesignTokens.OvercapAmber
                CapStatus.UNDER_CAP -> VaultDesignTokens.EmberRed
            },
            trackColor = VaultDesignTokens.CharcoalBorder
        )
    }
}

@Composable
fun ResistanceRow(
    res: ResistanceStat,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(VaultDesignTokens.SlateIron)
            .padding(horizontal = 10.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(RoundedCornerShape(5.dp))
                    .background(Color(res.element.hexColor))
            )
            Text(
                text = "${res.element.displayName} Res",
                color = VaultDesignTokens.SanctuaryParchment,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = "${res.effectiveResistance.toInt()}% / ${res.hardCap.toInt()}%",
                color = VaultDesignTokens.SanctuaryParchment,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )

            when (res.status) {
                CapStatus.OPTIMAL -> {
                    VaultStatBadge(status = CapStatus.OPTIMAL, label = "Optimal")
                }
                CapStatus.OVERCAPPED -> {
                    VaultStatBadge(
                        status = CapStatus.OVERCAPPED,
                        label = "+${res.overcapAmount.toInt()}% Overcap"
                    )
                }
                CapStatus.UNDER_CAP -> {
                    VaultStatBadge(
                        status = CapStatus.UNDER_CAP,
                        label = "-${(res.hardCap - res.effectiveResistance).toInt()}%"
                    )
                }
            }
        }
    }
}

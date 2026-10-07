package com.horadricvault.app

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Link
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.horadricvault.core.auth.AuthState
import com.horadricvault.core.model.Realm
import com.horadricvault.core.model.UserSettings
import com.horadricvault.feature.statcheck.VaultDesignTokens

@Composable
fun SettingsScreen(
    authState: AuthState,
    settings: UserSettings,
    onLinkGoogleAccount: () -> Unit,
    onUpdateSettings: (UserSettings) -> Unit,
    onSignOut: () -> Unit,
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
                    text = "VAULT PREFERENCES",
                    color = VaultDesignTokens.HellfireGold,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = "Account Session & Sanctuary Settings",
                    color = VaultDesignTokens.DimParchment,
                    fontSize = 11.sp
                )
            }
        }

        // Account Profile Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = VaultDesignTokens.SlateIron),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "ACCOUNT IDENTITY",
                        color = VaultDesignTokens.DimParchment,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    when (authState) {
                        is AuthState.Authenticated -> {
                            Text(
                                text = authState.displayName ?: "Nephalem",
                                color = VaultDesignTokens.SanctuaryParchment,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = authState.email ?: "Google Authenticated",
                                color = VaultDesignTokens.DimParchment,
                                fontSize = 12.sp
                            )
                            Text(
                                text = "UID: ${authState.userId}",
                                color = VaultDesignTokens.DimParchment.copy(alpha = 0.6f),
                                fontSize = 10.sp
                            )
                        }
                        is AuthState.Anonymous -> {
                            Text(
                                text = "Guest Nephalem (Anonymous)",
                                color = VaultDesignTokens.OvercapAmber,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "UID: ${authState.userId}",
                                color = VaultDesignTokens.DimParchment,
                                fontSize = 11.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = onLinkGoogleAccount,
                                colors = ButtonDefaults.buttonColors(containerColor = VaultDesignTokens.HellfireGold),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Link, contentDescription = "Link", tint = VaultDesignTokens.AbyssalBlack)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Link with Google Account", color = VaultDesignTokens.AbyssalBlack, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                        else -> Unit
                    }
                }
            }
        }

        // Gameplay Defaults Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = VaultDesignTokens.SlateIron),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "GAMEPLAY DEFAULTS",
                        color = VaultDesignTokens.DimParchment,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )

                    // Default Realm
                    Text(text = "Default Realm:", color = VaultDesignTokens.SanctuaryParchment, fontSize = 12.sp)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Realm.entries.forEach { realm ->
                            FilterChip(
                                selected = settings.defaultRealm == realm,
                                onClick = { onUpdateSettings(settings.copy(defaultRealm = realm)) },
                                label = { Text(realm.displayName, fontSize = 10.sp) }
                            )
                        }
                    }

                    // Stat Glow Warnings
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "Enable Stat Cap Glow Badges", color = VaultDesignTokens.SanctuaryParchment, fontSize = 12.sp)
                            Text(text = "Shows inline amber warning badges for overcapped armor/res", color = VaultDesignTokens.DimParchment, fontSize = 10.sp)
                        }
                        Switch(
                            checked = settings.enableCapGlowWarnings,
                            onCheckedChange = { onUpdateSettings(settings.copy(enableCapGlowWarnings = it)) },
                            colors = SwitchDefaults.colors(checkedThumbColor = VaultDesignTokens.RunicTeal)
                        )
                    }

                    // Auto Sync
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "Auto-sync Builds", color = VaultDesignTokens.SanctuaryParchment, fontSize = 12.sp)
                            Text(text = "Continuously stream Firestore writes offline and online", color = VaultDesignTokens.DimParchment, fontSize = 10.sp)
                        }
                        Switch(
                            checked = settings.autoSyncBuilds,
                            onCheckedChange = { onUpdateSettings(settings.copy(autoSyncBuilds = it)) },
                            colors = SwitchDefaults.colors(checkedThumbColor = VaultDesignTokens.RunicTeal)
                        )
                    }
                }
            }
        }

        // Sign Out Button
        item {
            Button(
                onClick = onSignOut,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = VaultDesignTokens.SlateIron),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    brush = androidx.compose.ui.graphics.SolidColor(VaultDesignTokens.DangerRed)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(imageVector = Icons.Default.ExitToApp, contentDescription = "Sign Out", tint = VaultDesignTokens.DangerRed)
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "Sign Out of Sanctuary", color = VaultDesignTokens.DangerRed, fontWeight = FontWeight.Bold)
            }
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

package com.horadricvault.app

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.horadricvault.feature.statcheck.VaultDesignTokens

@Composable
fun LoginScreen(
    onGoogleSignIn: () -> Unit,
    onGuestSignIn: () -> Unit,
    isLoading: Boolean,
    errorMessage: String?,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(VaultDesignTokens.AbyssalBlack)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Sanctuary Icon & Title
            Icon(
                imageVector = Icons.Default.Shield,
                contentDescription = "Horadric Crest",
                tint = VaultDesignTokens.HellfireGold,
                modifier = Modifier.size(64.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "HORADRIC VAULT",
                color = VaultDesignTokens.HellfireGold,
                fontSize = 26.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp
            )

            Text(
                text = "Diablo IV Companion • Vessel of Hatred",
                color = VaultDesignTokens.DimParchment,
                fontSize = 12.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(36.dp))

            // Welcome Box
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = VaultDesignTokens.SlateIron),
                shape = RoundedCornerShape(10.dp),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = androidx.compose.ui.graphics.SolidColor(VaultDesignTokens.CharcoalBorder)
                )
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Access Your Armory",
                        color = VaultDesignTokens.SanctuaryParchment,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Sign in to synchronize your character builds, target farm tables, and Sanctuary atlas progress with Cloud Firestore.",
                        color = VaultDesignTokens.DimParchment,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    if (errorMessage != null) {
                        Text(
                            text = errorMessage,
                            color = VaultDesignTokens.DangerRed,
                            fontSize = 11.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .background(VaultDesignTokens.DangerRed.copy(alpha = 0.15f))
                                .padding(8.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    if (isLoading) {
                        CircularProgressIndicator(
                            color = VaultDesignTokens.HellfireGold,
                            modifier = Modifier.size(32.dp)
                        )
                    } else {
                        // Google SSO Button
                        Button(
                            onClick = onGoogleSignIn,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = VaultDesignTokens.EmberRed),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = "Google SSO",
                                tint = VaultDesignTokens.SanctuaryParchment
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Sign In with Google",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Guest Mode Button
                        OutlinedButton(
                            onClick = onGuestSignIn,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp),
                            shape = RoundedCornerShape(8.dp),
                            border = ButtonDefaults.outlinedButtonBorder.copy(
                                brush = androidx.compose.ui.graphics.SolidColor(VaultDesignTokens.HellfireGold)
                            )
                        ) {
                            Text(
                                text = "Enter Sanctuary as Guest",
                                color = VaultDesignTokens.HellfireGold,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

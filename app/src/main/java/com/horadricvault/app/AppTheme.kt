package com.horadricvault.app

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import com.horadricvault.feature.statcheck.VaultDesignTokens

private val DarkFantasyColorScheme = darkColorScheme(
    primary = VaultDesignTokens.EmberRed,
    onPrimary = VaultDesignTokens.SanctuaryParchment,
    primaryContainer = VaultDesignTokens.SlateIron,
    onPrimaryContainer = VaultDesignTokens.HellfireGold,
    secondary = VaultDesignTokens.HellfireGold,
    onSecondary = VaultDesignTokens.AbyssalBlack,
    background = VaultDesignTokens.AbyssalBlack,
    onBackground = VaultDesignTokens.SanctuaryParchment,
    surface = VaultDesignTokens.SlateIron,
    onSurface = VaultDesignTokens.SanctuaryParchment,
    surfaceVariant = VaultDesignTokens.CharcoalBorder,
    onSurfaceVariant = VaultDesignTokens.DimParchment,
    error = VaultDesignTokens.DangerRed,
    onError = VaultDesignTokens.AbyssalBlack
)

@Composable
fun HoradricVaultTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkFantasyColorScheme,
        content = content
    )
}

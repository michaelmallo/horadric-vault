package com.horadricvault.app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.horadricvault.core.auth.AuthState
import com.horadricvault.core.model.*
import com.horadricvault.feature.armory.ArmoryScreen
import com.horadricvault.feature.buildguide.BuildGuideScreen
import com.horadricvault.feature.map.SanctuaryMapScreen
import com.horadricvault.feature.statcheck.VaultDesignTokens
import kotlinx.coroutines.launch

enum class AppDestination(val title: String, val icon: ImageVector) {
    ARMORY("Armory", Icons.Default.Shield),
    BUILDS("Builds", Icons.Default.AutoAwesome),
    MAP("Atlas", Icons.Default.Map),
    BOSSES("Bosses", Icons.Default.Whatshot),
    SETTINGS("Settings", Icons.Default.Settings)
}

@Composable
fun MainAppShell(
    userId: String,
    authState: AuthState,
    container: AppContainer,
    onSignOut: () -> Unit,
    onLinkGoogleAccount: () -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    var currentDestination by remember { mutableStateOf(AppDestination.ARMORY) }

    // State from repositories
    val characters by container.characterRepository.getCharactersStream(userId).collectAsState(initial = emptyList())
    var selectedCharacterId by remember { mutableStateOf<String?>(null) }

    val activeCharacter = remember(characters, selectedCharacterId) {
        if (selectedCharacterId != null) {
            characters.find { it.characterId == selectedCharacterId }
        } else {
            characters.firstOrNull()
        }
    }

    val userBuilds by container.buildRepository.getBuildsStream(userId).collectAsState(initial = emptyList())
    var preloadedBuilds by remember { mutableStateOf<List<TargetBuild>>(emptyList()) }
    var allBosses by remember { mutableStateOf<List<EndgameBoss>>(emptyList()) }
    var allMapNodes by remember { mutableStateOf<List<MapNode>>(emptyList()) }
    val userSettings by container.settingsRepository.getUserSettingsStream(userId).collectAsState(initial = UserSettings())
    val allRegionProgress by container.mapProgressRepository.getAllRegionsProgressStream(userId).collectAsState(initial = emptyList())

    // Load static game definitions once
    LaunchedEffect(Unit) {
        preloadedBuilds = container.buildImportService.getPreloadedBuilds()
        allBosses = container.gameDefinitionsRepository.getAllBosses()
        val allNodesList = mutableListOf<MapNode>()
        SanctuaryRegion.entries.forEach { reg ->
            allNodesList.addAll(container.gameDefinitionsRepository.getMapNodes(reg))
        }
        allMapNodes = allNodesList
    }

    val combinedBuilds = remember(userBuilds, preloadedBuilds) {
        (userBuilds + preloadedBuilds).distinctBy { it.buildId }
    }

    val completedNodeIds = remember(allRegionProgress) {
        allRegionProgress.flatMap { it.completedNodeIds }.toSet()
    }

    val completedQuests = remember(allRegionProgress) {
        allRegionProgress.flatMap { it.completedQuests }.toSet()
    }

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = VaultDesignTokens.SlateIron,
                contentColor = VaultDesignTokens.HellfireGold
            ) {
                AppDestination.entries.forEach { dest ->
                    val isSelected = currentDestination == dest
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { currentDestination = dest },
                        icon = {
                            Icon(
                                imageVector = dest.icon,
                                contentDescription = dest.title,
                                tint = if (isSelected) VaultDesignTokens.HellfireGold else VaultDesignTokens.DimParchment
                            )
                        },
                        label = {
                            Text(
                                text = dest.title,
                                color = if (isSelected) VaultDesignTokens.HellfireGold else VaultDesignTokens.DimParchment,
                                fontSize = 10.sp
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = VaultDesignTokens.EmberRed.copy(alpha = 0.3f)
                        )
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(VaultDesignTokens.AbyssalBlack)
        ) {
            when (currentDestination) {
                AppDestination.ARMORY -> {
                    ArmoryScreen(
                        characters = characters,
                        activeCharacter = activeCharacter,
                        targetBuilds = combinedBuilds,
                        allBosses = allBosses,
                        onSelectCharacter = { selectedCharacterId = it.characterId },
                        onCreateCharacter = { name, cls, realm, tier ->
                            coroutineScope.launch {
                                val newChar = D4Character(
                                    characterName = name,
                                    characterClass = cls,
                                    realm = realm,
                                    activeTormentTier = tier,
                                    activeTargetBuildId = combinedBuilds.firstOrNull { it.characterClass == cls }?.buildId
                                )
                                container.characterRepository.upsertCharacter(userId, newChar)
                            }
                        },
                        onUpdateTormentTier = { newTier ->
                            if (activeCharacter != null) {
                                coroutineScope.launch {
                                    container.characterRepository.upsertCharacter(
                                        userId,
                                        activeCharacter.copy(activeTormentTier = newTier)
                                    )
                                }
                            }
                        },
                        onLinkTargetBuild = { buildId ->
                            if (activeCharacter != null) {
                                coroutineScope.launch {
                                    container.characterRepository.upsertCharacter(
                                        userId,
                                        activeCharacter.copy(activeTargetBuildId = buildId)
                                    )
                                }
                            }
                        },
                        onSaveEquipmentItem = { slot, item ->
                            if (activeCharacter != null) {
                                coroutineScope.launch {
                                    container.characterRepository.updateEquipmentSlot(
                                        userId,
                                        activeCharacter.characterId,
                                        slot,
                                        item
                                    )
                                }
                            }
                        }
                    )
                }

                AppDestination.BUILDS -> {
                    BuildGuideScreen(
                        builds = combinedBuilds,
                        activeCharacter = activeCharacter,
                        onImportUrl = { url ->
                            coroutineScope.launch {
                                val imported = container.buildImportService.importBuildFromUrl(url).getOrNull()
                                if (imported != null) {
                                    container.buildRepository.upsertBuild(userId, imported)
                                }
                            }
                        },
                        onSelectActiveBuild = { buildId ->
                            if (activeCharacter != null) {
                                coroutineScope.launch {
                                    container.characterRepository.upsertCharacter(
                                        userId,
                                        activeCharacter.copy(activeTargetBuildId = buildId)
                                    )
                                }
                            }
                        }
                    )
                }

                AppDestination.MAP -> {
                    SanctuaryMapScreen(
                        nodes = allMapNodes,
                        completedNodeIds = completedNodeIds,
                        completedQuests = completedQuests,
                        onToggleNode = { node ->
                            coroutineScope.launch {
                                container.mapProgressRepository.toggleNodeCompletion(userId, node.region, node.nodeId)
                            }
                        },
                        onToggleQuest = { quest ->
                            coroutineScope.launch {
                                container.mapProgressRepository.toggleQuestCompletion(userId, quest.region, quest.questId)
                            }
                        }
                    )
                }

                AppDestination.BOSSES -> {
                    BossGuideScreen(bosses = allBosses)
                }

                AppDestination.SETTINGS -> {
                    SettingsScreen(
                        authState = authState,
                        settings = userSettings,
                        onLinkGoogleAccount = onLinkGoogleAccount,
                        onUpdateSettings = { newSettings ->
                            coroutineScope.launch {
                                container.settingsRepository.saveUserSettings(userId, newSettings)
                            }
                        },
                        onSignOut = onSignOut
                    )
                }
            }
        }
    }
}

package com.horadricvault.core.network

import com.horadricvault.core.model.*
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import org.jsoup.Jsoup
import java.util.UUID

interface BuildImportService {
    suspend fun importBuildFromUrl(url: String): Result<TargetBuild>
    suspend fun getPreloadedBuilds(characterClass: CharacterClass? = null): List<TargetBuild>
}

class BuildScraperService(
    private val client: HttpClient = HttpClient(OkHttp) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }
    }
) : BuildImportService {

    override suspend fun getPreloadedBuilds(characterClass: CharacterClass?): List<TargetBuild> {
        return if (characterClass != null) {
            DefaultBuildCatalog.preloadedBuilds.filter { it.characterClass == characterClass }
        } else {
            DefaultBuildCatalog.preloadedBuilds
        }
    }

    override suspend fun importBuildFromUrl(url: String): Result<TargetBuild> = withContext(Dispatchers.IO) {
        runCatching {
            val normalizedUrl = url.trim()
            val responseText = try {
                client.get(normalizedUrl).bodyAsText()
            } catch (e: Exception) {
                // If network fetch fails (e.g. offline mode or cors), fallback to URL heuristics
                ""
            }

            when {
                normalizedUrl.contains("maxroll.gg") -> parseMaxroll(normalizedUrl, responseText)
                normalizedUrl.contains("d4builds.gg") -> parseD4Builds(normalizedUrl, responseText)
                normalizedUrl.contains("mobalytics.gg") -> parseMobalytics(normalizedUrl, responseText)
                normalizedUrl.contains("icy-veins.com") -> parseIcyVeins(normalizedUrl, responseText)
                else -> createGenericBuildFromUrl(normalizedUrl, responseText)
            }
        }
    }

    private fun parseMaxroll(url: String, html: String): TargetBuild {
        val title = if (html.isNotBlank()) {
            val doc = Jsoup.parse(html)
            doc.select("h1, meta[property='og:title']").first()?.text()
                ?: doc.title()
        } else "Maxroll Imported Build"

        val detectedClass = detectClassFromText(url + " " + title)

        // Find matches from default catalog if available or construct target build
        val catalogMatch = DefaultBuildCatalog.preloadedBuilds.find { it.characterClass == detectedClass }
        return catalogMatch?.copy(
            buildId = UUID.randomUUID().toString(),
            buildName = cleanTitle(title),
            sourceUrl = url,
            sourcePlatform = "Maxroll"
        ) ?: TargetBuild(
            buildId = UUID.randomUUID().toString(),
            buildName = cleanTitle(title),
            sourceUrl = url,
            sourcePlatform = "Maxroll",
            characterClass = detectedClass,
            skillBar = listOf("Core Skill", "Defensive Skill", "Mobility Skill", "Ultimate Skill"),
            targetEquipmentSlots = createDefaultEquipmentSlots(detectedClass)
        )
    }

    private fun parseD4Builds(url: String, html: String): TargetBuild {
        val title = if (html.isNotBlank()) {
            val doc = Jsoup.parse(html)
            doc.select("h1, .build-title, meta[property='og:title']").first()?.text() ?: doc.title()
        } else "D4Builds Guide"

        val detectedClass = detectClassFromText(url + " " + title)
        val catalogMatch = DefaultBuildCatalog.preloadedBuilds.find { it.characterClass == detectedClass }

        return catalogMatch?.copy(
            buildId = UUID.randomUUID().toString(),
            buildName = cleanTitle(title),
            sourceUrl = url,
            sourcePlatform = "D4Builds"
        ) ?: TargetBuild(
            buildId = UUID.randomUUID().toString(),
            buildName = cleanTitle(title),
            sourceUrl = url,
            sourcePlatform = "D4Builds",
            characterClass = detectedClass,
            skillBar = listOf("Core Skill", "Defensive Skill", "Mobility Skill", "Ultimate"),
            targetEquipmentSlots = createDefaultEquipmentSlots(detectedClass)
        )
    }

    private fun parseMobalytics(url: String, html: String): TargetBuild {
        val title = if (html.isNotBlank()) {
            val doc = Jsoup.parse(html)
            doc.select("h1, meta[property='og:title']").first()?.text() ?: doc.title()
        } else "Mobalytics Sanctuary Build"

        val detectedClass = detectClassFromText(url + " " + title)
        val catalogMatch = DefaultBuildCatalog.preloadedBuilds.find { it.characterClass == detectedClass }

        return catalogMatch?.copy(
            buildId = UUID.randomUUID().toString(),
            buildName = cleanTitle(title),
            sourceUrl = url,
            sourcePlatform = "Mobalytics"
        ) ?: TargetBuild(
            buildId = UUID.randomUUID().toString(),
            buildName = cleanTitle(title),
            sourceUrl = url,
            sourcePlatform = "Mobalytics",
            characterClass = detectedClass,
            skillBar = listOf("Core", "Focus", "Defensive", "Ultimate"),
            targetEquipmentSlots = createDefaultEquipmentSlots(detectedClass)
        )
    }

    private fun parseIcyVeins(url: String, html: String): TargetBuild {
        val title = if (html.isNotBlank()) {
            val doc = Jsoup.parse(html)
            doc.select("h1.page_title, meta[property='og:title']").first()?.text() ?: doc.title()
        } else "Icy-Veins Endgame Build"

        val detectedClass = detectClassFromText(url + " " + title)
        val catalogMatch = DefaultBuildCatalog.preloadedBuilds.find { it.characterClass == detectedClass }

        return catalogMatch?.copy(
            buildId = UUID.randomUUID().toString(),
            buildName = cleanTitle(title),
            sourceUrl = url,
            sourcePlatform = "Icy-Veins"
        ) ?: TargetBuild(
            buildId = UUID.randomUUID().toString(),
            buildName = cleanTitle(title),
            sourceUrl = url,
            sourcePlatform = "Icy-Veins",
            characterClass = detectedClass,
            targetEquipmentSlots = createDefaultEquipmentSlots(detectedClass)
        )
    }

    private fun createGenericBuildFromUrl(url: String, html: String): TargetBuild {
        val title = if (html.isNotBlank()) {
            Jsoup.parse(html).title()
        } else "Custom Imported Build"

        val detectedClass = detectClassFromText(url + " " + title)
        return TargetBuild(
            buildId = UUID.randomUUID().toString(),
            buildName = cleanTitle(title),
            sourceUrl = url,
            sourcePlatform = "Web",
            characterClass = detectedClass,
            targetEquipmentSlots = createDefaultEquipmentSlots(detectedClass)
        )
    }

    private fun detectClassFromText(text: String): CharacterClass {
        val lower = text.lowercase()
        return when {
            lower.contains("spiritborn") || lower.contains("quill") || lower.contains("stinger") -> CharacterClass.SPIRITBORN
            lower.contains("sorcerer") || lower.contains("sorc") || lower.contains("lightning spear") -> CharacterClass.SORCERER
            lower.contains("barbarian") || lower.contains("barb") || lower.contains("whirlwind") -> CharacterClass.BARBARIAN
            lower.contains("necromancer") || lower.contains("necro") || lower.contains("bone spear") -> CharacterClass.NECROMANCER
            lower.contains("rogue") || lower.contains("heartseeker") -> CharacterClass.ROGUE
            lower.contains("druid") || lower.contains("landslide") -> CharacterClass.DRUID
            else -> CharacterClass.SPIRITBORN
        }
    }

    private fun cleanTitle(title: String): String {
        return title.replace(Regex("(?i) - (Maxroll\\.gg|D4Builds|Mobalytics|Icy-Veins).*"), "").trim()
    }

    private fun createDefaultEquipmentSlots(characterClass: CharacterClass): Map<String, TargetItemSpec> {
        return mapOf(
            EquipmentSlot.HELM.name to TargetItemSpec(
                slot = EquipmentSlot.HELM,
                targetItemName = "Harlequin Crest",
                rarity = ItemRarity.MYTHIC_UNIQUE,
                isMythicUnique = true,
                bossSource = "Tormented Duriel / Andariel"
            ),
            EquipmentSlot.CHEST.name to TargetItemSpec(
                slot = EquipmentSlot.CHEST,
                targetItemName = "Tyrael's Might",
                rarity = ItemRarity.MYTHIC_UNIQUE,
                isMythicUnique = true,
                bossSource = "Tormented Duriel / Andariel"
            ),
            EquipmentSlot.GLOVES.name to TargetItemSpec(
                slot = EquipmentSlot.GLOVES,
                targetItemName = "Fists of Fate",
                rarity = ItemRarity.UNIQUE,
                bossSource = "The Beast in the Ice"
            )
        )
    }
}

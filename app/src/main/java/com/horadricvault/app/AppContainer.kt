package com.horadricvault.app

import android.content.Context
import com.horadricvault.core.auth.AuthRepository
import com.horadricvault.core.auth.FirebaseAuthRepository
import com.horadricvault.core.auth.GoogleAuthClient
import com.horadricvault.core.database.GameDefinitionsRepository
import com.horadricvault.core.database.SqliteGameDefinitionsRepository
import com.horadricvault.core.firestore.*
import com.horadricvault.core.network.BuildImportService
import com.horadricvault.core.network.BuildScraperService

class AppContainer(private val context: Context) {

    val googleAuthClient: GoogleAuthClient by lazy {
        GoogleAuthClient()
    }

    val authRepository: AuthRepository by lazy {
        FirebaseAuthRepository()
    }

    val characterRepository: CharacterRepository by lazy {
        FirestoreCharacterRepository()
    }

    val settingsRepository: SettingsRepository by lazy {
        FirestoreSettingsRepository()
    }

    val buildRepository: BuildRepository by lazy {
        FirestoreBuildRepository()
    }

    val mapProgressRepository: MapProgressRepository by lazy {
        FirestoreMapProgressRepository()
    }

    val gameDefinitionsRepository: GameDefinitionsRepository by lazy {
        SqliteGameDefinitionsRepository(context)
    }

    val buildImportService: BuildImportService by lazy {
        BuildScraperService()
    }
}

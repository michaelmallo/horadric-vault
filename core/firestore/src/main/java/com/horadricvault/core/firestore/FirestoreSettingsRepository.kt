package com.horadricvault.core.firestore

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.MetadataChanges
import com.google.firebase.firestore.SetOptions
import com.horadricvault.core.model.UserProfile
import com.horadricvault.core.model.UserSettings
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

interface SettingsRepository {
    fun getUserSettingsStream(userId: String): Flow<UserSettings>
    suspend fun saveUserSettings(userId: String, settings: UserSettings): Result<Unit>
    suspend fun syncUserProfile(userId: String, displayName: String, email: String): Result<Unit>
    fun getUserProfileStream(userId: String): Flow<UserProfile?>
}

class FirestoreSettingsRepository(
    private val firestore: FirebaseFirestore = FirestoreClient.instance
) : SettingsRepository {

    private fun userDocument(userId: String) =
        firestore.collection("users").document(userId)

    private fun preferencesDocument(userId: String) =
        userDocument(userId).collection("settings").document("preferences")

    override fun getUserSettingsStream(userId: String): Flow<UserSettings> = callbackFlow {
        if (userId.isBlank()) {
            trySend(UserSettings())
            close()
            return@callbackFlow
        }

        val registration = preferencesDocument(userId)
            .addSnapshotListener(MetadataChanges.INCLUDE) { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val settings = snapshot?.toObject(UserSettingsDto::class.java)?.toDomain() ?: UserSettings()
                trySend(settings)
            }

        awaitClose { registration.remove() }
    }

    override suspend fun saveUserSettings(userId: String, settings: UserSettings): Result<Unit> = runCatching {
        val dto = UserSettingsDto.fromDomain(settings)
        preferencesDocument(userId)
            .set(dto, SetOptions.merge())
            .await()
    }

    override suspend fun syncUserProfile(userId: String, displayName: String, email: String): Result<Unit> = runCatching {
        val profileData = mapOf(
            "userId" to userId,
            "displayName" to displayName,
            "email" to email,
            "lastSyncTimestamp" to Timestamp.now()
        )

        userDocument(userId)
            .collection("profile")
            .document("info")
            .set(profileData, SetOptions.merge())
            .await()
    }

    override fun getUserProfileStream(userId: String): Flow<UserProfile?> = callbackFlow {
        if (userId.isBlank()) {
            trySend(null)
            close()
            return@callbackFlow
        }

        val registration = userDocument(userId)
            .collection("profile")
            .document("info")
            .addSnapshotListener(MetadataChanges.INCLUDE) { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val dto = snapshot?.toObject(UserProfileDto::class.java)
                trySend(dto?.toDomain())
            }

        awaitClose { registration.remove() }
    }
}

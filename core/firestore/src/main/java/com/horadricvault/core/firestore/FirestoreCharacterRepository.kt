package com.horadricvault.core.firestore

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.MetadataChanges
import com.google.firebase.firestore.SetOptions
import com.horadricvault.core.model.D4Character
import com.horadricvault.core.model.EquipmentItem
import com.horadricvault.core.model.EquipmentSlot
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.UUID

interface CharacterRepository {
    fun getCharactersStream(userId: String): Flow<List<D4Character>>
    fun getCharacterStream(userId: String, characterId: String): Flow<D4Character?>
    suspend fun upsertCharacter(userId: String, character: D4Character): Result<Unit>
    suspend fun updateEquipmentSlot(userId: String, characterId: String, slot: EquipmentSlot, item: EquipmentItem?): Result<Unit>
    suspend fun deleteCharacter(userId: String, characterId: String): Result<Unit>
}

class FirestoreCharacterRepository(
    private val firestore: FirebaseFirestore = FirestoreClient.instance
) : CharacterRepository {

    private fun charactersCollection(userId: String) =
        firestore.collection("users").document(userId).collection("characters")

    override fun getCharactersStream(userId: String): Flow<List<D4Character>> = callbackFlow {
        if (userId.isBlank()) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val registration = charactersCollection(userId)
            .addSnapshotListener(MetadataChanges.INCLUDE) { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val characters = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(CharacterDto::class.java)?.toDomain()
                    }
                    trySend(characters)
                }
            }

        awaitClose { registration.remove() }
    }

    override fun getCharacterStream(userId: String, characterId: String): Flow<D4Character?> = callbackFlow {
        if (userId.isBlank() || characterId.isBlank()) {
            trySend(null)
            close()
            return@callbackFlow
        }

        val registration = charactersCollection(userId).document(characterId)
            .addSnapshotListener(MetadataChanges.INCLUDE) { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val character = snapshot?.toObject(CharacterDto::class.java)?.toDomain()
                trySend(character)
            }

        awaitClose { registration.remove() }
    }

    override suspend fun upsertCharacter(userId: String, character: D4Character): Result<Unit> = runCatching {
        val targetId = if (character.characterId.isBlank()) UUID.randomUUID().toString() else character.characterId
        val finalCharacter = character.copy(characterId = targetId)
        val dto = CharacterDto.fromDomain(finalCharacter)
        
        charactersCollection(userId)
            .document(targetId)
            .set(dto, SetOptions.merge())
            .await()
    }

    override suspend fun updateEquipmentSlot(
        userId: String,
        characterId: String,
        slot: EquipmentSlot,
        item: EquipmentItem?
    ): Result<Unit> = runCatching {
        val itemDto = item?.let { EquipmentItemDto.fromDomain(it) }
        val updateMap = mapOf(
            "currentArmory.${slot.name}" to itemDto
        )

        charactersCollection(userId)
            .document(characterId)
            .update(updateMap)
            .await()
    }

    override suspend fun deleteCharacter(userId: String, characterId: String): Result<Unit> = runCatching {
        charactersCollection(userId)
            .document(characterId)
            .delete()
            .await()
    }
}

package com.horadricvault.core.firestore

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.MetadataChanges
import com.google.firebase.firestore.SetOptions
import com.horadricvault.core.model.RegionMapProgress
import com.horadricvault.core.model.SanctuaryRegion
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

interface MapProgressRepository {
    fun getRegionProgressStream(userId: String, regionId: String): Flow<RegionMapProgress?>
    fun getAllRegionsProgressStream(userId: String): Flow<List<RegionMapProgress>>
    suspend fun toggleNodeCompletion(userId: String, region: SanctuaryRegion, nodeId: String): Result<Unit>
    suspend fun toggleQuestCompletion(userId: String, region: SanctuaryRegion, questId: String): Result<Unit>
    suspend fun toggleStrongholdUnlocked(userId: String, region: SanctuaryRegion, strongholdId: String): Result<Unit>
    suspend fun saveRegionProgress(userId: String, progress: RegionMapProgress): Result<Unit>
}

class FirestoreMapProgressRepository(
    private val firestore: FirebaseFirestore = FirestoreClient.instance
) : MapProgressRepository {

    private fun mapProgressCollection(userId: String) =
        firestore.collection("users").document(userId).collection("map_progress")

    override fun getRegionProgressStream(userId: String, regionId: String): Flow<RegionMapProgress?> = callbackFlow {
        if (userId.isBlank() || regionId.isBlank()) {
            trySend(null)
            close()
            return@callbackFlow
        }

        val registration = mapProgressCollection(userId).document(regionId)
            .addSnapshotListener(MetadataChanges.INCLUDE) { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val progress = snapshot?.toObject(MapProgressDto::class.java)?.toDomain()
                trySend(progress)
            }

        awaitClose { registration.remove() }
    }

    override fun getAllRegionsProgressStream(userId: String): Flow<List<RegionMapProgress>> = callbackFlow {
        if (userId.isBlank()) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val registration = mapProgressCollection(userId)
            .addSnapshotListener(MetadataChanges.INCLUDE) { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val progressList = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(MapProgressDto::class.java)?.toDomain()
                    }
                    trySend(progressList)
                }
            }

        awaitClose { registration.remove() }
    }

    override suspend fun toggleNodeCompletion(
        userId: String,
        region: SanctuaryRegion,
        nodeId: String
    ): Result<Unit> = runCatching {
        val docRef = mapProgressCollection(userId).document(region.regionId)
        val snapshot = docRef.get().await()
        val currentDto = snapshot.toObject(MapProgressDto::class.java) ?: MapProgressDto(regionId = region.regionId)

        val updatedNodes = if (currentDto.completedNodeIds.contains(nodeId)) {
            currentDto.completedNodeIds - nodeId
        } else {
            currentDto.completedNodeIds + nodeId
        }

        val newDto = currentDto.copy(completedNodeIds = updatedNodes)
        docRef.set(newDto, SetOptions.merge()).await()
    }

    override suspend fun toggleQuestCompletion(
        userId: String,
        region: SanctuaryRegion,
        questId: String
    ): Result<Unit> = runCatching {
        val docRef = mapProgressCollection(userId).document(region.regionId)
        val snapshot = docRef.get().await()
        val currentDto = snapshot.toObject(MapProgressDto::class.java) ?: MapProgressDto(regionId = region.regionId)

        val updatedQuests = if (currentDto.completedQuests.contains(questId)) {
            currentDto.completedQuests - questId
        } else {
            currentDto.completedQuests + questId
        }

        val newDto = currentDto.copy(completedQuests = updatedQuests)
        docRef.set(newDto, SetOptions.merge()).await()
    }

    override suspend fun toggleStrongholdUnlocked(
        userId: String,
        region: SanctuaryRegion,
        strongholdId: String
    ): Result<Unit> = runCatching {
        val docRef = mapProgressCollection(userId).document(region.regionId)
        val snapshot = docRef.get().await()
        val currentDto = snapshot.toObject(MapProgressDto::class.java) ?: MapProgressDto(regionId = region.regionId)

        val updatedStrongholds = if (currentDto.strongholdsUnlocked.contains(strongholdId)) {
            currentDto.strongholdsUnlocked - strongholdId
        } else {
            currentDto.strongholdsUnlocked + strongholdId
        }

        val newDto = currentDto.copy(strongholdsUnlocked = updatedStrongholds)
        docRef.set(newDto, SetOptions.merge()).await()
    }

    override suspend fun saveRegionProgress(userId: String, progress: RegionMapProgress): Result<Unit> = runCatching {
        val dto = MapProgressDto.fromDomain(progress)
        mapProgressCollection(userId)
            .document(progress.regionId)
            .set(dto, SetOptions.merge())
            .await()
    }
}

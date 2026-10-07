package com.horadricvault.core.firestore

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.MetadataChanges
import com.google.firebase.firestore.SetOptions
import com.horadricvault.core.model.TargetBuild
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.UUID

interface BuildRepository {
    fun getBuildsStream(userId: String): Flow<List<TargetBuild>>
    fun getBuildStream(userId: String, buildId: String): Flow<TargetBuild?>
    suspend fun upsertBuild(userId: String, build: TargetBuild): Result<Unit>
    suspend fun deleteBuild(userId: String, buildId: String): Result<Unit>
}

class FirestoreBuildRepository(
    private val firestore: FirebaseFirestore = FirestoreClient.instance
) : BuildRepository {

    private fun buildsCollection(userId: String) =
        firestore.collection("users").document(userId).collection("builds")

    override fun getBuildsStream(userId: String): Flow<List<TargetBuild>> = callbackFlow {
        if (userId.isBlank()) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val registration = buildsCollection(userId)
            .addSnapshotListener(MetadataChanges.INCLUDE) { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val builds = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(BuildDto::class.java)?.toDomain()
                    }
                    trySend(builds)
                }
            }

        awaitClose { registration.remove() }
    }

    override fun getBuildStream(userId: String, buildId: String): Flow<TargetBuild?> = callbackFlow {
        if (userId.isBlank() || buildId.isBlank()) {
            trySend(null)
            close()
            return@callbackFlow
        }

        val registration = buildsCollection(userId).document(buildId)
            .addSnapshotListener(MetadataChanges.INCLUDE) { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val build = snapshot?.toObject(BuildDto::class.java)?.toDomain()
                trySend(build)
            }

        awaitClose { registration.remove() }
    }

    override suspend fun upsertBuild(userId: String, build: TargetBuild): Result<Unit> = runCatching {
        val targetId = if (build.buildId.isBlank()) UUID.randomUUID().toString() else build.buildId
        val finalBuild = build.copy(buildId = targetId)
        val dto = BuildDto.fromDomain(finalBuild)

        buildsCollection(userId)
            .document(targetId)
            .set(dto, SetOptions.merge())
            .await()
    }

    override suspend fun deleteBuild(userId: String, buildId: String): Result<Unit> = runCatching {
        buildsCollection(userId)
            .document(buildId)
            .delete()
            .await()
    }
}

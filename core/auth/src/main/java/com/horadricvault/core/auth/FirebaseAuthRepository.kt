package com.horadricvault.core.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

interface AuthRepository {
    val authState: StateFlow<AuthState>
    suspend fun signInWithGoogleIdToken(idToken: String): Result<AuthState>
    suspend fun signInAnonymously(): Result<AuthState>
    suspend fun signOut(): Result<Unit>
    fun getCurrentUserId(): String?
    fun isCurrentUserAnonymous(): Boolean
}

class FirebaseAuthRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) : AuthRepository {

    private val repositoryScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val _authState = MutableStateFlow<AuthState>(AuthState.Initializing)
    override val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val authListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        val user = firebaseAuth.currentUser
        _authState.value = mapFirebaseUserToAuthState(user)
    }

    init {
        auth.addAuthStateListener(authListener)
        _authState.value = mapFirebaseUserToAuthState(auth.currentUser)
    }

    override suspend fun signInWithGoogleIdToken(idToken: String): Result<AuthState> = withContext(Dispatchers.IO) {
        try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val currentUser = auth.currentUser

            val authResult = if (currentUser != null && currentUser.isAnonymous) {
                try {
                    // Seamless account linking so guest character data is preserved under the same UID
                    currentUser.linkWithCredential(credential).await()
                } catch (collision: FirebaseAuthUserCollisionException) {
                    // If the Google account already exists as a separate user, sign into that account
                    auth.signInWithCredential(credential).await()
                }
            } else {
                auth.signInWithCredential(credential).await()
            }

            val finalState = mapFirebaseUserToAuthState(authResult.user)
            _authState.value = finalState
            Result.success(finalState)
        } catch (e: Exception) {
            val errorState = AuthState.Error(e.localizedMessage ?: "Google Authentication Failed")
            _authState.value = errorState
            Result.failure(e)
        }
    }

    override suspend fun signInAnonymously(): Result<AuthState> = withContext(Dispatchers.IO) {
        try {
            val authResult = auth.signInAnonymously().await()
            val state = mapFirebaseUserToAuthState(authResult.user)
            _authState.value = state
            Result.success(state)
        } catch (e: Exception) {
            val errorState = AuthState.Error(e.localizedMessage ?: "Anonymous Sign-in Failed")
            _authState.value = errorState
            Result.failure(e)
        }
    }

    override suspend fun signOut(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            auth.signOut()
            _authState.value = AuthState.Unauthenticated
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getCurrentUserId(): String? = auth.currentUser?.uid

    override fun isCurrentUserAnonymous(): Boolean = auth.currentUser?.isAnonymous == true

    private fun mapFirebaseUserToAuthState(user: FirebaseUser?): AuthState {
        return when {
            user == null -> AuthState.Unauthenticated
            user.isAnonymous -> AuthState.Anonymous(userId = user.uid)
            else -> AuthState.Authenticated(
                userId = user.uid,
                email = user.email,
                displayName = user.displayName
            )
        }
    }
}

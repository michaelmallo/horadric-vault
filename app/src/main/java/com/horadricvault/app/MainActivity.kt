package com.horadricvault.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.horadricvault.core.auth.AuthState
import com.horadricvault.feature.statcheck.VaultDesignTokens
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val app = application as HoradricVaultApp
        val container = app.container

        setContent {
            HoradricVaultTheme {
                val authState by container.authRepository.authState.collectAsState()
                var isAuthLoading by remember { mutableStateOf(false) }
                var authErrorMessage by remember { mutableStateOf<String?>(null) }

                val onGoogleSignIn = {
                    isAuthLoading = true
                    authErrorMessage = null
                    lifecycleScope.launch {
                        val tokenResult = container.googleAuthClient.signInWithGoogle(activityContext = this@MainActivity)
                        tokenResult.fold(
                            onSuccess = { idToken ->
                                val firebaseResult = container.authRepository.signInWithGoogleIdToken(idToken)
                                firebaseResult.onFailure {
                                    authErrorMessage = it.localizedMessage ?: "Google sign-in failed"
                                }
                                isAuthLoading = false
                            },
                            onFailure = { error ->
                                authErrorMessage = error.localizedMessage ?: "Credential Manager cancelled or failed"
                                isAuthLoading = false
                            }
                        )
                    }
                }

                val onGuestSignIn = {
                    isAuthLoading = true
                    authErrorMessage = null
                    lifecycleScope.launch {
                        val result = container.authRepository.signInAnonymously()
                        result.onFailure {
                            authErrorMessage = it.localizedMessage ?: "Guest sign-in failed"
                        }
                        isAuthLoading = false
                    }
                }

                val onSignOut = {
                    lifecycleScope.launch {
                        container.authRepository.signOut()
                    }
                }

                when (val state = authState) {
                    is AuthState.Initializing -> {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(VaultDesignTokens.AbyssalBlack),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = VaultDesignTokens.HellfireGold)
                        }
                    }

                    is AuthState.Unauthenticated, is AuthState.Error -> {
                        LoginScreen(
                            onGoogleSignIn = { onGoogleSignIn() },
                            onGuestSignIn = { onGuestSignIn() },
                            isLoading = isAuthLoading,
                            errorMessage = authErrorMessage ?: (state as? AuthState.Error)?.message
                        )
                    }

                    is AuthState.Authenticated -> {
                        MainAppShell(
                            userId = state.userId,
                            authState = state,
                            container = container,
                            onSignOut = { onSignOut() },
                            onLinkGoogleAccount = { onGoogleSignIn() }
                        )
                    }

                    is AuthState.Anonymous -> {
                        MainAppShell(
                            userId = state.userId,
                            authState = state,
                            container = container,
                            onSignOut = { onSignOut() },
                            onLinkGoogleAccount = { onGoogleSignIn() }
                        )
                    }
                }
            }
        }
    }
}

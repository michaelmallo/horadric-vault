package com.horadricvault.core.auth

sealed interface AuthState {
    data object Initializing : AuthState
    
    data class Authenticated(
        val userId: String,
        val email: String? = null,
        val displayName: String? = null
    ) : AuthState

    data class Anonymous(
        val userId: String
    ) : AuthState

    data object Unauthenticated : AuthState

    data class Error(
        val message: String
    ) : AuthState
}

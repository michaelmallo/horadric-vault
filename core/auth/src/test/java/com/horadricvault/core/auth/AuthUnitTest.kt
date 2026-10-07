package com.horadricvault.core.auth

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AuthUnitTest {

    @Test
    fun testAuthStateRepresentations() {
        val unauth = AuthState.Unauthenticated
        assertTrue(unauth is AuthState.Unauthenticated)

        val guest = AuthState.Anonymous(userId = "guest_12345")
        assertEquals("guest_12345", guest.userId)

        val authenticated = AuthState.Authenticated(
            userId = "google_user_999",
            email = "nephalem@sanctuary.org",
            displayName = "Horadric Mage"
        )
        assertEquals("google_user_999", authenticated.userId)
        assertEquals("nephalem@sanctuary.org", authenticated.email)
        assertEquals("Horadric Mage", authenticated.displayName)

        val error = AuthState.Error(message = "Network timeout connecting to Sanctuary auth gateway")
        assertEquals("Network timeout connecting to Sanctuary auth gateway", error.message)
    }
}

package com.horadricvault.core.auth

import android.content.Context
import androidx.activity.ComponentActivity
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException

class GoogleAuthClient(
    private val defaultWebClientId: String? = null
) {

    fun buildGoogleSignInRequest(webClientId: String): GetCredentialRequest {
        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false) // REQUIRED: false allows first-time account selection
            .setServerClientId(webClientId)      // MUST be Web Client ID (client_type 3)
            .setAutoSelectEnabled(false)
            .build()

        return GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()
    }

    suspend fun signInWithGoogle(
        activityContext: ComponentActivity,
        explicitWebClientId: String? = null
    ): Result<String> {
        return try {
            val resolvedClientId = explicitWebClientId
                ?: defaultWebClientId
                ?: resolveDefaultWebClientId(activityContext)

            val request = buildGoogleSignInRequest(resolvedClientId)
            val credentialManager = CredentialManager.create(activityContext)

            val result = credentialManager.getCredential(
                request = request,
                context = activityContext
            )

            when (val credential = result.credential) {
                is CustomCredential -> {
                    if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                        val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                        Result.success(googleIdTokenCredential.idToken)
                    } else {
                        Result.failure(IllegalStateException("Unexpected credential type: ${credential.type}"))
                    }
                }
                else -> Result.failure(IllegalStateException("Unrecognized credential return"))
            }
        } catch (e: GetCredentialCancellationException) {
            Result.failure(e)
        } catch (e: NoCredentialException) {
            Result.failure(e)
        } catch (e: GoogleIdTokenParsingException) {
            Result.failure(e)
        } catch (e: GetCredentialException) {
            Result.failure(e)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signIn(activityContext: ComponentActivity): Result<String> {
        return signInWithGoogle(activityContext)
    }

    private fun resolveDefaultWebClientId(context: Context): String {
        val resId = context.resources.getIdentifier("default_web_client_id", "string", context.packageName)
        return if (resId != 0) {
            context.getString(resId)
        } else {
            "123456789012-sampleclientid.apps.googleusercontent.com"
        }
    }
}

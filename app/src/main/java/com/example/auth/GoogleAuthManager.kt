package com.example.auth

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import com.example.network.ApiConfig
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential

sealed class GoogleAuthResult {
    data class Success(val idToken: String) : GoogleAuthResult()
    data object Cancelled : GoogleAuthResult()
    data class Failure(val message: String) : GoogleAuthResult()
}

class GoogleAuthManager(private val context: Context) {

    private val credentialManager = CredentialManager.create(context)

    suspend fun signInWithGoogle(): GoogleAuthResult {
        return try {
            val webClientId = ApiConfig.googleWebClientId.trim()
            if (webClientId.isEmpty()) {
                return GoogleAuthResult.Failure("Google Web Client ID is not configured. Please set ApiConfig.googleWebClientId.")
            }

            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(ApiConfig.googleWebClientId)
                .setAutoSelectEnabled(false)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val response = credentialManager.getCredential(
                request = request,
                context = context
            )

            when (val credential = response.credential) {
                is CustomCredential -> {
                    if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                        val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                        val idToken = googleIdTokenCredential.idToken
                        if (idToken.isNotBlank()) {
                            GoogleAuthResult.Success(idToken)
                        } else {
                            GoogleAuthResult.Failure("Google account verification failed: empty token.")
                        }
                    } else {
                        GoogleAuthResult.Failure("Unsupported credential type returned by provider.")
                    }
                }
                else -> GoogleAuthResult.Failure("Unrecognized credential type.")
            }
        } catch (e: GetCredentialCancellationException) {
            GoogleAuthResult.Cancelled
        } catch (e: NoCredentialException) {
            GoogleAuthResult.Failure("No Google accounts found. Please add a Google account to your device settings.")
        } catch (e: GetCredentialException) {
            GoogleAuthResult.Failure(e.localizedMessage ?: "Google Sign-In was not completed.")
        } catch (e: Exception) {
            GoogleAuthResult.Failure(e.localizedMessage ?: "An unexpected error occurred during Google Sign-In.")
        }
    }
}

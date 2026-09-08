package com.zahraag.pawsitivehabits

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.lifecycle.lifecycleScope
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.zahraag.pawsitivehabits.Navigation.AppNavigation
import com.zahraag.pawsitivehabits.ui.theme.PawsitiveHabitsTheme
import com.zahraag.pawsitivehabits.viewmodel.AuthViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val authViewModel: AuthViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PawsitiveHabitsTheme {
                AppNavigation(
                    authViewModel = authViewModel,
                    onGoogleSignInTriggered = { signInWithGoogle() }
                )
            }

        }
    }


    private fun signInWithGoogle() {
        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(getString(R.string.default_web_client_id))
            .build()

        val credentialRequest = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        lifecycleScope.launch {
            try {
                val credentialManager = CredentialManager.create(this@MainActivity)
                val credentialResult = credentialManager.getCredential(
                    request = credentialRequest,
                    context = this@MainActivity
                )

                val credential = credentialResult.credential
                if (credential is CustomCredential &&
                    credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
                ) {
                    val googleIdTokenCredential =
                        GoogleIdTokenCredential.createFrom(credential.data)
                    // Pass the retrieved ID token to the ViewModel for Firebase & MongoDB sync
                    authViewModel.handleGoogleIdToken(googleIdTokenCredential.idToken)
                }
            } catch (e: Exception) {
                Log.e("MainActivity", "Google Sign-In failed", e)
            }
        }
    }
}


package com.zahraag.pawsitivehabits.data.repository

import android.R.attr.password
import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.zahraag.pawsitivehabits.data.models.User
import kotlinx.coroutines.tasks.await

class AuthRepository(private val context: Context) {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    suspend fun signUpWithEmail(
        email: String,
        pass: String,
        firstName: String,
        lastName: String
    ): Pair<String, User>? {
        // create User in Firebase Auth
        val result = auth.createUserWithEmailAndPassword(email, pass).await()
        val firebaseUser = result.user ?: return null

        val fullName = "$firstName $lastName".trim()

        // set Firebase Display Name Profile
        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName(fullName)
            .build()
        firebaseUser.updateProfile(profileUpdates).await()

        // get Firebase ID Token for Backend Sync
        val idToken = firebaseUser.getIdToken(true).await().token ?: return null

        // construct local entity payload
        val localUser = User(
            firebaseUid = firebaseUser.uid,
            email = email,
            firstName = firstName,
            lastName = lastName,
            displayName = fullName,
            authProvider = "password",
            password = pass
        )

        return Pair("Bearer $idToken", localUser)
    }
}
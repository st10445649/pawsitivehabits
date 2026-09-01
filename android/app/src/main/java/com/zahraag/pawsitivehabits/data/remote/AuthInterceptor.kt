package com.zahraag.pawsitivehabits.data.remote

import android.content.Context
import android.content.SharedPreferences
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val tokenManager: TokenManager) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        var token = tokenManager.getCustomJwtToken()
        val user = FirebaseAuth.getInstance().currentUser

        if (token.isNullOrEmpty()) {
            val currentUser = FirebaseAuth.getInstance().currentUser
            token = user?.let {
                try {
                    runBlocking { it.getIdToken(false).await().token }
                } catch (e: Exception) {
                    null
                }
            }
        }

        val requestBuilder = originalRequest.newBuilder()
        token?.isEmpty()?.let {
            if (!it) {
                requestBuilder.addHeader("Authorization", "Bearer $token")
            }
        }

        return chain.proceed(requestBuilder.build())
    }
}

class TokenManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(
        PREF_NAME,
        Context.MODE_PRIVATE
    )
    private var customJwt: String? = null

    fun saveCustomJwtToken(token: String) {
        prefs.edit().putString(KEY_JWT_TOKEN, token).apply()
    }

    fun getCustomJwtToken(): String? {
        return prefs.getString(KEY_JWT_TOKEN, null)
    }

    fun clear() {
        prefs.edit().remove(KEY_JWT_TOKEN).apply()
    }

    companion object {
        private const val PREF_NAME = "pawsitive_habits_auth_prefs"
        private const val KEY_JWT_TOKEN = "custom_jwt_token"
    }
}
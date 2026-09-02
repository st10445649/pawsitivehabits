package com.zahraag.pawsitivehabits.data.remote

import android.content.Context
import android.content.SharedPreferences
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import okhttp3.Interceptor
import okhttp3.Response
import org.json.JSONObject
import android.util.Base64

class AuthInterceptor(private val tokenManager: TokenManager) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        val path = originalRequest.url.encodedPath

        if (path.contains("/auth/login") ||
            path.contains("/auth/register") ||
            path.contains("/auth/google")
        ) {
            return chain.proceed(originalRequest)
        }

        val requestBuilder = originalRequest.newBuilder()

        var token = tokenManager.getCustomJwtToken()

        if (token.isNullOrEmpty()) {
            val user = FirebaseAuth.getInstance().currentUser
            if (user != null) {
                try {
                    val task = user.getIdToken(false)
                    val result = Tasks.await(task)
                    token = result.token
                } catch (e: Exception) {
                    token = null
                }
            }
        }

        if (!token.isNullOrEmpty()) {
            requestBuilder.header("Authorization", "Bearer $token")
        }

        return chain.proceed(requestBuilder.build())
    }
}

class TokenManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(
        PREF_NAME,
        Context.MODE_PRIVATE
    )

    @Volatile
    private var customJwt: String? = prefs.getString(KEY_JWT_TOKEN, null)

    fun saveCustomJwtToken(token: String, userId: String? = null) {
        customJwt = token
        val extractedId = userId ?: extractUserIdFromToken(token)

        prefs.edit().apply {
            putString(KEY_JWT_TOKEN, token)
            putString(KEY_USER_ID, extractedId)
            apply()
        }
    }

    fun getCustomJwtToken(): String? {
        if (customJwt == null) {
            customJwt = prefs.getString(KEY_JWT_TOKEN, null)
        }
        return customJwt
    }

    fun getUserId(): String? {
        return prefs.getString(KEY_USER_ID, null)
    }

    fun clear() {
        customJwt=null
        prefs.edit().remove(KEY_JWT_TOKEN).apply()
    }

    private fun extractUserIdFromToken(token: String): String? {
        return try {
            val parts = token.split(".")
            if (parts.size < 2) return null
            val payloadJson = String(
                Base64.decode(parts[1], Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP)
            )
            val jsonObject = JSONObject(payloadJson)
            if (jsonObject.has("id")) jsonObject.getString("id") else jsonObject.optString("sub", null)
        } catch (e: Exception) {
            null
        }
    }

    companion object {
        private const val PREF_NAME = "pawsitive_habits_auth_prefs"
        private const val KEY_JWT_TOKEN = "custom_jwt_token"
        private const val KEY_USER_ID = "current_user_id"
    }
}
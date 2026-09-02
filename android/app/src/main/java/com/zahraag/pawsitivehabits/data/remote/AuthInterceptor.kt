package com.zahraag.pawsitivehabits.data.remote

import android.content.Context
import android.content.SharedPreferences
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

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

    fun saveCustomJwtToken(token: String) {
        customJwt = token
        prefs.edit().putString(KEY_JWT_TOKEN, token).apply()
    }

    fun getCustomJwtToken(): String? {
        if (customJwt == null) {
            customJwt = prefs.getString(KEY_JWT_TOKEN, null)
        }
        return customJwt
    }

    fun clear() {
        customJwt=null
        prefs.edit().remove(KEY_JWT_TOKEN).apply()
    }

    companion object {
        private const val PREF_NAME = "pawsitive_habits_auth_prefs"
        private const val KEY_JWT_TOKEN = "custom_jwt_token"
    }
}